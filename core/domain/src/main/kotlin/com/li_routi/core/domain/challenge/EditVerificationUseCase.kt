package com.li_routi.core.domain.challenge

import com.li_routi.core.common.kotlin.util.ResultState

class EditVerificationUseCase(
    private val repository: ChallengeRepository,
) {
    suspend operator fun invoke(challengeId: Long, verificationId: Long, content: String): ResultState<EditedVerification> =
        repository.updateVerificationMemo(challengeId, verificationId, content)
}
