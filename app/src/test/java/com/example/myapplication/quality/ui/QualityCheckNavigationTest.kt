package com.example.myapplication.quality.ui

import com.example.myapplication.quality.domain.CheckScope
import com.example.myapplication.quality.domain.PlotRef
import com.example.myapplication.quality.domain.PlotTable
import com.example.myapplication.quality.domain.ZdbSourceRef
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class QualityCheckNavigationTest {
    private val plot = PlotRef(
        source = ZdbSourceRef("content://fixture", "综合监测-新洲区-20260527090338", "fixture.zdb", 1),
        rawPlotId = "420117006",
        displayPlotId = "420117006",
        countyCode = "420117",
        countyLabel = "新洲区",
        plotTable = PlotTable.NATURAL_GRASSLAND,
        parentGuid = null,
    )

    @Test
    fun screenAfterCompletedCheck_routesSingleToDetailAndFullToSummary() {
        assertEquals(QualityScreen.DETAIL, screenAfterCompletedCheck(CheckScope.Single(plot)))
        assertEquals(QualityScreen.SUMMARY, screenAfterCompletedCheck(CheckScope.All(listOf(plot))))
    }

    @Test
    fun screenAfterDetailBack_routesByRunScope() {
        assertEquals(QualityScreen.SCOPE, screenAfterDetailBack(CheckScope.Single(plot)))
        assertEquals(QualityScreen.SUMMARY, screenAfterDetailBack(CheckScope.All(listOf(plot))))
        assertEquals(QualityScreen.SUMMARY, screenAfterDetailBack(null))
    }

    @Test
    fun withCheckAllMode_temporarilyClearsFiltersAndRestoresCountyWhenDisabled() {
        val otherPlot = plot.copy(
            source = ZdbSourceRef("content://other", "综合监测-江岸区-20260526102825", "other.zdb", 1),
            rawPlotId = "420102001",
            displayPlotId = "420102001",
            countyCode = "420102",
            countyLabel = "江岸区",
        )
        val state = QualityCheckUiState(
            indexedPlots = listOf(plot, otherPlot),
            countyOptions = listOf("新洲区", "江岸区"),
            selectedCounty = "新洲区",
            plotQuery = "006",
            filteredPlots = listOf(plot),
            selectedPlot = plot,
            selectedScope = CheckScope.Single(plot),
            errorMessage = "previous",
        )

        val full = state.withCheckAllMode(enabled = true)

        assertTrue(full.checkAllMode)
        assertEquals("", full.plotQuery)
        assertEquals(listOf(plot, otherPlot), full.filteredPlots)
        assertNull(full.selectedPlot)
        assertNull(full.selectedScope)
        assertEquals("新洲区", full.selectedCounty)

        val normal = full.withCheckAllMode(enabled = false, restoredCounty = "新洲区")

        assertFalse(normal.checkAllMode)
        assertEquals("新洲区", normal.selectedCounty)
        assertEquals(listOf(plot), normal.filteredPlots)
        assertEquals("", normal.plotQuery)
    }
}
