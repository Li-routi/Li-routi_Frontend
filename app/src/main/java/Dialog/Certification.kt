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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun LiroutiCardDialog(
    modifier: Modifier = Modifier,
    titleText: String = "새 인증",
    userName: String = "민지",
    detailInfoText: String = "상세 정보",
    subMessageText: String = "보조 설명 메세지 또는 상세 내용 작성란",
    totalPhotos: Int = 5,
    currentPhotoIndex: Int = 0,
    onPageSelected: (Int) -> Unit = {},
    onLeftButtonClick: () -> Unit = {},
    onRightButtonClick: () -> Unit = {}
) {

    Box(
        modifier = modifier
            .size(width = 320.dp, height = 422.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(top = 24.dp, bottom = 24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth()
                    .background(Color.Transparent),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = titleText,
                    fontSize = 18.sp,
                    fontWeight = FontWeight(700),
                    color = Color(0xFF121416)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))


            Column(
                modifier = Modifier
                    .size(width = 272.dp, height = 184.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 272.dp, height = 160.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF7F7F8)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "사진 영역 (272x160)",
                        fontSize = 13.sp,
                        color = Color(0xFF8A919E)
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    LiroutiInstagramIndicator(
                        totalCount = totalPhotos,
                        currentPage = currentPhotoIndex,
                        onPageSelected = onPageSelected
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))


            Box(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth()
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Text(
                            text = userName,
                            fontSize = 16.sp,
                            fontWeight = FontWeight(700),
                            color = Color(0xFF171719)
                        )


                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .size(width = 1.dp, height = 12.dp)
                                .background(Color(0xFFD9D9D9))
                        )


                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = detailInfoText,
                            fontSize = 16.sp,
                            fontWeight = FontWeight(700),
                            color = Color(0xFF171719)
                        )
                    }


                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(22.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = subMessageText,
                            fontSize = 14.sp,
                            color = Color(0xFF46474C),
                            fontWeight = FontWeight.Medium
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


@Preview(showBackground = false)
@Composable
private fun PureLiroutiCardDialogPreview() {
    var currentPage by remember { mutableIntStateOf(0) }

    LiroutiCardDialog(
        titleText = "새 인증",
        userName = "민지",
        detailInfoText = "물 마시기",
        subMessageText = "오늘도 루틴을 성공하셨네요! 멋져요.",
        totalPhotos = 7,
        currentPhotoIndex = currentPage,
        onPageSelected = { newPage ->
            currentPage = newPage
        },
        onLeftButtonClick = { },
        onRightButtonClick = { }
    )
}