package com.yrezgui.filepicker.pickercompose.picker

import android.net.Uri

data class PickerConfig(
    val styleMode: StyleMode = StyleMode.Inline,
    val selectionMode: SelectionMode = SelectionMode.Ordered,
    val referrer: String = "",
    val height: Int = FULL_SCREEN_HEIGHT,
    val preSelectedUris: List<Uri> = emptyList(),
    val showToolbar: Boolean = true
) {
    companion object {
        const val ACTION = "universal.OPEN_PICKER"
        const val RESULT_ACTION = "universal.RESULT_PICKER"
        const val FULL_SCREEN_HEIGHT = -1
        const val DEFAULT_PEEK_HEIGHT = 300
    }

    enum class ExtraOption(val id: String) {
        StyleMode("style_mode"),
        SelectionMode("selection_mode"),
        ShowToolbar("show_toolbar"),
        Height("height"),
        SearchSuggestions("search_suggestions")
    }

    enum class StyleMode {
        Modal, Inline, Carousel
    }

    enum class SelectionMode {
        Ordered, Live, LiveOrdered
    }
}