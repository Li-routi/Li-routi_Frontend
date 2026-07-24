package com.example.ri_routi

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp



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

    paddingValues: PaddingValues = PaddingValues(4.dp), // 피그마 패딩 (기본 4dp)

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







@Preview(showBackground = false, name = "Full Showcase")

@Composable

fun TextLabelShowcase() {

    Column(

        verticalArrangement = Arrangement.spacedBy(20.dp),

        modifier = Modifier

            .verticalScroll(rememberScrollState())

            .padding(24.dp)

    ) {

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

}