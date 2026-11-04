package com.pawverio.app

import androidx.compose.material3.*
import androidx.compose.ui.graphics.Color

internal fun pawverioColors(dark: Boolean): ColorScheme =
    if (dark)
        darkColorScheme(
            primary = Color(0xFF9AB7A5),
            onPrimary = Color(0xFF172C29),
            primaryContainer = Color(0xFF294F4D),
            onPrimaryContainer = Color(0xFFD9EEE4),
            secondary = Color(0xFFD98A78),
            onSecondary = Color(0xFF321F1A),
            secondaryContainer = Color(0xFF633F36),
            onSecondaryContainer = Color(0xFFF9DDD4),
            tertiary = Color(0xFFC0C9AD),
            background = Color(0xFF162324),
            onBackground = Color(0xFFE5EBE3),
            surface = Color(0xFF192829),
            onSurface = Color(0xFFE5EBE3),
            surfaceVariant = Color(0xFF354946),
            onSurfaceVariant = Color(0xFFC9D7CB),
            outline = Color(0xFF9AAC9F),
            outlineVariant = Color(0xFF496057),
            surfaceContainerLowest = Color(0xFF111D1E),
            surfaceContainerLow = Color(0xFF1D2E2E),
            surfaceContainer = Color(0xFF243635),
            surfaceContainerHigh = Color(0xFF2B403C),
            surfaceContainerHighest = Color(0xFF344C45),
        )
    else
        lightColorScheme(
            primary = Color(0xFF286B70),
            onPrimary = Color.White,
            primaryContainer = Color(0xFFD6E8E3),
            onPrimaryContainer = Color(0xFF17494D),
            secondary = Color(0xFF98513F),
            onSecondary = Color.White,
            secondaryContainer = Color(0xFFF1D9D2),
            onSecondaryContainer = Color(0xFF4F2920),
            tertiary = Color(0xFF4F6A56),
            background = Color(0xFFFAF7F0),
            onBackground = Color(0xFF2B3335),
            surface = Color(0xFFFAF7F0),
            onSurface = Color(0xFF2B3335),
            surfaceVariant = Color(0xFFECEFE6),
            onSurfaceVariant = Color(0xFF45574D),
            outline = Color(0xFF6E8176),
            outlineVariant = Color(0xFFCCD7CB),
            surfaceContainerLowest = Color.White,
            surfaceContainerLow = Color(0xFFF3F3E9),
            surfaceContainer = Color(0xFFECEFE4),
            surfaceContainerHigh = Color(0xFFE5EBDE),
            surfaceContainerHighest = Color(0xFFDDE7D9),
        )
