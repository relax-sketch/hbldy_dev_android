package com.example.myapplication.quality.ui

import com.example.myapplication.quality.check.CheckProgress
import com.example.myapplication.quality.domain.CheckScope
import com.example.myapplication.quality.domain.DataDirectoryRef
import com.example.myapplication.quality.domain.InvalidZdbSource
import com.example.myapplication.quality.domain.PlotRef
import com.example.myapplication.quality.domain.ZdbScanResult
import com.example.myapplication.quality.review.ReviewedCheckRun
import com.example.myapplication.quality.review.ReviewedPlotResult

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
    val progress: CheckProgress? = null,
    val reviewedRun: ReviewedCheckRun? = null,
    val detailPlot: ReviewedPlotResult? = null,
    val isScanning: Boolean = false,
    val errorMessage: String? = null,
)
