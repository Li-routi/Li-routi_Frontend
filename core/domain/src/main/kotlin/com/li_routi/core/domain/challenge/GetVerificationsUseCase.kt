package com.li_routi.core.domain.challenge

import com.li_routi.core.common.kotlin.util.ResultState

class GetVerificationsUseCase(
    private val repository: ChallengeRepository,
) {
    suspend operator fun invoke(
        challengeId: Long,
        cursor: Long?,
        size: Int?,
    ): ResultState<CertificationPage> = repository.getVerifications(challengeId, cursor, size)
}
