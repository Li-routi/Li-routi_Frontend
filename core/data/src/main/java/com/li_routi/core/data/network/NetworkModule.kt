package com.li_routi.core.data.network

import com.li_routi.core.data.network.service.ChallengeApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Retrofit/OkHttp 싱글턴 제공자. 프로젝트 전반에 DI 프레임워크(Hilt)가 아직 연결되어 있지 않아서
 * 일단 수동으로 구성한다.
 */
object NetworkModule {

    private const val BASE_URL = "http://13.125.35.99:8080/"

    private val okHttpClient: OkHttpClient by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val challengeApiService: ChallengeApiService by lazy {
        retrofit.create(ChallengeApiService::class.java)
    }
}
