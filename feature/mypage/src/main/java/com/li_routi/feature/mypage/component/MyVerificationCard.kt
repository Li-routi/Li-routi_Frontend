package com.li_routi.feature.mypage.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.li_routi.core.designsystem.component.LiroutiBadge
import com.li_routi.core.designsystem.component.LiroutiBadgeColor
import com.li_routi.core.designsystem.component.LiroutiBadgeSize
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme

private val CardImageHeight = 180.dp
private val PendingScrimColor = Color.Black.copy(alpha = 0.5f)

/** "내 인증" 목록 한 건. */
data class MyVerificationCardUiModel(
    val routineName: String,
    val memo: String,
    val imageUrl: String?,
    val isPending: Boolean = false,
)

/**
 * "내 인증" 카드 한 줄. Figma node `4224:46549`/`4421:51518`("Certification_IMG") 기준 —
 * 루틴명(+대기중 배지) + 메모 + 인증 이미지(180dp, Figma node `3610:30142` 인증 피드 사진 크기 기준)로 구성된다.
 *
 * [MyVerificationCardUiModel.isPending]인 카드는 승인 대기 중이라 이미지 위에 어두운 스크림을 덮는다
 * — Figma는 `backdrop-blur`도 함께 쓰지만 이 앱 minSdk(24)에서는 배경 블러를 구현할 방법이 마땅치
 * 않아([com.li_routi.feature.mypage.screen.AccountManageScreen] 참고) 스크림만 적용한다.
 */
@Composable
fun MyVerificationCard(
    item: MyVerificationCardUiModel,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = item.routineName,
                style = LiroutiTheme.typography.body3Regular.copy(lineHeight = 16.sp),
                color = LiroutiTheme.colors.labelInfo,
            )
            if (item.isPending) {
                LiroutiBadge(text = "대기중", color = LiroutiBadgeColor.Blue, size = LiroutiBadgeSize.XSmall)
            }
        }
        Text(
            text = item.memo,
            style = LiroutiTheme.typography.body2LongRegular,
            color = LiroutiTheme.colors.labelSub,
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(CardImageHeight)
                .clip(RoundedCornerShape(6.dp)),
        ) {
            if (item.imageUrl.isNullOrBlank()) {
                // 인증 사진 자리표시자 — imageUrl이 비어있을 때(프리뷰 등)만 노출.
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(LiroutiTheme.colors.backgroundSecondary),
                )
            } else {
                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
            }
            if (item.isPending) {
                Box(modifier = Modifier.fillMaxSize().background(PendingScrimColor))
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
)

@Preview(showBackground = true)
@Composable
private fun MyVerificationCardPreview() {
    LiroutiFrontendTheme {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            SampleMyVerifications.forEach { MyVerificationCard(item = it) }
        }
    }
}
