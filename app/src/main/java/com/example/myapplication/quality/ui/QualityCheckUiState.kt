package com.example.myapplication.quality.ui

import com.example.myapplication.quality.check.CheckProgress
import com.example.myapplication.quality.domain.CheckScope
import com.example.myapplication.quality.domain.DataDirectoryRef
import com.example.myapplication.quality.domain.InvalidZdbSource
import com.example.myapplication.quality.domain.PlotRef
import com.example.myapplication.quality.domain.ZdbScanResult
import com.example.myapplication.quality.review.ReviewedCheckRun
import com.example.myapplication.quality.review.ReviewedPlotResult
import com.example.myapplication.quality.review.DetailStatusFilter
import com.example.myapplication.quality.review.QualityTableGroup

enum class QualityScreen {
    SOURCE,
    SCOPE,
    PROGRESS,
    SUMMARY,
    DETAIL,
}

data class QualityCheckUiState(
    val screen: QualityScreen = QualityScreen.SOURCE,
    val directory: DataDirectoryRef? = null,
    val scanResult: ZdbScanResult? = null,
    val indexedPlots: List<PlotRef> = emptyList(),
    val rejectedSources: List<InvalidZdbSource> = emptyList(),
    val countyOptions: List<String> = emptyList(),
    val selectedCounty: String? = null,
    val plotQuery: String = "",
    val filteredPlots: List<PlotRef> = emptyList(),
    val selectedPlot: PlotRef? = null,
    val selectedScope: CheckScope? = null,
    val checkAllMode: Boolean = false,
    val testMode: Boolean = false,
    val progress: CheckProgress? = null,
    val reviewedRun: ReviewedCheckRun? = null,
    val detailPlot: ReviewedPlotResult? = null,
    val detailStatusFilter: DetailStatusFilter = DetailStatusFilter.ALL,
    val detailTableGroup: QualityTableGroup = QualityTableGroup.ALL,
    val isScanning: Boolean = false,
    val errorMessage: String? = null,
)

internal fun screenAfterCompletedCheck(scope: CheckScope): QualityScreen =
    when (scope) {
        is CheckScope.Single -> QualityScreen.DETAIL
        is CheckScope.County,
        is CheckScope.All,
        -> QualityScreen.SUMMARY
    }

internal fun screenAfterDetailBack(scope: CheckScope?): QualityScreen =
    if (scope is CheckScope.Single) QualityScreen.SCOPE else QualityScreen.SUMMARY

internal fun QualityCheckUiState.withCheckAllMode(
    enabled: Boolean,
    restoredCounty: String? = selectedCounty,
): QualityCheckUiState =
    if (enabled) {
        copy(
            checkAllMode = true,
            plotQuery = "",
            filteredPlots = indexedPlots,
            selectedPlot = null,
            selectedScope = null,
            errorMessage = null,
        )
    } else {
        val filtered = indexedPlots.filter { plot ->
            restoredCounty.isNullOrBlank() || plot.countyLabel == restoredCounty
        }
        copy(
            checkAllMode = false,
            selectedCounty = restoredCounty,
            plotQuery = "",
            filteredPlots = filtered,
            selectedPlot = null,
            selectedScope = null,
            errorMessage = null,
        )
    }
