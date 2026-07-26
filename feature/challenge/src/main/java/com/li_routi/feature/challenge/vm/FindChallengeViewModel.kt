package com.li_routi.feature.challenge.vm

import androidx.lifecycle.viewModelScope
import com.li_routi.core.common.android.architecture.BaseViewModel
import com.li_routi.core.common.kotlin.util.ResultState
import com.li_routi.core.domain.challenge.Challenge
import com.li_routi.core.domain.challenge.ChallengeCategory
import com.li_routi.core.domain.challenge.GetChallengesUseCase
import com.li_routi.core.domain.challenge.RoutineCycle
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
    tagLabel = category.toLabel(),
    description = description,
    badge = routineCycle.toBadgeLabel(),
    participantCount = participantCount.toIntClamped(),
    // 백엔드 목록 API(GET /api/challenges)에 리워드 필드가 없어 임시로 0 처리.
    rewardCount = 0,
    postCount = verificationPostCount.toIntClamped(),
)

private fun ChallengeCategory.toLabel(): String = when (this) {
    ChallengeCategory.HEALTH -> "건강"
    ChallengeCategory.EXERCISE -> "운동"
    ChallengeCategory.STUDY -> "공부"
    ChallengeCategory.LIFE -> "생활"
    ChallengeCategory.HOBBY -> "취미"
}

private fun RoutineCycle.toBadgeLabel(): String = when (this) {
    RoutineCycle.DAILY -> "매일 루틴"
    RoutineCycle.WEEKLY -> "주간 루틴"
    RoutineCycle.MONTHLY -> "월간 루틴"
}

private fun Long.toIntClamped(): Int = coerceIn(0L, Int.MAX_VALUE.toLong()).toInt()
