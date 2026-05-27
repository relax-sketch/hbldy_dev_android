package com.example.myapplication.quality.check

import com.example.myapplication.quality.domain.CheckScope
import com.example.myapplication.quality.domain.PlotCheckResult
import com.example.myapplication.quality.domain.PlotRef
import com.example.myapplication.quality.rules.RuleRepository
import com.example.myapplication.quality.rules.RuleSetSummary
import com.example.myapplication.quality.rules.TemporarilyDisabledRules

data class CheckProgress(
    val completedPlots: Int,
    val totalPlots: Int,
    val currentPlot: PlotRef?,
)

data class QualityCheckRun(
    val scope: CheckScope,
    val ruleSetSummary: RuleSetSummary,
    val plotResults: List<PlotCheckResult>,
    val cancelled: Boolean,
)

class QualityCheckEngine(
    private val ruleRepository: RuleRepository,
    private val plotRuleExecutor: PlotChecker,
) {
    fun check(
        scope: CheckScope,
        onProgress: (CheckProgress) -> Unit = {},
        isCancelled: () -> Boolean = { false },
    ): QualityCheckRun {
        val ruleSet = ruleRepository.loadRuleSet()
        val executableRules = TemporarilyDisabledRules.enabledRules(ruleSet.rules)
        val results = mutableListOf<PlotCheckResult>()
        val totalPlots = scope.plots.size
        onProgress(CheckProgress(0, totalPlots, scope.plots.firstOrNull()))

        var cancelled = false
        for (plot in scope.plots) {
            if (isCancelled()) {
                cancelled = true
                break
            }
            results.add(plotRuleExecutor.check(plot, executableRules))
            onProgress(CheckProgress(results.size, totalPlots, plot))
        }

        return QualityCheckRun(
            scope = scope,
            ruleSetSummary = ruleRepository.summary(),
            plotResults = results,
            cancelled = cancelled,
        )
    }
}
