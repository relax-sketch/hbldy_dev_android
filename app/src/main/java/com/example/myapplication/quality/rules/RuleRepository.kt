package com.example.myapplication.quality.rules

import android.content.res.AssetManager

interface RuleRepository {
    fun loadRuleSet(): EmbeddedRuleSet

    fun summary(): RuleSetSummary
}

data class RuleSetSummary(
    val ruleSetVersion: String,
    val totalRules: Int,
    val baselineRules: Int,
    val additionalRules: Int,
    val mandatoryRules: Int,
    val advisoryRules: Int,
)

class AssetRuleRepository(
    private val assetManager: AssetManager,
) : RuleRepository {
    private val ruleSet: EmbeddedRuleSet by lazy {
        assetManager.open(RULE_SET_ASSET_PATH).bufferedReader(Charsets.UTF_8).use { reader ->
            EmbeddedRuleSetParser.parse(reader.readText()).also { parsed ->
                parsed.rules.forEach(ReadOnlySqlValidator::validate)
            }
        }
    }

    override fun loadRuleSet(): EmbeddedRuleSet = ruleSet

    override fun summary(): RuleSetSummary {
        val rules = ruleSet.rules
        val sourcesById = ruleSet.sources.associateBy(RuleSourceMetadata::id)
        return RuleSetSummary(
            ruleSetVersion = ruleSet.ruleSetVersion,
            totalRules = rules.size,
            baselineRules = rules.count { sourcesById[it.sourceId]?.kind == RuleSourceKind.BASE_SNAPSHOT },
            additionalRules = rules.count { sourcesById[it.sourceId]?.kind == RuleSourceKind.ADDITIONAL },
            mandatoryRules = rules.count { it.severity == RuleSeverity.MANDATORY },
            advisoryRules = rules.count { it.severity == RuleSeverity.ADVISORY },
        )
    }

    private companion object {
        const val RULE_SET_ASSET_PATH = "rules/rule-set.json"
    }
}
