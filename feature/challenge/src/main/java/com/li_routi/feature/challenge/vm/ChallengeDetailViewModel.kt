package com.li_routi.feature.challenge.vm

import androidx.lifecycle.viewModelScope
import com.li_routi.core.common.android.architecture.BaseViewModel
import com.li_routi.core.common.kotlin.util.ResultState
import com.li_routi.core.domain.challenge.Certification
import com.li_routi.core.domain.challenge.ChallengeDetail
import com.li_routi.core.domain.challenge.GetChallengeDetailUseCase
import com.li_routi.core.domain.challenge.GetVerificationsUseCase
import com.li_routi.core.domain.challenge.LeaveChallengeUseCase
import com.li_routi.core.domain.challenge.ParticipateChallengeUseCase
import com.li_routi.feature.challenge.navigation.ChallengeDetailScreenActions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val VerificationPageSize = 20

/**
 * 챌린지 상세 화면 ViewModel.
 *
 * 상세 정보와 "인증"(전체) 탭은 실제 API로 조회한다. "내 인증 보기" 탭은 필터 API가 아직 없어
 * 더미 데이터([SampleMyCertifications])를 그대로 사용한다.
 */
class ChallengeDetailViewModel(
    private val challengeId: Long,
    private val getChallengeDetailUseCase: GetChallengeDetailUseCase,
    private val getVerificationsUseCase: GetVerificationsUseCase,
    private val participateChallengeUseCase: ParticipateChallengeUseCase,
    private val leaveChallengeUseCase: LeaveChallengeUseCase,
) : BaseViewModel(), ChallengeDetailScreenActions {

    private val _uiState = MutableStateFlow(ChallengeDetailUiState(challengeId = challengeId))
    val uiState: StateFlow<ChallengeDetailUiState> = _uiState.asStateFlow()

    init {
        loadDetail()
        loadVerifications(cursor = null)
    }

    private fun loadDetail() {
        viewModelScope.launch {
            when (val result = getChallengeDetailUseCase(challengeId)) {
                is ResultState.Success -> _uiState.update { it.applyDetail(result.data) }
                is ResultState.Error -> _uiState.update { it.copy(isLoading = false) }
                ResultState.Loading -> Unit
            }
        }
    }

    private fun loadVerifications(cursor: Long?) {
        _uiState.update { it.copy(isLoadingMoreAll = true) }
        viewModelScope.launch {
            when (val result = getVerificationsUseCase(challengeId, cursor, VerificationPageSize)) {
                is ResultState.Success -> _uiState.update { state ->
                    state.copy(
                        isLoadingMoreAll = false,
                        allCertifications = state.allCertifications + result.data.certifications.map { it.toUiModel() },
                        allCursor = result.data.nextCursor,
                        allHasNext = result.data.hasNext,
                    )
                }
                is ResultState.Error -> _uiState.update { it.copy(isLoadingMoreAll = false, allHasNext = false) }
                ResultState.Loading -> Unit
            }
        }
    }

    override fun onJoinClick() {
        if (_uiState.value.isJoined) return
        viewModelScope.launch {
            when (val result = participateChallengeUseCase(challengeId)) {
                is ResultState.Success -> _uiState.update { it.copy(isJoined = result.data.participating) }
                is ResultState.Error -> Unit
                ResultState.Loading -> Unit
            }
        }
    }

    override fun onTabSelected(tab: CertificationTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    override fun onLoadMore() {
        val state = _uiState.value
        if (!state.hasMoreCertifications) return
        when (state.selectedTab) {
            CertificationTab.All -> {
                if (!state.isLoadingMoreAll) loadVerifications(cursor = state.allCursor)
            }
            CertificationTab.Mine -> _uiState.update {
                it.copy(
                    visibleMyCount = (it.visibleMyCount + CertificationPageSize)
                        .coerceAtMost(it.myCertifications.size),
                )
            }
        }
    }

    override fun onLeaveChallengeClick() {
        viewModelScope.launch {
            when (val result = leaveChallengeUseCase(challengeId)) {
                is ResultState.Success -> _uiState.update { it.copy(isJoined = result.data.participating) }
                is ResultState.Error -> Unit
                ResultState.Loading -> Unit
            }
        }
    }
}

private fun ChallengeDetailUiState.applyDetail(detail: ChallengeDetail): ChallengeDetailUiState = copy(
    isLoading = false,
    title = detail.name,
    badge = detail.routineCycle.toBadgeLabel(),
    description = detail.description,
    participantCount = detail.participantCount.toIntClamped(),
    activityCount = detail.todayCompletionCount.toIntClamped(),
    postCount = detail.verificationPostCount.toIntClamped(),
    isJoined = detail.participating,
)

private fun Certification.toUiModel(): CertificationUiModel = CertificationUiModel(
    id = id,
    authorName = authorName,
    content = content,
    timeLabel = verifiedAt.toDisplayTimeLabel(),
)

// 서버는 ISO 날짜/시간 문자열(verifiedAt)만 내려주고 상대 시간("9시간 전") 포맷은 제공하지 않는다.
// java.time은 minSdk 24에서 데스슈가링 없이는 쓸 수 없어, 날짜/시간 부분만 잘라 간단히 표시한다.
private fun String.toDisplayTimeLabel(): String = replace("T", " ").take(16)

private fun Long.toIntClamped(): Int = coerceIn(0L, Int.MAX_VALUE.toLong()).toInt()
