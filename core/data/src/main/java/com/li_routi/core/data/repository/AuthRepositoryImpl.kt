package com.li_routi.core.data.repository

import com.li_routi.core.common.kotlin.util.ResultState
import com.li_routi.core.common.kotlin.util.safeApiCall
import com.li_routi.core.data.mapper.toDomain
import com.li_routi.core.data.network.apiCall
import com.li_routi.core.data.network.dto.request.SocialLoginRequest
import com.li_routi.core.data.network.service.AuthApiService
import com.li_routi.core.data.preference.AuthTokenPreference
import com.li_routi.core.domain.auth.AuthRepository
import com.li_routi.core.domain.auth.AuthToken
import com.li_routi.core.domain.auth.SocialProvider

class AuthRepositoryImpl(
    private val api: AuthApiService,
    private val tokenPreference: AuthTokenPreference,
) : AuthRepository {

    override suspend fun issueGoogleNonce(): ResultState<String> = safeApiCall {
        apiCall { api.issueGoogleNonce() }.toDomain()
    }

    override suspend fun socialLogin(
        provider: SocialProvider,
        providerToken: String,
        nonce: String?,
    ): ResultState<AuthToken> = safeApiCall {
        apiCall {
            api.socialLogin(
                SocialLoginRequest(
                    provider = provider.name,
                    providerToken = providerToken,
                    nonce = nonce,
                ),
            )
        }.toDomain().also { tokenPreference.saveTokens(it) }
    }
}
