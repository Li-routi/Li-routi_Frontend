package com.li_routi.core.domain.auth

import com.li_routi.core.common.kotlin.util.ResultState

class SocialLoginUseCase(
    private val repository: AuthRepository,
) {
    suspend operator fun invoke(
        provider: SocialProvider,
        providerToken: String,
        nonce: String?,
    ): ResultState<AuthToken> = repository.socialLogin(provider, providerToken, nonce)
}
