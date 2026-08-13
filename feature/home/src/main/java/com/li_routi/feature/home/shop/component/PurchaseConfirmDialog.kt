package com.li_routi.feature.home.shop.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.li_routi.core.designsystem.R
import com.li_routi.core.designsystem.component.LiroutiBottomSheetCloseButton
import com.li_routi.core.designsystem.foundation.color.Neutral96
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme

/**
 * 여러 아이템을 한 번에 구매하기 전 확인하는 다이얼로그 (Figma node `6008:28844`, `6008:25036`).
 *
 * 잔액이 모자라면 확인 버튼 대신 충전 화면으로 보내는 버튼을 보여준다.
 */
@Composable
fun PurchaseConfirmDialog(
    targets: List<ShopItemUiModel>,
    coinBalance: Int,
    gemBalance: Int,
    onRemoveItem: (String) -> Unit,
    onDismissRequest: () -> Unit,
    onConfirmPurchase: () -> Unit,
    onChargeClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val gemTotal = targets.filter { it.currency == "GEM" }.sumOf { it.price }
    val topazTotal = targets.filter { it.currency == "TOPAZ" }.sumOf { it.price }
    val isGemShort = gemTotal > gemBalance
    val isTopazShort = topazTotal > coinBalance
    val isShort = isGemShort || isTopazShort

    Dialog(onDismissRequest = onDismissRequest) {
        Box(
            modifier = modifier
                .width(320.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(LiroutiTheme.colors.backgroundDefault)
                .padding(24.dp),
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top,
                ) {
                    Column {
                        Text(
                            text = "구매하시겠어요?",
                            style = LiroutiTheme.typography.heading2SemiBold,
                            color = LiroutiTheme.colors.labelDefault,
                        )
                        Text(
                            text = "총 ${targets.size}개 선택됨",
                            style = LiroutiTheme.typography.body3Regular,
                            color = LiroutiTheme.colors.labelSub,
                        )
                    }
                    LiroutiBottomSheetCloseButton(onClick = onDismissRequest)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    targets.forEach { item ->
                        PurchaseTargetThumbnail(item = item, onRemoveClick = { onRemoveItem(item.id) })
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                BalanceRow(
                    iconRes = R.drawable.diamond_blue,
                    label = "보유 블루젬",
                    amount = gemBalance,
                )
                Spacer(modifier = Modifier.height(8.dp))
                BalanceRow(
                    iconRes = R.drawable.diamond_orange,
                    label = "보유 오렌지젬",
                    amount = coinBalance,
                )

                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(LiroutiTheme.colors.borderSub),
                )
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "결제 금액",
                        style = LiroutiTheme.typography.body1SemiBold,
                        color = LiroutiTheme.colors.labelDefault,
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (topazTotal > 0) {
                            PriceChip(iconRes = R.drawable.diamond_orange, amount = topazTotal)
                            if (gemTotal > 0) Spacer(modifier = Modifier.width(8.dp))
                        }
                        if (gemTotal > 0) {
                            PriceChip(iconRes = R.drawable.diamond_blue, amount = gemTotal)
                        }
                    }
                }

                if (isShort) {
                    Spacer(modifier = Modifier.height(12.dp))
                    val shortMessage = listOfNotNull(
                        if (isGemShort) "${gemTotal - gemBalance} 블루젬" else null,
                        if (isTopazShort) "${topazTotal - coinBalance} 오렌지젬" else null,
                    ).joinToString("・") + "이 더 필요해요"
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(LiroutiTheme.colors.dangerSurface)
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                    ) {
                        Text(
                            text = shortMessage,
                            style = LiroutiTheme.typography.body3SemiBold,
                            color = LiroutiTheme.colors.dangerText,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    DialogButton(
                        text = "취소",
                        backgroundColor = LiroutiTheme.colors.backgroundAlternative,
                        textColor = LiroutiTheme.colors.labelDefault,
                        onClick = onDismissRequest,
                        modifier = Modifier.weight(1f),
                    )
                    if (isShort) {
                        DialogButton(
                            text = "충전하러 가기",
                            backgroundColor = LiroutiTheme.colors.primaryNormal,
                            textColor = LiroutiTheme.colors.backgroundAlternative,
                            onClick = onChargeClick,
                            modifier = Modifier.weight(1f),
                        )
                    } else {
                        DialogButton(
                            text = "구매하기",
                            backgroundColor = LiroutiTheme.colors.primaryNormal,
                            textColor = LiroutiTheme.colors.backgroundAlternative,
                            onClick = onConfirmPurchase,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PurchaseTargetThumbnail(
    item: ShopItemUiModel,
    onRemoveClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.size(50.dp)) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(LiroutiTheme.colors.backgroundAlternative),
        ) {
            when {
                item.imageRes != null -> Image(
                    painter = painterResource(id = item.imageRes),
                    contentDescription = item.name,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                )

                !item.imageUrl.isNullOrBlank() -> AsyncImage(
                    model = item.imageUrl,
                    contentDescription = item.name,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                )
            }
        }
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(16.dp)
                .clip(CircleShape)
                .background(Neutral96)
                .clickable(onClick = onRemoveClick),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(id = R.drawable.close),
                contentDescription = "선택 해제",
                modifier = Modifier.size(8.dp),
            )
        }
    }
}

@Composable
private fun BalanceRow(
    iconRes: Int,
    label: String,
    amount: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = LiroutiTheme.typography.body2Regular,
            color = LiroutiTheme.colors.labelSub,
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                modifier = Modifier.size(16.dp),
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = amount.toString(),
                style = LiroutiTheme.typography.body2SemiBold,
                color = LiroutiTheme.colors.labelDefault,
            )
        }
    }
}

@Composable
private fun PriceChip(iconRes: Int, amount: Int, modifier: Modifier = Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            modifier = Modifier.size(16.dp),
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = amount.toString(),
            style = LiroutiTheme.typography.body1SemiBold,
            color = LiroutiTheme.colors.labelDefault,
        )
    }
}

@Composable
private fun DialogButton(
    text: String,
    backgroundColor: androidx.compose.ui.graphics.Color,
    textColor: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .height(44.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = text, style = LiroutiTheme.typography.body1SemiBold, color = textColor)
    }
}

@Preview(showBackground = true, name = "잔액 충분")
@Composable
private fun PurchaseConfirmDialogPreview() {
    LiroutiFrontendTheme {
        PurchaseConfirmDialog(
            targets = SampleShopItems.take(3),
            coinBalance = 5000,
            gemBalance = 5000,
            onRemoveItem = {},
            onDismissRequest = {},
            onConfirmPurchase = {},
            onChargeClick = {},
        )
    }
}

@Preview(showBackground = true, name = "잔액 부족")
@Composable
private fun PurchaseConfirmDialogShortPreview() {
    LiroutiFrontendTheme {
        PurchaseConfirmDialog(
            targets = SampleShopItems.take(3),
            coinBalance = 0,
            gemBalance = 0,
            onRemoveItem = {},
            onDismissRequest = {},
            onConfirmPurchase = {},
            onChargeClick = {},
        )
    }
}
