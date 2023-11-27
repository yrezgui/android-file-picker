package com.yrezgui.filepicker.supportlibrary

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Build
import android.os.ext.SdkExtensions
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContract
import androidx.annotation.CallSuper
import androidx.annotation.RequiresApi
import com.yrezgui.filepicker.supportlibrary.PickMultipleVisualMedia.Companion.getClipDataUris
import com.yrezgui.filepicker.supportlibrary.PickVisualMedia.Companion.ACTION_SYSTEM_FALLBACK_PICK_IMAGES
import com.yrezgui.filepicker.supportlibrary.PickVisualMedia.Companion.GMS_ACTION_PICK_IMAGES
import com.yrezgui.filepicker.supportlibrary.PickVisualMedia.Companion.GMS_EXTRA_PICK_IMAGES_MAX
import com.yrezgui.filepicker.supportlibrary.PickVisualMedia.Companion.getGmsPicker
import com.yrezgui.filepicker.supportlibrary.PickVisualMedia.Companion.getSystemFallbackPicker

@RequiresApi(19)
open class PickVisualMedia : ActivityResultContract<PickVisualMediaRequest, Uri?>() {
    companion object {
        /**
         * Check if the current device has support for the photo picker by checking the running
         * Android version or the SDK extension version.
         *
         * Note that this does not check for any Intent handled by
         * [ACTION_SYSTEM_FALLBACK_PICK_IMAGES].
         */
        @SuppressLint("ClassVerificationFailure", "NewApi")
        @Deprecated(
            message = "This method is deprecated in favor of isPhotoPickerAvailable(context) " +
                    "to support the picker provided by updatable system apps",
            replaceWith = ReplaceWith("isPhotoPickerAvailable(context)")
        )
        @JvmStatic
        fun isPhotoPickerAvailable(): Boolean {
            return isSystemPickerAvailable()
        }

        /**
         * In cases where the system framework provided [MediaStore.ACTION_PICK_IMAGES]
         * Photo Picker cannot be implemented, OEMs or system apps can provide a consistent
         * Photo Picker experience to those devices by creating an Activity that handles
         * this action. This app must also include [Intent.CATEGORY_DEFAULT] in the activity's
         * intent filter.
         *
         * Only system apps can implement this action - any non-system apps will be ignored
         * when searching for the activities that handle this Intent.
         *
         * Note: this should not be used directly, instead relying on the selection logic
         * done by [createIntent] to create the correct Intent for the current device.
         */
        @Suppress("ActionValue") /* Don't include SYSTEM_FALLBACK in the action */
        const val ACTION_SYSTEM_FALLBACK_PICK_IMAGES =
            "androidx.activity.result.contract.action.PICK_IMAGES"

        /**
         * Extra that will be sent by [PickMultipleVisualMedia] to an Activity that handles
         * [ACTION_SYSTEM_FALLBACK_PICK_IMAGES] that indicates that maximum number of photos
         * the user should select.
         *
         * If this extra is not present, only a single photo should be selectable.
         *
         * If this extra is present but equal to [Int.MAX_VALUE], then no limit should
         * be enforced.
         */
        @Suppress("ActionValue") /* Don't include SYSTEM_FALLBACK in the extra */
        const val EXTRA_SYSTEM_FALLBACK_PICK_IMAGES_MAX =
            "androidx.activity.result.contract.extra.PICK_IMAGES_MAX"

        internal const val GMS_ACTION_PICK_IMAGES =
            "com.google.android.gms.provider.action.PICK_IMAGES"
        internal const val GMS_EXTRA_PICK_IMAGES_MAX =
            "com.google.android.gms.provider.extra.PICK_IMAGES_MAX"

        /**
         * Check if the current device has support for the photo picker by checking the running
         * Android version, the SDK extension version or the picker provided by
         * a system app implementing [ACTION_SYSTEM_FALLBACK_PICK_IMAGES].
         */
        @SuppressLint("ClassVerificationFailure", "NewApi")
        @JvmStatic
        fun isPhotoPickerAvailable(context: Context): Boolean {
            return isSystemPickerAvailable() || isSystemFallbackPickerAvailable(context) ||
                    isGmsPickerAvailable(context)
        }

        /**
         * Check if the current device has support for the system framework provided photo
         * picker by checking the running Android version or the SDK extension version.
         *
         * Note that this does not check for any Intent handled by
         * [ACTION_SYSTEM_FALLBACK_PICK_IMAGES].
         */
        @SuppressLint("ClassVerificationFailure", "NewApi")
        @JvmStatic
        internal fun isSystemPickerAvailable(): Boolean {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                true
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                // getExtension is seen as part of Android Tiramisu only while the SdkExtensions
                // have been added on Android R
                SdkExtensions.getExtensionVersion(Build.VERSION_CODES.R) >= 2
            } else {
                false
            }
        }

        @JvmStatic
        internal fun isSystemFallbackPickerAvailable(context: Context): Boolean {
            return getSystemFallbackPicker(context) != null
        }

