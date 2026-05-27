package com.example.myapplication.quality.check

import android.database.sqlite.SQLiteDatabase
import com.example.myapplication.quality.domain.PlotRef
import com.example.myapplication.quality.domain.PreparedRuleQuery
import com.example.myapplication.quality.domain.SkippedRule
import com.example.myapplication.quality.rules.EmbeddedRule

interface ZdbSchemaInspector {
    fun hasTable(tableName: String): Boolean

    fun columns(tableName: String): Set<String>
}

class SQLiteZdbSchemaInspector(
    private val database: SQLiteDatabase,
) : ZdbSchemaInspector {
    override fun hasTable(tableName: String): Boolean =
        database.rawQuery(
            "SELECT 1 FROM sqlite_master WHERE type = 'table' AND name = ? LIMIT 1",
            arrayOf(tableName),
        ).use { cursor -> cursor.moveToFirst() }

    override fun columns(tableName: String): Set<String> =
        database.rawQuery("PRAGMA table_info(\"${tableName.replace("\"", "\"\"")}\")", null).use { cursor ->
            val nameIndex = cursor.getColumnIndexOrThrow("name")
            buildSet {
                while (cursor.moveToNext()) {
                    add(cursor.getString(nameIndex).uppercase())
                }
            }
        }
}

sealed interface RuleCompatibility {
    data class Ready(val query: PreparedRuleQuery) : RuleCompatibility

    data class Skipped(val result: SkippedRule) : RuleCompatibility
}

class RuleCompatibilityChecker {
    fun prepare(
        plot: PlotRef,
        rule: EmbeddedRule,
        schema: ZdbSchemaInspector,
    ): RuleCompatibility {
        val missingTables = rule.requiredTables.filterNot(schema::hasTable)
        if (missingTables.isNotEmpty()) {
            return skipped(plot, rule, "Missing required tables: ${missingTables.joinToString()}.")
        }

        val targetColumns = schema.columns(rule.targetTable)
        val missingFields = rule.requiredFields.filterNot { it.uppercase() in targetColumns }
        if (missingFields.isNotEmpty()) {
            return skipped(plot, rule, "Missing required fields in ${rule.targetTable}: ${missingFields.joinToString()}.")
        }

        return RuleCompatibility.Ready(
            PreparedRuleQuery(
                rule = rule,
                sql = rule.sql.replace(":ydId", "?"),
                arguments = arrayOf(plot.rawPlotId),
            ),
        )
    }

    private fun skipped(plot: PlotRef, rule: EmbeddedRule, reason: String): RuleCompatibility.Skipped =
        RuleCompatibility.Skipped(
            SkippedRule(
                plot = plot,
                ruleId = rule.id,
                severity = rule.severity,
                title = rule.title,
                tableName = rule.targetTable,
                reason = reason,
            ),
        )
}
