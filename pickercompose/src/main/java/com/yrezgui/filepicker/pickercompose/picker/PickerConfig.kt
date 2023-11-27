package com.yrezgui.filepicker.pickercompose.picker

import android.net.Uri
import com.yrezgui.filepicker.supportlibrary.PickVisualMedia

data class PickerConfig(
    val referrer: String = "",
    val selectionMode: PickVisualMedia.SelectionMode = PickVisualMedia.Unordered,
    val presentationMode: PickVisualMedia.PresentationMode = PickVisualMedia.ModalMode,
    val height: Int = PickVisualMedia.FULL_SCREEN_HEIGHT,
    val showToolbar: Boolean = true,
    val cameraSupport: Boolean = true,
    val preSelectedUris: List<Uri> = emptyList(),
)