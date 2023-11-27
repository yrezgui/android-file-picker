package com.yrezgui.filepicker.supportlibrary

import android.net.Uri

/**
 * Creates a request for a
 * [androidx.activity.result.contract.ActivityResultContracts.PickMultipleVisualMedia] or
 * [androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia] Activity Contract.
 *
 * @param mediaType type to go into the PickVisualMediaRequest
 * @param selectionMode request selection mode when available
 * @param presentationMode request presentation mode when available
 * @param height request setting picker's height when possible
 * @param showToolbar show toolbar when available
 * @param cameraSupport request camera support when available
 * @param preSelectedUris list of pre-selected [Uri]
 * @return a [PickVisualMediaRequest] that contains the given input
 */
fun PickVisualMediaRequest(
    mediaType: PickVisualMedia.VisualMediaType = PickVisualMedia.ImageAndVideo,
    selectionMode: PickVisualMedia.SelectionMode = PickVisualMedia.Unordered,
    presentationMode: PickVisualMedia.PresentationMode = PickVisualMedia.ModalMode,
    height: Int = PickVisualMedia.FULL_SCREEN_HEIGHT,
    showToolbar: Boolean = true,
    cameraSupport: Boolean = true,
    preSelectedUris: List<Uri> = emptyList()
) = PickVisualMediaRequest.Builder()
    .setMediaType(mediaType)
    .setSelectionMode(selectionMode)
    .setPresentationMode(presentationMode)
    .setHeight(height)
    .showToolbar(showToolbar)
    .setCameraSupport(cameraSupport)
    .setPreSelectedUris(preSelectedUris)
    .build()

/**
 * A request for a
 * [androidx.activity.result.contract.ActivityResultContracts.PickMultipleVisualMedia] or
 * [androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia] Activity Contract.
 */
class PickVisualMediaRequest internal constructor() {

    var mediaType: PickVisualMedia.VisualMediaType = PickVisualMedia.ImageAndVideo
        internal set
    var selectionMode: PickVisualMedia.SelectionMode = PickVisualMedia.Unordered
        internal set
    var presentationMode: PickVisualMedia.PresentationMode = PickVisualMedia.InlineMode
        internal set
    var height: Int = PickVisualMedia.FULL_SCREEN_HEIGHT
        internal set
    var showToolbar: Boolean = true
        internal set
    var cameraSupport: Boolean = true
        internal set
    var preSelectedUris: List<Uri> = emptyList()
        internal set

    /**
     * A builder for constructing [PickVisualMediaRequest] instances.
     */
    class Builder {

        private var mediaType: PickVisualMedia.VisualMediaType = PickVisualMedia.ImageAndVideo
        private var selectionMode: PickVisualMedia.SelectionMode = PickVisualMedia.Unordered
        private var presentationMode: PickVisualMedia.PresentationMode = PickVisualMedia.InlineMode
        private var height: Int = PickVisualMedia.FULL_SCREEN_HEIGHT
        private var showToolbar: Boolean = true
        private var cameraSupport: Boolean = true
        private var preSelectedUris: List<Uri> = emptyList()

        /**
         * Set the media type for the [PickVisualMediaRequest].
         *
         * The type is the mime type to filter by, e.g. `PickVisualMedia.ImageOnly`,
         * `PickVisualMedia.ImageAndVideo`, `PickVisualMedia.SingleMimeType("image/gif")`
         *
         * @param mediaType type to go into the PickVisualMediaRequest
         * @return This builder.
         */
        fun setMediaType(mediaType: PickVisualMedia.VisualMediaType): Builder {
            this.mediaType = mediaType
            return this
        }

        fun setSelectionMode(selectionMode: PickVisualMedia.SelectionMode): Builder {
            this.selectionMode = selectionMode
            return this
        }

        fun setPresentationMode(presentationMode: PickVisualMedia.PresentationMode): Builder {
            this.presentationMode = presentationMode
            return this
        }

        fun setHeight(height: Int): Builder {
            this.height = height
            return this
        }

        fun showToolbar(showToolbar: Boolean): Builder {
            this.showToolbar = showToolbar
            return this
        }

        fun setCameraSupport(cameraSupport: Boolean): Builder {
            this.cameraSupport = cameraSupport
            return this
        }

        fun setPreSelectedUris(preSelectedUris: List<Uri>): Builder {
            this.preSelectedUris = preSelectedUris
            return this
        }

        /**
         * Build the PickVisualMediaRequest specified by this builder.
         *
         * @return the newly constructed PickVisualMediaRequest.
         */
        fun build(): PickVisualMediaRequest = PickVisualMediaRequest().apply {
            this.mediaType = this@Builder.mediaType
            this.selectionMode = this@Builder.selectionMode
            this.presentationMode = this@Builder.presentationMode
            this.height = this@Builder.height
            this.showToolbar = this@Builder.showToolbar
            this.cameraSupport = this@Builder.cameraSupport
            this.preSelectedUris = this@Builder.preSelectedUris
        }
    }
}
