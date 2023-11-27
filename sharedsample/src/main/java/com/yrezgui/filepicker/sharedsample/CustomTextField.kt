package com.yrezgui.filepicker.sharedsample

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    // parameters below will be passed to BasicTextField for correct behavior of the text field,
    // and to the decoration box for proper styling and sizing
    val enabled = true
    val singleLine = true

    val colors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = Color.Transparent,
        unfocusedBorderColor = Color.Transparent,
    )

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        // internal implementation of the BasicTextField will dispatch focus events
        interactionSource = interactionSource,
        enabled = enabled,
        singleLine = singleLine,
        textStyle = MaterialTheme.typography.bodyLarge
    ) {
        OutlinedTextFieldDefaults.DecorationBox(
            value = value,
            visualTransformation = VisualTransformation.None,
            innerTextField = it,
            singleLine = singleLine,
            enabled = enabled,
            // same interaction source as the one passed to BasicTextField to read focus state
            // for text field styling
            interactionSource = interactionSource,
            // keep vertical paddings but change the horizontal
            contentPadding = TextFieldDefaults.contentPaddingWithoutLabel(
                start = 8.dp,
                end = 8.dp,
                top = 9.dp,
                bottom = 9.dp
            ),
            // update border colors
            colors = colors,
        )
    }
}