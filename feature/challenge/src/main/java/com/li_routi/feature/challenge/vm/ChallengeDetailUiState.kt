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
 * "수정하기"(내 글) / "신고하기"(타인 글)를 결정하는 데 쓰인다 — 전체 탭 응답에는 작성자 식별 필드가
 * 없어, 내 인증 API(GET .../verifications/me) 응답으로 채워진 항목만 true다.
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
