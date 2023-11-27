package com.yrezgui.filepicker.sharedsample

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MessageBubble(message: Message) {
    Box(modifier = Modifier.fillMaxWidth()) {
        var modifier = Modifier
            .fillMaxWidth(0.85f)
            .align(Alignment.CenterStart)
        var colors = CardDefaults.cardColors()

        if (message.sender == SampleData.self) {
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .align(Alignment.CenterEnd)
            colors =
                CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        }

        Card(
            modifier = modifier,
            colors = colors
        ) {
            Column(Modifier.padding(top = 8.dp, start = 8.dp, bottom = 8.dp, end = 16.dp)) {
                Text(text = message.text)
            }
        }
    }
}