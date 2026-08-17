package com.li_routi.feature.challenge.vm

import androidx.lifecycle.viewModelScope
import com.li_routi.core.common.android.architecture.BaseViewModel
import com.li_routi.core.common.kotlin.util.ResultState
import com.li_routi.core.domain.challenge.Certification
import com.li_routi.core.domain.challenge.ChallengeDetail
import com.li_routi.core.domain.challenge.DeleteVerificationUseCase
import com.li_routi.core.domain.challenge.EditVerificationUseCase
import com.li_routi.core.domain.challenge.GetChallengeDetailUseCase
import com.li_routi.core.domain.challenge.GetMyVerificationsUseCase
import com.li_routi.core.domain.challenge.GetVerificationsUseCase
import com.li_routi.core.domain.challenge.LeaveChallengeUseCase
import com.li_routi.core.domain.challenge.LikeResult
import com.li_routi.core.domain.challenge.LikeVerificationUseCase
import com.li_routi.core.domain.challenge.MyCertification
import com.li_routi.core.domain.challenge.ParticipateChallengeUseCase
import com.li_routi.core.domain.challenge.ReportType
import com.li_routi.core.domain.challenge.ReportVerificationUseCase
import com.li_routi.core.domain.challenge.UnlikeVerificationUseCase
import com.li_routi.core.domain.challenge.VerificationSort
import com.li_routi.feature.challenge.navigation.ChallengeDetailScreenActions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val VerificationPageSize = 20
private const val MaxVerificationPageSize = 50

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
    private val deleteVerificationUseCase: DeleteVerificationUseCase,
    private val likeVerificationUseCase: LikeVerificationUseCase,
    private val unlikeVerificationUseCase: UnlikeVerificationUseCase,
    private val editVerificationUseCase: EditVerificationUseCase,
) : BaseViewModel(), ChallengeDetailScreenActions {

    private val _uiState = MutableStateFlow(ChallengeDetailUiState(challengeId = challengeId))
    val uiState: StateFlow<ChallengeDetailUiState> = _uiState.asStateFlow()

    // 새로고침/인증 등록으로 목록이 통째로 리셋될 때마다 올린다. loadVerifications/loadMyVerifications는
    // 요청을 시작한 시점의 값을 들고 있다가, 응답이 왔을 때 이 값이 그대로면(그 사이 리셋이 없었으면)만
    // 결과를 반영한다 — 그렇지 않으면 새로고침 도중 끝난 오래된 페이지네이션 응답이 새로 받아온
    // 목록 뒤에 잘못 이어붙거나 커서를 엉뚱한 값으로 덮어쓸 수 있다.
    private var certificationGeneration = 0

    init {
        loadDetail()
        loadVerifications(cursor = null, cursorLikeCount = null, generation = certificationGeneration)
        loadMyVerifications(cursor = null, cursorLikeCount = null, generation = certificationGeneration)
        loadHeroImage()
    }

    /**
     * 대문 이미지용 "좋아요 1위" 인증을 딱 1건만 조회한다. 목록의 [ChallengeDetailUiState.selectedSort]와
     * 무관하게 항상 sort=LIKES로 별도 조회한다 — 사용자가 목록 정렬을 최신순으로 바꿔도 대문은 안 바뀌어야 한다.
     */
    private fun loadHeroImage() {
        // 조회 도중 새로고침/인증 등록 등으로 목록이 통째로 리셋됐으면(generation 변경) 이 응답은
        // 버린다 — 그렇지 않으면 더 최근에 시작된 요청보다 먼저 끝난 오래된 응답이 대문 이미지를
        // 도로 옛 값으로 덮어쓸 수 있다.
        val generation = certificationGeneration
        viewModelScope.launch {
            val result = getVerificationsUseCase(
                challengeId,
                cursor = null,
                cursorLikeCount = null,
                size = 1,
                sort = VerificationSort.LIKES,
            )
            if (result is ResultState.Success && generation == certificationGeneration) {
                _uiState.update { it.copy(heroImageUrl = result.data.certifications.firstOrNull()?.imageUrl) }
            }
        }
    }

    // "인증"(전체)/"내 인증 보기" 두 탭 모두 각 1페이지를 다시 조회해 id가 같은 기존 항목의 좋아요
    // 수(전체 탭은 liked도)만 패치한다. 좋아요는 같은 verificationId를 두 탭이 같이 보여주는 값이라,
    // 지금 보고 있지 않은 탭 것도 같이 갱신해둬야 탭을 전환했을 때 곧바로 최신 값이 보인다.
    // 목록 순서·페이지네이션·스크롤 위치는 건드리지 않는다.
    private suspend fun refreshLikeCounts() {
        val generation = certificationGeneration
        val state = _uiState.value
        val sort = state.selectedSort
        // 이미 불러온 만큼(최대 서버 상한 50)을 다시 조회해야 뒤쪽 페이지 항목도 패치된다 —
        // 고정 20개(VerificationPageSize)만 조회하면 그 이후에 로드된 항목은 계속 stale로 남는다.
        val allSize = state.allCertifications.size.coerceIn(VerificationPageSize, MaxVerificationPageSize)
        val mySize = state.myCertifications.size.coerceIn(VerificationPageSize, MaxVerificationPageSize)

        val allResult = getVerificationsUseCase(
            challengeId,
            cursor = null,
            cursorLikeCount = null,
            size = allSize,
            sort = sort,
        )
        // 조회 도중 새로고침/정렬 변경 등으로 목록이 통째로 리셋됐으면(generation 변경) 이 응답은 버린다
        // — 그렇지 않으면 새로 불러온 목록에 오래된 좋아요 값이 섞여 들어갈 수 있다.
        if (allResult is ResultState.Success && generation == certificationGeneration) {
            val freshById = allResult.data.certifications.associateBy { it.id }
            _uiState.update { s ->
                s.copy(
                    allCertifications = s.allCertifications.map { certification ->
                        freshById[certification.id]?.let {
                            certification.copy(likeCount = it.likeCount, liked = it.liked)
                        } ?: certification
                    },
                ).withMyLikedSyncedFromAll()
            }
        }

        val mineResult = getMyVerificationsUseCase(
            challengeId,
            cursor = null,
            cursorLikeCount = null,
            size = mySize,
            sort = sort,
        )
        if (mineResult is ResultState.Success && generation == certificationGeneration) {
            val freshById = mineResult.data.certifications.associateBy { it.id }
            _uiState.update { s ->
                s.copy(
                    myCertifications = s.myCertifications.map { certification ->
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

    private fun loadVerifications(cursor: Long?, cursorLikeCount: Long?, generation: Int) {
        _uiState.update { it.copy(isLoadingMoreAll = true) }
        val sort = _uiState.value.selectedSort
        viewModelScope.launch {
            when (
                val result = getVerificationsUseCase(
                    challengeId,
                    cursor = cursor,
                    cursorLikeCount = cursorLikeCount,
                    size = VerificationPageSize,
                    sort = sort,
                )
            ) {
                is ResultState.Success -> {
                    if (generation != certificationGeneration) return@launch
                    _uiState.update { state ->
                        state.copy(
                            isLoadingMoreAll = false,
                            allCertifications = state.allCertifications + result.data.certifications.map { it.toUiModel() },
                            allCursor = result.data.nextCursor,
                            allCursorLikeCount = result.data.nextCursorLikeCount,
                            allHasNext = result.data.hasNext,
                        ).withMyLikedSyncedFromAll()
                    }
                }
                is ResultState.Error -> if (generation == certificationGeneration) {
                    _uiState.update { it.copy(isLoadingMoreAll = false, allHasNext = false) }
                }
                ResultState.Loading -> Unit
            }
        }
    }

    private fun loadMyVerifications(cursor: Long?, cursorLikeCount: Long?, generation: Int) {
        _uiState.update { it.copy(isLoadingMoreMy = true) }
        val sort = _uiState.value.selectedSort
        viewModelScope.launch {
            when (
                val result = getMyVerificationsUseCase(
                    challengeId,
                    cursor = cursor,
                    cursorLikeCount = cursorLikeCount,
                    size = VerificationPageSize,
                    sort = sort,
                )
            ) {
                is ResultState.Success -> {
                    if (generation != certificationGeneration) return@launch
                    _uiState.update { state ->
                        state.copy(
                            isLoadingMoreMy = false,
                            myLoaded = true,
                            myCertifications = state.myCertifications + result.data.certifications.map { it.toUiModel() },
                            myCursor = result.data.nextCursor,
                            myCursorLikeCount = result.data.nextCursorLikeCount,
                            myHasNext = result.data.hasNext,
                        ).withMyLikedSyncedFromAll()
                    }
                }
                is ResultState.Error -> if (generation == certificationGeneration) {
                    _uiState.update { it.copy(isLoadingMoreMy = false, myHasNext = false, myLoaded = true) }
                }
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
            loadMyVerifications(cursor = null, cursorLikeCount = null, generation = certificationGeneration)
        }
    }

    override fun onLoadMore() {
        val state = _uiState.value
        // 새로고침 중에는 페이지네이션을 막는다 — 그렇지 않으면 새로고침이 방금 리셋한 목록 뒤에
        // 새로고침 이전 커서 기준 응답이 잘못 이어붙을 수 있다.
        if (!state.hasMoreCertifications || state.isRefreshing) return
        when (state.selectedTab) {
            CertificationTab.All ->
                if (!state.isLoadingMoreAll) {
                    loadVerifications(cursor = state.allCursor, cursorLikeCount = state.allCursorLikeCount, generation = certificationGeneration)
                }
            CertificationTab.Mine ->
                if (!state.isLoadingMoreMy) {
                    loadMyVerifications(cursor = state.myCursor, cursorLikeCount = state.myCursorLikeCount, generation = certificationGeneration)
                }
        }
    }

    // 정렬 기준이 바뀌면 두 탭 모두 처음부터 다시 불러온다 — sort별로 커서(cursorLikeCount 포함) 의미가
    // 달라 이전 목록에 이어붙일 수 없다. onVerificationSubmitted와 동일한 "통째로 리셋" 패턴.
    override fun onSortSelected(sort: VerificationSort) {
        if (_uiState.value.selectedSort == sort) return
        certificationGeneration++
        _uiState.update {
            it.copy(
                selectedSort = sort,
                allCertifications = emptyList(),
                allCursor = null,
                allCursorLikeCount = null,
                allHasNext = true,
                isLoadingMoreAll = false,
                myCertifications = emptyList(),
                myCursor = null,
                myCursorLikeCount = null,
                myHasNext = true,
                myLoaded = false,
                isLoadingMoreMy = false,
            )
        }
        loadVerifications(cursor = null, cursorLikeCount = null, generation = certificationGeneration)
        // "내 인증 보기" 탭을 아직 한 번도 안 봤으면 여기서 미리 불러오지 않는다 — myLoaded=false로
        // 남겨두면 onTabSelected가 실제로 그 탭에 들어갈 때 알아서 불러온다(onVerificationSubmitted와
        // 동일한 패턴). "인증"(전체) 탭만 쓰는 사용자가 정렬을 바꿀 때마다 불필요한 API 호출이 나가는 걸 막는다.
        if (_uiState.value.selectedTab == CertificationTab.Mine) {
            loadMyVerifications(cursor = null, cursorLikeCount = null, generation = certificationGeneration)
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

    override fun onDeleteCertificationClick(certificationId: Long) {
        viewModelScope.launch {
            when (val result = deleteVerificationUseCase(challengeId, certificationId)) {
                is ResultState.Success -> {
                    _uiState.update { state ->
                        state.copy(
                            allCertifications = state.allCertifications.filterNot { it.id == certificationId },
                            myCertifications = state.myCertifications.filterNot { it.id == certificationId },
                            postCount = (state.postCount - 1).coerceAtLeast(0),
                            // 삭제는 내 게시글에만 가능하고, 한 주기(일/주/월)당 인증은 하나뿐이라(재인증 시
                            // 덮어쓰기) 삭제하면 그 주기는 항상 다시 "미인증" 상태가 된다(서버 스펙: 삭제 시
                            // 해당 주기가 재인증 가능하게 다시 열림).
                            verifiedInCurrentPeriod = false,
                        )
                    }
                    // 지운 게시글이 대문(좋아요 1위)이었을 수 있어 같이 새로고침한다.
                    loadHeroImage()
                }
                is ResultState.Error -> _uiState.update { it.copy(actionErrorMessage = result.message) }
                ResultState.Loading -> Unit
            }
        }
    }

    override fun onReportCertificationClick(certificationId: Long, reportType: ReportType, reason: String?) {
        viewModelScope.launch {
            when (val result = reportVerificationUseCase(challengeId, certificationId, reportType, reason)) {
                is ResultState.Error -> _uiState.update { it.copy(actionErrorMessage = result.message) }
                is ResultState.Success, ResultState.Loading -> Unit
            }
        }
    }

    override fun onActionErrorDismissed() {
        _uiState.update { it.copy(actionErrorMessage = null) }
    }

    // 새 인증 업로드는 사진 촬영이 필요해 화면(Route)에서 직접 처리하고, 성공 후 여기로 알려온다.
    // 상세(참여자/게시글 수)와 "인증"/"내 인증 보기" 두 탭 모두 처음부터 다시 불러온다.
    override fun onVerificationSubmitted() {
        // 목록을 통째로 리셋하므로, 이전 세대에서 진행 중이던 loadMore 응답이 나중에 도착해도
        // 무시되도록 세대를 올린다.
        certificationGeneration++
        loadDetail()
        _uiState.update {
            it.copy(
                allCertifications = emptyList(),
                allCursor = null,
                allCursorLikeCount = null,
                allHasNext = true,
                isLoadingMoreAll = false,
                myCertifications = emptyList(),
                myCursor = null,
                myCursorLikeCount = null,
                myHasNext = true,
                myLoaded = false,
                isLoadingMoreMy = false,
                // 방금 인증을 등록했으니, 아래 loadDetail()의 서버 응답이 오기 전에도 버튼이 즉시
                // "인증 완료"로 바뀌도록 우선 반영한다. loadDetail()이 곧 서버 값으로 덮어쓴다.
                verifiedInCurrentPeriod = true,
            )
        }
        loadVerifications(cursor = null, cursorLikeCount = null, generation = certificationGeneration)
        if (_uiState.value.selectedTab == CertificationTab.Mine) {
            loadMyVerifications(cursor = null, cursorLikeCount = null, generation = certificationGeneration)
        }
        // 방금 올린 인증이 좋아요 1위가 될 순 없지만(항상 0개로 시작), 대문에 걸려 있던 게시글이
        // 방금 삭제·재인증 등으로 바뀌었을 수 있어 같이 새로고침해둔다.
        loadHeroImage()
    }

    // 당겨서 새로고침. loadDetail/loadVerifications류와 달리 각 요청을 이 코루틴 안에서 직접 await해
    // isRefreshing을 "실제로 다 끝났을 때"만 내린다(그 함수들은 fire-and-forget이라 그대로 못 씀).
    override fun onRefresh() {
        if (_uiState.value.isRefreshing) return
        // 목록을 1페이지로 통째로 교체하므로, 새로고침 시작 전에 이미 진행 중이던 loadMore가 나중에
        // 도착해도(위 onLoadMore의 isRefreshing 가드를 통과해 이미 시작된 경우) 결과를 버리도록
        // 세대를 올린다.
        certificationGeneration++
        val generation = certificationGeneration
        _uiState.update { it.copy(isRefreshing = true, isLoadingMoreAll = false, isLoadingMoreMy = false) }
        val sort = _uiState.value.selectedSort
        viewModelScope.launch {
            when (val result = getChallengeDetailUseCase(challengeId)) {
                is ResultState.Success -> _uiState.update { it.applyDetail(result.data) }
                is ResultState.Error -> Unit
                ResultState.Loading -> Unit
            }
            when (
                val result = getVerificationsUseCase(challengeId, cursor = null, cursorLikeCount = null, size = VerificationPageSize, sort = sort)
            ) {
                // 응답을 기다리는 동안 정렬이 바뀌는 등으로 generation이 또 올라갔으면(더 새로운 리셋이
                // 이미 진행 중이면) 이 응답은 버린다 — 그렇지 않으면 오래된 정렬 기준 응답이 방금
                // 새로 불러온 목록을 덮어쓸 수 있다.
                is ResultState.Success -> if (generation == certificationGeneration) {
                    _uiState.update { state ->
                        state.copy(
                            allCertifications = result.data.certifications.map { it.toUiModel() },
                            allCursor = result.data.nextCursor,
                            allCursorLikeCount = result.data.nextCursorLikeCount,
                            allHasNext = result.data.hasNext,
                        ).withMyLikedSyncedFromAll()
                    }
                }
                is ResultState.Error -> Unit
                ResultState.Loading -> Unit
            }
            if (_uiState.value.selectedTab == CertificationTab.Mine) {
                when (
                    val result = getMyVerificationsUseCase(challengeId, cursor = null, cursorLikeCount = null, size = VerificationPageSize, sort = sort)
                ) {
                    is ResultState.Success -> if (generation == certificationGeneration) {
                        _uiState.update { state ->
                            state.copy(
                                myCertifications = result.data.certifications.map { it.toUiModel() },
                                myCursor = result.data.nextCursor,
                                myCursorLikeCount = result.data.nextCursorLikeCount,
                                myHasNext = result.data.hasNext,
                                myLoaded = true,
                            ).withMyLikedSyncedFromAll()
                        }
                    }
                    is ResultState.Error -> Unit
                    ResultState.Loading -> Unit
                }
            }
            // isRefreshing은 generation과 무관하게 항상 내린다 — 그렇지 않으면(다른 리셋이 그 사이
            // 끼어들어 generation이 바뀌었다는 이유로 안 내리면) onRefresh() 재진입 가드가 영영 막혀
            // 당겨서 새로고침을 다시는 못 쓰게 된다.
            _uiState.update { it.copy(isRefreshing = false) }
        }
        loadHeroImage()
    }

    // "인증"(전체)/"내 인증 보기" 어느 카드에서 눌러도 동작한다 — 같은 verificationId를 두 탭이
    // 같이 보여주는 값이라, 좋아요/좋아요 취소 결과를 두 목록 모두에 반영한다. "내 인증 보기" API
    // 자체엔 liked가 없어서(withMyLikedSyncedFromAll 참고) 대상을 못 찾으면 "인증" 쪽에서 찾는다.
    override fun onLikeToggleClick(certificationId: Long) {
        val state = _uiState.value
        val foundInAll = state.allCertifications.find { it.id == certificationId }
        val target = foundInAll ?: state.myCertifications.find { it.id == certificationId } ?: return
        val sort = state.selectedSort
        viewModelScope.launch {
            // "인증"(전체)에 아직 안 불러와진(현재 페이지 밖) "내 인증 보기" 전용 항목은 liked가
            // withMyLikedSyncedFromAll로 맞춰진 적이 없어 로컬 값을 못 믿는다(항상 false로 시작).
            // 이 값을 그대로 믿고 반대 API(예: 이미 좋아요했는데 좋아요 API)를 부르지 않도록,
            // 이 경우에만 최신 목록을 한 번 더 조회해 진짜 liked를 확인한다.
            val liked = if (foundInAll != null) {
                target.liked
            } else {
                val lookup = getVerificationsUseCase(
                    challengeId,
                    cursor = null,
                    cursorLikeCount = null,
                    size = MaxVerificationPageSize,
                    sort = sort,
                )
                (lookup as? ResultState.Success)?.data?.certifications
                    ?.find { it.id == certificationId }?.liked ?: target.liked
            }
            val result = if (liked) {
                unlikeVerificationUseCase(challengeId, certificationId)
            } else {
                likeVerificationUseCase(challengeId, certificationId)
            }
            if (result is ResultState.Success) {
                _uiState.update { s ->
                    s.copy(
                        allCertifications = s.allCertifications.withLikeResult(result.data),
                        // "내 인증 보기"에서 눌렀어도 결과(liked 포함)를 그대로 반영한다 — 이전엔
                        // likeCount만 맞추고 liked는 항상 false로 둬서, "인증" 탭에서 좋아요를 눌러도
                        // "내 인증 보기"엔 반영되지 않고 반대로 "내 인증 보기"에선 좋아요 자체를 누를
                        // 수도 없었다.
                        myCertifications = s.myCertifications.withLikeResult(result.data),
                    )
                }
                // 이 화면은 주기적 자동 새로고침이 없어(당겨서 새로고침/탭 재진입 시에만 갱신),
                // 내가 좋아요를 누른 시점에 다른 사람이 누른 좋아요도 같이 최신화해둔다 — 안 그러면
                // 남이 새로 누른 좋아요는 내가 직접 새로고침하거나 탭을 나갔다 와야만 보인다.
                refreshLikeCounts()
                // 좋아요 순위가 바뀌어 대문(좋아요 1위) 게시글이 달라졌을 수 있다.
                loadHeroImage()
            }
        }
    }
}

private fun List<CertificationUiModel>.withUpdatedContent(id: Long, content: String): List<CertificationUiModel> =
    map { if (it.id == id) it.copy(content = content) else it }

private fun List<CertificationUiModel>.withLikeResult(result: LikeResult): List<CertificationUiModel> =
    map { if (it.id == result.verificationId) it.copy(likeCount = result.likeCount, liked = result.liked) else it }

// "내 인증 보기"(MyCertification) API엔 liked가 아예 없어 toUiModel()에서 항상 false로 매핑된다.
// 대신 같은 verificationId를 "인증"(전체) 쪽이 알고 있으면 그 liked 값을 그대로 맞춰준다 —
// 두 목록 중 어느 쪽에서 좋아요를 눌렀어도, 아직 안 눌러본 탭으로 처음 전환했을 때도 liked가
// 정확히 보이게 하기 위함.
private fun ChallengeDetailUiState.withMyLikedSyncedFromAll(): ChallengeDetailUiState {
    if (myCertifications.isEmpty() || allCertifications.isEmpty()) return this
    val likedById = allCertifications.associate { it.id to it.liked }
    return copy(
        myCertifications = myCertifications.map { certification ->
            likedById[certification.id]?.let { certification.copy(liked = it) } ?: certification
        },
    )
}

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
// liked는 이 API 자체엔 없어 일단 false로 채우고, withMyLikedSyncedFromAll()이 "인증"(전체)
// 쪽에서 알고 있는 값으로 맞춰준다.
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
