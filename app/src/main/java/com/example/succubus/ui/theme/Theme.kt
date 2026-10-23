package com.example.succubus.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val SuccubusColorScheme = darkColorScheme(
    primary = SuccubusRed,
    onPrimary = TextIvory,
    primaryContainer = SuccubusRedDark,
    onPrimaryContainer = TextIvory,
    secondary = SpeakeasyGold,
    onSecondary = BarBlack,
    secondaryContainer = BarCardElevated,
    onSecondaryContainer = SpeakeasyGoldLight,
    tertiary = SpeakeasyGoldLight,
    onTertiary = BarBlack,
    background = BarBlack,
    onBackground = TextIvory,
    surface = BarDarkSurface,
    onSurface = TextIvory,
    surfaceVariant = BarCardBg,
    onSurfaceVariant = TextMuted,
    outline = BarBorder,
    error = StatusDanger,
    onError = TextIvory
)

@Composable
fun SuccubusTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = BarBlack.toArgb()
                window.navigationBarColor = BarBlack.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = SuccubusColorScheme,
        typography = Typography,
        content = content
    )
}