package com.example.myapplication.quality.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class DefaultCheckScopeSelectorTest {
    private val selector = DefaultCheckScopeSelector()

    @Test
    fun cleanProjectCountyLabel_removesMonitoringPrefixAndTimestamp() {
        assertEquals("新洲区", cleanProjectCountyLabel("综合监测-新洲区-20260527090338"))
        assertEquals("江岸区", cleanProjectCountyLabel("综合监测-江岸区-20260526102825"))
        assertEquals("自定义目录", cleanProjectCountyLabel("自定义目录"))
    }

    @Test
    fun countyOptions_useCleanedCountyLabels() {
        val plots = listOf(
            plot("420117006", "综合监测-新洲区-20260527090338"),
            plot("420102001", "综合监测-江岸区-20260526102825"),
            plot("420117007", "综合监测-新洲区-20260527090338"),
        )

        assertEquals(setOf("新洲区", "江岸区"), selector.countyOptions(plots).toSet())
    }

    @Test
    fun filterPlots_combinesCountyAndPlotIdQuery() {
        val plots = listOf(
            plot("420117006", "综合监测-新洲区-20260527090338"),
            plot("420117007", "综合监测-新洲区-20260527090338"),
            plot("420102001", "综合监测-江岸区-20260526102825"),
        )

        assertEquals(
            listOf("420117006"),
            selector.filterPlots(plots, "新洲区", "006").map(PlotRef::displayPlotId),
        )
    }

    @Test
    fun restoreCounty_returnsSavedCountyOnlyWhenAvailable() {
        val options = listOf("新洲区", "江岸区")

        assertEquals("新洲区", selector.restoreCounty("新洲区", options))
        assertNull(selector.restoreCounty("洪山区", options))
        assertNull(selector.restoreCounty(null, options))
    }

    private fun plot(plotId: String, projectName: String): PlotRef {
        val source = ZdbSourceRef(
            uri = "content://fixture/$plotId",
            projectName = projectName,
            fileName = "$plotId.zdb",
            sizeBytes = 1,
        )
        return PlotRef(
            source = source,
            rawPlotId = plotId,
            displayPlotId = plotId,
            countyCode = null,
            countyLabel = source.countyDisplayLabel(),
            plotTable = PlotTable.NATURAL_GRASSLAND,
            parentGuid = null,
        )
    }
}
