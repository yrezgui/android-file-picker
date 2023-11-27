package com.yrezgui.filepicker.supportlibrary

fun PickVisualMediaRequest(
    mediaType: PickVisualMedia.VisualMediaType = PickVisualMedia.ImageAndVideo
) = PickVisualMediaRequest.Builder().setMediaType(mediaType).build()

/**
 * A request for a
 * [androidx.activity.result.contract.ActivityResultContracts.PickMultipleVisualMedia] or
 * [androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia] Activity Contract.
 */
class PickVisualMediaRequest internal constructor() {

    var mediaType: PickVisualMedia.VisualMediaType = PickVisualMedia.ImageAndVideo
        internal set

    /**
     * A builder for constructing [PickVisualMediaRequest] instances.
     */
    class Builder {

        private var mediaType: PickVisualMedia.VisualMediaType = PickVisualMedia.ImageAndVideo

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

        /**
         * Build the PickVisualMediaRequest specified by this builder.
         *
         * @return the newly constructed PickVisualMediaRequest.
         */
        fun build(): PickVisualMediaRequest = PickVisualMediaRequest().apply {
            this.mediaType = this@Builder.mediaType
        }
    }
}
