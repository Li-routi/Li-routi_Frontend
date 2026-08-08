package com.li_routi.core.designsystem.component

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.li_routi.core.designsystem.theme.LiroutiTheme

/**
 * 당겨서 새로고침 컨테이너. 상단바 바로 아래(콘텐츠 영역)를 감싸서 쓴다 — 고정 헤더는 이 안에 넣지 않는다.
 * Material3 [PullToRefreshBox]를 브랜드 컬러(primaryNormal)로 감싼 공용 컴포넌트.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiroutiPullToRefreshBox(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    val state = rememberPullToRefreshState()
    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        state = state,
        modifier = modifier,
        indicator = {
            PullToRefreshDefaults.Indicator(
                state = state,
                isRefreshing = isRefreshing,
                modifier = Modifier.align(Alignment.TopCenter),
                containerColor = LiroutiTheme.colors.backgroundDefault,
                color = LiroutiTheme.colors.primaryNormal,
            )
        },
        content = content,
    )
}
