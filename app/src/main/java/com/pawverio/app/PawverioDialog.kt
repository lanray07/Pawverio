package com.pawverio.app

import android.content.Context
import android.content.res.Configuration
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

/**
 * Dialog windows install their own Android context. Reapply the selected app locale inside every
 * slot so forms, confirmations and accessibility copy cannot fall back to the device language while
 * the surrounding screen uses another language.
 */
@Composable
internal fun PawverioAlertDialog(
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    dismissButton: (@Composable () -> Unit)? = null,
    title: (@Composable () -> Unit)? = null,
    text: (@Composable () -> Unit)? = null,
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val direction = LocalLayoutDirection.current
    val localizedTitle: (@Composable () -> Unit)? =
        if (title == null) null
        else {
            { DialogSlot(context, configuration, direction, title) }
        }
    val localizedText: (@Composable () -> Unit)? =
        if (text == null) null
        else {
            { DialogSlot(context, configuration, direction, text) }
        }
    val localizedDismiss: (@Composable () -> Unit)? =
        if (dismissButton == null) null
        else {
            { DialogSlot(context, configuration, direction, dismissButton) }
        }
    androidx.compose.material3.AlertDialog(
        modifier = Modifier.widthIn(max = (configuration.screenWidthDp - 40).coerceIn(280, 560).dp),
        onDismissRequest = onDismissRequest,
        confirmButton = { DialogSlot(context, configuration, direction, confirmButton) },
        dismissButton = localizedDismiss,
        title = localizedTitle,
        text = localizedText,
    )
}

@Composable
private fun DialogSlot(
    context: Context,
    configuration: Configuration,
    direction: LayoutDirection,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalContext provides context,
        LocalConfiguration provides configuration,
        LocalLayoutDirection provides direction,
        content = content,
    )
}
