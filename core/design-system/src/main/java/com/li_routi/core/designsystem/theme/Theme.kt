package com.li_routi.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.li_routi.core.designsystem.foundation.typography.LocalRirotiTypography
import com.li_routi.core.designsystem.foundation.typography.RirotiTypography
import com.li_routi.core.designsystem.foundation.typography.Typography

@Composable
fun LiroutiFrontendTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) RirotiDarkColorScheme else RirotiLightColorScheme

    CompositionLocalProvider(
        LocalRirotiColors provides colorScheme,
        LocalRirotiTypography provides RirotiTypography(),
    ) {
        MaterialTheme(
            typography = Typography,
            content = content,
        )
    }
}

object RirotiTheme {
    val colors: RirotiColorScheme
        @Composable get() = LocalRirotiColors.current

    val typography: RirotiTypography
        @Composable get() = LocalRirotiTypography.current
}
