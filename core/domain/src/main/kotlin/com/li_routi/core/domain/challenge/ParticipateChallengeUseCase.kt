package com.li_routi.core.domain.challenge

import com.li_routi.core.common.kotlin.util.ResultState

class ParticipateChallengeUseCase(
    private val repository: ChallengeRepository,
) {
    suspend operator fun invoke(challengeId: Long): ResultState<Participation> = repository.participate(challengeId)
}
