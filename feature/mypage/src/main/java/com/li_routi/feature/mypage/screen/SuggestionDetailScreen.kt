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
 * 건의하기 상세. 제목 자리에 분류 이름을 보여 준다. 상세 GET이 없어 목록에서 받은 본문을 쓴다.
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
                        text = suggestion.categoryName,
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
                categoryName = "버그 신고",
                date = "2026. 08. 19",
                content = "메인 화면에서 오늘 루틴이 더 잘 보이면 좋겠어요.",
            ),
            onBackClick = {},
        )
    }
}
