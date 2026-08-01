package com.li_routi.core.domain.challenge

import com.li_routi.core.common.kotlin.util.ResultState

class UnlikeVerificationUseCase(
    private val repository: ChallengeRepository,
) {
    suspend operator fun invoke(challengeId: Long, verificationId: Long): ResultState<LikeResult> =
        repository.unlikeVerification(challengeId, verificationId)
}
