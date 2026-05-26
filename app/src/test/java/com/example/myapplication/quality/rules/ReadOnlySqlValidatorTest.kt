package com.example.myapplication.quality.rules

import java.io.File
import org.junit.Assert.assertThrows
import org.junit.Test

class ReadOnlySqlValidatorTest {
    @Test
    fun validate_packagedRuleSet_acceptsOnlyReadOnlyScopedQueries() {
        val rules = EmbeddedRuleSetParser.parse(assetRuleSet().readText()).rules

        rules.forEach(ReadOnlySqlValidator::validate)
    }

    @Test
    fun validate_writeCapableStatement_rejectsRule() {
        val rule = rule("UPDATE YD_TRCY_PT SET MIAN_JI = 0 WHERE YD_ID = :ydId")

        assertThrows(UnsafeRuleSqlException::class.java) {
            ReadOnlySqlValidator.validate(rule)
        }
    }

    @Test
    fun validate_multipleStatements_rejectsRule() {
        val rule = rule(
            "SELECT YD_ID FROM YD_TRCY_PT WHERE YD_ID = :ydId; " +
                "DELETE FROM YD_TRCY_PT WHERE YD_ID = :ydId",
        )

        assertThrows(UnsafeRuleSqlException::class.java) {
            ReadOnlySqlValidator.validate(rule)
        }
    }

    private fun rule(sql: String): EmbeddedRule =
        EmbeddedRule(
            id = "SAFETY_TEST",
            sourceId = "test",
            severity = RuleSeverity.MANDATORY,
            targetTable = "YD_TRCY_PT",
            title = "safety",
            explanation = "safety",
            requiredTables = listOf("YD_TRCY_PT"),
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
