package com.example.ri_routi

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog


@Composable
fun LiroutiDefaultDialog(
    title: String = "제목",
    value: String,
    onValueChange: (String) -> Unit,
    onDismissRequest: () -> Unit = {},
    onLeftClick: () -> Unit = {},
    onRightClick: () -> Unit = {}
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Box(
            modifier = Modifier
                .size(width = 320.dp, height = 192.dp)
                .background(Color.White, shape = RectangleShape)
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // 상단 272x28 타이틀 및 Close 아이콘
                Box(
                    modifier = Modifier.size(width = 272.dp, height = 28.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF121416),
                        modifier = Modifier.align(Alignment.CenterStart)
                    )

                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .align(Alignment.TopEnd)
                            .clickable { onDismissRequest() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.close),
                            contentDescription = "Close",
                            tint = Color(0xFF121416),
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // 272x44 직접 입력창
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    singleLine = true,
                    textStyle = TextStyle(
                        fontSize = 14.sp,
                        color = Color(0xFF121416)
                    ),
                    modifier = Modifier
                        .size(width = 272.dp, height = 44.dp)
                        .background(Color.Transparent),
                    decorationBox = { innerTextField ->
                        Box(
                            contentAlignment = Alignment.CenterStart,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            if (value.isEmpty()) {
                                Text(
                                    text = "내용을 입력해주세요.",

                                    fontSize = 14.sp,
                                    color = Color.LightGray
                                )
                            }
                            innerTextField()
                        }
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 하단 대칭 버튼
                Horizontal_Sgment(
                    modifier = Modifier.fillMaxWidth(),
                    onLeftClick = onLeftClick,
                    onRightClick = onRightClick
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DefaultDialogPreview() {
    var text by remember { mutableStateOf("") }
    LiroutiDefaultDialog(
        title = "다이얼로그",
        value = text,
        onValueChange = { text = it }
    )
}