package com.example.myapplication.quality.data

import android.content.ContentResolver
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import com.example.myapplication.quality.domain.ZdbSourceRef

interface ReadOnlyZdbAccess {
    fun <T> read(source: ZdbSourceRef, block: (SQLiteDatabase) -> T): T
}

class SafReadOnlyZdbAccess(
    private val contentResolver: ContentResolver,
) : ReadOnlyZdbAccess {
    override fun <T> read(source: ZdbSourceRef, block: (SQLiteDatabase) -> T): T {
        val descriptor = requireNotNull(contentResolver.openFileDescriptor(Uri.parse(source.uri), "r")) {
            "Content provider did not supply a read-only file descriptor."
        }
        return descriptor.use { fileDescriptor ->
            val database = SQLiteDatabase.openDatabase(
                "/proc/self/fd/${fileDescriptor.fd}",
                null,
                SQLiteDatabase.OPEN_READONLY or SQLiteDatabase.NO_LOCALIZED_COLLATORS,
            )
            database.use {
                it.rawQuery("PRAGMA query_only = ON", null).use(Cursor::moveToFirst)
                block(it)
            }
        }
    }
}
