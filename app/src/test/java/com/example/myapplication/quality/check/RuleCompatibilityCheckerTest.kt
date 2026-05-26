package com.example.myapplication.quality.check

import com.example.myapplication.quality.domain.PlotRef
import com.example.myapplication.quality.domain.PlotTable
import com.example.myapplication.quality.domain.ZdbSourceRef
import com.example.myapplication.quality.rules.EmbeddedRule
import com.example.myapplication.quality.rules.RuleSeverity
import org.junit.Assert.assertTrue
import org.junit.Test

class RuleCompatibilityCheckerTest {
    @Test
    fun prepare_missingTable_returnsSkippedResult() {
        val result = RuleCompatibilityChecker().prepare(
            plot = plot(),
            rule = rule(),
            schema = FakeSchema(emptyMap()),
        )

        assertTrue(result is RuleCompatibility.Skipped)
        assertTrue((result as RuleCompatibility.Skipped).result.reason.contains("Missing required tables"))
    }

    @Test
    fun prepare_missingRequiredField_returnsSkippedResult() {
        val result = RuleCompatibilityChecker().prepare(
            plot = plot(),
            rule = rule(),
            schema = FakeSchema(mapOf("YD_TRCY_PT" to setOf("YD_ID"))),
        )

        assertTrue(result is RuleCompatibility.Skipped)
        assertTrue((result as RuleCompatibility.Skipped).result.reason.contains("MIAN_JI"))
    }

    private fun rule(): EmbeddedRule =
        EmbeddedRule(
            id = "STRUCTURE_TEST",
            sourceId = "test",
            severity = RuleSeverity.MANDATORY,
            targetTable = "YD_TRCY_PT",
            title = "structure",
            explanation = "structure",
            requiredTables = listOf("YD_TRCY_PT"),
            requiredFields = listOf("YD_ID", "MIAN_JI"),
            locatorFields = listOf("YD_ID"),
            sql = "SELECT YD_ID FROM YD_TRCY_PT WHERE YD_ID = :ydId",
        )

    private fun plot(): PlotRef =
        PlotRef(
            source = ZdbSourceRef("content://fixture", "fixture", "fixture.zdb", 1),
            rawPlotId = "PLOT_1",
            displayPlotId = "PLOT_1",
            countyCode = "001",
            countyLabel = "测试区",
            plotTable = PlotTable.NATURAL_GRASSLAND,
            parentGuid = null,
        )

    private class FakeSchema(private val tables: Map<String, Set<String>>) : ZdbSchemaInspector {
        override fun hasTable(tableName: String): Boolean = tableName in tables

        override fun columns(tableName: String): Set<String> = tables.getValue(tableName)
    }
}
