package com.li_routi.core.designsystem.component


import com.li_routi.core.designsystem.R
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog



@Composable
fun LiroutiCardDialog(
    modifier: Modifier = Modifier,
    titleText: String = "새 인증",
    userName: String = "민지",
    detailInfoText: String = "상세 정보",
    subMessageText: String = "보조 설명 메세지 또는 상세 내용 작성란",
    placeholderPhotoText: String = "사진 영역 (272x160)",
    totalPhotos: Int = 5,
    currentPhotoIndex: Int = 0,
    // Size & Shape
    dialogWidth: Dp = 320.dp,
    dialogHeight: Dp = 422.dp,
    cornerRadius: Dp = 16.dp,
    imageCornerRadius: Dp = 12.dp,
    // Colors
    backgroundColor: Color = Color.White,
    titleTextColor: Color = Color(0xFF121416),
    imageBackgroundColor: Color = Color(0xFFF7F7F8),
    imagePlaceholderTextColor: Color = Color(0xFF8A919E),
    primaryTextColor: Color = Color(0xFF171719),
    subTextColor: Color = Color(0xFF46474C),
    dividerColor: Color = Color(0xFFD9D9D9),
    onPageSelected: (Int) -> Unit = {},
    onLeftButtonClick: () -> Unit = {},
    onRightButtonClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .size(width = dialogWidth, height = dialogHeight)
            .clip(RoundedCornerShape(cornerRadius))
            .background(backgroundColor)
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
                    color = titleTextColor
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Column(
                modifier = Modifier.size(width = 272.dp, height = 184.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 272.dp, height = 160.dp)
                        .clip(RoundedCornerShape(imageCornerRadius))
                        .background(imageBackgroundColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = placeholderPhotoText,
                        fontSize = 13.sp,
                        color = imagePlaceholderTextColor
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
                            color = primaryTextColor
                        )

                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .size(width = 1.dp, height = 12.dp)
                                .background(dividerColor)
                        )

                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = detailInfoText,
                            fontSize = 16.sp,
                            fontWeight = FontWeight(700),
                            color = primaryTextColor
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
                            color = subTextColor,
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


@Composable
fun LiroutiDefaultDialog(
    modifier: Modifier = Modifier,
    title: String = "제목",
    value: String,
    placeholderText: String = "내용을 입력해주세요.",
    @DrawableRes closeIconRes: Int = R.drawable.close,
    dialogWidth: Dp = 320.dp,
    dialogHeight: Dp = 192.dp,
    shape: Shape = RectangleShape,
    backgroundColor: Color = Color.White,
    titleTextColor: Color = Color(0xFF121416),
    inputTextColor: Color = Color(0xFF121416),
    placeholderColor: Color = Color.LightGray,
    iconTintColor: Color = Color(0xFF121416),
    onValueChange: (String) -> Unit,
    onDismissRequest: () -> Unit = {},
    onLeftClick: () -> Unit = {},
    onRightClick: () -> Unit = {}
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Box(
            modifier = modifier
                .size(width = dialogWidth, height = dialogHeight)
                .background(backgroundColor, shape = shape)
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {

                Box(
                    modifier = Modifier.size(width = 272.dp, height = 28.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = titleTextColor,
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
                            painter = painterResource(id = closeIconRes),
                            contentDescription = "Close",
                            tint = iconTintColor,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    singleLine = true,
                    textStyle = TextStyle(
                        fontSize = 14.sp,
                        color = inputTextColor
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
                                    text = placeholderText,
                                    fontSize = 14.sp,
                                    color = placeholderColor
                                )
                            }
                            innerTextField()
                        }
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                Horizontal_Sgment(
                    modifier = Modifier.fillMaxWidth(),
                    onLeftClick = onLeftClick,
                    onRightClick = onRightClick
                )
            }
        }
    }
}



