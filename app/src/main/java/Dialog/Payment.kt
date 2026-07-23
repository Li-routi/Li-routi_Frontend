package com.example.ri_routi

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun LiroutiPaymentDialog(
    topText: String = "상단 텍스트 내용 들어가는 위치입니다.",
    tagText: String = "태그 텍스트",
    subText: String = "서브 텍스트",
    nextText: String = "옆 상자 텍스트",
    onLeftButtonClick: () -> Unit = {},
    onRightButtonClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .size(width = 320.dp, height = 277.dp)
            .background(Color.Transparent)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            Box(
                modifier = Modifier
                    .padding(start = 24.dp, top = 24.dp, end = 24.dp)
                    .size(width = 272.dp, height = 161.dp)
                    .background(Color(0xFFF7F7F8))
                    .padding(20.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(66.dp)
                    ) {
                        Text(
                            text = topText,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF121416),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.align(Alignment.TopStart)
                        )

                        // 63x20 #FFDDB8 둥근 태그
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(width = 63.dp, height = 20.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFFFFDDB8))
                                .align(Alignment.BottomStart)
                        ) {
                            Text(
                                text = tagText,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF121416)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))


                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Color(0xFFE5E5E5))
                    )

                    Spacer(modifier = Modifier.height(16.dp))


                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(22.dp)
                    ) {
                        Text(
                            text = subText,
                            fontSize = 12.sp,
                            color = Color(0xFF6E747A),
                            modifier = Modifier.padding(end = 8.dp)
                        )

                        Text(
                            text = nextText,
                            fontSize = 12.sp,
                            color = Color(0xFF121416)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))


            Horizontal_Sgment(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth(),
                onLeftClick = onLeftButtonClick,
                onRightClick = onRightButtonClick
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PaymentDialogPreview() {
    LiroutiPaymentDialog()
}