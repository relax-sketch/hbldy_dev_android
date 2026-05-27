package com.example.myapplication.quality.rules

import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TemporarilyDisabledRulesTest {
    @Test
    fun packagedRuleSet_disablesExactlySeventyJmRules() {
        val rules = EmbeddedRuleSetParser.parse(assetRuleSet().readText()).rules
        val disabled = rules.filter(TemporarilyDisabledRules::isDisabled)

        assertEquals(70, disabled.size)
        assertEquals(rules.size - 70, TemporarilyDisabledRules.enabledRules(rules).size)
    }

    @Test
    fun disabledPredicate_matchesEncryptedSampleTableReferences() {
        assertTrue(TemporarilyDisabledRules.isDisabled(rule(targetTable = "YD_JM_PT")))
        assertTrue(TemporarilyDisabledRules.isDisabled(rule(requiredTables = listOf("YF_JMCC_TB"))))
        assertTrue(
            TemporarilyDisabledRules.isDisabled(
                rule(sql = "SELECT * FROM ZWDCB_JMGDCG_TB WHERE YD_ID = :ydId"),
            ),
        )
        assertFalse(TemporarilyDisabledRules.isDisabled(rule()))
    }

    private fun rule(
        targetTable: String = "YD_TRCY_PT",
        requiredTables: List<String> = listOf(targetTable),
        sql: String = "SELECT * FROM $targetTable WHERE YD_ID = :ydId",
    ): EmbeddedRule =
        EmbeddedRule(
            id = "TEST",
            sourceId = "test",
            severity = RuleSeverity.MANDATORY,
            targetTable = targetTable,
            title = "test",
            explanation = "test",
            requiredTables = requiredTables,
            requiredFields = listOf("YD_ID"),
            locatorFields = listOf("YD_ID"),
            sql = sql,
        )

    private fun assetRuleSet(): File =
        listOf(
            File("src/main/assets/rules/rule-set.json"),
            File("app/src/main/assets/rules/rule-set.json"),
        ).first(File::isFile)
}
