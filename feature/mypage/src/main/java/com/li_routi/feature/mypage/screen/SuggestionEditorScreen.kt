package com.li_routi.feature.mypage.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.li_routi.core.designsystem.component.LiroutiLabel
import com.li_routi.core.designsystem.component.LiroutiPrimaryButton
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme
import com.li_routi.feature.mypage.component.EditProfileTopBar
import com.li_routi.feature.mypage.component.SuggestionCategoryUiModel
import com.li_routi.feature.mypage.vm.SuggestionContentMaxLength

/**
 * 건의하기 작성. 제목 대신 서버 분류를 고르고, 본문은 최대 2000자다.
 */
@Composable
fun SuggestionEditorScreen(
    onBackClick: () -> Unit,
    onSaveClick: (content: String) -> Unit,
    modifier: Modifier = Modifier,
    categories: List<SuggestionCategoryUiModel> = emptyList(),
    selectedCategoryId: Long? = null,
    onCategorySelected: (Long) -> Unit = {},
    isSaving: Boolean = false,
    isCategoriesLoading: Boolean = false,
    categoriesError: String? = null,
    onRetryCategories: () -> Unit = {},
) {
    var content by rememberSaveable { mutableStateOf("") }
    val canSave = selectedCategoryId != null && content.isNotBlank() && !isSaving

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(LiroutiTheme.colors.backgroundDefault)
            .imePadding()
            .navigationBarsPadding(),
    ) {
        EditProfileTopBar(title = "건의하기", onBackClick = onBackClick)
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)
                .padding(top = 12.dp),
        ) {
            Text(
                text = "분류",
                style = LiroutiTheme.typography.body2LongSemiBold,
                color = LiroutiTheme.colors.labelDefault,
                modifier = Modifier.padding(bottom = 8.dp),
            )
            when {
                isCategoriesLoading && categories.isEmpty() -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(38.dp),
                        contentAlignment = Alignment.CenterStart,
                    ) {
                        CircularProgressIndicator(
                            color = LiroutiTheme.colors.primaryNormal,
                            modifier = Modifier.size(24.dp),
                        )
                    }
                }
                categoriesError != null && categories.isEmpty() -> {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = categoriesError,
                            style = LiroutiTheme.typography.body2LongRegular,
                            color = LiroutiTheme.colors.labelInfo,
                        )
                        Text(
                            text = "다시 시도",
                            style = LiroutiTheme.typography.body2LongMedium,
                            color = LiroutiTheme.colors.primaryNormal,
                            modifier = Modifier.clickable(onClick = onRetryCategories),
                            textAlign = TextAlign.Start,
                        )
                    }
                }
                else -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        categories.forEach { category ->
                            LiroutiLabel(
                                text = category.name,
                                selected = selectedCategoryId == category.id,
                                onClick = { onCategorySelected(category.id) },
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            SuggestionContentField(
                value = content,
                onValueChange = { content = it.take(SuggestionContentMaxLength) },
            )
        }
        LiroutiPrimaryButton(
            text = "저장하기",
            onClick = { onSaveClick(content) },
            enabled = canSave,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
        )
    }
}

@Composable
private fun SuggestionContentField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "건의 내용",
            style = LiroutiTheme.typography.body2LongSemiBold,
            color = LiroutiTheme.colors.labelDefault,
            modifier = Modifier.padding(bottom = 8.dp),
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(LiroutiTheme.colors.backgroundDefault, RoundedCornerShape(6.dp))
                .border(1.dp, LiroutiTheme.colors.borderDefault, RoundedCornerShape(6.dp))
                .padding(16.dp),
        ) {
            if (value.isEmpty()) {
                Text(
                    text = "건의 내용을 입력해 주세요",
                    style = LiroutiTheme.typography.body2LongRegular,
                    color = LiroutiTheme.colors.labelInfo,
                )
            }
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                textStyle = LiroutiTheme.typography.body2LongRegular.copy(color = LiroutiTheme.colors.labelDefault),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 20.dp),
            )
            Text(
                text = "${value.length}/$SuggestionContentMaxLength",
                style = LiroutiTheme.typography.captionRegular,
                color = LiroutiTheme.colors.labelInfo,
                modifier = Modifier.align(Alignment.BottomEnd),
            )
        }
    }
}

@Preview(showBackground = true, heightDp = 800)
@Composable
private fun SuggestionEditorScreenPreview() {
    LiroutiFrontendTheme {
        SuggestionEditorScreen(
            onBackClick = {},
            onSaveClick = {},
            categories = listOf(
                SuggestionCategoryUiModel(id = 1, name = "버그 신고"),
                SuggestionCategoryUiModel(id = 2, name = "기능 제안"),
            ),
            selectedCategoryId = 1,
        )
    }
}
