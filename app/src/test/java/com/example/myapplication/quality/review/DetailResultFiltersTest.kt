package com.example.myapplication.quality.review

import com.example.myapplication.quality.domain.CheckIssue
import com.example.myapplication.quality.domain.PassedRule
import com.example.myapplication.quality.domain.PlotRef
import com.example.myapplication.quality.domain.PlotTable
import com.example.myapplication.quality.domain.SkippedRule
import com.example.myapplication.quality.domain.ZdbSourceRef
import com.example.myapplication.quality.rules.RuleSeverity
import org.junit.Assert.assertEquals
import org.junit.Test

class DetailResultFiltersTest {
    private val plot = PlotRef(
        source = ZdbSourceRef("content://fixture", "fixture", "fixture.zdb", 1),
        rawPlotId = "PLOT_1",
        displayPlotId = "PLOT_1",
        countyCode = "001",
        countyLabel = "江岸区",
        plotTable = PlotTable.NATURAL_GRASSLAND,
        parentGuid = null,
    )

    @Test
    fun failedFilter_includesOnlyPendingMandatoryAndAdvisoryIssues() {
        val result = reviewedResult()

        val filtered = result.filteredDetail(
            statusFilter = DetailStatusFilter.FAILED,
            tableGroup = QualityTableGroup.ALL,
            includePassed = true,
        )

        assertEquals(listOf("MANDATORY_PLOT"), filtered.pendingMandatory.map(CheckIssue::ruleId))
        assertEquals(listOf("ADVISORY_TRANSECT"), filtered.pendingAdvisory.map(CheckIssue::ruleId))
        assertEquals(emptyList<CheckIssue>(), filtered.ignored)
        assertEquals(emptyList<SkippedRule>(), filtered.skippedRules)
        assertEquals(emptyList<PassedRule>(), filtered.passedRules)
        assertEquals(2, filtered.counts.failed)
    }

    @Test
    fun tableGroupFilter_appliesToIssuesSkippedAndPassedRules() {
        val filtered = reviewedResult().filteredDetail(
            statusFilter = DetailStatusFilter.ALL,
            tableGroup = QualityTableGroup.MEASUREMENT_SAMPLE,
            includePassed = true,
        )

        assertEquals(emptyList<CheckIssue>(), filtered.pendingMandatory)
        assertEquals(emptyList<CheckIssue>(), filtered.pendingAdvisory)
        assertEquals(listOf("IGNORED_MEASUREMENT_PLANT"), filtered.ignored.map(CheckIssue::ruleId))
        assertEquals(listOf("SKIPPED_MEASUREMENT"), filtered.skippedRules.map(SkippedRule::ruleId))
        assertEquals(listOf("PASSED_MEASUREMENT"), filtered.passedRules.map(PassedRule::ruleId))
        assertEquals(3, filtered.counts.all)
        assertEquals(1, filtered.counts.ignored)
    }

    @Test
    fun plantSurveyTables_mapToCorrespondingBusinessGroups() {
        assertEquals(QualityTableGroup.MEASUREMENT_SAMPLE, tableGroupFor("ZWDCB_CCYF_TB"))
        assertEquals(QualityTableGroup.OBSERVATION_SAMPLE, tableGroupFor("ZWDCB_GCYF_TB"))
        assertEquals(QualityTableGroup.TALL_SHRUB, tableGroupFor("ZWDCB_GDCG_TB"))
    }

    @Test
    fun specificTableGroup_excludesUnknownTablesButAllIncludesThem() {
        val result = ReviewedPlotResult(
            plot = plot,
            pendingMandatory = listOf(issue("UNKNOWN", RuleSeverity.MANDATORY, "UNKNOWN_TABLE")),
            pendingAdvisory = emptyList(),
            ignored = emptyList(),
            skippedRules = emptyList(),
            passedRules = emptyList(),
            executedRuleCount = 1,
        )

        assertEquals(
            1,
            result.filteredDetail(DetailStatusFilter.ALL, QualityTableGroup.ALL, includePassed = false).counts.all,
        )
        assertEquals(
            0,
            result.filteredDetail(DetailStatusFilter.ALL, QualityTableGroup.PLOT, includePassed = false).counts.all,
        )
    }

    private fun reviewedResult(): ReviewedPlotResult =
        ReviewedPlotResult(
            plot = plot,
            pendingMandatory = listOf(issue("MANDATORY_PLOT", RuleSeverity.MANDATORY, "YD_TRCY_PT")),
            pendingAdvisory = listOf(issue("ADVISORY_TRANSECT", RuleSeverity.ADVISORY, "YX_TRCY_TB")),
            ignored = listOf(
                issue("IGNORED_MEASUREMENT_PLANT", RuleSeverity.MANDATORY, "ZWDCB_CCYF_TB", ignored = true),
            ),
            skippedRules = listOf(
                SkippedRule(
                    plot = plot,
                    ruleId = "SKIPPED_MEASUREMENT",
                    severity = RuleSeverity.MANDATORY,
                    title = "skipped",
                    tableName = "YF_TRCYCC_TB",
                    reason = "missing field",
                ),
            ),
            passedRules = listOf(
                PassedRule(
                    plot = plot,
                    ruleId = "PASSED_MEASUREMENT",
                    severity = RuleSeverity.ADVISORY,
                    title = "passed",
                    explanation = "passed",
                    tableName = "YF_TRCYCC_TB",
                ),
            ),
            executedRuleCount = 4,
        )

    private fun issue(
        ruleId: String,
        severity: RuleSeverity,
        tableName: String,
        ignored: Boolean = false,
    ): CheckIssue =
        CheckIssue(
            fingerprint = ruleId,
            plot = plot,
            ruleId = ruleId,
            severity = severity,
            title = ruleId,
            explanation = ruleId,
            tableName = tableName,
            locationValues = mapOf("YD_ID" to plot.rawPlotId),
            actualValues = emptyMap(),
            ignored = ignored,
        )
}
