package com.example.ri_routi

import com.li_routi.core.designsystem.R
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// =============================================================================
// 1. Text Style Helpers & Text Label Components
// =============================================================================

@Composable
fun astaSansTextStyle(
    fontSize: TextUnit,
    fontWeight: FontWeight = FontWeight.Normal,
    lineHeight: TextUnit = TextUnit.Unspecified,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    isUnderline: Boolean = false
): TextStyle {
    return TextStyle(
        fontFamily = AstaSans,
        fontSize = fontSize,
        fontWeight = fontWeight,
        lineHeight = lineHeight,
        letterSpacing = letterSpacing,
        textDecoration = if (isUnderline) TextDecoration.Underline else TextDecoration.None,
        platformStyle = PlatformTextStyle(
            includeFontPadding = false
        ),
        lineHeightStyle = LineHeightStyle(
            alignment = LineHeightStyle.Alignment.Center,
            trim = LineHeightStyle.Trim.Both
        )
    )
}

@Composable
fun FlexibleEditableTextBox(
    text: String,
    style: TextStyle,
    color: Color,
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(4.dp),
    alignment: Alignment = Alignment.CenterStart
) {
    Box(
        contentAlignment = alignment,
        modifier = modifier
            .background(Color.Transparent)
            .padding(paddingValues)
    ) {
        Text(
            text = text,
            style = style.copy(color = color)
        )
    }
}

@Composable
fun TextLabelLargeActive(
    text: String = "Text Label",
    color: Color = Color(0xFF171719),
    style: TextStyle = astaSansTextStyle(fontSize = 20.sp, lineHeight = 28.sp),
    modifier: Modifier = Modifier
) {
    FlexibleEditableTextBox(
        text = text,
        style = style,
        color = color,
        modifier = modifier
    )
}

@Composable
fun TextLabelLargeSecondary(
    text: String = "Text Label",
    color: Color = Color(0xFF46474C),
    style: TextStyle = astaSansTextStyle(fontSize = 20.sp, lineHeight = 28.sp),
    modifier: Modifier = Modifier
) {
    FlexibleEditableTextBox(
        text = text,
        style = style,
        color = color,
        modifier = modifier
    )
}

@Composable
fun TextLabelLargeUnderline(
    text: String = "Text Label",
    color: Color = Color(0xFF878A93),
    style: TextStyle = astaSansTextStyle(fontSize = 20.sp, lineHeight = 28.sp, isUnderline = true),
    modifier: Modifier = Modifier
) {
    FlexibleEditableTextBox(
        text = text,
        style = style,
        color = color,
        modifier = modifier
    )
}

@Composable
fun TextLabelMediumActive(
    text: String = "Text Label",
    color: Color = Color(0xFF171719),
    style: TextStyle = astaSansTextStyle(fontSize = 16.sp, lineHeight = 24.sp),
    modifier: Modifier = Modifier
) {
    FlexibleEditableTextBox(
        text = text,
        style = style,
        color = color,
        modifier = modifier
    )
}

@Composable
fun TextLabelMediumSecondary(
    text: String = "Text Label",
    color: Color = Color(0xFF46474C),
    style: TextStyle = astaSansTextStyle(fontSize = 16.sp, lineHeight = 24.sp),
    modifier: Modifier = Modifier
) {
    FlexibleEditableTextBox(
        text = text,
        style = style,
        color = color,
        modifier = modifier
    )
}

@Composable
fun TextLabelMediumUnderline(
    text: String = "Text Label",
    color: Color = Color(0xFF878A93),
    style: TextStyle = astaSansTextStyle(fontSize = 16.sp, lineHeight = 24.sp, isUnderline = true),
    modifier: Modifier = Modifier
) {
    FlexibleEditableTextBox(
        text = text,
        style = style,
        color = color,
        modifier = modifier
    )
}

@Composable
fun TextLabelSmallActive(
    text: String = "Text Label",
    color: Color = Color(0xFF171719),
    style: TextStyle = astaSansTextStyle(fontSize = 14.sp, lineHeight = 22.sp),
    modifier: Modifier = Modifier
) {
    FlexibleEditableTextBox(
        text = text,
        style = style,
        color = color,
        modifier = modifier
    )
}

