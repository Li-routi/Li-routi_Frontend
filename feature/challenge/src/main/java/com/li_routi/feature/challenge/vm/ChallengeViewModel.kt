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
// badge("매일 루틴" 등)를 채울 수 없다. 서버에 필드가 추가되기 전까지는 빈 값으로 둔다.
private fun MyChallenge.toRoutineUiModel(): RoutineUiModel = RoutineUiModel(
    id = id,
    title = name,
    categories = listOf(category.toDisplayLabel()),
    badge = "",
)
