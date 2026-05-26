package com.example.myapplication.quality.review

import com.example.myapplication.quality.annotations.IssueAnnotationStore
import com.example.myapplication.quality.check.QualityCheckRun
import com.example.myapplication.quality.domain.CheckIssue
import com.example.myapplication.quality.domain.CheckScope
import com.example.myapplication.quality.domain.PlotCheckResult
import com.example.myapplication.quality.domain.PlotRef
import com.example.myapplication.quality.domain.PlotTable
import com.example.myapplication.quality.domain.ZdbSourceRef
import com.example.myapplication.quality.rules.RuleSetSummary
import com.example.myapplication.quality.rules.RuleSeverity
import org.junit.Assert.assertEquals
import org.junit.Test

class IssueReviewServiceTest {
    private val plot = PlotRef(
        source = ZdbSourceRef("content://fixture", "fixture", "fixture.zdb", 1),
        rawPlotId = "PLOT_1",
        displayPlotId = "PLOT_1",
        countyCode = "001",
        countyLabel = "江岸区",
        plotTable = PlotTable.NATURAL_GRASSLAND,
        parentGuid = null,
    )
    private val issue = CheckIssue(
        fingerprint = "fingerprint-1",
        plot = plot,
        ruleId = "RULE_1",
        severity = RuleSeverity.MANDATORY,
        title = "问题",
        explanation = "说明",
        tableName = "YD_TRCY_PT",
        locationValues = mapOf("YD_ID" to "PLOT_1"),
        actualValues = mapOf("actualValue" to "bad"),
    )

    @Test
    fun ignoreAndCancelIgnore_moveIssueBetweenPendingAndIgnoredSections() {
        val service = IssueReviewService(InMemoryAnnotationStore())
        val run = runWithIssues(listOf(issue))

        assertEquals(1, service.review(run).plotResults.single().pendingMandatory.size)
        service.ignore(issue)
        assertEquals(1, service.review(run).plotResults.single().ignored.size)
        service.cancelIgnore(issue)
        assertEquals(1, service.review(run).plotResults.single().pendingMandatory.size)
    }

    @Test
    fun recheck_preservesStillMatchingIgnoreAndRemovesResolvedIssue() {
        val service = IssueReviewService(InMemoryAnnotationStore())
        service.ignore(issue)

        assertEquals(1, service.review(runWithIssues(listOf(issue))).plotResults.single().ignored.size)
        val resolvedReview = service.review(runWithIssues(emptyList()))
        assertEquals(0, resolvedReview.plotResults.single().ignored.size)
        assertEquals(0, resolvedReview.summary.pendingMandatoryIssues)
        assertEquals(0, resolvedReview.summary.ignoredIssues)
    }

    private fun runWithIssues(issues: List<CheckIssue>): QualityCheckRun =
        QualityCheckRun(
            scope = CheckScope.Single(plot),
            ruleSetSummary = RuleSetSummary("test", 1, 0, 1, 1, 0),
            plotResults = listOf(
                PlotCheckResult(
                    plot = plot,
                    issues = issues,
                    skippedRules = emptyList(),
                    executedRuleCount = 1,
                ),
            ),
            cancelled = false,
        )

    private class InMemoryAnnotationStore : IssueAnnotationStore {
        private val ignored = mutableSetOf<String>()

        override fun ignoredFingerprints(fingerprints: Set<String>): Set<String> = ignored.intersect(fingerprints)

        override fun markIgnored(fingerprint: String, ignoredAtEpochMillis: Long) {
            ignored.add(fingerprint)
        }

        override fun removeIgnored(fingerprint: String) {
            ignored.remove(fingerprint)
        }
    }
}
