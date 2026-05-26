package com.example.myapplication.quality.ui

import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.quality.annotations.SQLiteIssueAnnotationStore
import com.example.myapplication.quality.check.PlotRuleExecutor
import com.example.myapplication.quality.check.QualityCheckEngine
import com.example.myapplication.quality.data.AndroidDataDirectoryStore
import com.example.myapplication.quality.data.AndroidZdbSourceScanner
import com.example.myapplication.quality.data.SafPlotIndexRepository
import com.example.myapplication.quality.data.SafReadOnlyZdbAccess
import com.example.myapplication.quality.domain.CheckScope
import com.example.myapplication.quality.domain.DataDirectoryRef
import com.example.myapplication.quality.domain.DefaultCheckScopeSelector
import com.example.myapplication.quality.domain.PlotRef
import com.example.myapplication.quality.domain.ZdbScanResult
import com.example.myapplication.quality.review.IssueReviewService
import com.example.myapplication.quality.review.ReviewedCheckRun
import com.example.myapplication.quality.review.ReviewedPlotResult
import com.example.myapplication.quality.rules.AssetRuleRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class QualityCheckViewModel(application: Application) : AndroidViewModel(application) {
    private val directoryStore = AndroidDataDirectoryStore(application)
    private val sourceScanner = AndroidZdbSourceScanner(application.contentResolver)
    private val databaseAccess = SafReadOnlyZdbAccess(application.contentResolver)
    private val plotIndexRepository = SafPlotIndexRepository(databaseAccess)
    private val scopeSelector = DefaultCheckScopeSelector()
    private val engine = QualityCheckEngine(
        ruleRepository = AssetRuleRepository(application.assets),
        plotRuleExecutor = PlotRuleExecutor(databaseAccess),
    )
    private val reviewService = IssueReviewService(SQLiteIssueAnnotationStore(application))

    private val _uiState = MutableStateFlow(
        QualityCheckUiState(directory = directoryStore.savedDirectory()),
    )
    val uiState: StateFlow<QualityCheckUiState> = _uiState.asStateFlow()

    private var runJob: Job? = null
    @Volatile private var cancelRequested = false

    fun onDirectoryPicked(uri: Uri) {
        try {
            val directory = directoryStore.persistPickedDirectory(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION,
            )
            scan(directory)
        } catch (exception: Exception) {
            setError("无法读取选择的目录：${exception.message.orEmpty()}")
        }
    }

    fun scanSavedDirectory() {
        val directory = _uiState.value.directory ?: return
        scan(directory)
    }

    fun showSourceSelection() {
        _uiState.update { it.copy(screen = QualityScreen.SOURCE, errorMessage = null) }
    }

    fun showScopeSelection() {
        if (_uiState.value.indexedPlots.isNotEmpty()) {
            _uiState.update { it.copy(screen = QualityScreen.SCOPE, errorMessage = null) }
        }
    }

    fun updateCountyFilter(county: String?) {
        _uiState.update { state ->
            filter(state, selectedCounty = county, plotQuery = state.plotQuery)
        }
    }

    fun updatePlotQuery(query: String) {
        _uiState.update { state ->
            filter(state, selectedCounty = state.selectedCounty, plotQuery = query)
        }
    }

    fun selectPlot(plot: PlotRef) {
        _uiState.update {
            it.copy(
                selectedPlot = plot,
                selectedScope = scopeSelector.single(plot),
                errorMessage = null,
            )
        }
    }

    fun chooseSingleScope() {
        val selectedPlot = _uiState.value.selectedPlot
        if (selectedPlot == null) {
            setError("请先选择一个样地。")
        } else {
            _uiState.update { it.copy(selectedScope = scopeSelector.single(selectedPlot), errorMessage = null) }
        }
    }

    fun chooseCountyScope() {
        val selectedCounty = _uiState.value.selectedCounty
        if (selectedCounty.isNullOrBlank()) {
            setError("请选择一个区县后再进行区县批量质检。")
            return
        }
        val scope = scopeSelector.county(_uiState.value.indexedPlots, selectedCounty)
        _uiState.update { it.copy(selectedScope = scope, errorMessage = null) }
    }

    fun chooseAllScope() {
        _uiState.update {
            it.copy(selectedScope = scopeSelector.all(it.indexedPlots), errorMessage = null)
        }
    }

    fun startCheck() {
        val scope = _uiState.value.selectedScope
        if (scope == null || scope.plots.isEmpty()) {
            setError("当前质检范围没有样地。")
            return
        }

        cancelRequested = false
        runJob?.cancel()
        _uiState.update { it.copy(screen = QualityScreen.PROGRESS, progress = null, errorMessage = null) }
        runJob = viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                engine.check(
                    scope = scope,
                    onProgress = { progress -> _uiState.update { it.copy(progress = progress) } },
                    isCancelled = { cancelRequested },
                )
            }.onSuccess { run ->
                val reviewed = reviewService.review(run)
                _uiState.update {
                    it.copy(
                        screen = QualityScreen.SUMMARY,
                        reviewedRun = reviewed,
                        progress = null,
                    )
                }
            }.onFailure { exception ->
                _uiState.update {
                    it.copy(
                        screen = QualityScreen.SCOPE,
                        progress = null,
                        errorMessage = "质检未完成：${exception.message.orEmpty()}",
                    )
                }
            }
        }
    }

    fun cancelCheck() {
        cancelRequested = true
    }

    fun showPlotDetails(result: ReviewedPlotResult) {
        _uiState.update { it.copy(screen = QualityScreen.DETAIL, detailPlot = result) }
    }

    fun showSummary() {
        _uiState.update { it.copy(screen = QualityScreen.SUMMARY, detailPlot = null) }
    }

    fun recheck() {
        val previousScope = _uiState.value.reviewedRun?.sourceRun?.scope ?: _uiState.value.selectedScope
        if (previousScope != null) {
            _uiState.update { it.copy(selectedScope = previousScope) }
            startCheck()
        }
    }

    fun setIgnored(issueFingerprint: String, ignored: Boolean) {
        val current = _uiState.value.reviewedRun ?: return
        val issue = current.plotResults
            .flatMap { it.pendingMandatory + it.pendingAdvisory + it.ignored }
            .firstOrNull { it.fingerprint == issueFingerprint } ?: return
        if (ignored) {
            reviewService.ignore(issue)
        } else {
            reviewService.cancelIgnore(issue)
        }
        updateReview(reviewService.review(current.sourceRun))
    }

    private fun scan(directory: DataDirectoryRef) {
        _uiState.update {
            it.copy(
                directory = directory,
                isScanning = true,
                errorMessage = null,
                scanResult = null,
                indexedPlots = emptyList(),
                filteredPlots = emptyList(),
            )
        }
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                val scanResult = sourceScanner.scan(directory)
                scanResult to plotIndexRepository.index(scanResult.validSources)
            }.onSuccess { (scanResult, indexResult) ->
                val indexedPlots = indexResult.plots
                _uiState.update {
                    it.copy(
                        screen = if (indexedPlots.isEmpty()) QualityScreen.SOURCE else QualityScreen.SCOPE,
                        scanResult = scanResult,
                        indexedPlots = indexedPlots,
                        rejectedSources = scanResult.invalidSources + indexResult.rejectedSources,
                        countyOptions = scopeSelector.countyOptions(indexedPlots),
                        selectedCounty = null,
                        plotQuery = "",
                        filteredPlots = indexedPlots,
                        selectedPlot = null,
                        selectedScope = null,
                        isScanning = false,
                        errorMessage = if (indexedPlots.isEmpty()) "未发现可质检的样地数据。" else null,
                    )
                }
            }.onFailure { exception ->
                _uiState.update {
                    it.copy(
                        isScanning = false,
                        errorMessage = "扫描失败：${exception.message.orEmpty()}",
                    )
                }
            }
        }
    }

    private fun filter(
        state: QualityCheckUiState,
        selectedCounty: String?,
        plotQuery: String,
    ): QualityCheckUiState =
        state.copy(
            selectedCounty = selectedCounty,
            plotQuery = plotQuery,
            filteredPlots = scopeSelector.filterPlots(state.indexedPlots, selectedCounty, plotQuery),
            selectedPlot = null,
            selectedScope = null,
            errorMessage = null,
        )

    private fun updateReview(reviewed: ReviewedCheckRun) {
        _uiState.update { state ->
            val details = state.detailPlot?.plot?.let { displayedPlot ->
                reviewed.plotResults.firstOrNull { it.plot == displayedPlot }
            }
            state.copy(reviewedRun = reviewed, detailPlot = details)
        }
    }

    private fun setError(message: String) {
        _uiState.update { it.copy(errorMessage = message) }
    }
}