@Composable
fun TextLabelSmallSecondary(
    text: String = "Text Label",
    color: Color = Color(0xFF46474C),
    style: TextStyle = astaSansTextStyle(fontSize = 14.sp, lineHeight = 22.sp),
    modifier: Modifier = Modifier
) {
    FlexibleEditableTextBox(
        text = text,
        style = style,
        color = color,
        modifier = modifier
    )
}

@Composable
fun TextLabelSmallUnderline(
    text: String = "Text Label",
    color: Color = Color(0xFF878A93),
    style: TextStyle = astaSansTextStyle(fontSize = 14.sp, lineHeight = 22.sp, isUnderline = true),
    modifier: Modifier = Modifier
) {
    FlexibleEditableTextBox(
        text = text,
        style = style,
        color = color,
        modifier = modifier
    )
}

// =============================================================================
// 2. Custom Boxes Components
// =============================================================================

@Composable
fun AllCustomBoxes() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("123x36 Boxes", fontSize = 14.sp, color = Color.Gray)
        Box123x36Left(textColor = Color(0xFF171719), fontSize = 20.sp, underline = false)
        Box123x36Left(textColor = Color(0xFF46474C), fontSize = 20.sp, underline = false)
        Box123x36Left(textColor = Color(0xFF878A93), fontSize = 20.sp, underline = true)

        Spacer(modifier = Modifier.height(8.dp))

        Box123x36Right(textColor = Color(0xFF171719), fontSize = 20.sp, underline = false)
        Box123x36Right(textColor = Color(0xFF46474C), fontSize = 20.sp, underline = false)
        Box123x36Right(textColor = Color(0xFF878A93), fontSize = 20.sp, underline = true)

        Spacer(modifier = Modifier.height(24.dp))

        Text("105x32 Boxes", fontSize = 14.sp, color = Color.Gray)
        Box105x32Left(textColor = Color(0xFF171719), fontSize = 16.sp, underline = false)
        Box105x32Left(textColor = Color(0xFF46474C), fontSize = 16.sp, underline = false)
        Box105x32Left(textColor = Color(0xFF878A93), fontSize = 16.sp, underline = true)

        Spacer(modifier = Modifier.height(8.dp))

        Box105x32Right(textColor = Color(0xFF171719), fontSize = 16.sp, underline = false)
        Box105x32Right(textColor = Color(0xFF46474C), fontSize = 16.sp, underline = false)
        Box105x32Right(textColor = Color(0xFF878A93), fontSize = 16.sp, underline = true)

        Spacer(modifier = Modifier.height(24.dp))

        Text("87x28 Boxes", fontSize = 14.sp, color = Color.Gray)
        Box87x28Left(textColor = Color(0xFF171719), fontSize = 14.sp, underline = false)
        Box87x28Left(textColor = Color(0xFF46474C), fontSize = 14.sp, underline = false)
        Box87x28Left(textColor = Color(0xFF878A93), fontSize = 14.sp, underline = true)

        Spacer(modifier = Modifier.height(8.dp))

        Box87x28Right(textColor = Color(0xFF171719), fontSize = 14.sp, underline = false)
        Box87x28Right(textColor = Color(0xFF46474C), fontSize = 14.sp, underline = false)
        Box87x28Right(textColor = Color(0xFF878A93), fontSize = 14.sp, underline = true)
    }
}

@Composable
fun Box123x36Left(textColor: Color, fontSize: TextUnit, underline: Boolean) {
    Box(modifier = Modifier.size(width = 123.dp, height = 36.dp)) {
        Icon(
            painter = painterResource(id = R.drawable.chevron__left),
            contentDescription = null,
            tint = textColor,
            modifier = Modifier.size(24.dp).align(Alignment.CenterStart).offset(x = 4.dp)
        )
        Text(
            text = "Text Label",
            color = textColor,
            fontSize = fontSize,
            maxLines = 1,
            textDecoration = if (underline) TextDecoration.Underline else null,
            modifier = Modifier
                .height(28.dp)
                .align(Alignment.CenterEnd)
                .offset(x = (-4.dp))
        )
    }
}

