package com.example.myapplication.quality.data

import android.content.ContentResolver
import android.net.Uri
import android.provider.DocumentsContract
import com.example.myapplication.quality.domain.DataDirectoryRef
import com.example.myapplication.quality.domain.InvalidZdbSource
import com.example.myapplication.quality.domain.ZdbScanResult
import com.example.myapplication.quality.domain.ZdbSourceRef
import java.io.IOException
import java.util.ArrayDeque

class AndroidZdbSourceScanner(
    private val contentResolver: ContentResolver,
) : com.example.myapplication.quality.domain.ZdbSourceScanner {
    override fun scan(directory: DataDirectoryRef): ZdbScanResult {
        val rootUri = Uri.parse(directory.uri)
        val validSources = mutableListOf<ZdbSourceRef>()
        val invalidSources = mutableListOf<InvalidZdbSource>()
        val queue = ArrayDeque<DirectoryNode>()
        queue.add(DirectoryNode(DocumentsContract.getTreeDocumentId(rootUri), directory.displayName))

        while (queue.isNotEmpty()) {
            val current = queue.removeFirst()
            val childrenUri = DocumentsContract.buildChildDocumentsUriUsingTree(rootUri, current.documentId)
            try {
                contentResolver.query(childrenUri, PROJECTION, null, null, null)?.use { cursor ->
                    val idIndex = cursor.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_DOCUMENT_ID)
                    val nameIndex = cursor.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_DISPLAY_NAME)
                    val mimeIndex = cursor.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_MIME_TYPE)
                    val sizeIndex = cursor.getColumnIndex(DocumentsContract.Document.COLUMN_SIZE)
                    while (cursor.moveToNext()) {
                        val documentId = cursor.getString(idIndex)
                        val displayName = cursor.getString(nameIndex)
                        val mimeType = cursor.getString(mimeIndex)
                        if (mimeType == DocumentsContract.Document.MIME_TYPE_DIR) {
                            queue.add(DirectoryNode(documentId, displayName))
                        } else if (displayName.endsWith(".zdb", ignoreCase = true)) {
                            val fileUri = DocumentsContract.buildDocumentUriUsingTree(rootUri, documentId)
                            val size = if (sizeIndex >= 0 && !cursor.isNull(sizeIndex)) {
                                cursor.getLong(sizeIndex)
                            } else {
                                UNKNOWN_SIZE
                            }
                            indexCandidate(
                                fileUri = fileUri,
                                displayName = displayName,
                                projectName = current.displayName,
                                sizeBytes = size,
                                validSources = validSources,
                                invalidSources = invalidSources,
                            )
                        }
                    }
                } ?: invalidSources.add(
                    InvalidZdbSource(directory.uri, directory.displayName, "Directory cannot be read."),
                )
            } catch (exception: Exception) {
                invalidSources.add(
                    InvalidZdbSource(
                        uri = childrenUri.toString(),
                        displayName = current.displayName,
                        reason = "Directory scan failed: ${exception.message.orEmpty()}",
                    ),
                )
            }
        }

        return ZdbScanResult(directory, validSources, invalidSources)
    }

    private fun indexCandidate(
        fileUri: Uri,
        displayName: String,
        projectName: String,
        sizeBytes: Long,
        validSources: MutableList<ZdbSourceRef>,
        invalidSources: MutableList<InvalidZdbSource>,
    ) {
        if (sizeBytes == 0L) {
            invalidSources.add(InvalidZdbSource(fileUri.toString(), displayName, "File is empty."))
            return
        }

        val header = try {
            readHeader(fileUri)
        } catch (exception: Exception) {
            invalidSources.add(
                InvalidZdbSource(fileUri.toString(), displayName, "File cannot be read: ${exception.message.orEmpty()}"),
            )
            return
        }
        if (!header.contentEquals(SQLITE_HEADER)) {
            invalidSources.add(InvalidZdbSource(fileUri.toString(), displayName, "File is not a SQLite database."))
            return
        }

        validSources.add(
            ZdbSourceRef(
                uri = fileUri.toString(),
                projectName = projectName,
                fileName = displayName,
                sizeBytes = sizeBytes,
            ),
        )
    }

    @Throws(IOException::class)
    private fun readHeader(uri: Uri): ByteArray {
        val result = ByteArray(SQLITE_HEADER.size)
        contentResolver.openInputStream(uri)?.use { stream ->
            var offset = 0
            while (offset < result.size) {
                val read = stream.read(result, offset, result.size - offset)
                if (read < 0) break
                offset += read
            }
            if (offset != result.size) {
                return result.copyOf(offset)
            }
        } ?: throw IOException("No input stream returned.")
        return result
    }

    private data class DirectoryNode(
        val documentId: String,
        val displayName: String,
    )

    private companion object {
        const val UNKNOWN_SIZE = -1L
        val SQLITE_HEADER: ByteArray = "SQLite format 3\u0000".toByteArray(Charsets.US_ASCII)
        val PROJECTION = arrayOf(
            DocumentsContract.Document.COLUMN_DOCUMENT_ID,
            DocumentsContract.Document.COLUMN_DISPLAY_NAME,
            DocumentsContract.Document.COLUMN_MIME_TYPE,
            DocumentsContract.Document.COLUMN_SIZE,
        )
    }
}
