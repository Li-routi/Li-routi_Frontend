package com.li_routi.feature.challenge.component

import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.li_routi.core.designsystem.component.LiroutiRoutineDetailCard
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme

// "챌린지 찾아보기" 카드 한 건. 백엔드(Spring)에서 받아온 챌린지 목록을 이 모델로 매핑해서 넘긴다.
data class ChallengeCardUiModel(
    val id: Long,
    val title: String,
    val tagLabel: String,
    val description: String,
    val badge: String,
    val participantCount: Int,
    val rewardCount: Int,
    val postCount: Int,
)

/**
 * "챌린지 찾아보기" 목록의 카드 한 건 (Figma node 3610:30191). 화면(FindChallengeScreen)에서
 * 카드 마크업을 분리해뒀기 때문에, 백엔드 응답 매핑이 바뀌어도 여기만 고치면 된다.
 *
 * 설명 앞에 카테고리를 "카테고리 | 설명" 형태로 붙여 보여준다(타이틀 옆 별도 태그 표기는 없어짐).
 */
@Composable
fun ChallengeCard(
    challenge: ChallengeCardUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LiroutiRoutineDetailCard(
        title = challenge.title,
        subtitle = "${challenge.tagLabel} | ${challenge.description}",
        participants = challenge.participantCount.toString(),
        activity = challenge.rewardCount.toString(),
        activityLabel = "리워드",
        posts = challenge.postCount.toString(),
        badgeText = challenge.badge,
        showIcon = false,
        modifier = modifier.clickable(onClick = onClick),
    )
}

@Preview(showBackground = true)
@Composable
private fun ChallengeCardPreview() {
    LiroutiFrontendTheme {
        ChallengeCard(
            challenge = ChallengeCardUiModel(
                id = 1L,
                title = "매일 우유 한 잔",
                tagLabel = "건강",
                description = "매일 우유를 마시며 건강 관리를 해요",
                badge = "매일 루틴",
                participantCount = 300,
                rewardCount = 100,
                postCount = 80,
            ),
            onClick = {},
        )
    }
}
