package com.li_routi.feature.challenge.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.li_routi.core.designsystem.R
import com.li_routi.core.designsystem.component.LiroutiAvatar
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme
import com.li_routi.feature.challenge.vm.CertificationUiModel

/**
 * 인증 게시글 카드 한 건 (Figma "Certification_IMG", node 2372:49703 / 2372:39010).
 * 챌린지 상세 화면의 인증 목록에서 백엔드 응답을 그대로 매핑해 쓸 수 있도록 화면에서 분리했다.
 */
@Composable
fun CertificationCard(
    certification: CertificationUiModel,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            // 작성자 프로필 이미지(비-DS 이미지 자산) — 실제 에셋은 백엔드에서 제공, 지금은 자리만
            LiroutiAvatar(size = 32.dp)
            Text(
                text = certification.authorName,
                style = LiroutiTheme.typography.body2LongSemiBold,
                color = LiroutiTheme.colors.labelDefault,
                modifier = Modifier.weight(1f),
            )
            // 게시글별 수정/삭제/신고 메뉴는 이후 단계에서 연결 — 지금은 아이콘만
            Image(
                painter = painterResource(id = R.drawable.overflow_menu__vertical),
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                colorFilter = ColorFilter.tint(LiroutiTheme.colors.labelInfo),
            )
        }
        Text(
            text = certification.content,
            style = LiroutiTheme.typography.body2LongRegular,
            color = LiroutiTheme.colors.labelDefault,
        )
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            // 인증 사진(비-DS 이미지 자산) — 실제 에셋은 백엔드에서 제공, 지금은 자리만
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(144.dp)
                    .background(LiroutiTheme.colors.backgroundSecondary, RoundedCornerShape(6.dp)),
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    Image(
                        painter = painterResource(id = R.drawable.favorite),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        colorFilter = ColorFilter.tint(LiroutiTheme.colors.labelDefault),
                    )
                    Text(
                        text = certification.likeCount.toString(),
                        style = LiroutiTheme.typography.body3Medium,
                        color = LiroutiTheme.colors.labelDefault,
                    )
                }
                Text(
                    text = certification.timeLabel,
                    style = LiroutiTheme.typography.body3Regular,
                    color = LiroutiTheme.colors.labelInfo,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CertificationCardPreview() {
    LiroutiFrontendTheme {
        CertificationCard(
            certification = CertificationUiModel(
                id = 1L,
                authorName = "민지",
                content = "물 마시기 1일차 입니다~ 다들 열심히 하고 있지?",
                likeCount = 1,
                timeLabel = "9시간 전",
            ),
        )
    }
}
