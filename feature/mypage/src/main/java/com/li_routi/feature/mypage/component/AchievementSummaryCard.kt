package com.li_routi.feature.mypage.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.li_routi.core.designsystem.R
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme

private val RepresentativeBadgeBackground = Color(0xFFF1F8E9)
private val RepresentativeBadgeText = Color(0xFF3A8009)
private val StatValueColor = Color(0xFF05141F)
private val StatLabelColor = Color(0xFF4A565E)

private val RepresentativeBadgeTextStyle = TextStyle(fontWeight = FontWeight.Bold, fontSize = 11.sp, lineHeight = 16.sp)
private val StatValueTextStyle = TextStyle(fontSize = 12.sp, lineHeight = 18.sp)
private val StatLabelTextStyle = TextStyle(fontSize = 11.sp, lineHeight = 16.sp)

data class AchievementStatUiModel(
    val label: String,
    val value: String,
    val icon: @Composable () -> Unit,
)

/** 획득 통계 지갑 아이콘. Figma `icon/ic_wallet_20px`(205:18237) 기준. */
@Composable
fun AchievementWalletIcon(modifier: Modifier = Modifier) {
    Image(painter = painterResource(id = R.drawable.ic_wallet_20px), contentDescription = null, modifier = modifier.size(20.dp))
}

/** 스페셜 통계 바코드 아이콘. Figma `icon/ic_barcode_20px`(205:18249) 기준. */
@Composable
fun AchievementBarcodeIcon(modifier: Modifier = Modifier) {
    Box(modifier = modifier.size(20.dp), contentAlignment = Alignment.Center) {
        Image(painter = painterResource(id = R.drawable.ic_barcode_20px), contentDescription = null)
    }
}

/**
 * 진행 중 통계 쿠폰 아이콘. Figma `icon/ic_coupon_20px`(205:18243) 기준 — 티켓 외곽선 위에
 * 작은 천공 마크를 겹쳐 그린다(단일 drawable로 합치기엔 서로 다른 좌표계라 Box+offset으로 조합).
 */
@Composable
fun AchievementCouponIcon(modifier: Modifier = Modifier) {
    Box(modifier = modifier.size(20.dp)) {
        Image(
            painter = painterResource(id = R.drawable.ic_coupon_outline),
            contentDescription = null,
            modifier = Modifier.offset(x = 1.25.dp, y = 3.dp),
        )
        Image(
            painter = painterResource(id = R.drawable.ic_coupon_marks),
            contentDescription = null,
            modifier = Modifier.offset(x = 7.17.dp, y = 7.41.dp),
        )
    }
}

/**
 * 업적 화면 상단 요약 카드. Figma node `205:18221`("wallet") 기준.
 *
 * 대표 배지 목록 + 캐릭터 일러스트 + 획득/진행 중/스페셜 통계 3개(구분선 포함)로 구성된다.
 */
@Composable
fun AchievementSummaryCard(
    representativeBadges: List<String>,
    highlightText: String,
    stats: List<AchievementStatUiModel>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(6.dp))
            .background(LiroutiTheme.colors.backgroundDefault, RoundedCornerShape(6.dp))
            .padding(vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                representativeBadges.forEach { badge ->
                    Box(
                        modifier = Modifier
                            .background(RepresentativeBadgeBackground, RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                    ) {
                        Text(text = badge, style = RepresentativeBadgeTextStyle, color = RepresentativeBadgeText)
                    }
                }
            }
            Text(text = highlightText, style = StatValueTextStyle, color = LiroutiTheme.colors.labelSub)
        }
        AchievementCharacterIcon(modifier = Modifier.size(width = 220.dp, height = 180.dp))
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            stats.forEachIndexed { index, stat ->
                if (index != 0) {
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(24.dp)
                            .background(LiroutiTheme.colors.borderSub),
                    )
                }
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    stat.icon()
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(text = stat.label, style = StatValueTextStyle, color = StatValueColor)
                        Text(text = stat.value, style = StatLabelTextStyle, color = StatLabelColor)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AchievementSummaryCardPreview() {
    LiroutiFrontendTheme {
        AchievementSummaryCard(
            representativeBadges = listOf("대표 배지", "대표 배지"),
            highlightText = "첫 인증!",
            stats = listOf(
                AchievementStatUiModel("획득", "8/36") { AchievementWalletIcon() },
                AchievementStatUiModel("진행 중", "12") { AchievementCouponIcon() },
                AchievementStatUiModel("스페셜", "1/4") { AchievementBarcodeIcon() },
            ),
            modifier = Modifier.padding(16.dp),
        )
    }
}
