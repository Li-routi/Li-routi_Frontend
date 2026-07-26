package com.li_routi.feature.challenge.vm

import androidx.lifecycle.viewModelScope
import com.li_routi.core.common.android.architecture.BaseViewModel
import com.li_routi.core.common.kotlin.util.ResultState
import com.li_routi.core.domain.challenge.Challenge
import com.li_routi.core.domain.challenge.ChallengeCategory
import com.li_routi.core.domain.challenge.GetChallengesUseCase
import com.li_routi.feature.challenge.component.ChallengeCardUiModel
import com.li_routi.feature.challenge.navigation.FindChallengeScreenActions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val PageSize = 20

/**
 * "챌린지 찾아보기" 화면 ViewModel. [GetChallengesUseCase]로 목록을 조회한다.
 *
 * 검색어(keyword)와 무한 스크롤(cursor)은 이번 범위에서 제외하고, 카테고리 필터만 서버에 반영한다.
 */
class FindChallengeViewModel(
    private val getChallengesUseCase: GetChallengesUseCase,
) : BaseViewModel(), FindChallengeScreenActions {

    private val _uiState = MutableStateFlow(FindChallengeUiState())
    val uiState: StateFlow<FindChallengeUiState> = _uiState.asStateFlow()

    init {
        loadChallenges(category = null)
    }

    override fun onCategorySelected(category: ChallengeCategory?) {
        if (_uiState.value.selectedCategory == category) return
        loadChallenges(category)
    }

    override fun onRetryClick() {
        loadChallenges(_uiState.value.selectedCategory)
    }

    private fun loadChallenges(category: ChallengeCategory?) {
        _uiState.update { it.copy(isLoading = true, selectedCategory = category, errorMessage = null) }
        viewModelScope.launch {
            when (val result = getChallengesUseCase(category = category, size = PageSize)) {
                is ResultState.Success -> _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        challenges = result.data.challenges.map { it.toCardUiModel() },
                        errorMessage = null,
                    )
                }
                is ResultState.Error -> _uiState.update { state ->
                    state.copy(isLoading = false, errorMessage = result.message)
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
