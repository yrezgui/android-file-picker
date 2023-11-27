package com.yrezgui.filepicker.sharedsample

import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationScreen(
    modifier: Modifier = Modifier,
    contact: String,
    conversation: List<Message> = emptyList(),
    message: String = "",
    onModifyingMessage: (String) -> Unit = {},
    attachments: List<Uri> = emptyList(),
    onAddingAttachment: () -> Unit = {},
    onRemovingAttachment: (Int) -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.AccountCircle,
                            contentDescription = "Contact avatar",
                            tint = Color(0xFFed9550),
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(contact, fontSize = 18.sp)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to conversations list"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Icons.Outlined.Videocam,
                            contentDescription = "Start video call"
                        )
                    }
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Icons.Outlined.Phone,
                            contentDescription = "Start audio call"
                        )
                    }
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Icons.Outlined.MoreVert,
                            contentDescription = "Show other actions"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier.padding(paddingValues)) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                reverseLayout = true
            ) {
                items(conversation.asReversed()) {
                    MessageBubble(message = it)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            MessageEditor(
                text = message,
                onValueChange = onModifyingMessage,
                attachments = attachments,
                onAddingAttachment = onAddingAttachment,
                onRemovingAttachment = onRemovingAttachment
            )
        }
    }
}

