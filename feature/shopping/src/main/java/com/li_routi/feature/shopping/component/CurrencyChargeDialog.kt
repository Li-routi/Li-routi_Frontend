package com.li_routi.feature.shopping.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.li_routi.core.designsystem.component.DsPlaceholder
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme

/**
 * 재화 충전 확인 팝업 (Figma "재화 충전 팝업").
 *
 * 선택된 보석/코인 상품을 한 번 더 탭했을 때 표시한다.
 * 취소 → [onDismiss], 충전하기 → [onConfirmCharge].
 *
 * 하단 버튼은 Design System `Button` instance라 [DsPlaceholder]로 두고 clickable만 연결한다.
 * 상품명/보너스/결제 금액 텍스트는 실제 구현한다.
 */
@Composable
fun CurrencyChargeDialog(
    product: CurrencyProductUiModel,
    onDismiss: () -> Unit,
    onConfirmCharge: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(LiroutiTheme.colors.backgroundDefault)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(LiroutiTheme.colors.backgroundAlternative)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = product.chargeTitle,
                    style = LiroutiTheme.typography.heading2,
                    color = LiroutiTheme.colors.labelStrong,
                )
                if (product.bonusLabel != null) {
                    // TODO(design-system): Badge 완성 시 실제 Badge로 교체
                    Text(
                        text = product.bonusLabel,
                        style = LiroutiTheme.typography.caption,
                        color = LiroutiTheme.colors.dangerText,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(LiroutiTheme.colors.dangerSurface)
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                    )
                }
                HorizontalDivider(color = LiroutiTheme.colors.borderSub)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "결제 금액",
                        style = LiroutiTheme.typography.body2,
                        color = LiroutiTheme.colors.labelSub,
                    )
                    Text(
                        text = product.paymentAmount,
                        style = LiroutiTheme.typography.body2,
                        color = LiroutiTheme.colors.labelStrong,
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                DsPlaceholder(
                    componentName = "Button/취소",
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .clickable(onClick = onDismiss),
                )
                DsPlaceholder(
                    componentName = "Button_Filled/충전하기",
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .clickable(onClick = onConfirmCharge),
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0x99000000)
@Composable
private fun CurrencyChargeDialogPreview() {
    LiroutiFrontendTheme {
        CurrencyChargeDialog(
            product = SampleCurrencyProducts[2],
            onDismiss = {},
            onConfirmCharge = {},
        )
    }
}
