package com.li_routi.feature.shopping.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.li_routi.core.designsystem.component.DsPlaceholder
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme

/**
 * 재화 구매 리스트에 표시하는 상품 한 건 (Figma `List` instance).
 *
 * @param chargeTitle 충전 팝업에 표시할 상품명. 예: "550코인"
 * @param bonusLabel 충전 팝업 보너스 뱃지. 예: "+50 보너스". null이면 뱃지 숨김.
 * @param paymentAmount 충전 팝업 결제 금액. 예: "₩5,500"
 */
data class CurrencyProductUiModel(
    val id: String,
    val title: String,
    val subtitle: String,
    /** 가격 표기. 예: "10다이아"(재화 결제) 또는 "3,300"(원화 결제, [priceSuffix]="원"). */
    val price: String,
    val priceSuffix: String? = null,
    val isPopular: Boolean = false,
    val chargeTitle: String = title,
    val bonusLabel: String? = null,
    val paymentAmount: String = "",
)

/**
 * Preview/개발 확인용 샘플 데이터.
 * 실제 카피/금액은 백엔드/기획 확정 후 반영 필요.
 */
val SampleCurrencyProducts: List<CurrencyProductUiModel> = listOf(
    CurrencyProductUiModel(
        id = "currency_0",
        title = "100토파즈",
        subtitle = "Sub tit",
        price = "10다이아",
        chargeTitle = "100토파즈",
        paymentAmount = "10다이아",
    ),
    CurrencyProductUiModel(
        id = "currency_1",
        title = "Tit",
        subtitle = "Sub tit",
        price = "3,300",
        priceSuffix = "원",
        chargeTitle = "300코인",
        bonusLabel = "+30 보너스",
        paymentAmount = "₩3,300",
    ),
    CurrencyProductUiModel(
        id = "currency_2",
        title = "Tit",
        subtitle = "Sub tit",
        price = "5,500",
        priceSuffix = "원",
        chargeTitle = "550코인",
        bonusLabel = "+50 보너스",
        paymentAmount = "₩5,500",
    ),
    CurrencyProductUiModel(
        id = "currency_3",
        title = "Tit",
        subtitle = "Sub tit",
        price = "11,000",
        priceSuffix = "원",
        chargeTitle = "1,100코인",
        bonusLabel = "+100 보너스",
        paymentAmount = "₩11,000",
    ),
    CurrencyProductUiModel(
        id = "currency_4",
        title = "Tit",
        subtitle = "Sub tit",
        price = "33,000",
        priceSuffix = "원",
        isPopular = true,
        chargeTitle = "3,300코인",
        bonusLabel = "+300 보너스",
        paymentAmount = "₩33,000",
    ),
    CurrencyProductUiModel(
        id = "currency_5",
        title = "Tit",
        subtitle = "Sub tit",
        price = "55,000",
        priceSuffix = "원",
        isPopular = true,
        chargeTitle = "5,500코인",
        bonusLabel = "+500 보너스",
        paymentAmount = "₩55,000",
    ),
    CurrencyProductUiModel(
        id = "currency_6",
        title = "Tit",
        subtitle = "Sub tit",
        price = "110,000",
        priceSuffix = "원",
        isPopular = true,
        chargeTitle = "11,000코인",
        bonusLabel = "+1,000 보너스",
        paymentAmount = "₩110,000",
    ),
)

/**
 * 재화 구매 리스트 (Figma node `2305:13152`).
 *
 * 첫 탭 → [selectedProductId]에 파란 테두리(선택됨).
 * 이미 선택된 항목을 한 번 더 탭 → [onProductClick]에서 충전 팝업을 연다(ViewModel 처리).
 */
@Composable
fun CurrencyProductList(
    items: List<CurrencyProductUiModel>,
    selectedProductId: String?,
    onProductClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        items.forEach { item ->
            CurrencyProductRow(
                item = item,
                selected = item.id == selectedProductId,
                onClick = { onProductClick(item.id) },
            )
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
private fun CurrencyProductRow(
    item: CurrencyProductUiModel,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val borderColor = if (selected) {
        LiroutiTheme.colors.primaryNormal
    } else {
        LiroutiTheme.colors.borderAlternative
    }
    val borderWidth = if (selected) 1.5.dp else 1.dp

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(LiroutiTheme.colors.backgroundDefault)
            .border(
                border = BorderStroke(borderWidth, borderColor),
                shape = RoundedCornerShape(6.dp),
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            DsPlaceholder(
                componentName = "Diamond",
                modifier = Modifier.size(16.dp),
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.title,
                        style = LiroutiTheme.typography.body2,
                        color = LiroutiTheme.colors.labelStrong,
                    )
                    if (item.isPopular) {
                        Spacer(modifier = Modifier.width(6.dp))
                        DsPlaceholder(
                            componentName = "Badge",
                            modifier = Modifier.height(16.dp),
                        )
                    }
                }
                Text(
                    text = item.subtitle,
                    style = LiroutiTheme.typography.caption,
                    color = LiroutiTheme.colors.labelInfo,
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = item.price,
                    style = LiroutiTheme.typography.body2,
                    color = LiroutiTheme.colors.labelStrong,
                )
                if (item.priceSuffix != null) {
                    Text(
                        text = item.priceSuffix,
                        style = LiroutiTheme.typography.caption,
                        color = LiroutiTheme.colors.labelStrong,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, heightDp = 480)
@Composable
private fun CurrencyProductListPreview() {
    LiroutiFrontendTheme {
        CurrencyProductList(
            items = SampleCurrencyProducts,
            selectedProductId = "currency_2",
            onProductClick = {},
        )
    }
}
