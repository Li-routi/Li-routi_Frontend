package com.li_routi.feature.challenge.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val BgDefault = Color(0xFFFFFFFF)
private val BorderDefault = Color(0xFFDBDCDF)
private val LabelInfo = Color(0xFF878A93)
private val PrimaryActive = Color(0xFF296ECC)

private data class ChallengeBottomNavTab(val label: String, val icon: ImageVector, val selected: Boolean)

// 홈/그룹 루틴/챌린지(선택됨)/마이 4탭. 이 모듈의 화면들은 항상 "챌린지" 탭 안에 있으므로 selectedIndex는 고정.
private val ChallengeBottomNavTabs = listOf(
    ChallengeBottomNavTab(label = "홈", icon = Icons.Default.Home, selected = false),
    ChallengeBottomNavTab(label = "그룹 루틴", icon = Icons.Default.Face, selected = false),
    ChallengeBottomNavTab(label = "챌린지", icon = Icons.Default.Star, selected = true),
    ChallengeBottomNavTab(label = "마이", icon = Icons.Default.Person, selected = false),
)

// Figma node: 2380:37477 ("Bottom GNB", property1=Challenge).
// 다른 탭(홈/그룹 루틴/마이)으로의 실제 이동은 앱 전체 내비게이션이 아직 연결되지 않아 이번 범위 제외 —
// 지금은 시각적으로만 보여준다. 챌린지 상세 화면에는 이 바가 없다(디자인상 노출 안 됨).
// TODO: 아이콘은 Design System 완성되면 실제 에셋으로 교체 (지금은 Material 아이콘으로 자리만)
@Composable
fun ChallengeBottomNavBar(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(BorderDefault),
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(BgDefault)
                .navigationBarsPadding()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            ChallengeBottomNavTabs.forEach { tab ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = tab.label,
                        tint = if (tab.selected) PrimaryActive else LabelInfo,
                        modifier = Modifier.size(24.dp),
                    )
                    Text(
                        text = tab.label,
                        fontSize = 10.sp,
                        color = if (tab.selected) PrimaryActive else LabelInfo,
                    )
                }
            }
        }
    }
}
