package com.li_routi.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.li_routi.core.designsystem.foundation.typography.Pretendard
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme

internal val ListTitleTextStyle = TextStyle(
    fontFamily = Pretendard,
    fontWeight = FontWeight.Bold,
    fontSize = 14.sp,
    lineHeight = 22.sp,
    letterSpacing = (-0.025f).em,
)

private val PriceTextStyle = TextStyle(
    fontFamily = Pretendard,
    fontWeight = FontWeight.Bold,
    fontSize = 14.sp,
    lineHeight = 22.sp,
    letterSpacing = (-0.025f).em,
)

private val PriceUnitTextStyle = TextStyle(
    fontFamily = Pretendard,
    fontSize = 12.sp,
    lineHeight = 14.sp,
)

private val BorderedRowShape = RoundedCornerShape(6.dp)

@Composable
private fun Modifier.borderedRow() = this
    .fillMaxWidth()
    .background(LiroutiTheme.colors.backgroundDefault, BorderedRowShape)
    .border(1.dp, LiroutiTheme.colors.borderAlternative, BorderedRowShape)

@Composable
fun LiroutiListItemCamera(
    title: String,
    modifier: Modifier = Modifier,
    subtitle1: String? = null,
    subtitle2: String? = null,
    checked: Boolean = false,
    onCheckedChange: ((Boolean) -> Unit)? = null,
    badgeText: String? = "배지",
) {
    Row(
        modifier = modifier
            .borderedRow()
            .height(56.dp)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        LiroutiCheckbox(checked = checked, onCheckedChange = onCheckedChange)
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = ListTitleTextStyle.copy(fontWeight = FontWeight.Medium),
                color = LiroutiTheme.colors.labelDefault,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (subtitle1 != null || subtitle2 != null) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    subtitle1?.let { Text(text = it, style = InfoTextStyle, color = LiroutiTheme.colors.labelInfo) }
                    if (subtitle1 != null && subtitle2 != null) {
                        LiroutiDivider(
                            orientation = LiroutiDividerOrientation.Vertical,
                            color = LiroutiTheme.colors.borderStrong,
                            modifier = Modifier.height(10.dp),
                        )
                    }
                    subtitle2?.let { Text(text = it, style = InfoTextStyle, color = LiroutiTheme.colors.labelInfo) }
                }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
            LiroutiCameraIcon(modifier = Modifier.size(width = 17.5.dp, height = 13.75.dp), color = LiroutiTheme.colors.labelInfo)
            badgeText?.let { LiroutiBadge(text = it, color = LiroutiBadgeColor.Blue) }
        }
    }
}

@Composable
fun LiroutiListItemComplete(
    title: String,
    modifier: Modifier = Modifier,
    badgeText: String? = "완료",
) {
    Row(
        modifier = modifier
            .borderedRow()
            .height(50.dp)
            .padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        LiroutiCheckbox(checked = true)
        Text(
            text = title,
            modifier = Modifier.weight(1f),
            style = ListTitleTextStyle.copy(fontWeight = FontWeight.Medium),
            color = LiroutiTheme.colors.labelInfo,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        badgeText?.let { LiroutiBadge(text = it, color = LiroutiBadgeColor.Neutral) }
    }
}

@Composable
fun LiroutiListItemRadio(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(24.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        LiroutiCheckbox(checked = checked)
        Text(
            text = title,
            modifier = Modifier.weight(1f),
            style = ListTitleTextStyle.copy(fontWeight = FontWeight.Medium),
            color = LiroutiTheme.colors.labelDefault,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        LiroutiSwitch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
fun LiroutiListItemRecharge(
    title: String,
    price: String,
    modifier: Modifier = Modifier,
    priceUnit: String = "원",
    subtitle: String? = null,
    badgeText: String? = "인기",
) {
    Row(
        modifier = modifier
            .borderedRow()
            .height(56.dp)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        LiroutiDiamondIcon(modifier = Modifier.size(width = 16.dp, height = 13.3.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = title,
                    style = ListTitleTextStyle.copy(fontWeight = FontWeight.Medium),
                    color = LiroutiTheme.colors.labelDefault,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                badgeText?.let { LiroutiBadge(text = it, color = LiroutiBadgeColor.Blue) }
            }
            subtitle?.let { Text(text = it, style = InfoTextStyle, color = LiroutiTheme.colors.labelInfo) }
        }
        Row(verticalAlignment = Alignment.Bottom) {
            Text(text = price, style = PriceTextStyle, color = LiroutiTheme.colors.labelDefault)
            Text(text = priceUnit, style = PriceUnitTextStyle, color = LiroutiTheme.colors.labelDefault)
        }
    }
}

@Composable
private fun Modifier.settingRow() = this
    .fillMaxWidth()
    .background(LiroutiTheme.colors.backgroundDefault)
    .height(54.dp)
    .padding(horizontal = 16.dp, vertical = 15.dp)

@Composable
fun LiroutiListItemSettingToggle(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.settingRow(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            modifier = Modifier.weight(1f),
            style = ListTitleTextStyle,
            color = LiroutiTheme.colors.labelDefault,
        )
        LiroutiSwitch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
fun LiroutiListItemSettingArrow(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .settingRow()
            .clickable(onClick = onClick),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            LiroutiGroupIcon(modifier = Modifier.size(width = 24.dp, height = 22.4.dp), color = LiroutiTheme.colors.labelDefault)
            Text(text = title, style = ListTitleTextStyle, color = LiroutiTheme.colors.labelDefault)
        }
        LiroutiChevronRightIcon(modifier = Modifier.size(24.dp), color = LiroutiTheme.colors.labelDefault)
    }
}

@Composable
fun LiroutiListItemSettingPeople(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.settingRow(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        LiroutiAvatar(size = 24.dp)
        Text(text = title, modifier = Modifier.weight(1f), style = ListTitleTextStyle, color = LiroutiTheme.colors.labelDefault)
        LiroutiCheckbox(
            checked = checked,
            shape = LiroutiCheckboxShape.Circle,
            onCheckedChange = onCheckedChange,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LiroutiListItemPreview() {
    LiroutiFrontendTheme {
        var cameraChecked by remember { mutableStateOf(false) }
        var toggleChecked by remember { mutableStateOf(true) }
        var peopleChecked by remember { mutableStateOf(false) }
        var radioChecked by remember { mutableStateOf(true) }

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {

            LiroutiListItemCamera(
                title = "Tit",
                subtitle1 = "Sub tit",
                subtitle2 = "Sub tit",
                checked = cameraChecked,
                onCheckedChange = { cameraChecked = it },
            )
            LiroutiListItemComplete(title = "Tit")
            LiroutiListItemRecharge(title = "Tit", price = "10000", subtitle = "Sub tit")


            Column {
                LiroutiListItemSettingToggle(
                    title = "Tit",
                    checked = toggleChecked,
                    onCheckedChange = { toggleChecked = it },
                )
                LiroutiDivider(color = LiroutiTheme.colors.borderDefault)
                LiroutiListItemSettingArrow(title = "Tit", onClick = {})
                LiroutiDivider(color = LiroutiTheme.colors.borderDefault)
                LiroutiListItemSettingPeople(
                    title = "Tit",
                    checked = peopleChecked,
                    onCheckedChange = { peopleChecked = it },
                )
            }

            LiroutiListItemRadio(
                title = "공유 루틴 알림",
                checked = radioChecked,
                onCheckedChange = { radioChecked = it },
            )
        }
    }
}
