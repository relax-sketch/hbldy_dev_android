package com.example.myapplication.quality.review

import com.example.myapplication.quality.domain.CheckIssue
import com.example.myapplication.quality.domain.PassedRule
import com.example.myapplication.quality.domain.SkippedRule

enum class DetailStatusFilter(val label: String) {
    ALL("全部"),
    FAILED("未通过"),
    MANDATORY("强制性"),
    ADVISORY("提示性"),
    IGNORED("已忽略"),
}

enum class QualityTableGroup(val label: String) {
    ALL("全部"),
    PLOT("样地表"),
    TRANSECT("样线表"),
    MEASUREMENT_SAMPLE("测产样方"),
    OBSERVATION_SAMPLE("观测样方"),
    TALL_SHRUB("高大草灌"),
}

data class DetailFilterCounts(
    val all: Int,
    val failed: Int,
    val mandatory: Int,
    val advisory: Int,
    val ignored: Int,
)

data class FilteredDetailResult(
    val pendingMandatory: List<CheckIssue>,
    val pendingAdvisory: List<CheckIssue>,
    val ignored: List<CheckIssue>,
    val skippedRules: List<SkippedRule>,
    val passedRules: List<PassedRule>,
    val counts: DetailFilterCounts,
) {
    val visibleCount: Int =
        pendingMandatory.size + pendingAdvisory.size + ignored.size + skippedRules.size + passedRules.size
}

fun ReviewedPlotResult.filteredDetail(
    statusFilter: DetailStatusFilter,
    tableGroup: QualityTableGroup,
    includePassed: Boolean,
): FilteredDetailResult {
    val groupMandatory = pendingMandatory.filter { it.matches(tableGroup) }
    val groupAdvisory = pendingAdvisory.filter { it.matches(tableGroup) }
    val groupIgnored = ignored.filter { it.matches(tableGroup) }
    val groupSkipped = skippedRules.filter { it.matches(tableGroup) }
    val groupPassed = passedRules.filter { it.matches(tableGroup) }
    val visiblePassed = if (includePassed) groupPassed else emptyList()
    val counts = DetailFilterCounts(
        all = groupMandatory.size + groupAdvisory.size + groupIgnored.size + groupSkipped.size + visiblePassed.size,
        failed = groupMandatory.size + groupAdvisory.size,
        mandatory = groupMandatory.size,
        advisory = groupAdvisory.size,
        ignored = groupIgnored.size,
    )
    return when (statusFilter) {
        DetailStatusFilter.ALL -> FilteredDetailResult(
            pendingMandatory = groupMandatory,
            pendingAdvisory = groupAdvisory,
            ignored = groupIgnored,
            skippedRules = groupSkipped,
            passedRules = visiblePassed,
            counts = counts,
        )
        DetailStatusFilter.FAILED -> FilteredDetailResult(
            pendingMandatory = groupMandatory,
            pendingAdvisory = groupAdvisory,
            ignored = emptyList(),
            skippedRules = emptyList(),
            passedRules = emptyList(),
            counts = counts,
        )
        DetailStatusFilter.MANDATORY -> FilteredDetailResult(
            pendingMandatory = groupMandatory,
            pendingAdvisory = emptyList(),
            ignored = emptyList(),
            skippedRules = emptyList(),
            passedRules = emptyList(),
            counts = counts,
        )
        DetailStatusFilter.ADVISORY -> FilteredDetailResult(
            pendingMandatory = emptyList(),
            pendingAdvisory = groupAdvisory,
            ignored = emptyList(),
            skippedRules = emptyList(),
            passedRules = emptyList(),
            counts = counts,
        )
        DetailStatusFilter.IGNORED -> FilteredDetailResult(
            pendingMandatory = emptyList(),
            pendingAdvisory = emptyList(),
            ignored = groupIgnored,
            skippedRules = emptyList(),
            passedRules = emptyList(),
            counts = counts,
        )
    }
}

fun tableGroupFor(tableName: String): QualityTableGroup? =
    when (tableName.uppercase()) {
        "YD_TRCY_PT",
        "YD_RGCD_PT",
        -> QualityTableGroup.PLOT
        "YX_TRCY_TB" -> QualityTableGroup.TRANSECT
        "YF_TRCYCC_TB",
        "ZWDCB_CCYF_TB",
        "YF_JMCC_TB",
        "ZWDCB_JMCCYF_TB",
        -> QualityTableGroup.MEASUREMENT_SAMPLE
        "YF_TRCYGC_TB",
        "ZWDCB_GCYF_TB",
        -> QualityTableGroup.OBSERVATION_SAMPLE
        "YF_TRCY_TB",
        "ZWDCB_GDCG_TB",
        "YF_JMGDCG_TB",
        "ZWDCB_JMGDCG_TB",
        -> QualityTableGroup.TALL_SHRUB
        else -> null
    }

private fun CheckIssue.matches(group: QualityTableGroup): Boolean =
    group == QualityTableGroup.ALL || tableGroupFor(tableName) == group

private fun SkippedRule.matches(group: QualityTableGroup): Boolean =
    group == QualityTableGroup.ALL || tableGroupFor(tableName) == group

private fun PassedRule.matches(group: QualityTableGroup): Boolean =
    group == QualityTableGroup.ALL || tableGroupFor(tableName) == group
