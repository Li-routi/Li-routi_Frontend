package com.li_routi.feature.shopping.component

import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.foundation.Image
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.li_routi.core.designsystem.R
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme

/**
 * 상점/재화구매 화면 공용 상단 바 (Figma `Nav` variant `store`, node `2372:55357`).
 */
@Composable
fun ShopTopBar(
    title: String,
    coinBalance: Int,
    gemBalance: Int,
    onBackClick: () -> Unit,
    onCurrencyChipClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(id = R.drawable.chevron__left),
            contentDescription = "뒤로가기",
            modifier = Modifier
                .size(20.dp)
                .clickable(onClick = onBackClick),
            colorFilter = ColorFilter.tint(LiroutiTheme.colors.labelStrong),
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = title,
            style = LiroutiTheme.typography.heading2,
            color = LiroutiTheme.colors.labelStrong,
            modifier = Modifier.weight(1f),
        )
        CurrencyBalanceChip(
            iconResId = R.drawable.diamond_orange,
            balance = coinBalance,
            onClick = onCurrencyChipClick,
        )
        Spacer(modifier = Modifier.width(8.dp))
        CurrencyBalanceChip(
            iconResId = R.drawable.diamond_blue,
            balance = gemBalance,
            onClick = onCurrencyChipClick,
        )
    }
}

@Composable
private fun CurrencyBalanceChip(
    @androidx.annotation.DrawableRes iconResId: Int,
    balance: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .border(
                border = BorderStroke(1.dp, LiroutiTheme.colors.borderDefault),
                shape = RoundedCornerShape(20.dp),
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(id = iconResId),
            contentDescription = null,
            modifier = Modifier.size(16.dp),
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = balance.toString(),
            style = LiroutiTheme.typography.body2,
            color = LiroutiTheme.colors.labelSub,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ShopTopBarPreview() {
    LiroutiFrontendTheme {
        ShopTopBar(
            title = "상점",
            coinBalance = 450,
            gemBalance = 30,
            onBackClick = {},
        )
    }
}
