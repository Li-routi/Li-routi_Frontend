package com.li_routi.core.data.repository

import com.li_routi.core.common.kotlin.util.ResultState
import com.li_routi.core.common.kotlin.util.safeApiCall
import com.li_routi.core.data.mapper.toDomain
import com.li_routi.core.data.network.apiCall
import com.li_routi.core.data.network.dto.request.LogoutRequest
import com.li_routi.core.data.network.dto.request.SocialLoginRequest
import com.li_routi.core.data.network.service.AuthApiService
import com.li_routi.core.data.preference.AuthTokenPreference
import com.li_routi.core.domain.auth.AuthRepository
import com.li_routi.core.domain.auth.AuthToken
import com.li_routi.core.domain.auth.SocialProvider
import kotlinx.coroutines.flow.first

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

    // 로그아웃 응답은 성공해도 result가 null이라 non-null 결과를 요구하는 apiCall()을 못 쓰고,
    // isSuccess만 직접 확인한다.
    override suspend fun logout(): ResultState<Unit> = safeApiCall {
        val accessToken = tokenPreference.accessTokenFlow.first().orEmpty()
        val response = api.logout(LogoutRequest(accessToken = accessToken))
        if (!response.isSuccess) throw ApiException(response.message)
        tokenPreference.clear()
    }
}
