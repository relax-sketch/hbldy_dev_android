package com.example.myapplication.quality.data

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.DocumentsContract
import com.example.myapplication.quality.domain.DataDirectoryRef
import com.example.myapplication.quality.domain.DataDirectoryStore

class AndroidDataDirectoryStore(private val context: Context) : DataDirectoryStore {
    private val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    override fun savedDirectory(): DataDirectoryRef? {
        val uri = preferences.getString(KEY_URI, null) ?: return null
        val displayName = preferences.getString(KEY_DISPLAY_NAME, null) ?: uri
        return DataDirectoryRef(uri = uri, displayName = displayName)
    }

    override fun saveDirectory(directory: DataDirectoryRef) {
        preferences.edit()
            .putString(KEY_URI, directory.uri)
            .putString(KEY_DISPLAY_NAME, directory.displayName)
            .apply()
    }

    fun savedCountyLabel(): String? = preferences.getString(KEY_SELECTED_COUNTY, null)

    fun saveCountyLabel(countyLabel: String?) {
        preferences.edit().apply {
            if (countyLabel.isNullOrBlank()) {
                remove(KEY_SELECTED_COUNTY)
            } else {
                putString(KEY_SELECTED_COUNTY, countyLabel)
            }
        }.apply()
    }

    fun persistPickedDirectory(uri: Uri, resultFlags: Int): DataDirectoryRef {
        val readFlag = resultFlags and Intent.FLAG_GRANT_READ_URI_PERMISSION
        context.contentResolver.takePersistableUriPermission(uri, readFlag)
        return DataDirectoryRef(uri.toString(), resolveDisplayName(uri)).also(::saveDirectory)
    }

    private fun resolveDisplayName(treeUri: Uri): String {
        val documentUri = DocumentsContract.buildDocumentUriUsingTree(
            treeUri,
            DocumentsContract.getTreeDocumentId(treeUri),
        )
        return context.contentResolver.query(
            documentUri,
            arrayOf(DocumentsContract.Document.COLUMN_DISPLAY_NAME),
            null,
            null,
            null,
        )?.use { cursor ->
            if (cursor.moveToFirst()) cursor.getString(0) else null
        } ?: treeUri.lastPathSegment.orEmpty()
    }

    private companion object {
        const val PREFERENCES_NAME = "quality_data_directory"
        const val KEY_URI = "uri"
        const val KEY_DISPLAY_NAME = "display_name"
        const val KEY_SELECTED_COUNTY = "selected_county"
    }
}
