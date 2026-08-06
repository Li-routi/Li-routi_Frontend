package com.li_routi.feature.challenge.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.li_routi.core.designsystem.R
import com.li_routi.core.designsystem.component.LiroutiAvatar
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme
import com.li_routi.feature.challenge.vm.CertificationUiModel

/**
 * 인증 게시글 카드 한 건 (Figma "Certification_IMG", node 2372:39010 / 2372:49703).
 * 챌린지 상세 화면의 인증 목록에서 백엔드 응답(imageUrl, likeCount, liked 포함)을 그대로 매핑해
 * 쓸 수 있도록 화면에서 분리했다. 이미지는 정적 자리표시자가 아니라 게시글별 imageUrl을 실제로 그린다.
 *
 * [onLikeClick]이 null이면 좋아요 아이콘은 개수만 보여주고 탭할 수 없다 — "내 인증 보기" 응답에는
 * liked 여부가 내려오지 않아, 잘못된 상태로 토글되는 것을 막기 위해 그 탭에서는 null을 넘긴다.
 *
 * [onMoreClick]이 null이면 더보기 버튼 자체가 렌더링되지 않는다 — "인증"(전체) 탭에서 본인 글에는
 * 신고하기를 띄울 수 없으니 아예 버튼을 숨기기 위해 화면에서 null을 넘긴다.
 */
@Composable
fun CertificationCard(
    certification: CertificationUiModel,
    modifier: Modifier = Modifier,
    onMoreClick: (() -> Unit)? = null,
    onLikeClick: (() -> Unit)? = null,
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
            // 게시글 컨텍스트(내 글=수정하기 / 타인 글=신고하기)에 맞는 바텀시트를 화면에서 띄운다.
            if (onMoreClick != null) {
                Image(
                    painter = painterResource(id = R.drawable.overflow_menu__vertical),
                    contentDescription = "더보기",
                    modifier = Modifier
                        .size(20.dp)
                        .clickable(onClick = onMoreClick),
                    colorFilter = ColorFilter.tint(LiroutiTheme.colors.labelInfo),
                )
            }
        }
        Text(
            text = certification.content,
            style = LiroutiTheme.typography.body2LongRegular,
            color = LiroutiTheme.colors.labelDefault,
        )
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            if (certification.imageUrl.isNotBlank()) {
                AsyncImage(
                    model = certification.imageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                        .clip(RoundedCornerShape(6.dp)),
                )
            } else {
                // 인증 사진 자리표시자 — imageUrl이 비어있을 때(프리뷰 등)만 노출.
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                        .background(LiroutiTheme.colors.backgroundSecondary, RoundedCornerShape(6.dp)),
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    modifier = Modifier.let { base ->
                        onLikeClick?.let { base.clickable(onClick = it) } ?: base
                    },
                ) {
                    Image(
                        painter = painterResource(
                            id = if (certification.liked) R.drawable.favorite__filled else R.drawable.favorite,
                        ),
                        contentDescription = "좋아요",
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
                imageUrl = "",
                timeLabel = "9시간 전",
                likeCount = 1,
                liked = false,
                isMine = false,
            ),
            onMoreClick = {},
            onLikeClick = {},
        )
    }
}
