package com.li_routi.feature.challenge.vm

import androidx.lifecycle.viewModelScope
import com.li_routi.core.common.android.architecture.BaseViewModel
import com.li_routi.core.common.kotlin.util.ResultState
import com.li_routi.core.domain.challenge.GetMyChallengesUseCase
import com.li_routi.core.domain.challenge.MyChallenge
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** "챌린지" 메인 화면 ViewModel. [GetMyChallengesUseCase]로 내가 참여 중인 챌린지 목록을 조회한다. */
class ChallengeViewModel(
    private val getMyChallengesUseCase: GetMyChallengesUseCase,
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(ChallengeUiState())
    val uiState: StateFlow<ChallengeUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    /**
     * 목록을 다시 불러온다. 챌린지 찾기에서 새로 참여하고 뒤로가기로 이 화면에 돌아와도
     * ViewModel은 백스택에 남아 있던 게 그대로 재사용되어(재생성이 아님) init{}이 다시 안 불려서,
     * 이 화면이 다시 보일 때(ON_RESUME)마다 [ChallengeNavHost]가 호출해 새로 참여한 챌린지가
     * 바로 반영되게 한다.
     */
    fun refresh() {
        viewModelScope.launch {
            when (val result = getMyChallengesUseCase()) {
                is ResultState.Success -> _uiState.update {
                    it.copy(isLoading = false, routines = result.data.map(MyChallenge::toRoutineUiModel))
                }
                is ResultState.Error -> _uiState.update { it.copy(isLoading = false) }
                ResultState.Loading -> Unit
            }
        }
    }
}

// "내 챌린지 목록" API(GET /api/members/me/challenges)는 routineCycle을 내려주지 않아
// Figma처럼 "매일 루틴" 배지는 못 채운다. 대신 이 목록은 정의상 전부 참여 중인 챌린지라
// "참여중" 배지로 대체한다.
private fun MyChallenge.toRoutineUiModel(): RoutineUiModel = RoutineUiModel(
    id = id,
    title = name,
    categories = listOf(category.toDisplayLabel()),
    description = description,
    badge = "참여중",
)
