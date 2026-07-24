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
    val likeCount: Int,
    val timeLabel: String,
)

/** 탭당 한 번에 보여주는 개수. 스크롤이 끝에 닿으면 이만큼씩 더 보여준다. */
internal const val CertificationPageSize = 10

// Figma 목업 기준 샘플 데이터. 실제 목록은 백엔드에서 제공된다.
internal val SampleAllCertifications = List(37) { index ->
    CertificationUiModel(
        id = index.toLong(),
        authorName = "민지",
        content = "물 마시기 1일차 입니다~ 다들 열심히 하고 있지?",
        likeCount = 1,
        timeLabel = "9시간 전",
    )
}

internal val SampleMyCertifications = List(4) { index ->
    CertificationUiModel(
        id = 1000L + index,
        authorName = "민지",
        content = "물 마시기 1일차 입니다~ 다들 열심히 하고 있지?",
        likeCount = 1,
        timeLabel = "9시간 전",
    )
}

/**
 * 챌린지 상세 화면 UI 상태.
 *
 * 탭 선택/스크롤로 늘어나는 노출 개수는 화면 전환·페이지네이션과 직결되는 실제 상태라 여기서 관리하고,
 * 단순 바텀시트 노출 여부 같은 화면 로컬 UI 상태는 Screen에 둔다.
 */
data class ChallengeDetailUiState(
    val challengeId: Long,
    val title: String = "우유 한잔 마시기",
    val badge: String = "매일 루틴",
    val description: String = "매일 우유를 마시며 건강 관리를 해요",
    val participantCount: Int = 300,
    val activityCount: Int = 14000,
    val postCount: Int = 80,
    val isJoined: Boolean = false,
    val selectedTab: CertificationTab = CertificationTab.All,
    val allCertifications: List<CertificationUiModel> = SampleAllCertifications,
    val myCertifications: List<CertificationUiModel> = SampleMyCertifications,
    val visibleAllCount: Int = CertificationPageSize,
    val visibleMyCount: Int = CertificationPageSize,
) {
    val visibleCertifications: List<CertificationUiModel>
        get() = when (selectedTab) {
            CertificationTab.All -> allCertifications.take(visibleAllCount)
            CertificationTab.Mine -> myCertifications.take(visibleMyCount)
        }

    val hasMoreCertifications: Boolean
        get() = when (selectedTab) {
            CertificationTab.All -> visibleAllCount < allCertifications.size
            CertificationTab.Mine -> visibleMyCount < myCertifications.size
        }
}
