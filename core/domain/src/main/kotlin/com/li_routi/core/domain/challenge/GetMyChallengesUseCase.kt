package com.li_routi.core.domain.challenge

import com.li_routi.core.common.kotlin.util.ResultState

class GetMyChallengesUseCase(
    private val repository: ChallengeRepository,
) {
    suspend operator fun invoke(
        category: ChallengeCategory? = null,
        keyword: String? = null,
    ): ResultState<List<MyChallenge>> = repository.getMyChallenges(category = category, keyword = keyword)
}
