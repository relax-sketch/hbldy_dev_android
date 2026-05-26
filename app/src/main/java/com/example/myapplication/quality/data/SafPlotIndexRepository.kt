package com.example.myapplication.quality.data

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.example.myapplication.quality.domain.InvalidZdbSource
import com.example.myapplication.quality.domain.PlotIndexRepository
import com.example.myapplication.quality.domain.PlotIndexResult
import com.example.myapplication.quality.domain.PlotRef
import com.example.myapplication.quality.domain.PlotTable
import com.example.myapplication.quality.domain.ZdbSourceRef

class SafPlotIndexRepository(
    private val databaseAccess: ReadOnlyZdbAccess,
) : PlotIndexRepository {
    override fun index(sources: List<ZdbSourceRef>): PlotIndexResult {
        val indexedPlots = mutableListOf<PlotRef>()
        val rejectedSources = mutableListOf<InvalidZdbSource>()

        sources.forEach { source ->
            try {
                databaseAccess.read(source) { database ->
                    val supportedTables = PlotTable.entries.filter { table ->
                        database.hasTable(table.tableName)
                    }
                    if (supportedTables.isEmpty()) {
                        rejectedSources.add(
                            InvalidZdbSource(source.uri, source.fileName, "No supported plot table was found."),
                        )
                    } else {
                        supportedTables.forEach { table ->
                            indexedPlots.addAll(database.readPlots(source, table))
                        }
                    }
                }
            } catch (exception: Exception) {
                rejectedSources.add(
                    InvalidZdbSource(
                        uri = source.uri,
                        displayName = source.fileName,
                        reason = "Database cannot be indexed: ${exception.message.orEmpty()}",
                    ),
                )
            }
        }

        return PlotIndexResult(
            plots = assignDisplayIdentifiers(indexedPlots),
            rejectedSources = rejectedSources,
        )
    }

    private fun SQLiteDatabase.hasTable(tableName: String): Boolean =
        rawQuery(
            "SELECT 1 FROM sqlite_master WHERE type = 'table' AND name = ? LIMIT 1",
            arrayOf(tableName),
        ).use { cursor -> cursor.moveToFirst() }

    private fun SQLiteDatabase.readPlots(source: ZdbSourceRef, plotTable: PlotTable): List<PlotRef> {
        val fields = columns(plotTable.tableName)
        require("YD_ID" in fields) { "${plotTable.tableName} does not contain YD_ID." }
        val countyColumn = if ("XIAN" in fields) "XIAN" else "NULL"
        val guidColumn = if ("MZGUID" in fields) "MZGUID" else "NULL"
        val query = """
            SELECT YD_ID, $countyColumn AS countyCode, $guidColumn AS parentGuid
            FROM "${plotTable.tableName}"
            WHERE YD_ID IS NOT NULL AND TRIM(YD_ID) <> ''
        """.trimIndent()

        return rawQuery(query, null).use { cursor ->
            buildList {
                while (cursor.moveToNext()) {
                    val rawPlotId = cursor.getString(0)
                    val countyCode = cursor.getStringOrNull(1)
                    add(
                        PlotRef(
                            source = source,
                            rawPlotId = rawPlotId,
                            displayPlotId = rawPlotId,
                            countyCode = countyCode,
                            countyLabel = countyCode,
                            plotTable = plotTable,
                            parentGuid = cursor.getStringOrNull(2),
                        ),
                    )
                }
            }
        }
    }

    private fun SQLiteDatabase.columns(tableName: String): Set<String> =
        rawQuery("PRAGMA table_info(\"$tableName\")", null).use { cursor ->
            val nameIndex = cursor.getColumnIndexOrThrow("name")
            buildSet {
                while (cursor.moveToNext()) {
                    add(cursor.getString(nameIndex).uppercase())
                }
            }
        }

    private fun assignDisplayIdentifiers(plots: List<PlotRef>): List<PlotRef> {
        val seen = mutableMapOf<String, Int>()
        return plots.map { plot ->
            val occurrence = (seen[plot.rawPlotId] ?: 0) + 1
            seen[plot.rawPlotId] = occurrence
            val suffix = if (occurrence == 1) "" else "-$occurrence"
            plot.copy(displayPlotId = "${plot.rawPlotId}$suffix")
        }
    }

    private fun Cursor.getStringOrNull(columnIndex: Int): String? =
        if (isNull(columnIndex)) null else getString(columnIndex)
}
