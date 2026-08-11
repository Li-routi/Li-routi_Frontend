package com.li_routi.core.domain.challenge

import com.li_routi.core.common.kotlin.util.ResultState

class GetMyVerificationsUseCase(
    private val repository: ChallengeRepository,
) {
    suspend operator fun invoke(
        challengeId: Long,
        cursor: Long?,
        cursorLikeCount: Long?,
        size: Int?,
        sort: VerificationSort,
    ): ResultState<MyCertificationPage> = repository.getMyVerifications(challengeId, cursor, cursorLikeCount, size, sort)
}
