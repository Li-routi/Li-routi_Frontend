package com.li_routi.feature.mypage.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
 * 공지사항 목록 화면. Figma node `6008:17939`("공지사항") 기준 — 앱 정보 "공지사항" 메뉴로 진입한다.
 *
 * 항목을 누르면 [onNoticeClick]으로 그 공지의 id를 알려준다(호출부가 상세 화면으로 전환).
 */
@Composable
fun NoticeListScreen(
    onBackClick: () -> Unit,
    onNoticeClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    notices: List<NoticeUiModel> = SampleNotices,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(LiroutiTheme.colors.backgroundDefault),
    ) {
        EditProfileTopBar(title = "공지사항", onBackClick = onBackClick)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
        ) {
            notices.forEachIndexed { index, notice ->
                if (index > 0) {
                    LiroutiDivider(color = SettingsSectionDividerColor)
                }
                NoticeListItem(
                    notice = notice,
                    onClick = { onNoticeClick(notice.id) },
                )
            }
        }
    }
}

@Composable
private fun NoticeListItem(
    notice: NoticeUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(
            text = notice.title,
            style = LiroutiTheme.typography.body1Bold,
            color = LiroutiTheme.colors.labelDefault,
        )
        Text(
            text = notice.date,
            style = LiroutiTheme.typography.body2LongRegular,
            color = LiroutiTheme.colors.labelInfo,
        )
    }
}

@Preview(showBackground = true, heightDp = 800)
@Composable
private fun NoticeListScreenPreview() {
    LiroutiFrontendTheme {
        NoticeListScreen(onBackClick = {}, onNoticeClick = {})
    }
}
