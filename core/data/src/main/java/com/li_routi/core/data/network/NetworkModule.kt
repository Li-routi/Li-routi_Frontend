package com.li_routi.core.data.network

import android.content.Context
import com.li_routi.core.data.network.service.AuthApiService
import com.li_routi.core.data.network.service.ChallengeApiService
import com.li_routi.core.data.network.service.MediaApiService
import com.li_routi.core.data.network.service.MemberApiService
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
     * S3 presigned URL 업로드 전용 클라이언트. [okHttpClient]와 달리 [AuthInterceptor]를 붙이지 않는다 —
     * 우리 서버용 JWT가 S3로 함께 전송되면 안 되기 때문이다.
     */
    val uploadOkHttpClient: OkHttpClient by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
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

    val memberApiService: MemberApiService by lazy {
        retrofit.create(MemberApiService::class.java)
    }

    val mediaApiService: MediaApiService by lazy {
        retrofit.create(MediaApiService::class.java)
    }
}
