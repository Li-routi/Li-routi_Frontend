package com.li_routi.feature.challenge.vm

import com.li_routi.core.domain.challenge.VerificationSort

/** "인증"/"내 인증 보기" 탭 구분. */
enum class CertificationTab {
    All,
    Mine,
}

/**
 * 인증 게시글 한 건.
 *
 * "인증"(전체) 탭과 "내 인증 보기" 탭을 하나의 모델로 다룬다. [isMine]은 더보기 바텀시트가
 * "수정하기/삭제하기"(내 글) / "신고하기"(타인 글)를 결정하고, "인증"(전체) 탭에서 본인 글이면
 * 더보기 버튼 자체를 숨기는 데도 쓰인다. 두 탭 모두 서버가 토큰의 회원과 작성자를 대조해 내려주는
 * 값을 그대로 매핑한 것이다(닉네임으로 판별하지 않음 — 닉네임은 유니크하지 않다).
 */
data class CertificationUiModel(
    val id: Long,
    val authorName: String,
    val content: String,
    val imageUrl: String,
    val timeLabel: String,
    val likeCount: Long,
    val liked: Boolean,
    val isMine: Boolean,
)

/**
 * 챌린지 상세 화면 UI 상태.
 *
 * "인증"(전체)과 "내 인증 보기" 모두 서버 커서 페이지네이션을 따른다.
 * title/description 등은 ViewModel이 조회를 마치기 전까지는 비어있고, 조회 완료 후 채워진다.
 */
data class ChallengeDetailUiState(
    val challengeId: Long,
    val isLoading: Boolean = true,
    val title: String = "",
    val badge: String = "",
    val description: String = "",
    val participantCount: Int = 0,
    val rewardCount: Int = 0,
    val postCount: Int = 0,
    val isJoined: Boolean = false,
    /**
     * 현재 인증 주기(routineCycle 기준)에 이미 인증했는지 여부. 서버(GET 챌린지 상세)가 내려주는
     * 값을 그대로 반영해, 챌린지를 나갔다 다시 들어와도 유지된다. true면 "인증하기" 버튼이
     * "인증 완료"로 바뀌며 비활성화된다.
     */
    val verifiedInCurrentPeriod: Boolean = false,
    val selectedTab: CertificationTab = CertificationTab.All,
    val selectedSort: VerificationSort = VerificationSort.LATEST,
    val allCertifications: List<CertificationUiModel> = emptyList(),
    val allCursor: Long? = null,
    val allCursorLikeCount: Long? = null,
    val allHasNext: Boolean = true,
    val isLoadingMoreAll: Boolean = false,
    val myCertifications: List<CertificationUiModel> = emptyList(),
    val myCursor: Long? = null,
    val myCursorLikeCount: Long? = null,
    val myHasNext: Boolean = true,
    val isLoadingMoreMy: Boolean = false,
    val myLoaded: Boolean = false,
    /** 인증 수정("수정하기" > "완료") API 요청 진행 여부. 요청 결과 핸들러만 갱신한다(dismiss는 건드리지 않음). */
    val isSubmittingEdit: Boolean = false,
    /** 인증 수정 실패 메시지. 재시도를 시작하거나 수정 화면을 닫으면 지워진다. */
    val editCertificationError: String? = null,
    /**
     * 방금 수정에 성공한 인증 게시글 id. 화면은 이 값이 자신이 열고 있는 게시글 id와 같아지면 닫고
     * 곧바로 지운다("제출 중 아님 && 에러 없음"이라는 이중 부정으로 성공을 추론하지 않고, 성공
     * 이벤트 자체를 명시적으로 신호한다).
     */
    val editedCertificationId: Long? = null,
    /** 당겨서 새로고침 진행 여부. */
    val isRefreshing: Boolean = false,
    /** 삭제/신고 실패 메시지. 토스트로 보여주고 닫으면 지워진다. */
    val actionErrorMessage: String? = null,
) {
    val visibleCertifications: List<CertificationUiModel>
        get() = when (selectedTab) {
            CertificationTab.All -> allCertifications
            CertificationTab.Mine -> myCertifications
        }

    val hasMoreCertifications: Boolean
        get() = when (selectedTab) {
            CertificationTab.All -> allHasNext
            CertificationTab.Mine -> myHasNext
        }
}
