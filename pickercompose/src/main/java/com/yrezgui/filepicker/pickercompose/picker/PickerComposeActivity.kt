package com.yrezgui.filepicker.pickercompose.picker

import android.content.ContentValues
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.yrezgui.filepicker.pickercompose.ui.theme.PickerComposeTheme
import kotlinx.collections.immutable.persistentListOf
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class PickerComposeActivity : ComponentActivity() {
    private val viewModel: PickerViewModel by viewModels { PickerViewModel.Factory }

    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.updatePickerConfig(parseIntent() ?: return)
        val config = viewModel.state.pickerConfig

        if (config.height != PickerConfig.FULL_SCREEN_HEIGHT && config.styleMode != PickerConfig.StyleMode.Modal) {
            val params = window.attributes
            params.height = config.height
            params.gravity = Gravity.BOTTOM or Gravity.START or Gravity.END
            params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL

            window.attributes = params
        }

        setContent {
            val state = viewModel.state
            val gridEntries by viewModel.dataSource.collectAsStateWithLifecycle(initialValue = persistentListOf())

            val modifier =
                if (config.height != PickerConfig.FULL_SCREEN_HEIGHT && config.styleMode != PickerConfig.StyleMode.Modal) {
                    Modifier
                        .padding(top = 5.dp)
                        .height(config.height.dp)
                } else {
                    Modifier
                        .padding(top = 5.dp)
                        .fillMaxSize()
                }

            PickerComposeTheme {
                Surface(color = MaterialTheme.colorScheme.background, modifier = modifier) {
                    Column {
                        if (config.showToolbar) {
                            PickerToolbar(
                                entries = gridEntries,
                                selectedUris = state.selectedUris,
                                onToggleMedia = viewModel::toggleMedia,
                                onClear = viewModel::onClear,
                                onDone = ::onDone
                            )
                        }

                        when (config.styleMode) {
                            PickerConfig.StyleMode.Modal -> {
                                ModalMediaGrid(
                                    entries = gridEntries,
                                    selectedUris = state.selectedUris,
                                    onToggleMedia = viewModel::toggleMedia,
                                )
                            }

                            PickerConfig.StyleMode.Inline -> {
                                VisualMediaGrid(
                                    entries = gridEntries,
                                    selectedUris = state.selectedUris,
                                    onToggleMedia = viewModel::toggleMedia,
                                )
                            }

                            PickerConfig.StyleMode.Carousel -> {
                                CarouselMediaGrid(
                                    entries = gridEntries,
                                    selectedUris = state.selectedUris,
                                    onToggleMedia = viewModel::toggleMedia,
                                )
                            }
                        }
                    }
                }
            }
        }

        cameraExecutor = Executors.newSingleThreadExecutor()

    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    private fun onDone() {
        setResult(RESULT_OK, viewModel.prepareReturnedIntent())
        finish()
    }

    private fun parseIntent(): PickerConfig? {
        Log.d("parseIntent", intent.toString())
        if (intent.action != PickerConfig.ACTION) {
            Log.e(
                "parseIntent",
                "Wrong action (expected ${PickerConfig.ACTION} | received ${intent.action}"
            )
            return null
        }

        val referrer = ActivityCompat.getReferrer(this)

        // Note that this is not a security feature -- you can not trust the referrer information,
        // applications can spoof it.
        if (referrer == null || referrer.authority == null) {
            Log.e("parseIntent", "No referrer ($referrer | ${referrer?.authority}")
            return null
        }

        var pickerConfig = PickerConfig(referrer = referrer.authority!!)
        val extras = intent.extras

        if (extras == null) {
            Log.e("parseIntent", "No extras")
            return null
        } else {
            Log.d("preselectedUris", intent.getClipDataUris().toString())
            pickerConfig = pickerConfig.copy(
                styleMode = PickerConfig.StyleMode.values()[extras.getInt(
                    PickerConfig.ExtraOption.StyleMode.id,
                    0
                )],
                selectionMode = PickerConfig.SelectionMode.values()[extras.getInt(
                    PickerConfig.ExtraOption.SelectionMode.id,
                    0
                )],
                height = extras.getInt(
                    PickerConfig.ExtraOption.Height.id,
                    PickerConfig.FULL_SCREEN_HEIGHT
                ),
                showToolbar = extras.getBoolean(PickerConfig.ExtraOption.ShowToolbar.id),
                preSelectedUris = intent.getClipDataUris()
            )
        }


        Log.d("parseIntent|successful", pickerConfig.toString())

        return pickerConfig
    }

    private fun takePhoto() {
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        // Create time stamped name and MediaStore entry.
        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        }

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(
                contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )
            .build()

        // Set up image capture listener, which is triggered after photo has
        // been taken
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e("Camera", "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val msg = "Photo capture succeeded: ${output.savedUri}"
                    Log.d("Camera", msg)
                }
            }
        )
    }
}

private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"