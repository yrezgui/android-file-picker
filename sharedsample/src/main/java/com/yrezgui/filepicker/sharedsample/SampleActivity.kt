package com.yrezgui.filepicker.sharedsample

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.yrezgui.filepicker.sharedsample.ui.theme.SampleTheme
import com.yrezgui.filepicker.supportlibrary.PickMultipleVisualMedia
import com.yrezgui.filepicker.supportlibrary.PickVisualMedia
import com.yrezgui.filepicker.supportlibrary.PickVisualMedia.ImageAndVideo
import com.yrezgui.filepicker.supportlibrary.PickVisualMedia.InlineMode
import com.yrezgui.filepicker.supportlibrary.PickVisualMediaRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

open class SampleActivity : ComponentActivity() {
    private val _pickerPadding = MutableStateFlow(0)

    private val _selectedUris = MutableStateFlow<List<Uri>>(emptyList())
    private val selectedUris: StateFlow<List<Uri>> = _selectedUris

    private val launchIntent = registerForActivityResult(StartActivityForResult()) { result ->
        lifecycleScope.launch {
            _pickerPadding.emit(0)
            if (result.resultCode == RESULT_OK) {
                onUrisSelect(result.data?.getClipDataUris() ?: emptyList())
            }
        }
    }

    private val photoPicker = registerForActivityResult(PickMultipleVisualMedia()) { media ->
        lifecycleScope.launch {
            _pickerPadding.emit(0)
            onUrisSelect(media)
        }
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                Log.d("BroadcastReceiver", intent.action.toString())
                lifecycleScope.launch {
                    Log.d("BroadcastReceiver", intent.action.toString())
                    Log.d("BroadcastReceiver", intent.getClipDataUris().size.toString())

                    val uris = intent.getClipDataUris()
                    if (uris.isNotEmpty()) {
                        val uri = uris.first()
                        val isGranted = checkCallingUriPermission(
                            uri,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION
                        ) == PackageManager.PERMISSION_GRANTED
                        Log.d("CheckUri", "$uri || $isGranted")
                    }

                    onUrisSelect(intent.getClipDataUris())
                }
            }
        }, IntentFilter(PickVisualMedia.FILE_PICKER_INTENT), RECEIVER_EXPORTED)

        setContent {
            var message by rememberSaveable { mutableStateOf("") }
            val attachments by selectedUris.collectAsState(initial = emptyList())

            val pickerPaddingRaw by _pickerPadding.collectAsState()
            val pickerPaddingDp by animateDpAsState(
                pickerPaddingRaw.dp,
                label = "Photo Picker padding"
            )
            val pickerPadding = Modifier.padding(bottom = pickerPaddingDp)

            SampleTheme {
                Surface(
                    modifier = Modifier
                        .imePadding()
                        .fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ConversationScreen(
                        modifier = Modifier.then(pickerPadding),
                        contact = SampleData.contact,
                        conversation = SampleData.conversationSample.asReversed(),
                        message = message,
                        onModifyingMessage = { message = it },
                        attachments = attachments,
                        onAddingAttachment = {
                            launchPicker(
                                presentationMode = InlineMode,
                                height = 300,
                                showToolbar = false,
                            )
                        },
                        onRemovingAttachment = {}
                    )
                }
            }
        }
    }

    private fun resetSelectedUris() {
        lifecycleScope.launch {
            _selectedUris.emit(emptyList())
        }
    }

    private fun onUrisSelect(uris: List<Uri>) {
        lifecycleScope.launch {
            Log.d("onUrisSelect", uris.toString())
            _selectedUris.emit(uris)
        }
    }

    private fun launchPicker(
        presentationMode: PickVisualMedia.PresentationMode,
        height: Int = PickVisualMedia.FULL_SCREEN_HEIGHT,
        showToolbar: Boolean = true
    ) {
        lifecycleScope.launch {
            photoPicker.launch(
                PickVisualMediaRequest(
                    mediaType = ImageAndVideo,
                    presentationMode = presentationMode,
                    height = height,
                    showToolbar = showToolbar,
                    preSelectedUris = selectedUris.value
                )
            )

            _pickerPadding.emit(height)
            launchIntent.launch(intent)
        }
    }
}