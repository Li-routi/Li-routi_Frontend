package com.li_routi.feature.challenge.vm

/** "인증"/"내 인증 보기" 탭 구분. */
enum class CertificationTab {
    All,
    Mine,
}

/** 인증 게시글 한 건. */
data class CertificationUiModel(
    val id: Long,
    val authorName: String,
    val content: String,
    val timeLabel: String,
)

/** "내 인증 보기" 탭에서 한 번에 보여주는 개수. 스크롤이 끝에 닿으면 이만큼씩 더 보여준다. */
internal const val CertificationPageSize = 10

// "내 인증만 보기" 필터는 인증 피드 API(GET .../verifications)에 아직 없어(전체 피드만 제공),
// 이 탭은 더미 데이터를 그대로 사용한다.
internal val SampleMyCertifications = List(4) { index ->
    CertificationUiModel(
        id = 1000L + index,
        authorName = "민지",
        content = "물 마시기 1일차 입니다~ 다들 열심히 하고 있지?",
        timeLabel = "9시간 전",
    )
}

/**
 * 챌린지 상세 화면 UI 상태.
 *
 * "인증"(전체) 탭은 서버 커서 페이지네이션(allCursor/allHasNext)을 그대로 따라가고,
 * "내 인증 보기" 탭은 더미 데이터를 클라이언트에서 나눠 보여준다(visibleMyCount).
 * title/description 등은 ViewModel이 조회를 마치기 전까지는 비어있고, 조회 완료 후 채워진다.
 */
data class ChallengeDetailUiState(
    val challengeId: Long,
    val isLoading: Boolean = true,
    val title: String = "",
    val badge: String = "",
    val description: String = "",
    val participantCount: Int = 0,
    val activityCount: Int = 0,
    val postCount: Int = 0,
    val isJoined: Boolean = false,
    val selectedTab: CertificationTab = CertificationTab.All,
    val allCertifications: List<CertificationUiModel> = emptyList(),
    val allCursor: Long? = null,
    val allHasNext: Boolean = true,
    val isLoadingMoreAll: Boolean = false,
    val myCertifications: List<CertificationUiModel> = SampleMyCertifications,
    val visibleMyCount: Int = CertificationPageSize,
) {
    val visibleCertifications: List<CertificationUiModel>
        get() = when (selectedTab) {
            CertificationTab.All -> allCertifications
            CertificationTab.Mine -> myCertifications.take(visibleMyCount)
        }

    val hasMoreCertifications: Boolean
        get() = when (selectedTab) {
            CertificationTab.All -> allHasNext
            CertificationTab.Mine -> visibleMyCount < myCertifications.size
        }
}
