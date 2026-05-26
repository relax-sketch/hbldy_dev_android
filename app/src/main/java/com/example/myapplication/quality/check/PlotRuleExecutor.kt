package com.example.myapplication.quality.check

import android.database.Cursor
import android.database.sqlite.SQLiteException
import com.example.myapplication.quality.data.ReadOnlyZdbAccess
import com.example.myapplication.quality.domain.CheckIssue
import com.example.myapplication.quality.domain.PlotCheckResult
import com.example.myapplication.quality.domain.PlotRef
import com.example.myapplication.quality.domain.PreparedRuleQuery
import com.example.myapplication.quality.domain.SkippedRule
import com.example.myapplication.quality.rules.EmbeddedRule
import java.security.MessageDigest

interface PlotChecker {
    fun check(plot: PlotRef, rules: List<EmbeddedRule>): PlotCheckResult
}

class PlotRuleExecutor(
    private val databaseAccess: ReadOnlyZdbAccess,
    private val compatibilityChecker: RuleCompatibilityChecker = RuleCompatibilityChecker(),
) : PlotChecker {
    override fun check(plot: PlotRef, rules: List<EmbeddedRule>): PlotCheckResult =
        databaseAccess.read(plot.source) { database ->
            val schema = SQLiteZdbSchemaInspector(database)
            val issues = mutableListOf<CheckIssue>()
            val skippedRules = mutableListOf<SkippedRule>()
            var executedRuleCount = 0

            rules.forEach { rule ->
                when (val compatibility = compatibilityChecker.prepare(plot, rule, schema)) {
                    is RuleCompatibility.Skipped -> skippedRules.add(compatibility.result)
                    is RuleCompatibility.Ready -> {
                        try {
                            val matchedIssues = database.rawQuery(
                                compatibility.query.sql,
                                compatibility.query.arguments,
                            ).use { cursor ->
                                cursor.readIssues(plot, compatibility.query)
                            }
                            issues.addAll(matchedIssues)
                            executedRuleCount += 1
                        } catch (exception: SQLiteException) {
                            skippedRules.add(
                                SkippedRule(
                                    plot = plot,
                                    ruleId = rule.id,
                                    severity = rule.severity,
                                    title = rule.title,
                                    reason = "Rule query is incompatible with this source: ${exception.message.orEmpty()}",
                                ),
                            )
                        }
                    }
                }
            }

            PlotCheckResult(
                plot = plot,
                issues = issues,
                skippedRules = skippedRules,
                executedRuleCount = executedRuleCount,
            )
        }

    private fun Cursor.readIssues(plot: PlotRef, query: PreparedRuleQuery): List<CheckIssue> {
        val rule = query.rule
        val normalizedLocatorFields = rule.locatorFields.associateBy(String::uppercase)
        return buildList {
            while (moveToNext()) {
                val locationValues = linkedMapOf<String, String?>()
                val actualValues = linkedMapOf<String, String?>()
                columnNames.forEachIndexed { columnIndex, columnName ->
                    val canonicalLocator = normalizedLocatorFields[columnName.uppercase()]
                    val value = getDisplayValue(columnIndex)
                    if (canonicalLocator != null) {
                        locationValues[canonicalLocator] = value
                    } else {
                        actualValues[columnName] = value
                    }
                }
                add(
                    CheckIssue(
                        fingerprint = issueFingerprint(plot, rule, locationValues),
                        plot = plot,
                        ruleId = rule.id,
                        severity = rule.severity,
                        title = rule.title,
                        explanation = rule.explanation,
                        tableName = rule.targetTable,
                        locationValues = locationValues,
                        actualValues = actualValues,
                    ),
                )
            }
        }
    }

    private fun Cursor.getDisplayValue(columnIndex: Int): String? =
        when (getType(columnIndex)) {
            Cursor.FIELD_TYPE_NULL -> null
            Cursor.FIELD_TYPE_BLOB -> "[binary data]"
            else -> getString(columnIndex)
        }

    private fun issueFingerprint(
        plot: PlotRef,
        rule: EmbeddedRule,
        locationValues: Map<String, String?>,
    ): String {
        val raw = buildString {
            append(plot.source.uri)
            append('|')
            append(plot.rawPlotId)
            append('|')
            append(rule.id)
            locationValues.toSortedMap().forEach { (key, value) ->
                append('|')
                append(key)
                append('=')
                append(value.orEmpty())
            }
        }
        return MessageDigest.getInstance("SHA-256")
            .digest(raw.toByteArray(Charsets.UTF_8))
            .joinToString("") { byte -> "%02x".format(byte) }
    }
}
