package com.li_routi.core.domain.challenge

import com.li_routi.core.common.kotlin.util.ResultState

class ReportVerificationUseCase(
    private val repository: ChallengeRepository,
) {
    suspend operator fun invoke(
        challengeId: Long,
        verificationId: Long,
        reason: String? = null,
    ): ResultState<Unit> = repository.reportVerification(challengeId, verificationId, reason)
}
