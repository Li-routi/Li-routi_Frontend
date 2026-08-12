package com.li_routi.feature.challenge.navigation

import com.li_routi.core.domain.challenge.ChallengeCategory

/**
 * `FindChallengeScreen`에서 발생하는 사용자 이벤트에 대한 콜백 계약(contract).
 */
interface FindChallengeScreenActions {
    /** 필터 칩 선택 ("전체" 칩은 category = null) */
    fun onCategorySelected(category: ChallengeCategory?)

    /** 검색어 입력. 타이핑마다 호출되며, 실제 서버 조회는 디바운스되어 실행된다. */
    fun onSearchQueryChanged(query: String)

    /** 목록 조회 실패 후 재시도 */
    fun onRetryClick()

    /** 목록 끝에 가까워지면 다음 페이지를 요청한다(무한 스크롤). */
    fun onLoadMore()
}
