package com.li_routi.feature.home.shop.component

import androidx.compose.foundation.Image
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.li_routi.core.designsystem.R
import com.li_routi.core.designsystem.component.LiroutiBadge
import com.li_routi.core.designsystem.component.LiroutiBadgeColor
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme

/**
 * 재화 구매 리스트에 표시하는 상품 한 건 (Figma `List` instance).
 *
 * @param chargeTitle 충전 팝업에 표시할 상품명. 예: "550코인"
 * @param bonusLabel 충전 팝업 보너스 뱃지. 예: "+50 보너스". null이면 뱃지 숨김.
 * @param paymentAmount 충전 팝업 결제 금액. 예: "₩5,500"
 * @param priceSuffix 있으면 원화 결제(파란보석 탭). 예: "원". null이면 재화 결제(주황보석 탭).
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
) {
    /** 원화 결제(파란보석) 여부. */
    val isWonPayment: Boolean get() = priceSuffix != null
}

/** Preview/개발용 — 주황보석 탭 (재화로 결제). */
val SampleOrangeGemProducts: List<CurrencyProductUiModel> = listOf(
    CurrencyProductUiModel(
        id = "orange_0",
        title = "100토파즈",
        subtitle = "Sub tit",
        price = "3개",
        chargeTitle = "100토파즈",
        paymentAmount = "3개",
    ),
    CurrencyProductUiModel(
        id = "orange_1",
        title = "200토파즈",
        subtitle = "Sub tit",
        price = "5개",
        chargeTitle = "200토파즈",
        paymentAmount = "5개",
    ),
    CurrencyProductUiModel(
        id = "orange_2",
        title = "500토파즈",
        subtitle = "Sub tit",
        price = "10개",
        chargeTitle = "500토파즈",
        paymentAmount = "10개",
    ),
    CurrencyProductUiModel(
        id = "orange_3",
        title = "1100토파즈",
        subtitle = "Sub tit",
        price = "20개",
        chargeTitle = "1100토파즈",
        paymentAmount = "20개",
    ),
)

/**
 * Preview/개발용 — 파란보석 탭 (Figma `파란보석 탭` / 원화 결제).
 * 케이스: 기본 / 선택 / 인기 / 긴 제목 truncate.
 */
val SampleBlueGemProducts: List<CurrencyProductUiModel> = listOf(
    CurrencyProductUiModel(
        id = "blue_0",
        title = "Tit",
        subtitle = "Sub tit",
        price = "1,100",
        priceSuffix = "원",
        chargeTitle = "Tit",
        paymentAmount = "₩1,100",
    ),
    CurrencyProductUiModel(
        id = "blue_1",
        title = "Tit",
        subtitle = "Sub tit",
        price = "3,300",
        priceSuffix = "원",
        chargeTitle = "Tit",
        paymentAmount = "₩3,300",
    ),
    CurrencyProductUiModel(
        id = "blue_2",
        title = "Tit",
        subtitle = "Sub tit",
        price = "5,500",
        priceSuffix = "원",
        chargeTitle = "Tit",
        paymentAmount = "₩5,500",
    ),
    CurrencyProductUiModel(
        id = "blue_3",
        title = "Tit",
        subtitle = "Sub tit",
        price = "11,000",
        priceSuffix = "원",
        chargeTitle = "Tit",
        paymentAmount = "₩11,000",
    ),
    CurrencyProductUiModel(
        id = "blue_4",
        title = "Tit",
        subtitle = "Sub tit",
        price = "33,000",
        priceSuffix = "원",
        isPopular = true,
        chargeTitle = "Tit",
        paymentAmount = "₩33,000",
    ),
    CurrencyProductUiModel(
        id = "blue_5",
        title = "Tit",
        subtitle = "Sub tit",
        price = "55,000",
        priceSuffix = "원",
        isPopular = true,
        chargeTitle = "Tit",
        paymentAmount = "₩55,000",
    ),
    CurrencyProductUiModel(
        id = "blue_6",
        title = "일이삼사오육칠팔구십일이삼사오육칠팔구십",
        subtitle = "Sub tit",
        price = "110,000",
        priceSuffix = "원",
        isPopular = true,
        chargeTitle = "파란보석 패키지",
        paymentAmount = "₩110,000",
    ),
)

/** @deprecated [SampleOrangeGemProducts] 사용. 기존 호출부 호환용. */
@Deprecated(
    message = "Use SampleOrangeGemProducts or SampleBlueGemProducts",
    replaceWith = ReplaceWith("SampleOrangeGemProducts"),
)
val SampleCurrencyProducts: List<CurrencyProductUiModel> = SampleOrangeGemProducts

/**
 * 재화 구매 리스트 (Figma node `2305:13152` / 파란보석 탭).
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
    val leadingIcon = if (item.isWonPayment) {
        R.drawable.diamond_blue
    } else {
        R.drawable.diamond_orange
    }

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
            Image(
                painter = painterResource(id = leadingIcon),
                contentDescription = null,
                modifier = Modifier.size(16.dp),
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = item.title,
                        style = LiroutiTheme.typography.body2LongMedium,
                        color = LiroutiTheme.colors.labelStrong,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        // fill=false: 짧은 제목은 뱃지 바로 앞, 긴 제목은 줄어들며 ellipsis
                        modifier = Modifier.weight(1f, fill = false),
                    )
                    if (item.isPopular) {
                        Spacer(modifier = Modifier.width(6.dp))
                        LiroutiBadge(text = "인기", color = LiroutiBadgeColor.Blue)
                    }
                }
                Text(
                    text = item.subtitle,
                    style = LiroutiTheme.typography.body3Regular.copy(lineHeight = 16.sp),
                    color = LiroutiTheme.colors.labelInfo,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (!item.isWonPayment) {
                    Image(
                        painter = painterResource(id = R.drawable.diamond_blue),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                }
                Text(
                    text = item.price,
                    style = LiroutiTheme.typography.body2LongSemiBold,
                    color = LiroutiTheme.colors.labelStrong,
                )
                if (item.priceSuffix != null) {
                    Text(
                        text = item.priceSuffix,
                        style = LiroutiTheme.typography.body3Regular.copy(lineHeight = 16.sp),
                        color = LiroutiTheme.colors.labelStrong,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, heightDp = 560, name = "파란보석")
@Composable
private fun CurrencyProductListBluePreview() {
    LiroutiFrontendTheme {
        CurrencyProductList(
            items = SampleBlueGemProducts,
            selectedProductId = "blue_2",
            onProductClick = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}

@Preview(showBackground = true, heightDp = 480, name = "주황보석")
@Composable
private fun CurrencyProductListOrangePreview() {
    LiroutiFrontendTheme {
        CurrencyProductList(
            items = SampleOrangeGemProducts,
            selectedProductId = "orange_2",
            onProductClick = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}