@Composable
fun LiroutiJoinDialog(
    modifier: Modifier = Modifier,
    title: String = "타이틀 텍스트",
    mainText: String = "메인 텍스트 내용",
    label1: String = "라벨1",
    value1: String = "값1",
    label2: String = "라벨2",
    value2: String = "값2",
    @DrawableRes avatarIconRes: Int = R.drawable.avatar,
    dialogWidth: Dp = 320.dp,
    dialogHeight: Dp = 346.dp,
    cardCornerRadius: Dp = 8.dp,
    backgroundColor: Color = Color.White,
    cardBackgroundColor: Color = Color(0xFFF7F7F8),
    titleTextColor: Color = Color(0xFF121416),
    mainTextColor: Color = Color(0xFF121416),
    labelTextColor: Color = Color(0xFF6E747A),
    valueTextColor: Color = Color(0xFF121416),
    onLeftButtonClick: () -> Unit = {},
    onRightButtonClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .size(width = dialogWidth, height = dialogHeight)
            .background(backgroundColor)
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
                    color = titleTextColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .size(width = 272.dp, height = 178.dp)
                    .background(cardBackgroundColor, shape = RoundedCornerShape(cardCornerRadius))
                    .padding(20.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        painter = painterResource(id = avatarIconRes),
                        contentDescription = "Avatar",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(40.dp)
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
                                color = mainTextColor,
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
                                    color = labelTextColor
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
                                    color = valueTextColor
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
                                    color = labelTextColor
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
                                    color = valueTextColor
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


@Composable
fun LiroutiPaymentDialog(
    modifier: Modifier = Modifier,
    topText: String = "상단 텍스트 내용 들어가는 위치입니다.",
    tagText: String = "태그 텍스트",
    subText: String = "서브 텍스트",
    nextText: String = "옆 상자 텍스트",
    dialogWidth: Dp = 320.dp,
    dialogHeight: Dp = 277.dp,
    tagCornerRadius: Dp = 4.dp,
    cardBackgroundColor: Color = Color(0xFFF7F7F8),
    topTextColor: Color = Color(0xFF121416),
    tagBackgroundColor: Color = Color(0xFFFFDDB8),
    tagTextColor: Color = Color(0xFF121416),
    dividerColor: Color = Color(0xFFE5E5E5),
    subTextColor: Color = Color(0xFF6E747A),
    nextTextColor: Color = Color(0xFF121416),
    onLeftButtonClick: () -> Unit = {},
    onRightButtonClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .size(width = dialogWidth, height = dialogHeight)
            .background(Color.Transparent)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .padding(start = 24.dp, top = 24.dp, end = 24.dp)
                    .size(width = 272.dp, height = 161.dp)
                    .background(cardBackgroundColor)
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
                            color = topTextColor,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.align(Alignment.TopStart)
                        )

                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(width = 63.dp, height = 20.dp)
                                .clip(RoundedCornerShape(tagCornerRadius))
                                .background(tagBackgroundColor)
                                .align(Alignment.BottomStart)
                        ) {
                            Text(
                                text = tagText,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = tagTextColor
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(dividerColor)
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
                            color = subTextColor,
                            modifier = Modifier.padding(end = 8.dp)
                        )

                        Text(
                            text = nextText,
                            fontSize = 12.sp,
                            color = nextTextColor
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


@Preview(showBackground = false, name = "Card Dialog")
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
        onPageSelected = { newPage -> currentPage = newPage },
        onLeftButtonClick = { },
        onRightButtonClick = { }
    )
}

@Preview(showBackground = true, name = "Default Dialog")
@Composable
private fun DefaultDialogPreview() {
    var text by remember { mutableStateOf("") }
    LiroutiDefaultDialog(
        title = "다이얼로그",
        value = text,
        onValueChange = { text = it }
    )
}

@Preview(showBackground = true, name = "Join Dialog")
@Composable
private fun JoinDialogPreview() {
    LiroutiJoinDialog()
}

@Preview(showBackground = true, name = "Payment Dialog")
@Composable
private fun PaymentDialogPreview() {
    LiroutiPaymentDialog()
}