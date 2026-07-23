package com.example.ri_routi

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


enum class InputStatus {
    DEFAULT, SUCCESS, ERROR
}

@Composable
fun CustomInputField(
    modifier: Modifier = Modifier,

    value: String,
    onValueChange: (String) -> Unit,
    onEnterPressed: () -> Unit,
    status: InputStatus = InputStatus.DEFAULT,


    labelText: String = "Label",
    placeholderText: String = "Placeholder Text",
    defaultHelperText: String = "Helper Text",
    successHelperText: String = "Valid Message",
    errorHelperText: String = "Error Message",

    labelTextColor: Color = Color.Black,
    placeholderTextColor: Color = Color(0xFFDBDCDF),
    inputTextColor: Color = Color.Black,
    defaultBorderColor: Color = Color(0xFFDBDCDF),
    successBorderColor: Color = Color(0xFFDBDCDF),
    errorBorderColor: Color = Color(0xFFFF6363),

    defaultHelperColor: Color = Color.Gray,
    successHelperColor: Color = Color(0xFF00AAD2),
    errorHelperColor: Color = Color(0xFFFF6363),


    containerWidth: Dp = 360.dp,
    containerHeight: Dp = 96.dp,
    labelHeight: Dp = 22.dp,
    inputHeight: Dp = 44.dp,
    helperHeight: Dp = 18.dp,
    topToInputSpacing: Dp = 8.dp,
    inputToHelperSpacing: Dp = 4.dp,
    cornerRadius: Dp = 8.dp,


    fontFamily: FontFamily = FontFamily.Default
) {

    val borderColor = when (status) {
        InputStatus.DEFAULT -> defaultBorderColor
        InputStatus.SUCCESS -> successBorderColor
        InputStatus.ERROR -> errorBorderColor
    }

    val helperText = when (status) {
        InputStatus.DEFAULT -> defaultHelperText
        InputStatus.SUCCESS -> successHelperText
        InputStatus.ERROR -> errorHelperText
    }

    val helperTextColor = when (status) {
        InputStatus.DEFAULT -> defaultHelperColor
        InputStatus.SUCCESS -> successHelperColor
        InputStatus.ERROR -> errorHelperColor
    }


    Column(
        modifier = modifier
            .size(width = containerWidth, height = containerHeight)
            .background(Color.Transparent) // <-- 1. 컴포넌트 자체 투명
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(labelHeight),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = labelText,
                color = labelTextColor,
                fontSize = 14.sp,
                fontWeight = FontWeight(700),
                fontFamily = fontFamily
            )
        }

        Spacer(modifier = Modifier.height(topToInputSpacing)) // 8px 간격


        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = TextStyle(
                color = inputTextColor,
                fontSize = 14.sp,
                fontFamily = fontFamily
            ),
            cursorBrush = SolidColor(inputTextColor),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { onEnterPressed() }),
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(inputHeight)
                        .border(
                            width = 1.dp,
                            color = borderColor,
                            shape = RoundedCornerShape(cornerRadius)
                        )
                        .padding(horizontal = 12.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (value.isEmpty()) {
                        Text(
                            text = placeholderText,
                            color = placeholderTextColor,
                            fontSize = 14.sp,
                            fontFamily = fontFamily
                        )
                    }
                    innerTextField()
                }
            }
        )

        Spacer(modifier = Modifier.height(inputToHelperSpacing)) // 4px 간격


        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(helperHeight),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = helperText,
                color = helperTextColor,
                fontSize = 12.sp,
                fontWeight = FontWeight(400),
                fontFamily = fontFamily
            )
        }
    }
}


@Preview(showBackground = false, name = "Custom Input Field All States") // <-- 2. 프리뷰 자체 배경 비활성화
@Composable
fun CustomInputFieldPreview() {
    Surface(
        modifier = Modifier.padding(16.dp),
        color = Color.Transparent // <-- 3. Surface 배경색을 투명으로 설정
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            CustomInputField(
                value = "",
                onValueChange = {},
                onEnterPressed = {},
                status = InputStatus.DEFAULT,
                labelText = "Label"
            )


            CustomInputField(
                value = "Completed",
                onValueChange = {},
                onEnterPressed = {},
                status = InputStatus.SUCCESS,
                labelText = "Label"
            )


            CustomInputField(
                value = "Wrong Input",
                onValueChange = {},
                onEnterPressed = {},
                status = InputStatus.ERROR,
                labelText = "Label"
            )
        }
    }
}