package com.example.myapplication.quality.ui

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.quality.ui.screens.QualityDetailScreen
import com.example.myapplication.quality.ui.screens.QualityProgressScreen
import com.example.myapplication.quality.ui.screens.QualityScopeScreen
import com.example.myapplication.quality.ui.screens.QualitySourceScreen
import com.example.myapplication.quality.ui.screens.QualitySummaryScreen

@Composable
fun QualityCheckApp(viewModel: QualityCheckViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsState()
    BackHandler(enabled = state.screen != QualityScreen.SOURCE) {
        viewModel.navigateBack()
    }

    when (state.screen) {
        QualityScreen.SOURCE -> QualitySourceScreen(state = state, viewModel = viewModel)
        QualityScreen.SCOPE -> QualityScopeScreen(state = state, viewModel = viewModel)
        QualityScreen.PROGRESS -> QualityProgressScreen(state = state, viewModel = viewModel)
        QualityScreen.SUMMARY -> QualitySummaryScreen(state = state, viewModel = viewModel)
        QualityScreen.DETAIL -> QualityDetailScreen(state = state, viewModel = viewModel)
    }
}
