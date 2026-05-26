package com.example.myapplication.quality.domain

interface DataDirectoryStore {
    fun savedDirectory(): DataDirectoryRef?

    fun saveDirectory(directory: DataDirectoryRef)
}

interface ZdbSourceScanner {
    fun scan(directory: DataDirectoryRef): ZdbScanResult
}

interface PlotIndexRepository {
    fun index(sources: List<ZdbSourceRef>): PlotIndexResult
}

interface CheckScopeSelector {
    fun countyOptions(plots: List<PlotRef>): List<String>

    fun filterPlots(plots: List<PlotRef>, countyLabel: String?, plotIdQuery: String): List<PlotRef>

    fun single(plot: PlotRef): CheckScope.Single

    fun county(plots: List<PlotRef>, countyLabel: String): CheckScope.County

    fun all(plots: List<PlotRef>): CheckScope.All
}
