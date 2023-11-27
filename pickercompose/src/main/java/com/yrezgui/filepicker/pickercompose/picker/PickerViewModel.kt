package com.yrezgui.filepicker.pickercompose.picker

import android.app.Application
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn

class PickerViewModel(application: Application) : AndroidViewModel(application) {
    private val context: Context
        get() = getApplication()

    private val mediaStore = MediaStoreRepository(context)
    var dataSource = mediaStore.loadDataSource().shareIn(viewModelScope, SharingStarted.Lazily)

    data class State(
        val selectedUris: LinkedHashSet<Uri> = linkedSetOf(),
        val pickerConfig: PickerConfig = PickerConfig()
    )

    var state by mutableStateOf(createState())
        private set

    private fun createState(): State {
        return State()
    }

    fun toggleMedia(media: MediaEntry) {
        state = if (state.selectedUris.find { it == media.uri } != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.revokeUriPermission(
                    state.pickerConfig.referrer,
                    media.uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }
            state.copy(selectedUris = state.selectedUris - media.uri)
        } else {
            context.grantUriPermission(
                state.pickerConfig.referrer,
                media.uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            state.copy(selectedUris = state.selectedUris + media.uri)
        }

        context.sendBroadcast(prepareReturnedIntent())
    }

    fun onClear() {
        state = state.copy(selectedUris = linkedSetOf())
        context.sendBroadcast(prepareReturnedIntent())
    }

    fun prepareReturnedIntent(): Intent {
        val selectedUris = state.selectedUris.toList()

        val intent = Intent(PickerConfig.RESULT_ACTION).apply {
            setPackage(state.pickerConfig.referrer)
        }

        if (selectedUris.isNotEmpty()) {
            intent.apply {
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//                addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)

                clipData = ClipData(
                    null /* label */,
                    arrayOf("image/*", "video/*"),
                    ClipData.Item(selectedUris[0])
                ).apply {
                    for (i in 1 until selectedUris.size) {
                        addItem(ClipData.Item(selectedUris[i]))
                    }
                }
            }
        }

        return intent
    }

    fun updatePickerConfig(pickerConfig: PickerConfig) {
        state = state.copy(
            pickerConfig = pickerConfig,
            selectedUris = linkedSetOf(*pickerConfig.preSelectedUris.toTypedArray())
        )
        Log.d("updatePickerConfig", state.toString())
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val application = this[AndroidViewModelFactory.APPLICATION_KEY]!!

                PickerViewModel(application = application)
            }
        }
    }
}