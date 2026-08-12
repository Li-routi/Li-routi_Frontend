package com.li_routi.feature.challenge.vm

import com.li_routi.core.domain.challenge.ChallengeCategory
import com.li_routi.feature.challenge.component.ChallengeCardUiModel

/** "챌린지 찾아보기" 화면 UI 상태. */
data class FindChallengeUiState(
    val isLoading: Boolean = true,
    /** 무한 스크롤로 다음 페이지를 불러오는 중인지. 목록 하단에 작은 로딩 표시로 쓴다. */
    val isLoadingMore: Boolean = false,
    val challenges: List<ChallengeCardUiModel> = emptyList(),
    val selectedCategory: ChallengeCategory? = null,
    val searchQuery: String = "",
    val errorMessage: String? = null,
    /** 다음 페이지 요청에 쓸 커서. 마지막 응답의 nextCursor. */
    val nextCursor: Long? = null,
    /** 더 불러올 페이지가 남아있는지. */
    val hasNext: Boolean = false,
)
