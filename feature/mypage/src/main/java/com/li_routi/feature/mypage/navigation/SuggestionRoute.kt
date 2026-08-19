package com.li_routi.feature.mypage.navigation

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.li_routi.core.common.ui.nav.AppBottomTab
import com.li_routi.feature.mypage.screen.SuggestionDetailScreen
import com.li_routi.feature.mypage.screen.SuggestionEditorScreen
import com.li_routi.feature.mypage.screen.SuggestionListScreen
import com.li_routi.feature.mypage.vm.SuggestionUiEvent
import com.li_routi.feature.mypage.vm.SuggestionViewModel

private enum class SuggestionDestination {
    List,
    Detail,
    Create,
}

/**
 * 건의하기 진입점. 목록은 커서 페이지네이션, 작성은 서버 분류 + 본문만 받는다.
 */
@Composable
fun SuggestionRoute(
    onBackClick: () -> Unit,
    onTabSelected: (AppBottomTab) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SuggestionViewModel = viewModel { SuggestionViewModel() },
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var destination by rememberSaveable { mutableStateOf(SuggestionDestination.List) }
    var selectedId by rememberSaveable { mutableStateOf<Long?>(null) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.refresh()
    }

    LaunchedEffect(destination) {
        if (destination == SuggestionDestination.Create) {
            viewModel.loadCategories()
        }
    }

    LaunchedEffect(viewModel) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                SuggestionUiEvent.Created -> destination = SuggestionDestination.List
                is SuggestionUiEvent.ShowError -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    BackHandler(enabled = destination != SuggestionDestination.List) {
        destination = SuggestionDestination.List
        selectedId = null
    }

    when (destination) {
        SuggestionDestination.List -> SuggestionListScreen(
            onBackClick = onBackClick,
            onSuggestionClick = { id ->
                selectedId = id
                destination = SuggestionDestination.Detail
            },
            onCreateClick = { destination = SuggestionDestination.Create },
            onTabSelected = onTabSelected,
            onLoadMore = viewModel::loadMore,
            onRetryClick = viewModel::refresh,
            suggestions = uiState.items,
            isLoading = uiState.isLoading,
            hasNext = uiState.hasNext,
            errorMessage = uiState.listError,
            modifier = modifier,
        )

        SuggestionDestination.Detail -> {
            val suggestion = uiState.items.firstOrNull { it.id == selectedId }
            if (suggestion == null) {
                LaunchedEffect(selectedId) {
                    destination = SuggestionDestination.List
                    selectedId = null
                }
            } else {
                SuggestionDetailScreen(
                    suggestion = suggestion,
                    onBackClick = {
                        destination = SuggestionDestination.List
                        selectedId = null
                    },
                    modifier = modifier,
                )
            }
        }

        SuggestionDestination.Create -> SuggestionEditorScreen(
            onBackClick = { destination = SuggestionDestination.List },
            onSaveClick = viewModel::create,
            categories = uiState.categories,
            selectedCategoryId = uiState.selectedCategoryId,
            onCategorySelected = viewModel::onCreateCategorySelected,
            isSaving = uiState.isSaving,
            isCategoriesLoading = uiState.isCategoriesLoading,
            categoriesError = uiState.categoriesError,
            onRetryCategories = viewModel::loadCategories,
            modifier = modifier,
        )
    }
}
