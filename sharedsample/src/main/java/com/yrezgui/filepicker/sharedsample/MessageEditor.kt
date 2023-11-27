package com.yrezgui.filepicker.sharedsample

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.outlined.AddAPhoto
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.EmojiEmotions
import androidx.compose.material.icons.outlined.MicNone
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.CrossFade
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.yrezgui.filepicker.sharedsample.MessageEditorTokens.AttachmentRemovalIconSize
import com.yrezgui.filepicker.sharedsample.MessageEditorTokens.CarouselPaddingEnd
import com.yrezgui.filepicker.sharedsample.MessageEditorTokens.CarouselThumbnailInnerPadding
import com.yrezgui.filepicker.sharedsample.MessageEditorTokens.CarouselThumbnailSize
import com.yrezgui.filepicker.sharedsample.MessageEditorTokens.CarouselWrapperPadding
import com.yrezgui.filepicker.sharedsample.MessageEditorTokens.ComposerRoundedRadius
import com.yrezgui.filepicker.sharedsample.MessageEditorTokens.MessageEditorPadding

@Composable
fun MessageEditor(
    text: String,
    onValueChange: (String) -> Unit,
    attachments: List<Uri> = emptyList(),
    onAddingAttachment: () -> Unit = {},
    onRemovingAttachment: (index: Int) -> Unit = {}
) {
    Row(
        verticalAlignment = Alignment.Bottom,
        modifier = Modifier.padding(all = MessageEditorPadding)
    ) {
        IconButton(onClick = {}) {
            Icon(Icons.Outlined.AddCircleOutline, contentDescription = "Add attachment")
        }
        IconButton(onClick = onAddingAttachment) {
            Icon(Icons.Outlined.AddAPhoto, contentDescription = "Add attachment")
        }
        Surface(
            shape = RoundedCornerShape(ComposerRoundedRadius),
            color = MaterialTheme.colorScheme.surfaceContainer
        ) {
            Column {
                AnimatedVisibility(attachments.isNotEmpty()) {
                    CarouselThumbnailWrapper(
                        attachments = attachments,
                        onRemovingAttachment = onRemovingAttachment
                    )
                }
                Row(verticalAlignment = Alignment.Bottom) {
                    CustomTextField(
                        value = text,
                        onValueChange = onValueChange,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = {}) {
                        Icon(Icons.Outlined.EmojiEmotions, contentDescription = "Add emoji")
                    }
                    if (text.isNotEmpty()) {
                        IconButton(onClick = {}) {
                            Icon(
                                Icons.AutoMirrored.Outlined.Send,
                                contentDescription = "Send message"
                            )
                        }
                    } else {
                        IconButton(onClick = {}) {
                            Icon(
                                Icons.Outlined.MicNone,
                                contentDescription = "Record audio message"
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CarouselThumbnailWrapper(attachments: List<Uri>, onRemovingAttachment: (index: Int) -> Unit) {
    LazyRow(
        modifier = Modifier.padding(top = CarouselWrapperPadding),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        reverseLayout = true
    ) {
        item {
            Spacer(Modifier.width(CarouselPaddingEnd))
        }
        itemsIndexed(attachments.asReversed(), key = { _, uri -> uri }) { index, uri ->
            CarouselThumbnailItem(uri = uri, onRemove = { onRemovingAttachment(index) })
        }
        item {
            Spacer(Modifier.width(CarouselWrapperPadding))
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun CarouselThumbnailItem(uri: Uri, onRemove: () -> Unit) {
    Box(
        modifier = Modifier.size(CarouselThumbnailSize),
        contentAlignment = Alignment.TopEnd
    ) {
        GlideImage(
            model = uri,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            transition = CrossFade,
            modifier = Modifier
                .padding(
                    top = CarouselThumbnailInnerPadding,
                    end = CarouselThumbnailInnerPadding
                )
                .clip(RoundedCornerShape(8.dp))
                .aspectRatio(1f)
        )
        IconButton(
            modifier = Modifier.size(AttachmentRemovalIconSize),
            onClick = onRemove,
            colors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
        ) {
            Icon(
                imageVector = Icons.Outlined.Clear,
                contentDescription = "Remove attachment"
            )
        }
    }
}

private object MessageEditorTokens {
    val MessageEditorPadding = 8.dp
    val ComposerRoundedRadius = 20.dp
    val CarouselWrapperPadding = 8.dp
    val CarouselPaddingEnd = 48.dp
    val CarouselThumbnailSize = 80.dp
    val CarouselThumbnailInnerPadding = 8.dp
    val AttachmentRemovalIconSize = 28.dp
}