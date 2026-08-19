package com.li_routi.feature.mypage.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.li_routi.core.common.ui.nav.AppBottomNavBar
import com.li_routi.core.common.ui.nav.AppBottomTab
import com.li_routi.core.designsystem.R
import com.li_routi.core.designsystem.component.LiroutiDivider
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme
import com.li_routi.feature.mypage.component.EditProfileTopBar
import com.li_routi.feature.mypage.component.SettingsSectionDividerColor
import com.li_routi.feature.mypage.component.SuggestionUiModel
import kotlinx.coroutines.launch

/**
 * 건의하기 목록. 내가 보낸 건의만 보여 주고, 제목 자리에는 분류 이름을 쓴다.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuggestionListScreen(
    onBackClick: () -> Unit,
    onSuggestionClick: (Long) -> Unit,
    onCreateClick: () -> Unit,
    onTabSelected: (AppBottomTab) -> Unit,
    onLoadMore: () -> Unit,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier,
    suggestions: List<SuggestionUiModel> = emptyList(),
    isLoading: Boolean = false,
    hasNext: Boolean = false,
    errorMessage: String? = null,
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = LiroutiTheme.colors.backgroundDefault,
        topBar = {
            EditProfileTopBar(
                title = "건의하기",
                onBackClick = onBackClick,
                trailingContent = {
                    Image(
                        painter = painterResource(id = R.drawable.add),
                        contentDescription = "건의 작성",
                        modifier = Modifier
                            .size(20.dp)
                            .clickable(onClick = onCreateClick),
                        colorFilter = ColorFilter.tint(LiroutiTheme.colors.labelDefault),
                    )
                },
            )
        },
        bottomBar = {
                AppBottomNavBar(
                    selectedTab = AppBottomTab.My,
                    onTabSelected = onTabSelected,
                )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            when {
                isLoading && suggestions.isEmpty() && errorMessage == null -> {
                    CircularProgressIndicator(
                        color = LiroutiTheme.colors.primaryNormal,
                        modifier = Modifier.align(Alignment.Center),
                    )
                }
                errorMessage != null && suggestions.isEmpty() -> {
                    SuggestionStatusMessage(
                        message = errorMessage,
                        actionLabel = "다시 시도",
                        onActionClick = onRetryClick,
                        modifier = Modifier.align(Alignment.Center),
                    )
                }
                suggestions.isEmpty() -> {
                    SuggestionStatusMessage(
                        message = "건의가 없어요",
                        modifier = Modifier.align(Alignment.Center),
                    )
                }
                else -> {
                    val itemCount = suggestions.size
                    val shouldLoadMore by remember(itemCount) {
                        derivedStateOf {
                            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
                            val totalItems = listState.layoutInfo.totalItemsCount
                            lastVisible != null &&
                                totalItems > 0 &&
                                lastVisible >= totalItems - 3
                        }
                    }
                    LaunchedEffect(shouldLoadMore, hasNext, errorMessage) {
                        if (shouldLoadMore && hasNext && errorMessage == null) onLoadMore()
                    }
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 72.dp),
                    ) {
                        itemsIndexed(
                            items = suggestions,
                            key = { _, item -> item.id },
                        ) { index, suggestion ->
                            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                                if (index > 0) {
                                    LiroutiDivider(color = SettingsSectionDividerColor)
                                }
                                SuggestionListItem(
                                    suggestion = suggestion,
                                    onClick = { onSuggestionClick(suggestion.id) },
                                )
                            }
                        }
                        if (errorMessage != null) {
                            item(key = "load_more_error") {
                                SuggestionStatusMessage(
                                    message = errorMessage,
                                    actionLabel = "다시 시도",
                                    onActionClick = onLoadMore,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 12.dp),
                                )
                            }
                        }
                    }
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(end = 16.dp, bottom = 16.dp)
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(LiroutiTheme.colors.labelDefault)
                            .clickable {
                                coroutineScope.launch { listState.animateScrollToItem(0) }
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.chevron__left),
                            contentDescription = "맨 위로",
                            modifier = Modifier
                                .size(20.dp)
                                .rotate(90f),
                            colorFilter = ColorFilter.tint(LiroutiTheme.colors.labelReverse),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SuggestionListItem(
    suggestion: SuggestionUiModel,
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
            text = suggestion.categoryName,
            style = LiroutiTheme.typography.body1Bold,
            color = LiroutiTheme.colors.labelDefault,
        )
        Text(
            text = suggestion.date,
            style = LiroutiTheme.typography.body2LongRegular,
            color = LiroutiTheme.colors.labelInfo,
        )
    }
}

@Composable
private fun SuggestionStatusMessage(
    message: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onActionClick: () -> Unit = {},
) {
    Column(
        modifier = modifier.padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = message,
            style = LiroutiTheme.typography.body2LongRegular,
            color = LiroutiTheme.colors.labelInfo,
            textAlign = TextAlign.Center,
        )
        if (actionLabel != null) {
            Text(
                text = actionLabel,
                style = LiroutiTheme.typography.body2LongMedium,
                color = LiroutiTheme.colors.primaryNormal,
                modifier = Modifier.clickable(onClick = onActionClick),
            )
        }
    }
}

private val PreviewSuggestions = listOf(
    SuggestionUiModel(
        id = 1,
        categoryName = "버그 신고",
        date = "2026. 08. 19",
        content = "메인 화면에서 오늘 루틴이 더 잘 보이면 좋겠어요.",
    ),
    SuggestionUiModel(
        id = 2,
        categoryName = "기능 제안",
        date = "2026. 08. 18",
        content = "루틴 알림 시간을 분 단위로 설정할 수 있으면 좋겠습니다.",
    ),
)

@Preview(showBackground = true, heightDp = 800)
@Composable
private fun SuggestionListScreenPreview() {
    LiroutiFrontendTheme {
        SuggestionListScreen(
            onBackClick = {},
            onSuggestionClick = {},
            onCreateClick = {},
            onTabSelected = {},
            onLoadMore = {},
            onRetryClick = {},
            suggestions = PreviewSuggestions,
        )
    }
}
