package com.li_routi.feature.mypage.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.li_routi.core.common.ui.calendar.LiroutiCalendarBottomSheet
import com.li_routi.core.designsystem.R
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme
import com.li_routi.feature.mypage.component.EditProfileTopBar
import com.li_routi.feature.mypage.component.MyVerificationCard
import com.li_routi.feature.mypage.component.MyVerificationCardUiModel
import com.li_routi.feature.mypage.component.PendingVerificationCard
import com.li_routi.feature.mypage.component.PendingVerificationCountLabel
import com.li_routi.feature.mypage.component.PendingVerificationUiModel
import com.li_routi.feature.mypage.component.ReportPeriodHeader
import com.li_routi.feature.mypage.component.SimpleDate
import com.li_routi.feature.mypage.component.plusDays
import com.li_routi.feature.mypage.component.toLocalDate
import com.li_routi.feature.mypage.component.toSimpleDate

/**
 * "내 인증" 화면. Figma node `4869:36868`("내인증") 기준 — 마이페이지 "내 인증" 메뉴로 진입한다.
 *
 * 날짜 네비게이션(◀ 날짜 ▶) + AI 검증 대기 중인 인증 가로 목록 + 그 날 확정된 루틴 인증 카드 목록으로
 * 구성된다. 날짜 라벨을 탭하면 [com.li_routi.core.common.ui.calendar.LiroutiCalendarBottomSheet](일자/월 선택)가 뜬다.
 *
 * 날짜 선택 상태는 `GET /api/members/me/verifications`를 다시 호출해야 해서(
 * [com.li_routi.feature.mypage.vm.MyVerificationViewModel]) 화면이 직접 들고 있지 않고
 * [selectedDate]/[onDateChange]로 끌어올렸다.
 */
@Composable
fun MyVerificationScreen(
    onBackClick: () -> Unit,
    selectedDate: SimpleDate,
    onDateChange: (SimpleDate) -> Unit,
    modifier: Modifier = Modifier,
    verifications: List<MyVerificationCardUiModel> = SampleMyVerifications,
    pendingVerifications: List<PendingVerificationUiModel> = SamplePendingVerifications,
    isLoading: Boolean = false,
    isError: Boolean = false,
    onMenuClick: () -> Unit = {},
) {
    var showDatePicker by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(LiroutiTheme.colors.backgroundDefault),
    ) {
        EditProfileTopBar(
            title = "내 인증",
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
        ReportPeriodHeader(
            label = selectedDate.toDisplayLabel(),
            onPreviousClick = { onDateChange(selectedDate.plusDays(-1)) },
            onNextClick = { onDateChange(selectedDate.plusDays(1)) },
            onLabelClick = { showDatePicker = true },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 16.dp),
        )

        if (isLoading) {
            Box(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = LiroutiTheme.colors.primaryNormal)
            }
        } else if (isError) {
            Box(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                MyVerificationEmptyState(message = "인증 기록을 불러오지 못했어요")
            }
        } else if (verifications.isEmpty() && pendingVerifications.isEmpty()) {
            Box(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                MyVerificationEmptyState()
            }
        } else {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(top = 20.dp, bottom = 20.dp)
                    .navigationBarsPadding(),
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                if (pendingVerifications.isNotEmpty()) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        PendingVerificationCountLabel(
                            count = pendingVerifications.size,
                            modifier = Modifier.padding(horizontal = 16.dp),
                        )
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            items(pendingVerifications) { item -> PendingVerificationCard(item = item) }
                        }
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .background(LiroutiTheme.colors.borderAlternative),
                    )
                }

                Column(
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                ) {
                    verifications.forEach { item -> MyVerificationCard(item = item) }
                }
            }
        }
    }

    if (showDatePicker) {
        LiroutiCalendarBottomSheet(
            initialDate = selectedDate.toLocalDate(),
            onDismissRequest = { showDatePicker = false },
            onDateSelected = { date -> onDateChange(date.toSimpleDate()) },
        )
    }
}

/** 선택한 날짜에 인증 기록이 없거나(성공+빈 응답) 조회에 실패했을 때 화면 가운데에 보여주는 상태. */
@Composable
private fun MyVerificationEmptyState(modifier: Modifier = Modifier, message: String = "이 날의 인증 기록이 없어요") {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            painter = painterResource(id = R.drawable.warning),
            contentDescription = null,
            modifier = Modifier.size(28.dp),
        )
        Text(
            text = message,
            style = LiroutiTheme.typography.body2LongMedium,
            color = LiroutiTheme.colors.labelInfo,
            modifier = Modifier.padding(top = 8.dp),
        )
    }
}

private val SampleMyVerifications = listOf(
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

private val SamplePendingVerifications = listOf(
    PendingVerificationUiModel(
        routineName = "내루틴·물 마시기",
        memo = "물 마시기 1일차 입니다~ 다들 열심히 하고 있지?",
        imageUrl = null,
        remainingTimeLabel = "약 30초 남음",
    ),
    PendingVerificationUiModel(
        routineName = "내루틴·물 마시기",
        memo = "물 마시기 1일차 입니다~ 다들 열심히 하고 있지?",
        imageUrl = null,
        remainingTimeLabel = "약 30초 남음",
    ),
    PendingVerificationUiModel(
        routineName = "내루틴·물 마시기",
        memo = "물 마시기 1일차 입니다~ 다들 열심히 하고 있지?",
        imageUrl = null,
        remainingTimeLabel = "약 30초 남음",
    ),
)

@Preview(showBackground = true, heightDp = 900, apiLevel = 34)
@Composable
private fun MyVerificationScreenPreview() {
    LiroutiFrontendTheme {
        MyVerificationScreen(onBackClick = {}, selectedDate = SimpleDate(2026, 9, 2), onDateChange = {})
    }
}

@Preview(showBackground = true, heightDp = 900, name = "대기 중인 인증 없음")
@Composable
private fun MyVerificationScreenNoPendingPreview() {
    LiroutiFrontendTheme {
        MyVerificationScreen(
            onBackClick = {},
            selectedDate = SimpleDate(2026, 9, 2),
            onDateChange = {},
            pendingVerifications = emptyList(),
        )
    }
}

@Preview(showBackground = true, heightDp = 900, name = "인증 기록 없음")
@Composable
private fun MyVerificationScreenEmptyPreview() {
    LiroutiFrontendTheme {
        MyVerificationScreen(
            onBackClick = {},
            selectedDate = SimpleDate(2026, 9, 2),
            onDateChange = {},
            verifications = emptyList(),
            pendingVerifications = emptyList(),
        )
    }
}

@Preview(showBackground = true, heightDp = 900, name = "로딩 중")
@Composable
private fun MyVerificationScreenLoadingPreview() {
    LiroutiFrontendTheme {
        MyVerificationScreen(
            onBackClick = {},
            selectedDate = SimpleDate(2026, 9, 2),
            onDateChange = {},
            verifications = emptyList(),
            pendingVerifications = emptyList(),
            isLoading = true,
        )
    }
}

@Preview(showBackground = true, heightDp = 900, name = "조회 실패")
@Composable
private fun MyVerificationScreenErrorPreview() {
    LiroutiFrontendTheme {
        MyVerificationScreen(
            onBackClick = {},
            selectedDate = SimpleDate(2026, 9, 2),
            onDateChange = {},
            verifications = emptyList(),
            pendingVerifications = emptyList(),
            isError = true,
        )
    }
}
