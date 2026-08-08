package com.li_routi.core.data.network

import com.li_routi.core.data.preference.AuthTokenPreference
import okhttp3.Interceptor
import okhttp3.Response

private const val REISSUE_PATH = "api/auth/reissue"

/** 저장된 서비스 토큰이 있으면 모든 요청에 Authorization 헤더를 자동으로 붙인다. */
class AuthInterceptor(
    private val tokenPreference: AuthTokenPreference,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val accessToken = tokenPreference.getAccessTokenBlocking()
        // reissue는 refreshToken 자체가 자격증명이라 accessToken이 필요 없다. 만료된 accessToken을
        // 실어 보내면 서버가 그 헤더만 보고 401을 내려버려 재발급 자체가 막히므로 헤더를 붙이지 않는다.
        val isReissueRequest = originalRequest.url.encodedPath.endsWith(REISSUE_PATH)
        val request = if (accessToken.isNullOrBlank() || isReissueRequest) {
            originalRequest
        } else {
            originalRequest.newBuilder()
                .addHeader("Authorization", "Bearer $accessToken")
                .build()
        }
        return chain.proceed(request)
    }
}
