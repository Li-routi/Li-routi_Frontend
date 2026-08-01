package com.li_routi.core.domain.challenge

import com.li_routi.core.common.kotlin.util.ResultState

class CreateVerificationUseCase(
    private val repository: ChallengeRepository,
) {
    suspend operator fun invoke(challengeId: Long, mediaKey: String, content: String?): ResultState<CreatedVerification> =
        repository.createVerification(challengeId, mediaKey, content)
}
