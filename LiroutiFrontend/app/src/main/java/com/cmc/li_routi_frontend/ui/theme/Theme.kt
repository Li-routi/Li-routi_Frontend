package com.cmc.li_routi_frontend.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable

@Composable
fun LiroutiFrontendTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) LiroutiDarkColorScheme else LiroutiLightColorScheme

    CompositionLocalProvider(
        LocalLiroutiColors provides colorScheme,
        LocalLiroutiTypography provides LiroutiTypography(),
    ) {
        MaterialTheme(
            typography = Typography,
            content = content,
        )
    }
}

object LiroutiTheme {
    val colors: LiroutiColorScheme
        @Composable @ReadOnlyComposable get() = LocalLiroutiColors.current

    val typography: LiroutiTypography
        @Composable @ReadOnlyComposable get() = LocalLiroutiTypography.current
}
