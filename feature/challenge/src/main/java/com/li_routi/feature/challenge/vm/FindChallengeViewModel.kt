package com.li_routi.feature.challenge.vm

import androidx.lifecycle.viewModelScope
import com.li_routi.core.common.android.architecture.BaseViewModel
import com.li_routi.core.common.kotlin.util.ResultState
import com.li_routi.core.domain.challenge.Challenge
import com.li_routi.core.domain.challenge.ChallengeCategory
import com.li_routi.core.domain.challenge.GetChallengesUseCase
import com.li_routi.feature.challenge.component.ChallengeCardUiModel
import com.li_routi.feature.challenge.navigation.FindChallengeScreenActions
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val PageSize = 20
private const val SearchDebounceMillis = 300L

/**
 * "챌린지 찾아보기" 화면 ViewModel. [GetChallengesUseCase]로 목록을 조회한다.
 *
 * 카테고리 필터는 선택 즉시 반영하고, 검색어(keyword)는 타이핑마다 서버를 호출하지 않도록
 * [SearchDebounceMillis] 동안 입력이 없을 때만 조회한다. 무한 스크롤(cursor)은 이번 범위 제외.
 */
class FindChallengeViewModel(
    private val getChallengesUseCase: GetChallengesUseCase,
) : BaseViewModel(), FindChallengeScreenActions {

    private val _uiState = MutableStateFlow(FindChallengeUiState())
    val uiState: StateFlow<FindChallengeUiState> = _uiState.asStateFlow()

    private var searchDebounceJob: Job? = null
    // 카테고리 변경/디바운스된 검색/재시도가 겹치면 먼저 시작된 느린 요청이 나중에 끝나 최신 상태를
    // 덮어쓸 수 있다. 새 조회를 시작할 때마다 이전 조회 job을 취소해 항상 마지막 요청만 반영되게 한다.
    private var loadJob: Job? = null
    private var loadMoreJob: Job? = null

    init {
        loadChallenges(category = null, keyword = null)
    }

    override fun onCategorySelected(category: ChallengeCategory?) {
        if (_uiState.value.selectedCategory == category) return
        searchDebounceJob?.cancel()
        loadChallenges(category, _uiState.value.searchQuery)
    }

    override fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        searchDebounceJob?.cancel()
        searchDebounceJob = viewModelScope.launch {
            delay(SearchDebounceMillis)
            loadChallenges(_uiState.value.selectedCategory, query)
        }
    }

    override fun onRetryClick() {
        searchDebounceJob?.cancel()
        loadChallenges(_uiState.value.selectedCategory, _uiState.value.searchQuery)
    }

    private fun loadChallenges(category: ChallengeCategory?, keyword: String?) {
        loadJob?.cancel()
        // 필터/검색이 바뀌면 목록이 통째로 교체되므로, 이전 목록 기준으로 진행 중이던 다음 페이지
        // 요청은 더 이상 의미가 없다 — 같이 취소해 그 응답이 늦게 도착해 새 목록 뒤에 잘못 이어붙는 걸 막는다.
        loadMoreJob?.cancel()
        _uiState.update {
            it.copy(
                isLoading = true,
                isLoadingMore = false,
                selectedCategory = category,
                errorMessage = null,
                loadMoreError = null,
            )
        }
        loadJob = viewModelScope.launch {
            when (
                val result = getChallengesUseCase(
                    category = category,
                    keyword = keyword?.trim()?.takeIf { it.isNotEmpty() },
                    size = PageSize,
                )
            ) {
                is ResultState.Success -> _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        challenges = result.data.challenges.map { it.toCardUiModel() },
                        errorMessage = null,
                        nextCursor = result.data.nextCursor,
                        hasNext = result.data.hasNext,
                    )
                }
                is ResultState.Error -> _uiState.update { state ->
                    state.copy(isLoading = false, errorMessage = result.message)
                }
                ResultState.Loading -> Unit
            }
        }
    }

    override fun onLoadMore() {
        val state = _uiState.value
        val cursor = state.nextCursor
        if (state.isLoading || state.isLoadingMore || !state.hasNext || cursor == null) return

        loadMoreJob?.cancel()
        _uiState.update { it.copy(isLoadingMore = true, loadMoreError = null) }
        loadMoreJob = viewModelScope.launch {
            when (
                val result = getChallengesUseCase(
                    category = state.selectedCategory,
                    keyword = state.searchQuery.trim().takeIf { it.isNotEmpty() },
                    cursor = cursor,
                    size = PageSize,
                )
            ) {
                is ResultState.Success -> _uiState.update { s ->
                    s.copy(
                        isLoadingMore = false,
                        challenges = s.challenges + result.data.challenges.map { it.toCardUiModel() },
                        nextCursor = result.data.nextCursor,
                        hasNext = result.data.hasNext,
                    )
                }
                // 다음 페이지 실패는 기존 목록을 지우지 않고 조용히 멈춘다. hasNext는 끄지 않는다 —
                // 이 실패가 서버에 더 이상 페이지가 없다는 뜻은 아니므로, 재시도하면 다시 불러올 수 있어야
                // 한다. 대신 loadMoreError로 목록 하단에 재시도 UI를 보여준다.
                is ResultState.Error -> _uiState.update {
                    it.copy(isLoadingMore = false, loadMoreError = result.message)
                }
                ResultState.Loading -> Unit
            }
        }
    }
}

private fun Challenge.toCardUiModel(): ChallengeCardUiModel = ChallengeCardUiModel(
    id = id,
    title = name,
    tagLabel = category.toDisplayLabel(),
    description = description,
    badge = routineCycle.toBadgeLabel(),
    participantCount = participantCount.toIntClamped(),
    rewardCount = reward,
    postCount = verificationPostCount.toIntClamped(),
)

private fun Long.toIntClamped(): Int = coerceIn(0L, Int.MAX_VALUE.toLong()).toInt()
