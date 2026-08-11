package com.li_routi.feature.home.shop.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.li_routi.core.designsystem.component.LiroutiDivider
import com.li_routi.core.designsystem.component.LiroutiDividerThickness
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme

/**
 * 재화 상점용 Line Tab (Figma node `2305:14471`).
 *
 * DS [com.li_routi.core.designsystem.component.LiroutiLineTab]은 텍스트 너비만 쓰지만,
 * Figma 재화 탭은 각 탭이 가로를 균등 분할(flex-1)한다. DS를 수정하지 않고 shopping에서만 맞춘다.
 */
@Composable
fun CurrencyShopLineTab(
    tabs: List<String>,
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, top = 14.dp, end = 20.dp),
        ) {
            tabs.forEachIndexed { index, title ->
                CurrencyShopLineTabItem(
                    text = title,
                    selected = index == selectedIndex,
                    onClick = { onTabSelected(index) },
                    modifier = Modifier.weight(1f),
                )
            }
        }
        LiroutiDivider(color = LiroutiTheme.colors.borderDefault)
    }
}

@Composable
private fun CurrencyShopLineTabItem(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
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
            style = LiroutiTheme.typography.body2Long.copy(
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            ),
            color = if (selected) LiroutiTheme.colors.labelDefault else LiroutiTheme.colors.labelInfo,
        )
        if (selected) {
            LiroutiDivider(
                modifier = Modifier.fillMaxWidth(),
                thickness = LiroutiDividerThickness.Regular,
                color = LiroutiTheme.colors.labelDefault,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CurrencyShopLineTabPreview() {
    LiroutiFrontendTheme {
        CurrencyShopLineTab(
            tabs = listOf("주황보석", "파란보석"),
            selectedIndex = 0,
            onTabSelected = {},
        )
    }
}
