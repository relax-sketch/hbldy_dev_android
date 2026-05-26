package com.example.myapplication.quality.domain

class DefaultCheckScopeSelector : CheckScopeSelector {
    override fun countyOptions(plots: List<PlotRef>): List<String> =
        plots.mapNotNull(PlotRef::countyLabel)
            .filter(String::isNotBlank)
            .distinct()
            .sorted()

    override fun filterPlots(
        plots: List<PlotRef>,
        countyLabel: String?,
        plotIdQuery: String,
    ): List<PlotRef> {
        val query = plotIdQuery.trim()
        return plots.filter { plot ->
            val matchesCounty = countyLabel.isNullOrBlank() || plot.countyLabel == countyLabel
            val matchesId = query.isEmpty() ||
                plot.rawPlotId.contains(query, ignoreCase = true) ||
                plot.displayPlotId.contains(query, ignoreCase = true)
            matchesCounty && matchesId
        }
    }

    override fun single(plot: PlotRef): CheckScope.Single = CheckScope.Single(plot)

    override fun county(plots: List<PlotRef>, countyLabel: String): CheckScope.County =
        CheckScope.County(
            countyCode = plots.firstOrNull { it.countyLabel == countyLabel }?.countyCode,
            countyLabel = countyLabel,
            plots = plots.filter { it.countyLabel == countyLabel },
        )

    override fun all(plots: List<PlotRef>): CheckScope.All = CheckScope.All(plots)
}
