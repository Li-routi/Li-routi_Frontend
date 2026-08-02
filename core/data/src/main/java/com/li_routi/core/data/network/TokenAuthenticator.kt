package com.li_routi.core.data.network

import com.li_routi.core.data.network.dto.request.ReissueRequest
import com.li_routi.core.data.network.service.AuthApiService
import com.li_routi.core.data.preference.AuthTokenPreference
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

private const val REISSUE_PATH = "api/auth/reissue"
private const val MAX_RETRY_COUNT = 2

/**
 * accessToken 만료로 401 응답을 받으면 refreshToken으로 [AuthApiService.reissue]를 호출해
 * 토큰을 재발급받고, 원래 요청에 새 accessToken을 실어 재시도한다.
 *
 * [authApiServiceProvider]는 즉시 평가하지 않고 지연 참조로 받는다. 이 Authenticator는
 * [NetworkModule]의 OkHttpClient 생성 과정에서 만들어지는데, 그 OkHttpClient로 [AuthApiService]를
 * 만들기 때문에 즉시 참조하면 순환 초기화가 발생한다. 실제 참조는 401 응답을 받은 이후,
 * 즉 OkHttpClient 생성이 끝난 뒤에만 일어나므로 안전하다.
 */
class TokenAuthenticator(
    private val tokenPreference: AuthTokenPreference,
    private val authApiServiceProvider: () -> AuthApiService,
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        // reissue 요청 자체가 401이면(리프레시 토큰 만료/무효) 더 이상 재시도하지 않는다.
        if (response.request.url.encodedPath.endsWith(REISSUE_PATH)) return null
        if (retryCount(response) >= MAX_RETRY_COUNT) return null

        val failedAccessToken = response.request.header("Authorization")?.removePrefix("Bearer ")

        synchronized(this) {
            // 동시에 여러 요청이 401을 받은 경우, 먼저 재발급이 끝났다면 새 토큰으로 바로 재시도한다.
            val storedAccessToken = tokenPreference.getAccessTokenBlocking()
            val newAccessToken = if (!storedAccessToken.isNullOrBlank() && storedAccessToken != failedAccessToken) {
                storedAccessToken
            } else {
                reissueTokens() ?: return null
            }

            return response.request.newBuilder()
                .header("Authorization", "Bearer $newAccessToken")
                .build()
        }
    }

    private fun reissueTokens(): String? {
        val refreshToken = tokenPreference.getRefreshTokenBlocking()
        if (refreshToken.isNullOrBlank()) return null

        val newTokens = runCatching {
            runBlocking { authApiServiceProvider().reissue(ReissueRequest(refreshToken)) }
        }.getOrNull()?.takeIf { it.isSuccess }?.result

        if (newTokens == null) {
            runBlocking { tokenPreference.clear() }
            return null
        }

        runBlocking {
            tokenPreference.saveTokens(
                accessToken = newTokens.accessToken,
                refreshToken = newTokens.refreshToken,
                accessTokenExpiresIn = newTokens.accessTokenExpiresIn,
            )
        }
        return newTokens.accessToken
    }

    private fun retryCount(response: Response): Int {
        var count = 1
        var prior = response.priorResponse
        while (prior != null) {
            count++
            prior = prior.priorResponse
        }
        return count
    }
}