package com.li_routi.feature.mypage.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.li_routi.core.designsystem.R
import com.li_routi.core.designsystem.foundation.color.DividerSoft
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme

// Figma 원본이 시맨틱 변수 없이 raw hex(#eff0f0)만 쓰고 있어서(바인딩된 변수 아님) 로컬 리터럴로 둔다.
val SettingsSectionDividerColor = DividerSoft

/**
 * 마이페이지 하위 "설정 목록형" 화면(앱 정보/계정 관리 등)의 섹션 라벨.
 * Figma node `205:18314`("고객 지원" 등) 기준.
 */
@Composable
fun SettingsSectionLabel(title: String, modifier: Modifier = Modifier) {
    Text(
        text = title,
        style = LiroutiTheme.typography.body3Regular.copy(lineHeight = 16.sp),
        color = LiroutiTheme.colors.labelInfo,
        modifier = modifier.height(24.dp),
    )
}

/**
 * 마이페이지 하위 "설정 목록형" 화면(앱 정보/계정 관리 등)의 목록 한 줄.
 * Figma node `205:18315`("comp/list") 기준.
 *
 * [onClick]이 null이면 클릭 불가·화살표도 숨긴다(정보 표시 전용 행일 때).
 * [titleColor]는 "회원 탈퇴"처럼 위험 표시가 필요한 행에만 danger 색을 넘긴다.
 */
@Composable
fun SettingsListItem(
    title: String,
    modifier: Modifier = Modifier,
    titleColor: Color = LiroutiTheme.colors.labelSub,
    onClick: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(44.dp)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = LiroutiTheme.typography.body2LongMedium,
            color = titleColor,
            modifier = Modifier.weight(1f),
        )
        if (onClick != null) {
            Image(
                painter = painterResource(id = R.drawable.chevron__right),
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                colorFilter = ColorFilter.tint(LiroutiTheme.colors.labelSub),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingsListItemPreview() {
    LiroutiFrontendTheme {
        Column {
            SettingsSectionLabel(title = "고객 지원")
            SettingsListItem(title = "공지사항", onClick = {})
            SettingsListItem(title = "문의하기", onClick = {})
            SettingsSectionLabel(title = "기타")
            SettingsListItem(title = "로그아웃", onClick = {})
            SettingsListItem(title = "회원 탈퇴", titleColor = LiroutiTheme.colors.dangerText, onClick = {})
        }
    }
}
