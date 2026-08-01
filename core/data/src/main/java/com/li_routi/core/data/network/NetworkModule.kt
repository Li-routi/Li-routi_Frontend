package com.li_routi.core.data.network

import android.content.Context
import com.li_routi.core.data.network.service.AuthApiService
import com.li_routi.core.data.network.service.ChallengeApiService
import com.li_routi.core.data.network.service.HomeApiService
import com.li_routi.core.data.network.service.MediaApiService
import com.li_routi.core.data.network.service.RoutineApiService
import com.li_routi.core.data.preference.AuthTokenPreference
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Retrofit/OkHttp 싱글턴 제공자. 프로젝트 전반에 DI 프레임워크(Hilt)가 아직 연결되어 있지 않아서
 * 일단 수동으로 구성한다.
 *
 * [init]은 토큰 저장소(EncryptedSharedPreferences) 생성에 필요한 Application Context를 주입하기 위한
 * 명시적 초기화이며, 앱 프로세스 시작 시(Application.onCreate) 1회 호출되어야 한다.
 */
object NetworkModule {

    private const val BASE_URL = "http://13.125.35.99:8080/"

    internal lateinit var appContext: Context
        private set

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    private val okHttpClient: OkHttpClient by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(AuthTokenPreference(appContext)))
            .addInterceptor(logging)
            .build()
    }

    /**
     * S3 presigned PUT 전용 클라이언트.
     * Bearer 토큰을 붙이면 서명이 깨지므로 AuthInterceptor를 넣지 않는다.
     * 바이너리 body 로그는 남기지 않는다.
     */
    val s3OkHttpClient: OkHttpClient by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.HEADERS
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

    val authApiService: AuthApiService by lazy {
        retrofit.create(AuthApiService::class.java)
    }

    val homeApiService: HomeApiService by lazy {
        retrofit.create(HomeApiService::class.java)
    }

    val mediaApiService: MediaApiService by lazy {
        retrofit.create(MediaApiService::class.java)
    }

    val routineApiService: RoutineApiService by lazy {
        retrofit.create(RoutineApiService::class.java)
    }
}
