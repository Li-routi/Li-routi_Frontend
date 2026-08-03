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
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.li_routi.core.designsystem.R

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
        CustomCheckBox(
            state = if (checked) CheckBoxState.B else CheckBoxState.A,
            onClick = { onCheckedChange?.invoke(!checked) },
        )
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
        CustomCheckBox(state = CheckBoxState.B)
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
        CustomCheckBox(state = if (checked) CheckBoxState.B else CheckBoxState.A)
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
        CustomCheckBox(
            state = if (checked) CheckBoxState.B else CheckBoxState.A,
            isCircle = true,
            onClick = { onCheckedChange(!checked) },
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

private val RoutineListItemWidth = 295.dp
private val RoutineListItemHeight = 50.dp
private val RoutineListItemIconBoxSize = 50.dp
private val RoutineListItemIconBoxColor = Color(0xFFF4F7FB)
private val RoutineListItemIconBoxShape = RoundedCornerShape(12.dp)
private val RoutineListItemTextBoxWidth = 181.dp
private val RoutineListItemTextBoxHeight = 40.dp

private val RoutineListItemTitleTextStyle = TextStyle(
    fontFamily = Pretendard,
    fontWeight = FontWeight.Bold,
    fontSize = 16.sp,
    lineHeight = 24.sp,
    letterSpacing = (-0.005f).em,
)

private val RoutineListItemSubtitleTextStyle = TextStyle(
    fontFamily = Pretendard,
    fontWeight = FontWeight.Normal,
    fontSize = 11.sp,
    lineHeight = 14.sp,
)

@Composable
fun LiroutiListItemRoutine(
    modifier: Modifier = Modifier,
    title: String = "매일 우유 한잔",
    subtitle: String = "매일 우유를 마시며 건강 관리를 해요",
    badgeText: String = "참여중",
) {
    Row(
        modifier = modifier.size(width = RoutineListItemWidth, height = RoutineListItemHeight),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(RoutineListItemIconBoxSize)
                .background(RoutineListItemIconBoxColor, RoutineListItemIconBoxShape),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(id = R.drawable.milk),
                contentDescription = null,
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(
            modifier = Modifier.size(width = RoutineListItemTextBoxWidth, height = RoutineListItemTextBoxHeight),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(text = title, style = RoutineListItemTitleTextStyle, color = LiroutiTheme.colors.labelDefault)
            Text(text = subtitle, style = RoutineListItemSubtitleTextStyle, color = LiroutiTheme.colors.labelInfo)
        }
        Spacer(modifier = Modifier.weight(1f))
        LiroutiBadge(text = badgeText, color = LiroutiBadgeColor.Blue, size = LiroutiBadgeSize.XSmall)
    }
}

@Preview(showBackground = true)
@Composable
private fun LiroutiListItemRoutinePreview() {
    LiroutiFrontendTheme {
        LiroutiListItemRoutine(modifier = Modifier.padding(16.dp))
    }
}

private val RoutineSummaryBoxWidth = 360.dp
private val RoutineSummaryBoxHeight = 191.dp
private val RoutineHeaderItemWidth = 328.dp
private val RoutineDividerLineWidth = 297.dp
private val RoutineStatsBoxWidth = 328.dp
private val RoutineStatsBoxHeight = 68.dp
private val RoutineStatsBoxColor = Color(0xFFFAFAFA)
private val RoutineStatDividerColor = Color(0xFFDBDCDF)
private val RoutineStatDividerHeight = 40.dp
private val RoutineStatGap = 29.dp

@Composable
private fun RoutineSummaryHeaderItem(
    modifier: Modifier = Modifier,
    title: String = "매일 우유 한잔",
    subtitle: String = "매일 우유를 마시며 건강 관리를 해요",
    badgeText: String = "매일 루틴",
) {
    Row(
        modifier = modifier.size(width = RoutineHeaderItemWidth, height = RoutineListItemHeight),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(RoutineListItemIconBoxSize)
                .background(RoutineListItemIconBoxColor, RoutineListItemIconBoxShape),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(id = R.drawable.milk),
                contentDescription = null,
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(
            modifier = Modifier.size(width = RoutineListItemTextBoxWidth, height = RoutineListItemTextBoxHeight),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(text = title, style = RoutineListItemTitleTextStyle, color = LiroutiTheme.colors.labelDefault)
            Text(text = subtitle, style = RoutineListItemSubtitleTextStyle, color = LiroutiTheme.colors.labelInfo)
        }
        Spacer(modifier = Modifier.weight(1f))
        LiroutiBadge(text = badgeText, color = LiroutiBadgeColor.Blue, size = LiroutiBadgeSize.XSmall)
    }
}

@Composable
private fun RoutineStat(value: String, label: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, style = RoutineListItemTitleTextStyle, color = LiroutiTheme.colors.labelDefault)
        Text(text = label, style = RoutineListItemSubtitleTextStyle, color = LiroutiTheme.colors.labelInfo)
    }
}

