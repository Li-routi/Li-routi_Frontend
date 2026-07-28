package com.li_routi.core.data.network

import com.li_routi.core.data.preference.AuthTokenPreference
import okhttp3.Interceptor
import okhttp3.Response

/** 저장된 서비스 토큰이 있으면 모든 요청에 Authorization 헤더를 자동으로 붙인다. */
class AuthInterceptor(
    private val tokenPreference: AuthTokenPreference,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val accessToken = tokenPreference.getAccessTokenBlocking()
        val request = if (accessToken.isNullOrBlank()) {
            chain.request()
        } else {
            chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $accessToken")
                .build()
        }
        return chain.proceed(request)
    }
}
