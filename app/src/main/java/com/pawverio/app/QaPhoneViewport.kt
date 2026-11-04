package com.pawverio.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

/** Debug capture container for the real application UI; never changes the system display. */
@Composable
internal fun QaPhoneViewport(content: @Composable () -> Unit) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Box(Modifier.requiredSize(360.dp, 640.dp).clipToBounds().testTag("qa-phone-viewport")) {
            content()
        }
    }
}
