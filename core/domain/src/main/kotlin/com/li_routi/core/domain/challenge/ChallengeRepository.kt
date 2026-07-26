package com.li_routi.core.domain.challenge

import com.li_routi.core.common.kotlin.util.ResultState

interface ChallengeRepository {

    /**
     * 챌린지 목록을 최신순으로 조회한다 (무한 스크롤 커서 방식).
     *
     * @param category 분류 필터. null이면 전체.
     * @param keyword 챌린지 이름 부분 검색어.
     * @param cursor 직전 응답의 nextCursor. 첫 요청에는 null.
     * @param size 한 번에 가져올 개수 (서버 기본값 20, 최대 50).
     */
    suspend fun getChallenges(
        category: ChallengeCategory?,
        keyword: String?,
        cursor: Long?,
        size: Int?,
    ): ResultState<ChallengePage>
}
