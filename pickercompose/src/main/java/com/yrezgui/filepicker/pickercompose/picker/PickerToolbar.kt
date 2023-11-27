package com.yrezgui.filepicker.pickercompose.picker

import android.net.Uri
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PickerToolbar(
    entries: ImmutableList<MediaEntry>,
    selectedUris: LinkedHashSet<Uri>,
    onToggleMedia: (MediaEntry) -> Unit,
    onClear: () -> Unit,
    onDone: () -> Unit,
) {
    var selectedIndex by remember { mutableIntStateOf(0) }
    val options = listOf("Photos", "Albums")

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = 5.dp)
    ) {
        TextButton(onClick = onClear, enabled = selectedUris.isNotEmpty()) {
            Text("Clear")
        }

        Spacer(modifier = Modifier.weight(1f))

        SingleChoiceSegmentedButtonRow {
            options.forEachIndexed { index, label ->
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                    onClick = { selectedIndex = index },
                    selected = index == selectedIndex
                ) {
                    Text(label)
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        TextButton(onClick = onDone) {
            Text("Done", style = TextStyle(fontWeight = FontWeight.Bold))
        }
    }
}