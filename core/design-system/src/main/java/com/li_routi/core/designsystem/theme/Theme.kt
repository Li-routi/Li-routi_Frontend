package com.li_routi.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import com.li_routi.core.designsystem.foundation.typography.LocalLiroutiTypography
import com.li_routi.core.designsystem.foundation.typography.LiroutiTypography
import com.li_routi.core.designsystem.foundation.typography.Typography

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
