package com.li_routi.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
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
                    style = if (selected) LiroutiTheme.typography.body3SemiBold else LiroutiTheme.typography.body3Medium,
                    color = if (selected) LiroutiTheme.colors.labelDefault else LiroutiTheme.colors.labelInfo,
                )
            }
        }
    }
}

@Composable
fun LiroutiLineTab(
    tabs: List<String>,
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(start = 20.dp, top = 14.dp, end = 20.dp),
        ) {
            tabs.forEachIndexed { index, title ->
                LiroutiLineTabItem(
                    text = title,
                    selected = index == selectedIndex,
                    onClick = { onTabSelected(index) },
                )
            }
        }
        LiroutiDivider(color = LiroutiTheme.colors.borderDefault)
    }
}

@Composable
private fun LiroutiLineTabItem(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    var textWidth by remember { mutableStateOf(0.dp) }

    Column(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(bottom = if (selected) 0.dp else 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = text,
            softWrap = false,
            onTextLayout = { textWidth = with(density) { it.size.width.toDp() } },
            style = if (selected) LiroutiTheme.typography.body2LongSemiBold else LiroutiTheme.typography.body2LongRegular,
            color = if (selected) LiroutiTheme.colors.labelDefault else LiroutiTheme.colors.labelInfo,
        )
        if (selected) {
            LiroutiDivider(
                modifier = Modifier.width(textWidth),
                thickness = LiroutiDividerThickness.Regular,
                color = LiroutiTheme.colors.labelDefault,
            )
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
