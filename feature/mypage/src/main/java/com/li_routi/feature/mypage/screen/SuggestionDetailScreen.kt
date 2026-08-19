package com.li_routi.feature.mypage.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import com.li_routi.feature.mypage.component.SettingsSectionDividerColor
import com.li_routi.feature.mypage.component.SuggestionUiModel

/**
 * 건의하기 상세. 목록에서 고른 제목·날짜·본문을 보여 준다.
 */
@Composable
fun SuggestionDetailScreen(
    suggestion: SuggestionUiModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(LiroutiTheme.colors.backgroundDefault),
    ) {
        EditProfileTopBar(title = "건의하기", onBackClick = onBackClick)
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(top = 12.dp, bottom = 30.dp),
            verticalArrangement = Arrangement.spacedBy(25.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = suggestion.title,
                        style = LiroutiTheme.typography.heading2Bold,
                        color = LiroutiTheme.colors.labelDefault,
                    )
                    Text(
                        text = suggestion.date,
                        style = LiroutiTheme.typography.body2LongRegular,
                        color = LiroutiTheme.colors.labelInfo,
                    )
                }
                LiroutiDivider(color = SettingsSectionDividerColor)
            }
            Text(
                text = suggestion.content,
                style = LiroutiTheme.typography.body2LongRegular,
                color = LiroutiTheme.colors.labelSub,
            )
        }
    }
}

@Preview(showBackground = true, heightDp = 800)
@Composable
private fun SuggestionDetailScreenPreview() {
    LiroutiFrontendTheme {
        SuggestionDetailScreen(
            suggestion = SuggestionUiModel(
                id = 1,
                title = "건의사항 1",
                categoryName = "메인",
                date = "2026. 08. 21",
                content = "메인 화면에서 오늘 루틴이 더 잘 보이면 좋겠어요.",
            ),
            onBackClick = {},
        )
    }
}
