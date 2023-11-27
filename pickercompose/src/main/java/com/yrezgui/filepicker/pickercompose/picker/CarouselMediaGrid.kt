package com.yrezgui.filepicker.pickercompose.picker

import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.integration.compose.CrossFade
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.rememberGlidePreloadingData
import kotlinx.collections.immutable.ImmutableList

private const val THUMBNAIL_DIMENSION = 90
private val THUMBNAIL_SIZE = Size(THUMBNAIL_DIMENSION.toFloat(), THUMBNAIL_DIMENSION.toFloat())

@Composable
fun CarouselMediaGrid(
    entries: ImmutableList<MediaEntry>,
    selectedUris: LinkedHashSet<Uri>,
    onToggleMedia: (MediaEntry) -> Unit,
) {
    val lazyGridState = rememberLazyGridState()
    val requestBuilderTransform =
        { item: MediaEntry, requestBuilder: RequestBuilder<Drawable> ->
            requestBuilder.load(item.uri).signature(item.signature())
        }

    val preloadingData = rememberGlidePreloadingData(
        entries,
        THUMBNAIL_SIZE,
        requestBuilderTransform = requestBuilderTransform
    )

    LazyHorizontalGrid(
        state = lazyGridState,
        rows = GridCells.Fixed(1),
        verticalArrangement = Arrangement.spacedBy(3.dp),
        horizontalArrangement = Arrangement.spacedBy(3.dp),
    ) {
        itemsIndexed(entries, key = { _, entry -> entry.uri }) { index, entry ->
            VisualItem(
                entry = entry,
                selectedIndex = selectedUris.indexOfFirst { it == entry.uri },
                preloadRequestBuilder = preloadingData[index].second,
                modifier = Modifier.clickable { onToggleMedia(entry) }
            )
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun VisualItem(
    entry: MediaEntry,
    selectedIndex: Int,
    preloadRequestBuilder: RequestBuilder<Drawable>,
    modifier: Modifier = Modifier
) {
    val selected = selectedIndex != -1

    Surface(
        modifier = modifier.aspectRatio(1f),
        tonalElevation = 3.dp
    ) {
        Box {
            val transition = updateTransition(selected, label = "selected")
            val padding by transition.animateDp(label = "padding") { selected ->
                if (selected) 10.dp else 0.dp
            }
            val roundedCornerShape by transition.animateDp(label = "corner") { selected ->
                if (selected) 16.dp else 0.dp
            }

            GlideImage(
                model = entry.uri,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                transition = CrossFade,
                modifier = Modifier
                    .matchParentSize()
                    .padding(padding)
                    .clip(RoundedCornerShape(roundedCornerShape))
            ) {
                it.thumbnail(preloadRequestBuilder).signature(entry.signature())
            }

            if (selected) {
                val bgColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                Box(
                    modifier = Modifier
                        .border(2.dp, bgColor, RoundedCornerShape(roundedCornerShape))
                        .clip(RoundedCornerShape(roundedCornerShape))
                        .background(bgColor),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = (selectedIndex + 1).toString(),
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(5.dp)
                    )
                }
            }
        }
    }

}