package com.li_routi.feature.challenge.vm

import androidx.lifecycle.viewModelScope
import com.li_routi.core.common.android.architecture.BaseViewModel
import com.li_routi.core.common.kotlin.util.ResultState
import com.li_routi.core.domain.challenge.Certification
import com.li_routi.core.domain.challenge.ChallengeDetail
import com.li_routi.core.domain.challenge.GetChallengeDetailUseCase
import com.li_routi.core.domain.challenge.GetMyVerificationsUseCase
import com.li_routi.core.domain.challenge.GetVerificationsUseCase
import com.li_routi.core.domain.challenge.LeaveChallengeUseCase
import com.li_routi.core.domain.challenge.LikeResult
import com.li_routi.core.domain.challenge.LikeVerificationUseCase
import com.li_routi.core.domain.challenge.MyCertification
import com.li_routi.core.domain.challenge.ParticipateChallengeUseCase
import com.li_routi.core.domain.challenge.ReportVerificationUseCase
import com.li_routi.core.domain.challenge.UnlikeVerificationUseCase
import com.li_routi.feature.challenge.navigation.ChallengeDetailScreenActions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val VerificationPageSize = 20

/**
 * 챌린지 상세 화면 ViewModel. 상세 정보, "인증"(전체), "내 인증 보기" 모두 실제 API로 조회한다.
 *
 * "내 인증 보기"는 최초로 그 탭이 선택될 때 지연 로드한다(항상 필요한 데이터가 아니라서).
 */
class ChallengeDetailViewModel(
    private val challengeId: Long,
    private val getChallengeDetailUseCase: GetChallengeDetailUseCase,
    private val getVerificationsUseCase: GetVerificationsUseCase,
    private val getMyVerificationsUseCase: GetMyVerificationsUseCase,
    private val participateChallengeUseCase: ParticipateChallengeUseCase,
    private val leaveChallengeUseCase: LeaveChallengeUseCase,
    private val reportVerificationUseCase: ReportVerificationUseCase,
    private val likeVerificationUseCase: LikeVerificationUseCase,
    private val unlikeVerificationUseCase: UnlikeVerificationUseCase,
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

    private fun loadMyVerifications(cursor: Long?) {
        _uiState.update { it.copy(isLoadingMoreMy = true) }
        viewModelScope.launch {
            when (val result = getMyVerificationsUseCase(challengeId, cursor, VerificationPageSize)) {
                is ResultState.Success -> _uiState.update { state ->
                    state.copy(
                        isLoadingMoreMy = false,
                        myLoaded = true,
                        myCertifications = state.myCertifications + result.data.certifications.map { it.toUiModel() },
                        myCursor = result.data.nextCursor,
                        myHasNext = result.data.hasNext,
                    )
                }
                is ResultState.Error -> _uiState.update { it.copy(isLoadingMoreMy = false, myHasNext = false, myLoaded = true) }
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
        if (tab == CertificationTab.Mine && !_uiState.value.myLoaded) {
            loadMyVerifications(cursor = null)
        }
    }

    override fun onLoadMore() {
        val state = _uiState.value
        if (!state.hasMoreCertifications) return
        when (state.selectedTab) {
            CertificationTab.All -> if (!state.isLoadingMoreAll) loadVerifications(cursor = state.allCursor)
            CertificationTab.Mine -> if (!state.isLoadingMoreMy) loadMyVerifications(cursor = state.myCursor)
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

    // 인증 수정 API가 아직 없어(백엔드에 PATCH 엔드포인트 없음) 화면에서 바로 보이도록 로컬 상태만 갱신한다.
    override fun onEditCertificationSubmit(certificationId: Long, content: String) {
        _uiState.update { state ->
            state.copy(
                allCertifications = state.allCertifications.withUpdatedContent(certificationId, content),
                myCertifications = state.myCertifications.withUpdatedContent(certificationId, content),
            )
        }
    }

    override fun onReportCertificationClick(certificationId: Long) {
        viewModelScope.launch {
            reportVerificationUseCase(challengeId, certificationId)
        }
    }

    // 좋아요 취소/좋아요는 "인증"(전체) 탭 항목에서만 호출된다("내 인증 보기" 응답엔 liked 상태가 없음).
    override fun onLikeToggleClick(certificationId: Long) {
        val target = _uiState.value.allCertifications.find { it.id == certificationId } ?: return
        viewModelScope.launch {
            val result = if (target.liked) {
                unlikeVerificationUseCase(challengeId, certificationId)
            } else {
                likeVerificationUseCase(challengeId, certificationId)
            }
            if (result is ResultState.Success) {
                _uiState.update { state ->
                    state.copy(allCertifications = state.allCertifications.withLikeResult(result.data))
                }
            }
        }
    }
}

private fun List<CertificationUiModel>.withUpdatedContent(id: Long, content: String): List<CertificationUiModel> =
    map { if (it.id == id) it.copy(content = content) else it }

private fun List<CertificationUiModel>.withLikeResult(result: LikeResult): List<CertificationUiModel> =
    map { if (it.id == result.verificationId) it.copy(likeCount = result.likeCount, liked = result.liked) else it }

private fun ChallengeDetailUiState.applyDetail(detail: ChallengeDetail): ChallengeDetailUiState = copy(
    isLoading = false,
    title = detail.name,
    badge = detail.routineCycle.toBadgeLabel(),
    description = detail.description,
    participantCount = detail.participantCount.toIntClamped(),
    rewardCount = detail.reward,
    postCount = detail.verificationPostCount.toIntClamped(),
    isJoined = detail.participating,
)

private fun Certification.toUiModel(): CertificationUiModel = CertificationUiModel(
    id = id,
    authorName = authorName,
    content = content,
    imageUrl = imageUrl,
    timeLabel = verifiedAt.toDisplayTimeLabel(),
    likeCount = likeCount,
    liked = liked,
    isMine = false,
)

// "내 인증 보기"는 항상 로그인한 본인의 게시글이라 별도 작성자 이름을 내려주지 않는다.
private fun MyCertification.toUiModel(): CertificationUiModel = CertificationUiModel(
    id = id,
    authorName = "나",
    content = content,
    imageUrl = imageUrl,
    timeLabel = verifiedAt.toDisplayTimeLabel(),
    likeCount = likeCount,
    liked = false,
    isMine = true,
)

// 서버는 ISO 날짜/시간 문자열(verifiedAt)만 내려주고 상대 시간("9시간 전") 포맷은 제공하지 않는다.
// java.time은 minSdk 24에서 데스슈가링 없이는 쓸 수 없어, 날짜/시간 부분만 잘라 간단히 표시한다.
private fun String.toDisplayTimeLabel(): String = replace("T", " ").take(16)

private fun Long.toIntClamped(): Int = coerceIn(0L, Int.MAX_VALUE.toLong()).toInt()
