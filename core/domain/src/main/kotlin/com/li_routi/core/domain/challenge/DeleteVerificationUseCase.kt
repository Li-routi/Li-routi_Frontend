package com.li_routi.core.domain.challenge

import com.li_routi.core.common.kotlin.util.ResultState

class DeleteVerificationUseCase(
    private val repository: ChallengeRepository,
) {
    suspend operator fun invoke(challengeId: Long, verificationId: Long): ResultState<Unit> =
        repository.deleteVerification(challengeId, verificationId)
}
