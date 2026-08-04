package com.li_routi.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.li_routi.core.designsystem.foundation.color.Neutral97
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme

@Composable
fun LiroutiTabButton(
    tabs: List<String>,
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(40.dp)
            .background(color = Neutral97, shape = RoundedCornerShape(6.dp))
            .padding(3.dp),
    ) {
        tabs.forEachIndexed { index, title ->
            val selected = index == selectedIndex
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .then(
                        if (selected) {
                            Modifier.shadow(elevation = 2.dp, shape = RoundedCornerShape(4.dp))
                        } else {
                            Modifier
                        },
                    )
                    .clip(RoundedCornerShape(if (selected) 4.dp else 8.dp))
                    .then(
                        if (selected) {
                            Modifier.background(LiroutiTheme.colors.backgroundDefault)
                        } else {
                            Modifier
                        },
                    )
                    .clickable(onClick = { onTabSelected(index) }),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = title,
                    style = if (selected) {
                        LiroutiTheme.typography.body3SemiBold
                    } else {
                        LiroutiTheme.typography.body3Medium
                    },
                    color = if (selected) LiroutiTheme.colors.labelDefault else LiroutiTheme.colors.labelInfo,
                )
            }
        }
    }
}

/**
 * @param equalWidth 탭이 정확히 몇 개뿐이고 화면 폭을 꽉 채워야 할 때(예: 챌린지 상세의 "인증"/"내 인증
 * 보기") true로 설정한다. 카테고리처럼 여러 개를 가로 스크롤하는 경우엔 기본값(false, 텍스트 너비만큼만
 * 차지)을 쓴다.
 */
@Composable
fun LiroutiLineTab(
    tabs: List<String>,
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    equalWidth: Boolean = false,
) {
    var textHeight by remember { mutableStateOf(22.dp) }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .padding(start = 20.dp, top = 14.dp, end = 20.dp)
                .selectableGroup(),
        ) {
            tabs.forEachIndexed { index, title ->
                LiroutiLineTabItem(
                    text = title,
                    selected = index == selectedIndex,
                    onClick = { onTabSelected(index) },
                    fullWidthIndicator = equalWidth,
                    textHeight = textHeight,
                    onTextHeightMeasured = { textHeight = it },
                    modifier = if (equalWidth) Modifier.weight(1f) else Modifier,
                )
            }
        }
        LiroutiDivider(color = LiroutiTheme.colors.borderDefault)
    }
}

private val CategoryDotSize = 9.dp

@Composable
private fun LiroutiLineTabItem(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    textHeight: Dp,
    onTextHeightMeasured: (Dp) -> Unit,
    modifier: Modifier = Modifier,
    fullWidthIndicator: Boolean = false,
) {
    val density = LocalDensity.current
    var textWidth by remember { mutableStateOf(0.dp) }

    Column(
        modifier = modifier
            .selectable(
                selected = selected,
                onClick = onClick,
                role = Role.Tab,
            )
            .then(if (!selected) Modifier.semantics { contentDescription = text } else Modifier)
            .padding(bottom = if (selected) 0.dp else 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (selected) {
            Text(
                text = text,
                softWrap = false,
                onTextLayout = {
                    textWidth = with(density) { it.size.width.toDp() }
                    onTextHeightMeasured(with(density) { it.size.height.toDp() })
                },
                style = LiroutiTheme.typography.body2Long.copy(fontWeight = FontWeight.Bold),
                color = LiroutiTheme.colors.labelDefault,
            )
            LiroutiDivider(
                modifier = if (fullWidthIndicator) Modifier.fillMaxWidth() else Modifier.width(textWidth),
                thickness = LiroutiDividerThickness.Regular,
                color = LiroutiTheme.colors.labelDefault,
            )
        } else {
            Box(
                modifier = Modifier
                    .padding(top = ((textHeight - CategoryDotSize) / 2).coerceAtLeast(0.dp))
                    .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .size(CategoryDotSize)
                        .border(width = 1.dp, color = Color(0xFF878A93), shape = CircleShape),
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LiroutiTabButtonPreview() {
    LiroutiFrontendTheme {
        var selectedIndex by remember { mutableIntStateOf(0) }
        LiroutiTabButton(
            tabs = listOf("오늘의 루틴", "그룹 루틴"),
            selectedIndex = selectedIndex,
            onTabSelected = { selectedIndex = it },
            modifier = Modifier.padding(16.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LiroutiLineTabPreview() {
    LiroutiFrontendTheme {
        var selectedIndex by remember { mutableIntStateOf(0) }
        LiroutiLineTab(
            tabs = listOf("카테고리", "카테고리", "카테고리", "카테고리"),
            selectedIndex = selectedIndex,
            onTabSelected = { selectedIndex = it },
        )
    }
}