        @Suppress("DEPRECATION")
        @JvmStatic
        internal fun getSystemFallbackPicker(context: Context): ResolveInfo? {
            return context.packageManager.resolveActivity(
                Intent(ACTION_SYSTEM_FALLBACK_PICK_IMAGES),
                PackageManager.MATCH_DEFAULT_ONLY or PackageManager.MATCH_SYSTEM_ONLY
            )
        }

        @JvmStatic
        internal fun isGmsPickerAvailable(context: Context): Boolean {
            return getGmsPicker(context) != null
        }

        @Suppress("DEPRECATION")
        @JvmStatic
        internal fun getGmsPicker(context: Context): ResolveInfo? {
            return context.packageManager.resolveActivity(
                Intent(GMS_ACTION_PICK_IMAGES),
                PackageManager.MATCH_DEFAULT_ONLY or PackageManager.MATCH_SYSTEM_ONLY
            )
        }

        internal fun getVisualMimeType(input: VisualMediaType): String? {
            return when (input) {
                is ImageOnly -> "image/*"
                is VideoOnly -> "video/*"
                is SingleMimeType -> input.mimeType
                is ImageAndVideo -> null
            }
        }
    }

    /**
     * Represents filter input type accepted by the photo picker.
     */
    sealed interface VisualMediaType

    /**
     * [VisualMediaType] object used to filter images only when using the photo picker.
     */
    object ImageOnly : VisualMediaType

    /**
     * [VisualMediaType] object used to filter video only when using the photo picker.
     */
    object VideoOnly : VisualMediaType

    /**
     * [VisualMediaType] object used to filter images and video when using the photo picker.
     */
    object ImageAndVideo : VisualMediaType

    /**
     * [VisualMediaType] class used to filter a single mime type only when using the photo
     * picker.
     */
    class SingleMimeType(val mimeType: String) : VisualMediaType

    @CallSuper
    override fun createIntent(context: Context, input: PickVisualMediaRequest): Intent {
        // Check if Photo Picker is available on the device
        return if (isSystemPickerAvailable()) {
            Intent(MediaStore.ACTION_PICK_IMAGES).apply {
                type = getVisualMimeType(input.mediaType)
            }
        } else if (isSystemFallbackPickerAvailable(context)) {
            val fallbackPicker = checkNotNull(getSystemFallbackPicker(context)).activityInfo
            Intent(ACTION_SYSTEM_FALLBACK_PICK_IMAGES).apply {
                setClassName(fallbackPicker.applicationInfo.packageName, fallbackPicker.name)
                type = getVisualMimeType(input.mediaType)
            }
        } else if (isGmsPickerAvailable(context)) {
            val gmsPicker = checkNotNull(getGmsPicker(context)).activityInfo
            Intent(GMS_ACTION_PICK_IMAGES).apply {
                setClassName(gmsPicker.applicationInfo.packageName, gmsPicker.name)
                type = getVisualMimeType(input.mediaType)
            }
        } else {
            // For older devices running KitKat and higher and devices running Android 12
            // and 13 without the SDK extension that includes the Photo Picker, rely on the
            // ACTION_OPEN_DOCUMENT intent
            Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                type = getVisualMimeType(input.mediaType)

                if (type == null) {
                    // ACTION_OPEN_DOCUMENT requires to set this parameter when launching the
                    // intent with multiple mime types
                    type = "*/*"
                    putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*", "video/*"))
                }
            }
        }
    }

    @Suppress("InvalidNullabilityOverride")
    final override fun getSynchronousResult(
        context: Context,
        input: PickVisualMediaRequest
    ): SynchronousResult<Uri?>? = null

    final override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
        return intent.takeIf { resultCode == Activity.RESULT_OK }?.run {
            // Check both the data URI and ClipData since the GMS picker
            // only returns results through getClipDataUris()
            data ?: getClipDataUris().firstOrNull()
        }
    }
}

/**
 * An [ActivityResultContract] to use the
 * [Photo Picker](https://developer.android.com/training/data-storage/shared/photopicker)
 * to select a single image, video, or other type of visual media.
 *
 * This contract always prefers the system framework provided Photo Picker available via
 * [MediaStore.ACTION_PICK_IMAGES] when it is available, but will also provide a fallback
 * on devices that it is not available to provide a consistent API surface across all
 * Android API 19 or higher devices.
 *
 * The priority order for handling the Photo Picker is:
 * 1. The system framework provided [MediaStore.ACTION_PICK_IMAGES].
 * - An OEM can provide a system app that implements
 * [PickVisualMedia.ACTION_SYSTEM_FALLBACK_PICK_IMAGES] to provide a consistent Photo Picker
 * to older devices. These system apps may handle the
 * [PickVisualMedia.EXTRA_SYSTEM_FALLBACK_PICK_IMAGES_MAX] extra to respect the
 * [maxItems] passed to this contract.
 * - [Intent.ACTION_OPEN_DOCUMENT] is used as a final fallback on all Android API 19 or
 * higher devices. This Intent does not allow limiting the max items the user selects.
 *
 * The constructor accepts one parameter [maxItems] to limit the number of selectable items when
 * using the photo picker to return.
 *
 * The input is a [PickVisualMediaRequest].
 *
 * The output is a list `Uri` of the selected media. It can be empty if the user hasn't selected
 * any items. Keep in mind that `Uri` returned by the photo picker aren't writable.
 *
 * This can be extended to override [createIntent] if you wish to pass additional
 * extras to the Intent created by `super.createIntent()`.
 */
@RequiresApi(19)
open class PickMultipleVisualMedia(
    private val maxItems: Int = getMaxItems()
) : ActivityResultContract<PickVisualMediaRequest, List<@JvmSuppressWildcards Uri>>() {

    init {
        require(maxItems > 1) {
            "Max items must be higher than 1"
        }
    }

    @CallSuper
    @SuppressLint("NewApi", "ClassVerificationFailure")
    override fun createIntent(context: Context, input: PickVisualMediaRequest): Intent {
        // Check to see if the photo picker is available
        return if (PickVisualMedia.isSystemPickerAvailable()) {
            Intent(MediaStore.ACTION_PICK_IMAGES).apply {
                type = PickVisualMedia.getVisualMimeType(input.mediaType)
                require(maxItems <= MediaStore.getPickImagesMaxLimit()) {
                    "Max items must be less or equals MediaStore.getPickImagesMaxLimit()"
                }

                putExtra(MediaStore.EXTRA_PICK_IMAGES_MAX, maxItems)
            }
        } else if (PickVisualMedia.isSystemFallbackPickerAvailable(context)) {
            val fallbackPicker = checkNotNull(getSystemFallbackPicker(context)).activityInfo
            Intent(ACTION_SYSTEM_FALLBACK_PICK_IMAGES).apply {
                setClassName(fallbackPicker.applicationInfo.packageName, fallbackPicker.name)
                type = PickVisualMedia.getVisualMimeType(input.mediaType)
                putExtra(GMS_EXTRA_PICK_IMAGES_MAX, maxItems)
            }
        } else if (PickVisualMedia.isGmsPickerAvailable(context)) {
            val gmsPicker = checkNotNull(getGmsPicker(context)).activityInfo
            Intent(GMS_ACTION_PICK_IMAGES).apply {
                setClassName(gmsPicker.applicationInfo.packageName, gmsPicker.name)
                putExtra(GMS_EXTRA_PICK_IMAGES_MAX, maxItems)
            }
        } else {
            // For older devices running KitKat and higher and devices running Android 12
            // and 13 without the SDK extension that includes the Photo Picker, rely on the
            // ACTION_OPEN_DOCUMENT intent
            Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                type = PickVisualMedia.getVisualMimeType(input.mediaType)
                putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)

                if (type == null) {
                    // ACTION_OPEN_DOCUMENT requires to set this parameter when launching the
                    // intent with multiple mime types
                    type = "*/*"
                    putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*", "video/*"))
                }
            }
        }
    }

    @Suppress("InvalidNullabilityOverride")
    final override fun getSynchronousResult(
        context: Context,
        input: PickVisualMediaRequest
    ): SynchronousResult<List<@JvmSuppressWildcards Uri>>? = null

    final override fun parseResult(resultCode: Int, intent: Intent?): List<Uri> {
        return intent.takeIf {
            resultCode == Activity.RESULT_OK
        }?.getClipDataUris() ?: emptyList()
    }

    internal companion object {
        /**
         * The system photo picker has a maximum limit of selectable items returned by
         * [MediaStore.getPickImagesMaxLimit()]
         * On devices supporting picker provided via [ACTION_SYSTEM_FALLBACK_PICK_IMAGES],
         * the limit may be ignored if it's higher than the allowed limit.
         * On devices not supporting the photo picker, the limit is ignored.
         *
         * @see MediaStore.EXTRA_PICK_IMAGES_MAX
         */
        @SuppressLint("NewApi", "ClassVerificationFailure")
        internal fun getMaxItems() = if (PickVisualMedia.isSystemPickerAvailable()) {
            MediaStore.getPickImagesMaxLimit()
        } else {
            Integer.MAX_VALUE
        }

        internal fun Intent.getClipDataUris(): List<Uri> {
            // Use a LinkedHashSet to maintain any ordering that may be
            // present in the ClipData
            val resultSet = LinkedHashSet<Uri>()
            data?.let { data ->
                resultSet.add(data)
            }
            val clipData = clipData
            if (clipData == null && resultSet.isEmpty()) {
                return emptyList()
            } else if (clipData != null) {
                for (i in 0 until clipData.itemCount) {
                    val uri = clipData.getItemAt(i).uri
                    if (uri != null) {
                        resultSet.add(uri)
                    }
                }
            }
            return ArrayList(resultSet)
        }
    }
}