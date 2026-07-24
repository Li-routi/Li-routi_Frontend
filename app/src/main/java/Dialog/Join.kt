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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun LiroutiJoinDialog(
    title: String = "타이틀 텍스트",
    mainText: String = "메인 텍스트 내용",
    label1: String = "라벨1",
    value1: String = "값1",
    label2: String = "라벨2",
    value2: String = "값2",
    onLeftButtonClick: () -> Unit = {},
    onRightButtonClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .size(width = 320.dp, height = 346.dp)
            .background(Color.White)
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            Box(
                contentAlignment = Alignment.CenterStart,
                modifier = Modifier
                    .size(width = 272.dp, height = 28.dp)
                    .background(Color.Transparent)
            ) {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF121416),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(24.dp))


            Box(
                modifier = Modifier
                    .size(width = 272.dp, height = 178.dp)
                    .background(Color(0xFFF7F7F8), shape = RoundedCornerShape(8.dp))
                    .padding(20.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {

                    Icon(
                        painter = painterResource(id = R.drawable.avatar),
                        contentDescription = "Avatar",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))


                    Column(
                        modifier = Modifier
                            .size(width = 232.dp, height = 82.dp)
                            .background(Color.Transparent)
                    ) {

                        Box(
                            contentAlignment = Alignment.CenterStart,
                            modifier = Modifier.size(width = 232.dp, height = 24.dp)
                        ) {
                            Text(
                                text = mainText,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF121416),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))


                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.size(width = 232.dp, height = 22.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.CenterStart,
                                modifier = Modifier.size(width = 80.dp, height = 22.dp)
                            ) {
                                Text(
                                    text = label1,
                                    fontSize = 12.sp,
                                    color = Color(0xFF6E747A)
                                )
                            }
                            Box(
                                contentAlignment = Alignment.CenterStart,
                                modifier = Modifier.size(width = 40.dp, height = 22.dp)
                            ) {
                                Text(
                                    text = value1,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF121416)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))


                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.size(width = 232.dp, height = 22.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.CenterStart,
                                modifier = Modifier.size(width = 80.dp, height = 22.dp)
                            ) {
                                Text(
                                    text = label2,
                                    fontSize = 12.sp,
                                    color = Color(0xFF6E747A)
                                )
                            }
                            Box(
                                contentAlignment = Alignment.CenterStart,
                                modifier = Modifier.size(width = 40.dp, height = 22.dp)
                            ) {
                                Text(
                                    text = value2,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF121416)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))


            Horizontal_Sgment(
                modifier = Modifier.fillMaxWidth(),
                onLeftClick = onLeftButtonClick,
                onRightClick = onRightButtonClick
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun JoinDialogPreview() {
    LiroutiJoinDialog()
}