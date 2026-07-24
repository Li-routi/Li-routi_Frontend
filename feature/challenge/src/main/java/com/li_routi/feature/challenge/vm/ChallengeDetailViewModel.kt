package com.li_routi.feature.challenge.vm

import androidx.lifecycle.viewModelScope
import com.li_routi.core.common.android.architecture.BaseViewModel
import com.li_routi.feature.challenge.navigation.ChallengeDetailScreenActions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * 챌린지 상세 화면 ViewModel.
 *
 * [ChallengeDetailScreenActions]를 구현해 참여/탭 전환/페이지네이션/챌린지 나가기를 처리한다.
 * 실제 서버 연동(참여 신호 전송, 챌린지 나가기)은 이후 단계에서 추가하고, 지금은 로컬 상태만 갱신한다.
 */
class ChallengeDetailViewModel(
    challengeId: Long,
    initialState: ChallengeDetailUiState = ChallengeDetailUiState(challengeId = challengeId),
) : BaseViewModel(), ChallengeDetailScreenActions {

    private val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<ChallengeDetailUiState> = _uiState.asStateFlow()

    override fun onJoinClick() {
        if (_uiState.value.isJoined) return
        _uiState.update { it.copy(isJoined = true) }
        viewModelScope.launch {
            // TODO: 실제 참여 API 연동 — 서버에 "이 사용자가 참여 중"이라는 신호를 보내야 함 (이번 범위 제외)
        }
    }

    override fun onTabSelected(tab: CertificationTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    override fun onLoadMore() {
        _uiState.update { state ->
            if (!state.hasMoreCertifications) return@update state
            when (state.selectedTab) {
                CertificationTab.All -> state.copy(
                    visibleAllCount = (state.visibleAllCount + CertificationPageSize)
                        .coerceAtMost(state.allCertifications.size),
                )
                CertificationTab.Mine -> state.copy(
                    visibleMyCount = (state.visibleMyCount + CertificationPageSize)
                        .coerceAtMost(state.myCertifications.size),
                )
            }
        }
    }

    override fun onLeaveChallengeClick() {
        // TODO: 실제 챌린지 나가기 API 연동 (이번 범위 제외)
        _uiState.update { it.copy(isJoined = false) }
    }
}
