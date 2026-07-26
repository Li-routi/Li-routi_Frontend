package com.li_routi.core.domain.challenge

import com.li_routi.core.common.kotlin.util.ResultState

class GetChallengesUseCase(
    private val repository: ChallengeRepository,
) {
    suspend operator fun invoke(
        category: ChallengeCategory? = null,
        keyword: String? = null,
        cursor: Long? = null,
        size: Int? = null,
    ): ResultState<ChallengePage> = repository.getChallenges(
        category = category,
        keyword = keyword,
        cursor = cursor,
        size = size,
    )
}
