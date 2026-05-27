package com.example.myapplication.quality.review

import com.example.myapplication.quality.domain.CheckIssue
import com.example.myapplication.quality.domain.PassedRule
import com.example.myapplication.quality.domain.PlotCheckResult
import com.example.myapplication.quality.domain.PlotRef
import com.example.myapplication.quality.domain.SkippedRule
import com.example.myapplication.quality.check.QualityCheckRun

data class ReviewedPlotResult(
    val plot: PlotRef,
    val pendingMandatory: List<CheckIssue>,
    val pendingAdvisory: List<CheckIssue>,
    val ignored: List<CheckIssue>,
    val skippedRules: List<SkippedRule>,
    val passedRules: List<PassedRule>,
    val executedRuleCount: Int,
) {
    val pendingCount: Int = pendingMandatory.size + pendingAdvisory.size
    val detailCountText: String =
        "强制性 ${pendingMandatory.size} · 提示性 ${pendingAdvisory.size} · 跳过 ${skippedRules.size} · 忽略 ${ignored.size}"
}

data class ReviewSummary(
    val checkedPlots: Int,
    val plotsWithMandatoryIssues: Int,
    val pendingMandatoryIssues: Int,
    val pendingAdvisoryIssues: Int,
    val ignoredIssues: Int,
    val executedRules: Int,
    val skippedRules: Int,
    val passedRules: Int,
)

data class ReviewedCheckRun(
    val sourceRun: QualityCheckRun,
    val plotResults: List<ReviewedPlotResult>,
    val summary: ReviewSummary,
)

fun ReviewedPlotResult.toPlotCheckResult(): PlotCheckResult =
    PlotCheckResult(
        plot = plot,
        issues = pendingMandatory + pendingAdvisory + ignored,
        skippedRules = skippedRules,
        passedRules = passedRules,
        executedRuleCount = executedRuleCount,
    )
