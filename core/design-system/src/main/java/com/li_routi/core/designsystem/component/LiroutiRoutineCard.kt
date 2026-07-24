package com.li_routi.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.li_routi.core.designsystem.foundation.typography.Pretendard
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme

private val CardTitleTextStyle = TextStyle(
    fontFamily = Pretendard,
    fontWeight = FontWeight.Bold,
    fontSize = 16.sp,
    lineHeight = 24.sp,
    letterSpacing = (-0.025f).em,
)

private val StatValueTextStyle = CardTitleTextStyle

private val StatLabelTextStyle13 = TextStyle(
    fontFamily = Pretendard,
    fontSize = 13.sp,
    lineHeight = 16.sp,
    letterSpacing = (-0.025f).em,
)

private val AccentCaptionTextStyle = TextStyle(
    fontFamily = Pretendard,
    fontSize = 12.sp,
    lineHeight = 14.sp,
)

private val CardShape = RoundedCornerShape(6.dp)
private val StatsRowShape = RoundedCornerShape(8.dp)

@Composable
private fun RoutineIconBox(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit = { LiroutiRoutineIcon() },
) {
    Box(
        modifier = modifier
            .size(50.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(LiroutiTheme.colors.backgroundSecondary),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}

@Composable
private fun RoutineStatBlock(value: String, label: String, modifier: Modifier = Modifier, width: Dp = 60.dp) {
    Column(
        modifier = modifier.width(width),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = value, style = StatValueTextStyle, color = LiroutiTheme.colors.labelSub)
        Text(text = label, style = StatLabelTextStyle13, color = LiroutiTheme.colors.labelSub)
    }
}

@Composable
private fun GroupStatBlock(value: String, label: String, modifier: Modifier = Modifier, width: Dp = 80.dp) {
    Column(
        modifier = modifier.width(width),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = value, style = StatValueTextStyle, color = LiroutiTheme.colors.labelSub)
        Text(text = label, style = InfoTextStyle, color = LiroutiTheme.colors.labelSub)
    }
}

@Composable
private fun StatDivider(modifier: Modifier = Modifier) {
    LiroutiDivider(
        orientation = LiroutiDividerOrientation.Vertical,
        color = LiroutiTheme.colors.borderDefault,
        modifier = modifier.size(width = 1.dp, height = 40.dp),
    )
}

@Composable
fun LiroutiRoutineSimpleCard(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    badgeText: String? = "참여중",
    icon: @Composable () -> Unit = { LiroutiRoutineIcon() },
) {
    Row(
        modifier = modifier
            .width(328.dp)
            .background(LiroutiTheme.colors.backgroundDefault, CardShape)
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RoutineIconBox(content = icon)
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = CardTitleTextStyle, color = LiroutiTheme.colors.labelDefault)
            Text(
                text = subtitle,
                style = InfoTextStyle,
                color = LiroutiTheme.colors.labelSub,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        badgeText?.let { LiroutiBadge(text = it, color = LiroutiBadgeColor.Blue) }
    }
}

@Composable
fun LiroutiRoutineDetailCard(
    title: String,
    subtitle: String,
    participants: String,
    activity: String,
    posts: String,
    modifier: Modifier = Modifier,
    badgeText: String? = "매일 루틴",
    icon: @Composable () -> Unit = { LiroutiRoutineIcon() },
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(LiroutiTheme.colors.backgroundDefault, CardShape)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            RoutineIconBox(content = icon)
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = CardTitleTextStyle, color = LiroutiTheme.colors.labelDefault)
                Text(
                    text = subtitle,
                    style = InfoTextStyle,
                    color = LiroutiTheme.colors.labelSub,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            badgeText?.let { LiroutiBadge(text = it, color = LiroutiBadgeColor.Blue) }
        }
        LiroutiDivider(color = LiroutiTheme.colors.borderDefault)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(LiroutiTheme.colors.backgroundFill, StatsRowShape)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            RoutineStatBlock(value = participants, label = "참여자")
            StatDivider()
            RoutineStatBlock(value = activity, label = "활동")
            StatDivider()
            RoutineStatBlock(value = posts, label = "인증 게시글")
        }
    }
}

@Composable
fun LiroutiGroupCard(
    title: String,
    activityTime: String,
    subtitle: String,
    routineCount: String,
    todayProgress: String,
    streakDays: String,
    monthlyRate: String,
    todayVerified: String,
    modifier: Modifier = Modifier,
    badgeText: String? = "진행중",
    memberAvatarCount: Int = 3,
    icon: @Composable () -> Unit = { LiroutiRoutineIcon() },
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(LiroutiTheme.colors.backgroundDefault, CardShape)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            RoutineIconBox(content = icon)
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(text = title, style = CardTitleTextStyle, color = LiroutiTheme.colors.labelDefault)
                    Text(text = activityTime, style = AccentCaptionTextStyle, color = LiroutiTheme.colors.primaryNormal)
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = subtitle,
                        style = InfoTextStyle,
                        color = LiroutiTheme.colors.labelSub,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    LiroutiDivider(
                        orientation = LiroutiDividerOrientation.Vertical,
                        color = LiroutiTheme.colors.borderDefault,
                        modifier = Modifier.size(width = 1.dp, height = 10.dp),
                    )
                    Text(text = routineCount, style = InfoTextStyle, color = LiroutiTheme.colors.labelSub)
                }
            }
            badgeText?.let { LiroutiBadge(text = it, color = LiroutiBadgeColor.Blue) }
        }
        LiroutiDivider(color = LiroutiTheme.colors.borderDefault)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            LiroutiAvatarGroup(count = memberAvatarCount, avatarSize = 24.dp)
            Text(text = todayProgress, style = InfoTextStyle, color = LiroutiTheme.colors.labelInfo)
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(LiroutiTheme.colors.backgroundFill, StatsRowShape)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            GroupStatBlock(value = streakDays, label = "연속 달성")
            StatDivider()
            GroupStatBlock(value = monthlyRate, label = "이번 달 달성률")
            StatDivider()
            GroupStatBlock(value = todayVerified, label = "오늘 인증")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LiroutiRoutineSimpleCardPreview() {
    LiroutiFrontendTheme {
        LiroutiRoutineSimpleCard(
            title = "매일 우유 한 잔",
            subtitle = "매일 우유를 마시며 건강 관리를 해요",
            modifier = Modifier.padding(16.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LiroutiRoutineDetailCardPreview() {
    LiroutiFrontendTheme {
        LiroutiRoutineDetailCard(
            title = "매일 우유 한 잔",
            subtitle = "매일 우유를 마시며 건강 관리를 해요",
            participants = "300",
            activity = "14000",
            posts = "80",
            modifier = Modifier.padding(16.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LiroutiGroupCardPreview() {
    LiroutiFrontendTheme {
        LiroutiGroupCard(
            title = "코딩",
            activityTime = "1시간 전 활동",
            subtitle = "멤버 3명",
            routineCount = "루틴 6개",
            todayProgress = "오늘 3/6 완료",
            streakDays = "5일",
            monthlyRate = "60%",
            todayVerified = "3건",
            modifier = Modifier.padding(16.dp),
        )
    }
}
