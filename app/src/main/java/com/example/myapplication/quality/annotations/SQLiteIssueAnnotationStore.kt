package com.example.myapplication.quality.annotations

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class SQLiteIssueAnnotationStore(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION),
    IssueAnnotationStore {
    override fun onCreate(database: SQLiteDatabase) {
        database.execSQL(
            """
            CREATE TABLE ignored_issue (
                fingerprint TEXT PRIMARY KEY NOT NULL,
                ignored_at_epoch_millis INTEGER NOT NULL
            )
            """.trimIndent(),
        )
    }

    override fun onUpgrade(database: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // The initial schema has no migrations.
    }

    override fun ignoredFingerprints(fingerprints: Set<String>): Set<String> {
        if (fingerprints.isEmpty()) return emptySet()
        val placeholders = fingerprints.joinToString(",") { "?" }
        return readableDatabase.query(
            "ignored_issue",
            arrayOf("fingerprint"),
            "fingerprint IN ($placeholders)",
            fingerprints.toTypedArray(),
            null,
            null,
            null,
        ).use { cursor ->
            buildSet {
                while (cursor.moveToNext()) {
                    add(cursor.getString(0))
                }
            }
        }
    }

    override fun markIgnored(fingerprint: String, ignoredAtEpochMillis: Long) {
        writableDatabase.insertWithOnConflict(
            "ignored_issue",
            null,
            ContentValues().apply {
                put("fingerprint", fingerprint)
                put("ignored_at_epoch_millis", ignoredAtEpochMillis)
            },
            SQLiteDatabase.CONFLICT_REPLACE,
        )
    }

    override fun removeIgnored(fingerprint: String) {
        writableDatabase.delete("ignored_issue", "fingerprint = ?", arrayOf(fingerprint))
    }

    private companion object {
        const val DATABASE_NAME = "quality_annotations.db"
        const val DATABASE_VERSION = 1
    }
}
