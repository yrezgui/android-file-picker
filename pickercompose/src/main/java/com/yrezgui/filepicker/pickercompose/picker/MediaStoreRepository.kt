package com.yrezgui.filepicker.pickercompose.picker

import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.MediaStore.Files.FileColumns.DATE_ADDED
import android.provider.MediaStore.Files.FileColumns.DATE_MODIFIED
import android.provider.MediaStore.Files.FileColumns.DATE_TAKEN
import android.provider.MediaStore.Files.FileColumns.DISPLAY_NAME
import android.provider.MediaStore.Files.FileColumns.DURATION
import android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE
import android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
import android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO
import android.provider.MediaStore.Files.FileColumns.MIME_TYPE
import android.provider.MediaStore.Files.FileColumns.ORIENTATION
import android.provider.MediaStore.Files.FileColumns.SIZE
import android.provider.MediaStore.Files.FileColumns._ID
import android.util.Log
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class MediaStoreRepository(private val context: Context) {
    fun loadDataSource(): Flow<ImmutableList<MediaEntry>> = flow {
//        val contentObserver = object : ContentObserver(Handler(Looper.getMainLooper())) {
//            override fun onChange(selfChange: Boolean) {
//                super.onChange(selfChange)
//                launch { trySend(query()) }
//            }
//        }
//
//        context.contentResolver.registerContentObserver(
//            MEDIA_STORE_FILE_URI,
//            /* notifyForDescendants=*/ true,
//            contentObserver
//        )

        emit(query())

//        awaitClose { context.contentResolver.unregisterContentObserver(contentObserver) }
    }

    private suspend fun query(): ImmutableList<MediaEntry> = withContext(Dispatchers.IO) {
        val sortOrder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            "${MediaStore.MediaColumns.GENERATION_ADDED} DESC"
        } else {
            "${MediaStore.MediaColumns.DATE_ADDED} DESC"
        }

        val cursor = context.contentResolver.query(
            MEDIA_STORE_FILE_URI,
            PROJECTION,
            "$MEDIA_TYPE = ? OR $MEDIA_TYPE = ?",
            arrayOf(MEDIA_TYPE_IMAGE.toString(), MEDIA_TYPE_VIDEO.toString()),
            sortOrder
        ) ?: return@withContext persistentListOf()

        val data = mutableListOf<MediaEntry>()

        cursor.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(_ID)
            val mimeTypeColumn = cursor.getColumnIndexOrThrow(MIME_TYPE)
            val displayNameColumn = cursor.getColumnIndexOrThrow(DISPLAY_NAME)
            val sizeColumn = cursor.getColumnIndexOrThrow(SIZE)
            val dateAddedColumn = cursor.getColumnIndexOrThrow(DATE_ADDED)
            val dateModifiedColumn = cursor.getColumnIndexOrThrow(DATE_MODIFIED)
            val dateTakenColumn = cursor.getColumnIndexOrThrow(DATE_TAKEN)
            val orientationColumn = cursor.getColumnIndexOrThrow(ORIENTATION)
            val durationColumn = cursor.getColumnIndexOrThrow(DURATION)
            val mediaTypeColumnIndex = cursor.getColumnIndexOrThrow(MEDIA_TYPE)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val mimeType = cursor.getString(mimeTypeColumn)
                val filename = cursor.getString(displayNameColumn)
                val size = cursor.getLong(sizeColumn)
                val dateAdded = cursor.getInt(dateAddedColumn)
                val dateModified = cursor.getInt(dateModifiedColumn)
                val dateTaken = cursor.getInt(dateTakenColumn)
                val orientation = cursor.getInt(orientationColumn)

                val entry = if (cursor.getInt(mediaTypeColumnIndex) == MEDIA_TYPE_IMAGE) {
                    ImageEntry(
                        uri = Uri.withAppendedPath(MEDIA_STORE_FILE_URI, id.toString()),
                        mimeType = mimeType,
                        filename = filename,
                        size = size,
                        dateAdded = dateAdded,
                        dateModified = dateModified,
                        dateTaken = dateTaken,
                        orientation = orientation
                    )
                } else {
                    val duration = cursor.getInt(durationColumn)

                    VideoEntry(
                        uri = Uri.withAppendedPath(MEDIA_STORE_FILE_URI, id.toString()),
                        mimeType = mimeType,
                        filename = filename,
                        size = size,
                        dateAdded = dateAdded,
                        dateModified = dateModified,
                        dateTaken = dateTaken,
                        orientation = orientation,
                        durationInMs = duration
                    )
                }

                data.add(entry)
            }
        }
        Log.d("MediaStoreRepository", "${data.size} elements queried")
        return@withContext data.toImmutableList()
    }

    companion object {
        private val MEDIA_STORE_FILE_URI = MediaStore.Files.getContentUri("external")
        private val PROJECTION = arrayOf(
            _ID,
            MIME_TYPE,
            DISPLAY_NAME,
            SIZE,
            DATE_ADDED,
            DATE_MODIFIED,
            DATE_TAKEN,
            ORIENTATION,
            DURATION,
            MEDIA_TYPE
        )
    }
}