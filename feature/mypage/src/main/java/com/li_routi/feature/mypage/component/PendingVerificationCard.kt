package com.li_routi.feature.mypage.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.li_routi.core.designsystem.foundation.color.ScrimMuted
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme

private val CardWidth = 192.dp
private val CardImageHeight = 144.dp
private val ScrimColor = ScrimMuted

/** AI 검증 대기 중인 인증 한 건. Figma node `4869:36895`("대기 중인 인증") 기준. */
data class PendingVerificationUiModel(
    val routineName: String,
    val memo: String,
    val imageUrl: String?,
    val remainingTimeLabel: String,
)

private val InfoLabelTextStyle = TextStyle(fontSize = 11.sp, lineHeight = 14.sp)
private val InfoMemoTextStyle = TextStyle(fontSize = 12.sp, lineHeight = 14.sp)
private val LoadingLabelTextStyle = TextStyle(fontSize = 11.sp, lineHeight = 14.sp)

/**
 * "대기 중인 인증" 가로 스크롤 목록 한 칸. 인증 사진 위에 스크림 + 로딩 스피너 + "Ai 검증 중" 문구를
 * 덮고, 하단에 루틴명/메모/남은 시간을 보여준다. Figma는 사진에 backdrop-blur도 함께 쓰지만
 * [MyVerificationCard]와 동일한 이유로 이 앱에서는 스크림만 적용한다.
 */
@Composable
fun PendingVerificationCard(
    item: PendingVerificationUiModel,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.width(CardWidth)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(CardImageHeight)
                .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp)),
        ) {
            if (item.imageUrl.isNullOrBlank()) {
                Box(modifier = Modifier.fillMaxSize().background(LiroutiTheme.colors.backgroundSecondary))
            } else {
                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    // 서명 URL 만료 등으로 로드 자체가 실패해도 빈 화면 대신 자리표시자를 보여준다.
                    error = ColorPainter(LiroutiTheme.colors.backgroundSecondary),
                    modifier = Modifier.fillMaxSize(),
                )
            }
            Box(modifier = Modifier.fillMaxSize().background(ScrimColor))
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = LiroutiTheme.colors.labelReverse,
                    strokeWidth = 2.dp,
                )
                Text(
                    text = "Ai 검증 중",
                    style = LoadingLabelTextStyle,
                    color = LiroutiTheme.colors.labelReverse,
                    modifier = Modifier.padding(top = 6.dp),
                )
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(LiroutiTheme.colors.backgroundAlternative, RoundedCornerShape(bottomStart = 6.dp, bottomEnd = 6.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp),
        ) {
            Text(text = item.routineName, style = InfoLabelTextStyle, color = LiroutiTheme.colors.labelInfo)
            // 메모가 없으면 빈 Text가 줄 높이 + 위 여백만큼 자리를 차지해 라벨들 사이가 붕 뜬다 —
            // 아예 렌더링을 건너뛴다([MyVerificationCard]와 동일한 이유).
            if (item.memo.isNotBlank()) {
                Text(
                    text = item.memo,
                    style = InfoMemoTextStyle,
                    color = LiroutiTheme.colors.labelSub,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
            Text(
                text = item.remainingTimeLabel,
                style = InfoLabelTextStyle,
                color = LiroutiTheme.colors.labelInfo,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 12.dp),
            )
        }
    }
}

private val CountLabelTextStyle = TextStyle(fontSize = 13.sp, lineHeight = 16.sp)

/**
 * "대기 중인 인증 N" 섹션 라벨. 라벨과 숫자의 폰트(한글/숫자)가 달라 글리프 메트릭이 달라지므로,
 * 단순 `Top` 정렬로는 숫자가 위로 붕 떠 보인다 — [alignByBaseline]으로 베이스라인을 맞춘다.
 */
@Composable
fun PendingVerificationCountLabel(count: Int, modifier: Modifier = Modifier) {
    Row(modifier = modifier) {
        Text(
            text = "대기 중인 인증",
            style = CountLabelTextStyle,
            color = LiroutiTheme.colors.labelInfo,
            modifier = Modifier.alignByBaseline(),
        )
        Text(
            text = " $count",
            style = CountLabelTextStyle,
            color = LiroutiTheme.colors.labelDefault,
            modifier = Modifier.alignByBaseline(),
        )
    }
}

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
)

@Preview(showBackground = true)
@Composable
private fun PendingVerificationCardPreview() {
    LiroutiFrontendTheme {
        Row {
            SamplePendingVerifications.forEach { PendingVerificationCard(item = it) }
        }
    }
}
