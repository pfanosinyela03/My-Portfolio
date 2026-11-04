package com.perplxdai.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = BluePrimary,
    background = BackgroundDark,
    surface = PanelDark,
    onPrimary = TextLight,
    onBackground = TextLight,
    onSurface = TextLight,
)

@Composable
fun PerplxdAITheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
