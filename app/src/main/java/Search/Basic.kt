package com.example.ri_routi

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp



@Composable
fun SearchInputFieldBasic(
    modifier: Modifier = Modifier,
    isDisabled: Boolean = false,
    onSearchSubmitted: (String) -> Unit = {}
) {
    if (isDisabled) {

        SearchDisabled(
            modifier = modifier,
            placeholderText = "Disabled"
        )
    } else {

        SearchInputFieldActive(
            modifier = modifier,
            onSearchSubmitted = onSearchSubmitted
        )
    }
}


@Composable
private fun SearchInputFieldActive(
    modifier: Modifier = Modifier,
    onSearchSubmitted: (String) -> Unit = {}
) {
    var text by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current


    Box(
        modifier = modifier
            .size(width = 360.dp, height = 44.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFFFFFFF))
            .border(
                width = 1.dp,
                color = Color(0xFFDBDCDF),
                shape = RoundedCornerShape(8.dp)
            )
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(10.dp))


            Icon(
                painter = painterResource(id = R.drawable.search),
                contentDescription = "검색 아이콘",
                modifier = Modifier.size(20.dp),
                tint = Color.Unspecified
            )

            Spacer(modifier = Modifier.width(10.dp))


            Box(
                modifier = Modifier
                    .size(width = 298.dp, height = 20.dp),
                contentAlignment = Alignment.CenterStart
            ) {

                BasicTextField(
                    value = text,
                    onValueChange = { text = it },
                    singleLine = true,
                    textStyle = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF121416)
                    ),
                    cursorBrush = SolidColor(Color(0xFF2B66F6)), // 커서(|) 색상
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            onSearchSubmitted(text)
                            focusManager.clearFocus()
                        }
                    ),
                    modifier = Modifier.fillMaxSize(),
                    decorationBox = { innerTextField ->
                        Box(contentAlignment = Alignment.CenterStart) {

                            if (text.isEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .size(width = 43.dp, height = 22.dp),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    Text(
                                        text = "Search",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Normal,
                                        color = Color(0xFF878A93)
                                    )
                                }
                            }
                            innerTextField()
                        }
                    }
                )
            }
        }
    }
}

// --- Preview ---

@Preview(showBackground = false, name = "Active 상태")
@Composable
private fun SearchInputFieldBasicActivePreview() {
    SearchInputFieldBasic(isDisabled = false)
}

@Preview(showBackground = false, name = "Disabled 상태 (Disabled 파일 호출)")
@Composable
private fun SearchInputFieldBasicDisabledPreview() {
    SearchInputFieldBasic(isDisabled = true)
}