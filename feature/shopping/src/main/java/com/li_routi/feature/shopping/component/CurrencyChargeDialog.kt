package com.li_routi.feature.shopping.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.li_routi.core.designsystem.component.HorizontalDoubleButton
import com.li_routi.core.designsystem.component.LiroutiBadge
import com.li_routi.core.designsystem.component.LiroutiBadgeColor
import com.li_routi.core.designsystem.component.LiroutiDivider
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme

/** Figma 재화 충전 팝업 카드 폭 (화면 360 기준 좌우 여백 반영). */
private val ChargeDialogWidth = 320.dp

private val ChargeDialogCorner = RoundedCornerShape(12.dp)
private val ChargeDialogContentCorner = RoundedCornerShape(8.dp)

/**
 * 재화 충전 확인 팝업 (Figma "재화 충전 팝업").
 *
 * 런타임은 [Dialog] Window로 띄운다. Preview에서는 Window가 그려지지 않는 경우가 많아
 * [CurrencyChargeDialogContent]를 화면 위 오버레이로 확인한다.
 */
@Composable
fun CurrencyChargeDialog(
    product: CurrencyProductUiModel,
    onDismiss: () -> Unit,
    onConfirmCharge: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        CurrencyChargeDialogContent(
            product = product,
            onDismiss = onDismiss,
            onConfirmCharge = onConfirmCharge,
        )
    }
}

@Composable
fun CurrencyChargeDialogContent(
    product: CurrencyProductUiModel,
    onDismiss: () -> Unit,
    onConfirmCharge: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .width(ChargeDialogWidth)
            .background(
                color = LiroutiTheme.colors.backgroundDefault,
                shape = ChargeDialogCorner,
            )
            .padding(20.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = LiroutiTheme.colors.backgroundAlternative,
                    shape = ChargeDialogContentCorner,
                )
                .padding(16.dp),
        ) {
            // softWrap=false: 한글이 "550코"/"인"으로 줄바꿈되지 않도록 Figma처럼 한 줄 고정
            Text(
                text = product.chargeTitle,
                modifier = Modifier.fillMaxWidth(),
                style = LiroutiTheme.typography.heading2.copy(
                    lineBreak = LineBreak.Simple,
                ),
                color = LiroutiTheme.colors.labelStrong,
                softWrap = false,
                maxLines = 1,
                overflow = TextOverflow.Clip,
            )
            if (product.bonusLabel != null) {
                LiroutiBadge(
                    text = product.bonusLabel,
                    color = LiroutiBadgeColor.Orange,
                    modifier = Modifier.padding(top = 12.dp),
                )
            }
            LiroutiDivider(
                color = LiroutiTheme.colors.borderSub,
                modifier = Modifier.padding(vertical = 12.dp),
            )
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
                    softWrap = false,
                    maxLines = 1,
                )
            }
        }

        // 카드 내부 폭(320 - 40 padding)에 맞춤. DS 기본 345dp는 팝업에서 넘친다.
        HorizontalDoubleButton(
            modifier = Modifier.padding(top = 20.dp),
            leftLabel = "취소",
            rightLabel = "충전하기",
            containerWidth = ChargeDialogWidth - 40.dp,
            onLeftClick = onDismiss,
            onRightClick = onConfirmCharge,
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0x99000000, name = "충전 팝업 단독")
@Composable
private fun CurrencyChargeDialogContentPreview() {
    LiroutiFrontendTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .background(Color.Black.copy(alpha = 0.45f)),
            contentAlignment = Alignment.Center,
        ) {
            CurrencyChargeDialogContent(
                product = SampleCurrencyProducts[2],
                onDismiss = {},
                onConfirmCharge = {},
            )
        }
    }
}
