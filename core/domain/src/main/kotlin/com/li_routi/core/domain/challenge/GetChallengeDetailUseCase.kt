package com.li_routi.core.domain.challenge

import com.li_routi.core.common.kotlin.util.ResultState

class GetChallengeDetailUseCase(
    private val repository: ChallengeRepository,
) {
    suspend operator fun invoke(challengeId: Long): ResultState<ChallengeDetail> =
        repository.getChallengeDetail(challengeId)
}
