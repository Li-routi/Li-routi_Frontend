package com.li_routi.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.li_routi.core.designsystem.foundation.color.Blue500
import com.li_routi.core.designsystem.foundation.color.Blue600
import com.li_routi.core.designsystem.foundation.color.Blue800
import com.li_routi.core.designsystem.foundation.color.DimmerDefault
import com.li_routi.core.designsystem.foundation.color.DimmerSecondary
import com.li_routi.core.designsystem.foundation.color.Neutral0
import com.li_routi.core.designsystem.foundation.color.Neutral10
import com.li_routi.core.designsystem.foundation.color.Neutral15
import com.li_routi.core.designsystem.foundation.color.Neutral20
import com.li_routi.core.designsystem.foundation.color.Neutral22
import com.li_routi.core.designsystem.foundation.color.Neutral23
import com.li_routi.core.designsystem.foundation.color.Neutral30
import com.li_routi.core.designsystem.foundation.color.Neutral40
import com.li_routi.core.designsystem.foundation.color.Neutral50
import com.li_routi.core.designsystem.foundation.color.Neutral60
import com.li_routi.core.designsystem.foundation.color.Neutral70
import com.li_routi.core.designsystem.foundation.color.Neutral80
import com.li_routi.core.designsystem.foundation.color.Neutral90
import com.li_routi.core.designsystem.foundation.color.Neutral95
import com.li_routi.core.designsystem.foundation.color.Neutral96
import com.li_routi.core.designsystem.foundation.color.Neutral97
import com.li_routi.core.designsystem.foundation.color.Neutral98
import com.li_routi.core.designsystem.foundation.color.Neutral99
import com.li_routi.core.designsystem.foundation.color.Neutral100
import com.li_routi.core.designsystem.foundation.color.Red100
import com.li_routi.core.designsystem.foundation.color.Red200
import com.li_routi.core.designsystem.foundation.color.Red300
import com.li_routi.core.designsystem.foundation.color.Red500
import com.li_routi.core.designsystem.foundation.color.Red600

@Immutable
data class RirotiColorScheme(
    val isDark: Boolean,

    // Primary
    val primaryNormal: Color,
    val primaryActive: Color,
    val primaryDisabled: Color,

    // Secondary
    val secondaryNormal: Color,
    val secondaryActive: Color,
    val secondaryDisabled: Color,

    // Label
    val labelDefault: Color,
    val labelStrong: Color,
    val labelSub: Color,
    val labelInfo: Color,
    val labelAssistive: Color,
    val labelDisable: Color,
    val labelReverse: Color,

    // Background
    val backgroundDefault: Color,
    val backgroundSecondary: Color,
    val backgroundAlternative: Color,
    val backgroundStrong: Color,
    val backgroundSelected: Color,

    // Dimmer
    val dimmerDefault: Color,
    val dimmerSecondary: Color,

    // Border
    val borderDefault: Color,
    val borderSub: Color,
    val borderAlternative: Color,
    val borderStrong: Color,
    val borderActive: Color,

    // Danger
    val dangerText: Color,
    val dangerBase: Color,
    val dangerBorder: Color,
    val dangerSurface: Color,
)

val RirotiLightColorScheme = RirotiColorScheme(
    isDark = false,

    primaryNormal = Blue500,
    primaryActive = Blue600,
    primaryDisabled = Neutral70,

    secondaryNormal = Blue500,
    secondaryActive = Blue600,
    secondaryDisabled = Neutral70,

    labelDefault = Neutral10,
    labelStrong = Neutral0,
    labelSub = Neutral30,
    labelInfo = Neutral60,
    labelAssistive = Neutral90,
    labelDisable = Neutral96,
    labelReverse = Neutral100,

    backgroundDefault = Neutral100,
    backgroundSecondary = Red200,
    backgroundAlternative = Neutral99,
    backgroundStrong = Neutral95,
    backgroundSelected = Red300,

    dimmerDefault = DimmerDefault,
    dimmerSecondary = DimmerSecondary,

    borderDefault = Neutral96,
    borderSub = Neutral97,
    borderAlternative = Neutral98,
    borderStrong = Neutral80,
    borderActive = Blue800,

    dangerText = Red600,
    dangerBase = Red500,
    dangerBorder = Red200,
    dangerSurface = Red100,
)

val RirotiDarkColorScheme = RirotiColorScheme(
    isDark = true,

    primaryNormal = Blue500,
    primaryActive = Blue600,
    primaryDisabled = Neutral70,

    secondaryNormal = Blue500,
    secondaryActive = Blue600,
    secondaryDisabled = Neutral70,

    labelDefault = Neutral99,
    labelStrong = Neutral100,
    labelSub = Neutral90,
    labelInfo = Neutral70,
    labelAssistive = Neutral50,
    labelDisable = Neutral40,
    labelReverse = Neutral100,

    backgroundDefault = Neutral10,
    backgroundSecondary = Neutral15,
    backgroundAlternative = Neutral20,
    backgroundStrong = Neutral0,
    backgroundSelected = Red300,

    dimmerDefault = DimmerDefault,
    dimmerSecondary = DimmerSecondary,

    borderDefault = Neutral40,
    borderSub = Neutral23,
    borderAlternative = Neutral22,
    borderStrong = Neutral40,
    borderActive = Neutral40,

    dangerText = Red600,
    dangerBase = Red500,
    dangerBorder = Red200,
    dangerSurface = Red100,
)

val LocalRirotiColors = staticCompositionLocalOf { RirotiLightColorScheme }