@Composable
fun Box123x36Right(textColor: Color, fontSize: TextUnit, underline: Boolean) {
    Box(modifier = Modifier.size(width = 123.dp, height = 36.dp)) {
        Text(
            text = "Text Label",
            color = textColor,
            fontSize = fontSize,
            maxLines = 1,
            textDecoration = if (underline) TextDecoration.Underline else null,
            modifier = Modifier
                .height(28.dp)
                .align(Alignment.CenterStart)
                .offset(x = 4.dp)
        )
        Icon(
            painter = painterResource(id = R.drawable.chevron__right),
            contentDescription = null,
            tint = textColor,
            modifier = Modifier.size(20.dp).align(Alignment.CenterEnd).offset(x = (-4.dp))
        )
    }
}

@Composable
fun Box105x32Left(textColor: Color, fontSize: TextUnit, underline: Boolean) {
    Box(modifier = Modifier.size(width = 105.dp, height = 32.dp)) {
        Icon(
            painter = painterResource(id = R.drawable.chevron__left),
            contentDescription = null,
            tint = textColor,
            modifier = Modifier.size(16.dp).align(Alignment.CenterStart).offset(x = 4.dp)
        )
        Text(
            text = "Text Label",
            color = textColor,
            fontSize = fontSize,
            maxLines = 1,
            textDecoration = if (underline) TextDecoration.Underline else null,
            modifier = Modifier
                .height(24.dp)
                .align(Alignment.CenterEnd)
                .offset(x = (-4.dp))
        )
    }
}

@Composable
fun Box105x32Right(textColor: Color, fontSize: TextUnit, underline: Boolean) {
    Box(modifier = Modifier.size(width = 105.dp, height = 32.dp)) {
        Text(
            text = "Text Label",
            color = textColor,
            fontSize = fontSize,
            maxLines = 1,
            textDecoration = if (underline) TextDecoration.Underline else null,
            modifier = Modifier
                .height(24.dp)
                .align(Alignment.CenterStart)
                .offset(x = 4.dp)
        )
        Icon(
            painter = painterResource(id = R.drawable.chevron__right),
            contentDescription = null,
            tint = textColor,
            modifier = Modifier.size(24.dp).align(Alignment.CenterEnd).offset(x = (-4.dp))
        )
    }
}

@Composable
fun Box87x28Left(textColor: Color, fontSize: TextUnit, underline: Boolean) {
    Box(
        modifier = Modifier.size(width = 87.dp, height = 28.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.padding(start = 4.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier.size(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.chevron__left),
                    contentDescription = null,
                    tint = textColor,
                    modifier = Modifier.size(16.dp)
                )
            }
            Box(
                modifier = Modifier.size(width = 61.dp, height = 22.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = "TextLabel",
                    color = textColor,
                    fontSize = fontSize,
                    // 피그마 자간 -2.5% 적용 (-0.35.sp)
                    letterSpacing = (-0.35).sp,
                    maxLines = 1,
                    textDecoration = if (underline) TextDecoration.Underline else null
                )
            }
        }
    }
}

@Composable
fun Box87x28Right(textColor: Color, fontSize: TextUnit, underline: Boolean) {
    Box(
        modifier = Modifier.size(width = 87.dp, height = 28.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.padding(start = 4.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier.size(width = 61.dp, height = 22.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = "Text Label",
                    color = textColor,
                    fontSize = fontSize,
                    // 피그마 자간 -2.5% 적용 (-0.35.sp)
                    letterSpacing = (-0.35).sp,
                    maxLines = 1,
                    textDecoration = if (underline) TextDecoration.Underline else null
                )
            }
            Box(
                modifier = Modifier.size(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.chevron__right),
                    contentDescription = null,
                    tint = textColor,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

// =============================================================================
// Combined Preview
// =============================================================================

@Preview(showBackground = true, widthDp = 400, heightDp = 1400)
@Composable
fun ShowcaseAllComponentsPreview() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text("Text Label Showcase", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)

        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                TextLabelLargeActive()
                TextLabelLargeSecondary()
                TextLabelLargeUnderline()
            }
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                TextLabelMediumActive()
                TextLabelMediumSecondary()
                TextLabelMediumUnderline()
            }
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                TextLabelSmallActive()
                TextLabelSmallSecondary()
                TextLabelSmallUnderline()
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Custom Boxes Showcase", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)

        AllCustomBoxes()
    }
}