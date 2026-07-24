package com.example.ri_routi

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


enum class NavTab {
    HOME, GROUP_ROUTINE, CHALLENGE, MY
}


@Composable
fun BottomNavigationBar(
    modifier: Modifier = Modifier,
    onTabSelected: (NavTab) -> Unit = {}
) {

    var selectedTab by remember { mutableStateOf(NavTab.HOME) }


    Box(
        modifier = modifier
            .size(width = 360.dp, height = 80.dp)
            .background(Color.White)
    ) {

        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .size(width = 360.dp, height = 48.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.Top
        ) {

            NavItem(
                activeIconRes = R.drawable.home__active,
                defaultIconRes = R.drawable.home__default,
                label = "홈",
                isSelected = selectedTab == NavTab.HOME,
                onClick = {
                    selectedTab = NavTab.HOME
                    onTabSelected(NavTab.HOME)
                }
            )


            NavItem(
                activeIconRes = R.drawable.group__active,
                defaultIconRes = R.drawable.group__default,
                label = "그룹 루틴",
                isSelected = selectedTab == NavTab.GROUP_ROUTINE,
                onClick = {
                    selectedTab = NavTab.GROUP_ROUTINE
                    onTabSelected(NavTab.GROUP_ROUTINE)
                }
            )


            NavItem(
                activeIconRes = R.drawable.medal__active,
                defaultIconRes = R.drawable.medal__default,
                label = "챌린지",
                isSelected = selectedTab == NavTab.CHALLENGE,
                onClick = {
                    selectedTab = NavTab.CHALLENGE
                    onTabSelected(NavTab.CHALLENGE)
                }
            )


            NavItem(
                activeIconRes = R.drawable.ic_my__active,
                defaultIconRes = R.drawable.ic_my__default,
                label = "마이",
                isSelected = selectedTab == NavTab.MY,
                onClick = {
                    selectedTab = NavTab.MY
                    onTabSelected(NavTab.MY)
                }
            )
        }
    }
}

/**
 * 90x48 개별 탭 아이템
 */
@Composable
private fun NavItem(
    @DrawableRes activeIconRes: Int,
    @DrawableRes defaultIconRes: Int,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val activeColor = Color(0xFF296ECC)
    val defaultColor = Color(0xFF878A93)

    val currentIconRes = if (isSelected) activeIconRes else defaultIconRes
    val currentTextColor = if (isSelected) activeColor else defaultColor

    // 90x48 박스
    Box(
        modifier = Modifier
            .size(width = 90.dp, height = 48.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null // 클릭 시 물결 리플 효과 제거
            ) { onClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 4.dp), // 상단 여백 4dp
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // 24x24 아이콘 (좌우 여백 33dp 계산: 90 - 33*2 = 24)
            Image(
                painter = painterResource(id = currentIconRes),
                contentDescription = label,
                modifier = Modifier.size(24.dp)
            )

            // 아이콘 밑 4dp 간격 후, 글씨 길이에 따라 유동적으로 크기가 조절되는 글 상자
            Box(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .wrapContentSize(), // 고정 width/height 대신 wrapContentSize 사용
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    fontSize = 14.sp, // 14sp Asta Sans 적용
                    fontWeight = FontWeight.Medium,
                    color = currentTextColor,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFEEEEEE)
@Composable
fun BottomNavigationBarPreview() {
    BottomNavigationBar()
}