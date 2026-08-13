package com.li_routi.feature.mypage.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.li_routi.core.designsystem.component.LiroutiDivider
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme
import com.li_routi.feature.mypage.component.EditProfileTopBar
import com.li_routi.feature.mypage.component.NoticeUiModel
import com.li_routi.feature.mypage.component.SampleNotices
import com.li_routi.feature.mypage.component.SettingsSectionDividerColor

/**
 * 공지사항 상세 화면. Figma node `6008:16123`("공지사항 > 공지사항")/`6008:17900`
 * ("공지사항 > 업데이트") 기준 — 두 노드 모두 같은 레이아웃(제목/날짜/구분선/본문)이라 하나의
 * 템플릿으로 구현했다. 목록과 달리 상단 바에 타이틀 텍스트가 없다(뒤로가기만).
 */
@Composable
fun NoticeDetailScreen(
    notice: NoticeUiModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(LiroutiTheme.colors.backgroundDefault),
    ) {
        EditProfileTopBar(title = "", onBackClick = onBackClick)
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(top = 12.dp),
            verticalArrangement = Arrangement.spacedBy(25.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = notice.title,
                        style = LiroutiTheme.typography.heading2Bold,
                        color = LiroutiTheme.colors.labelDefault,
                    )
                    Text(
                        text = notice.date,
                        style = LiroutiTheme.typography.body2LongRegular,
                        color = LiroutiTheme.colors.labelInfo,
                    )
                }
                LiroutiDivider(color = SettingsSectionDividerColor)
            }
            Text(
                text = notice.content,
                style = LiroutiTheme.typography.body2LongRegular,
                color = LiroutiTheme.colors.labelSub,
            )
        }
    }
}

@Preview(showBackground = true, heightDp = 800)
@Composable
private fun NoticeDetailScreenPreview() {
    LiroutiFrontendTheme {
        NoticeDetailScreen(notice = SampleNotices.first(), onBackClick = {})
    }
}
