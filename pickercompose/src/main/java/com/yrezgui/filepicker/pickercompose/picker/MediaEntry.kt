package com.yrezgui.filepicker.pickercompose.picker

import android.net.Uri
import android.os.Parcelable
import com.bumptech.glide.signature.MediaStoreSignature
import kotlinx.parcelize.Parcelize

sealed class MediaEntry(
    open val uri: Uri,
    open val mimeType: String,
    open val filename: String,
    open val size: Long,
    open val dateAdded: Int,
    open val dateModified: Int,
    open val dateTaken: Int,
    open val orientation: Int
) {
    fun signature() = MediaStoreSignature(mimeType, dateModified.toLong(), orientation)
}

@Parcelize
data class ImageEntry(
    override val uri: Uri,
    override val mimeType: String,
    override val filename: String,
    override val size: Long,
    override val dateAdded: Int,
    override val dateModified: Int,
    override val dateTaken: Int,
    override val orientation: Int
) : MediaEntry(
    uri,
    mimeType,
    filename,
    size,
    dateAdded,
    dateModified,
    dateTaken,
    orientation
), Parcelable

@Parcelize
data class VideoEntry(
    override val uri: Uri,
    override val mimeType: String,
    override val filename: String,
    override val size: Long,
    override val dateAdded: Int,
    override val dateModified: Int,
    override val dateTaken: Int,
    override val orientation: Int,
    val durationInMs: Int
) : MediaEntry(
    uri,
    mimeType,
    filename,
    size,
    dateAdded,
    dateModified,
    dateTaken,
    orientation
), Parcelable