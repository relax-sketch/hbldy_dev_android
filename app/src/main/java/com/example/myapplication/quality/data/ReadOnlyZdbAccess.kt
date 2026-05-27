package com.example.myapplication.quality.data

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import com.example.myapplication.quality.domain.ZdbSourceRef
import java.io.File

interface ReadOnlyZdbAccess {
    fun <T> read(source: ZdbSourceRef, block: (SQLiteDatabase) -> T): T
}

class SafReadOnlyZdbAccess(
    private val context: Context,
) : ReadOnlyZdbAccess {
    override fun <T> read(source: ZdbSourceRef, block: (SQLiteDatabase) -> T): T {
        val tempFile = copyToPrivateTempFile(source)
        try {
            val database = SQLiteDatabase.openDatabase(
                tempFile.absolutePath,
                null,
                SQLiteDatabase.OPEN_READONLY or SQLiteDatabase.NO_LOCALIZED_COLLATORS,
            )
            return database.use {
                it.rawQuery("PRAGMA query_only = ON", null).use(Cursor::moveToFirst)
                block(it)
            }
        } finally {
            tempFile.delete()
        }
    }

    private fun copyToPrivateTempFile(source: ZdbSourceRef): File {
        val directory = File(context.cacheDir, CACHE_DIRECTORY).apply { mkdirs() }
        val tempFile = File.createTempFile("zdb-readonly-", ".zdb", directory)
        try {
            context.contentResolver.openInputStream(Uri.parse(source.uri)).use { input ->
                requireNotNull(input) { "Content provider did not supply a read stream." }
                tempFile.outputStream().use { output -> input.copyTo(output) }
            }
        } catch (exception: Exception) {
            tempFile.delete()
            throw exception
        }
        return tempFile
    }

    private companion object {
        const val CACHE_DIRECTORY = "zdb-readonly"
    }
}
