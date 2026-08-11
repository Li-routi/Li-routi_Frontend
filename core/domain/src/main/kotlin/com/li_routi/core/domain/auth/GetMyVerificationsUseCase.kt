package com.li_routi.core.domain.auth

import com.li_routi.core.common.kotlin.util.ResultState

class GetMyVerificationsUseCase(
    private val repository: AuthRepository,
) {
    suspend operator fun invoke(
        date: String? = null,
        status: VerificationReviewStatus? = null,
    ): ResultState<MyVerificationDay> = repository.getMyVerifications(date, status)
}