@Composable
private fun RoutineStatsBox(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .size(width = RoutineStatsBoxWidth, height = RoutineStatsBoxHeight)
            .background(RoutineStatsBoxColor)
            .padding(start = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(RoutineStatGap),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RoutineStat(value = "300", label = "참여자")
        LiroutiDivider(
            orientation = LiroutiDividerOrientation.Vertical,
            color = RoutineStatDividerColor,
            modifier = Modifier.height(RoutineStatDividerHeight),
        )
        RoutineStat(value = "140000", label = "활동")
        LiroutiDivider(
            orientation = LiroutiDividerOrientation.Vertical,
            color = RoutineStatDividerColor,
            modifier = Modifier.height(RoutineStatDividerHeight),
        )
        RoutineStat(value = "80", label = "인증 게시글")
    }
}

@Composable
fun LiroutiListItemRoutineSummary(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.size(width = RoutineSummaryBoxWidth, height = RoutineSummaryBoxHeight),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        RoutineSummaryHeaderItem()
        Spacer(modifier = Modifier.height(20.dp))
        LiroutiDivider(
            color = RoutineStatDividerColor,
            modifier = Modifier.width(RoutineDividerLineWidth),
        )
        Spacer(modifier = Modifier.height(20.dp))
        RoutineStatsBox()
    }
}

@Preview(showBackground = true)
@Composable
private fun LiroutiListItemRoutineSummaryPreview() {
    LiroutiFrontendTheme {
        LiroutiListItemRoutineSummary(modifier = Modifier.padding(16.dp))
    }
}

private val RoutineActivityAccentTextStyle = TextStyle(
    fontFamily = Pretendard,
    fontWeight = FontWeight.Normal,
    fontSize = 12.sp,
    lineHeight = 14.sp,
)
private val RoutineActivityAccentColor = Color(0xFF338AFF)
private val RoutineProgressTextColor = Color(0xFF878A93)

@Composable
private fun RoutineActivityHeaderItem(
    modifier: Modifier = Modifier,
    title: String = "코딩",
    activityText: String = "1시간 전 활동",
    memberLabel: String = "멤버 3명",
    routineLabel: String = "루틴 6개",
    badgeText: String = "진행중",
) {
    Row(
        modifier = modifier.size(width = RoutineHeaderItemWidth, height = RoutineListItemHeight),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(RoutineListItemIconBoxSize)
                .background(RoutineListItemIconBoxColor, RoutineListItemIconBoxShape),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(id = R.drawable.milk),
                contentDescription = null,
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(
            modifier = Modifier.size(width = RoutineListItemTextBoxWidth, height = RoutineListItemTextBoxHeight),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(text = title, style = RoutineListItemTitleTextStyle, color = LiroutiTheme.colors.labelDefault)
                Text(text = activityText, style = RoutineActivityAccentTextStyle, color = RoutineActivityAccentColor)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(5.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(text = memberLabel, style = RoutineListItemSubtitleTextStyle, color = LiroutiTheme.colors.labelInfo)
                LiroutiDivider(
                    orientation = LiroutiDividerOrientation.Vertical,
                    color = RoutineStatDividerColor,
                    modifier = Modifier.height(10.dp),
                )
                Text(text = routineLabel, style = RoutineListItemSubtitleTextStyle, color = LiroutiTheme.colors.labelInfo)
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        LiroutiBadge(text = badgeText, color = LiroutiBadgeColor.Blue, size = LiroutiBadgeSize.XSmall)
    }
}

@Composable
private fun RoutineProgressFooterRow(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.size(width = RoutineHeaderItemWidth, height = RoutineListItemHeight),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        LiroutiAvatar(size = 24.dp)
        Spacer(modifier = Modifier.weight(1f))
        Text(text = "오늘 3/6 완료", style = RoutineListItemSubtitleTextStyle, color = RoutineProgressTextColor)
    }
}

@Composable
private fun RoutineActivityStatsBox(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .size(width = RoutineStatsBoxWidth, height = RoutineStatsBoxHeight)
            .background(RoutineStatsBoxColor)
            .padding(start = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(RoutineStatGap),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RoutineStat(value = "5일", label = "연속 달성")
        LiroutiDivider(
            orientation = LiroutiDividerOrientation.Vertical,
            color = RoutineStatDividerColor,
            modifier = Modifier.height(RoutineStatDividerHeight),
        )
        RoutineStat(value = "60%", label = "이번 달성률")
        LiroutiDivider(
            orientation = LiroutiDividerOrientation.Vertical,
            color = RoutineStatDividerColor,
            modifier = Modifier.height(RoutineStatDividerHeight),
        )
        RoutineStat(value = "3건", label = "오늘 인증")
    }
}

@Composable
fun LiroutiListItemRoutineProgress(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.width(RoutineSummaryBoxWidth),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        RoutineActivityHeaderItem()
        Spacer(modifier = Modifier.height(20.dp))
        LiroutiDivider(
            color = RoutineStatDividerColor,
            modifier = Modifier.width(RoutineDividerLineWidth),
        )
        Spacer(modifier = Modifier.height(16.dp))
        RoutineProgressFooterRow()
        Spacer(modifier = Modifier.height(12.dp))
        RoutineActivityStatsBox()
    }
}

@Preview(showBackground = true)
@Composable
private fun LiroutiListItemRoutineProgressPreview() {
    LiroutiFrontendTheme {
        LiroutiListItemRoutineProgress(modifier = Modifier.padding(16.dp))
    }
}
