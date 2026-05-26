package com.example.myapplication.quality.domain

data class DataDirectoryRef(
    val uri: String,
    val displayName: String,
)

data class ZdbSourceRef(
    val uri: String,
    val projectName: String,
    val fileName: String,
    val sizeBytes: Long,
)

data class InvalidZdbSource(
    val uri: String,
    val displayName: String,
    val reason: String,
)

data class ZdbScanResult(
    val directory: DataDirectoryRef,
    val validSources: List<ZdbSourceRef>,
    val invalidSources: List<InvalidZdbSource>,
)

enum class PlotTable(val tableName: String) {
    NATURAL_GRASSLAND("YD_TRCY_PT"),
    ARTIFICIAL_GRASSLAND("YD_RGCD_PT"),
}

data class PlotRef(
    val source: ZdbSourceRef,
    val rawPlotId: String,
    val displayPlotId: String,
    val countyCode: String?,
    val countyLabel: String?,
    val plotTable: PlotTable,
    val parentGuid: String?,
)

data class PlotIndexResult(
    val plots: List<PlotRef>,
    val rejectedSources: List<InvalidZdbSource>,
)

sealed interface CheckScope {
    val plots: List<PlotRef>

    data class Single(val plot: PlotRef) : CheckScope {
        override val plots: List<PlotRef> = listOf(plot)
    }

    data class County(
        val countyCode: String?,
        val countyLabel: String,
        override val plots: List<PlotRef>,
    ) : CheckScope

    data class All(override val plots: List<PlotRef>) : CheckScope
}
