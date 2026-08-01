package com.li_routi.feature.challenge.vm

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
    /** 오늘 이미 인증했는지 여부. true면 "인증하기" 버튼이 "다시 인증하기"로 바뀐다. */
    val verifiedToday: Boolean = false,
    val selectedTab: CertificationTab = CertificationTab.All,
    val allCertifications: List<CertificationUiModel> = emptyList(),
    val allCursor: Long? = null,
    val allHasNext: Boolean = true,
    val isLoadingMoreAll: Boolean = false,
    val myCertifications: List<CertificationUiModel> = emptyList(),
    val myCursor: Long? = null,
    val myHasNext: Boolean = true,
    val isLoadingMoreMy: Boolean = false,
    val myLoaded: Boolean = false,
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
