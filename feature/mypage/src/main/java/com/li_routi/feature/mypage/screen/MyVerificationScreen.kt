package com.li_routi.feature.mypage.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.li_routi.core.designsystem.R
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme
import com.li_routi.feature.mypage.component.EditProfileTopBar
import com.li_routi.feature.mypage.component.MyVerificationCard
import com.li_routi.feature.mypage.component.MyVerificationCardUiModel
import com.li_routi.feature.mypage.component.ReportPeriodHeader

/**
 * "내 인증" 화면. Figma node `4201:12204`("내인증") 기준 — 마이페이지 "내 인증" 메뉴로 진입한다.
 *
 * 날짜 네비게이션(◀ 날짜 ▶) + 그 날 인증한 루틴 카드 목록으로 구성된다.
 */
@Composable
fun MyVerificationScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    dateLabel: String = "2026.09.02",
    verifications: List<MyVerificationCardUiModel> = SampleMyVerifications,
    onPreviousDateClick: () -> Unit = {},
    onNextDateClick: () -> Unit = {},
    onMenuClick: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(LiroutiTheme.colors.backgroundDefault),
    ) {
        EditProfileTopBar(
            title = "내인증",
            onBackClick = onBackClick,
            trailingContent = {
                Image(
                    painter = painterResource(id = R.drawable.overflow_menu__vertical),
                    contentDescription = "더보기",
                    modifier = Modifier
                        .size(20.dp)
                        .clickable(onClick = onMenuClick),
                    colorFilter = ColorFilter.tint(LiroutiTheme.colors.labelDefault),
                )
            },
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            ReportPeriodHeader(
                label = dateLabel,
                onPreviousClick = onPreviousDateClick,
                onNextClick = onNextDateClick,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
            verifications.forEach { item ->
                MyVerificationCard(item = item)
            }
        }
    }
}

private val SampleMyVerifications = listOf(
    MyVerificationCardUiModel(
        routineName = "내루틴·물 마시기",
        memo = "물 마시기 1일차 입니다~ 다들 열심히 하고 있지?",
        imageUrl = null,
        isPending = true,
    ),
    MyVerificationCardUiModel(
        routineName = "내루틴·물 마시기",
        memo = "물 마시기 1일차 입니다~ 다들 열심히 하고 있지?",
        imageUrl = null,
    ),
    MyVerificationCardUiModel(
        routineName = "내루틴·물 마시기",
        memo = "물 마시기 1일차 입니다~ 다들 열심히 하고 있지?",
        imageUrl = null,
    ),
)

@Preview(showBackground = true, heightDp = 900)
@Composable
private fun MyVerificationScreenPreview() {
    LiroutiFrontendTheme {
        MyVerificationScreen(onBackClick = {})
    }
}
