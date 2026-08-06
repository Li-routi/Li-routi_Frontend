package com.li_routi.feature.challenge.vm

import androidx.lifecycle.viewModelScope
import com.li_routi.core.common.android.architecture.BaseViewModel
import com.li_routi.core.common.kotlin.util.ResultState
import com.li_routi.core.domain.challenge.Certification
import com.li_routi.core.domain.challenge.ChallengeDetail
import com.li_routi.core.domain.challenge.EditVerificationUseCase
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
 * 현재 인증 주기에 이미 인증했는지 여부([ChallengeDetailUiState.verifiedInCurrentPeriod])는 챌린지
 * 상세 조회(GET /api/challenges/{id}) 응답을 그대로 반영하므로, 챌린지를 나갔다 다시 들어와도
 * (ViewModel이 새로 생성되며 loadDetail()이 다시 호출되므로) 서버 값 그대로 유지된다.
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
    private val editVerificationUseCase: EditVerificationUseCase,
) : BaseViewModel(), ChallengeDetailScreenActions {

    private val _uiState = MutableStateFlow(ChallengeDetailUiState(challengeId = challengeId))
    val uiState: StateFlow<ChallengeDetailUiState> = _uiState.asStateFlow()

    init {
        loadDetail()
        loadVerifications(cursor = null)
        loadMyVerifications(cursor = null)
    }

    // "인증"(전체)/"내 인증 보기" 두 탭 모두 각 1페이지를 다시 조회해 id가 같은 기존 항목의 좋아요
    // 수(전체 탭은 liked도)만 패치한다. 좋아요는 같은 verificationId를 두 탭이 같이 보여주는 값이라,
    // 지금 보고 있지 않은 탭 것도 같이 갱신해둬야 탭을 전환했을 때 곧바로 최신 값이 보인다.
    // 목록 순서·페이지네이션·스크롤 위치는 건드리지 않는다.
    private suspend fun refreshLikeCounts() {
        val allResult = getVerificationsUseCase(challengeId, cursor = null, size = VerificationPageSize)
        if (allResult is ResultState.Success) {
            val freshById = allResult.data.certifications.associateBy { it.id }
            _uiState.update { state ->
                state.copy(
                    allCertifications = state.allCertifications.map { certification ->
                        freshById[certification.id]?.let {
                            certification.copy(likeCount = it.likeCount, liked = it.liked)
                        } ?: certification
                    },
                )
            }
        }

        val mineResult = getMyVerificationsUseCase(challengeId, cursor = null, size = VerificationPageSize)
        if (mineResult is ResultState.Success) {
            val freshById = mineResult.data.certifications.associateBy { it.id }
            _uiState.update { state ->
                state.copy(
                    myCertifications = state.myCertifications.map { certification ->
                        freshById[certification.id]?.let { certification.copy(likeCount = it.likeCount) }
                            ?: certification
                    },
                )
            }
        }
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
        // 화면 진입 시 init에서 이미 조회를 시작하므로, 아직 안 끝났으면(isLoadingMoreMy) 여기서 또 쏘지 않는다.
        val state = _uiState.value
        if (tab == CertificationTab.Mine && !state.myLoaded && !state.isLoadingMoreMy) {
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

    override fun onEditCertificationSubmit(certificationId: Long, content: String) {
        // isSubmittingEdit은 아래 요청의 성공/실패 분기에서만 내린다(onEditCertificationDismiss는
        // 건드리지 않음) — 그래야 화면을 닫고 바로 재제출해도 이 가드가 진행 중인 요청을 확실히 막는다.
        if (_uiState.value.isSubmittingEdit) return
        _uiState.update { it.copy(isSubmittingEdit = true, editCertificationError = null, editedCertificationId = null) }
        viewModelScope.launch {
            when (val result = editVerificationUseCase(challengeId, certificationId, content)) {
                is ResultState.Success -> {
                    val updatedContent = result.data.content
                    _uiState.update { state ->
                        state.copy(
                            isSubmittingEdit = false,
                            editedCertificationId = certificationId,
                            allCertifications = state.allCertifications.withUpdatedContent(certificationId, updatedContent),
                            myCertifications = state.myCertifications.withUpdatedContent(certificationId, updatedContent),
                        )
                    }
                    // refreshLikeCounts는 likeCount/liked만 패치하고 content는 건드리지 않아, 방금
                    // 반영한 수정 내용을 덮어쓰지 않으면서 좋아요 수만 즉시 최신화된다.
                    refreshLikeCounts()
                }
                is ResultState.Error -> _uiState.update {
                    it.copy(isSubmittingEdit = false, editCertificationError = result.message)
                }
                ResultState.Loading -> Unit
            }
        }
    }

    // 화면 닫기(뒤로가기/성공 후 소비)는 에러/성공 신호만 지운다. isSubmittingEdit은 절대 여기서 안 건드린다
    // — 그렇지 않으면 요청이 아직 끝나지 않았는데 닫고 바로 재제출할 수 있게 되어, 두 요청이 순서
    // 뒤바뀌어 끝나면서 더 오래된 내용이 서버에 남는 레이스가 생긴다.
    override fun onEditCertificationDismiss() {
        _uiState.update { it.copy(editCertificationError = null, editedCertificationId = null) }
    }

    // 백엔드에 인증 삭제 API가 없어 버튼 UI만 우선 노출한다. API가 추가되면 여기서 호출하고
    // 성공 시 allCertifications/myCertifications에서 해당 항목을 제거하도록 연동한다.
    override fun onDeleteCertificationClick(certificationId: Long) = Unit

    override fun onReportCertificationClick(certificationId: Long, reason: String?) {
        viewModelScope.launch {
            reportVerificationUseCase(challengeId, certificationId, reason)
        }
    }

    // 새 인증 업로드는 사진 촬영이 필요해 화면(Route)에서 직접 처리하고, 성공 후 여기로 알려온다.
    // 상세(참여자/게시글 수)와 "인증"/"내 인증 보기" 두 탭 모두 처음부터 다시 불러온다.
    override fun onVerificationSubmitted() {
        loadDetail()
        _uiState.update {
            it.copy(
                allCertifications = emptyList(),
                allCursor = null,
                allHasNext = true,
                myCertifications = emptyList(),
                myCursor = null,
                myHasNext = true,
                myLoaded = false,
                // 방금 인증을 등록했으니, 아래 loadDetail()의 서버 응답이 오기 전에도 버튼이 즉시
                // "인증 완료"로 바뀌도록 우선 반영한다. loadDetail()이 곧 서버 값으로 덮어쓴다.
                verifiedInCurrentPeriod = true,
            )
        }
        loadVerifications(cursor = null)
        if (_uiState.value.selectedTab == CertificationTab.Mine) {
            loadMyVerifications(cursor = null)
        }
    }

    // 당겨서 새로고침. loadDetail/loadVerifications류와 달리 각 요청을 이 코루틴 안에서 직접 await해
    // isRefreshing을 "실제로 다 끝났을 때"만 내린다(그 함수들은 fire-and-forget이라 그대로 못 씀).
    override fun onRefresh() {
        if (_uiState.value.isRefreshing) return
        _uiState.update { it.copy(isRefreshing = true) }
        viewModelScope.launch {
            when (val result = getChallengeDetailUseCase(challengeId)) {
                is ResultState.Success -> _uiState.update { it.applyDetail(result.data) }
                is ResultState.Error -> Unit
                ResultState.Loading -> Unit
            }
            when (val result = getVerificationsUseCase(challengeId, cursor = null, VerificationPageSize)) {
                is ResultState.Success -> _uiState.update { state ->
                    state.copy(
                        allCertifications = result.data.certifications.map { it.toUiModel() },
                        allCursor = result.data.nextCursor,
                        allHasNext = result.data.hasNext,
                    )
                }
                is ResultState.Error -> Unit
                ResultState.Loading -> Unit
            }
            if (_uiState.value.selectedTab == CertificationTab.Mine) {
                when (val result = getMyVerificationsUseCase(challengeId, cursor = null, VerificationPageSize)) {
                    is ResultState.Success -> _uiState.update { state ->
                        state.copy(
                            myCertifications = result.data.certifications.map { it.toUiModel() },
                            myCursor = result.data.nextCursor,
                            myHasNext = result.data.hasNext,
                            myLoaded = true,
                        )
                    }
                    is ResultState.Error -> Unit
                    ResultState.Loading -> Unit
                }
            }
            _uiState.update { it.copy(isRefreshing = false) }
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
                // 내가 누른 항목은 위에서 이미 갱신했지만, 그 사이 다른 사람이 누른 좋아요도 같이
                // 반영되도록 15초를 기다리지 않고 바로 한 번 더 조용히 새로고침한다.
                refreshLikeCounts()
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
    // 서버가 필드를 안 내려주면(null) "인증 안 함"으로 단정하지 않고 기존에 알던 값을 그대로 둔다.
    verifiedInCurrentPeriod = detail.verifiedInCurrentPeriod ?: verifiedInCurrentPeriod,
)

private fun Certification.toUiModel(): CertificationUiModel = CertificationUiModel(
    id = id,
    authorName = authorName,
    content = content,
    imageUrl = imageUrl,
    timeLabel = verifiedAt.toDisplayTimeLabel(),
    likeCount = likeCount,
    liked = liked,
    isMine = isMine,
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
