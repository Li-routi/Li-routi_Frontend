package com.li_routi.core.data.network

import android.content.Context
import com.li_routi.core.data.BuildConfig
import com.li_routi.core.data.network.service.AuthApiService
import com.li_routi.core.data.network.service.ChallengeApiService
import com.li_routi.core.data.network.service.ChatApiService
import com.li_routi.core.data.network.service.GroupRoutineApiService
import com.li_routi.core.data.network.service.HomeApiService
import com.li_routi.core.data.network.service.MediaApiService
import com.li_routi.core.data.network.service.NotificationApiService
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

    private val BASE_URL = BuildConfig.BASE_URL

    internal lateinit var appContext: Context
        private set

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    // ChatSocketClient(같은 모듈)와 앱 FCM 동기화가 액세스 토큰을 읽어야 해서 공개한다.
    val authTokenPreference: AuthTokenPreference by lazy { AuthTokenPreference(appContext) }

    private val okHttpClient: OkHttpClient by lazy {
        val logging = HttpLoggingInterceptor().apply {
            // TODO: 그룹 생성 409 디버깅용으로 BODY 사용 중. 원래는 릴리즈 빌드에서 로그를 끄고, 디버그
            // 모드에서도 Body 노출(초대코드 등) 방지를 위해 BASIC을 썼음(PR 반영) — 디버깅 끝나면 되돌릴 것.
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
        OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(authTokenPreference))
            .authenticator(TokenAuthenticator(authTokenPreference) { authApiService })
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

    /**
     * 채팅 STOMP 소켓 핸드셰이크 전용 클라이언트. Authorization은 Krossbow의 connect(headers=...)로
     * 직접 실어 보내므로, REST용 [okHttpClient]의 AuthInterceptor/TokenAuthenticator를 재사용하면
     * 헤더가 중복되거나 401 재시도 로직이 웹소켓 업그레이드에 잘못 개입할 수 있어 별도로 둔다.
     */
    val socketOkHttpClient: OkHttpClient by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BASIC else HttpLoggingInterceptor.Level.NONE
        }
        OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }

    /** [BASE_URL]("https://.../")의 스킴을 ws로 바꾸고 백엔드가 등록한 STOMP 엔드포인트 경로를 붙인다. */
    val chatSocketUrl: String by lazy {
        Regex("^http").replaceFirst(BASE_URL, "ws").trimEnd('/') + "/ws"
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

    val groupRoutineApiService: GroupRoutineApiService by lazy {
        retrofit.create(GroupRoutineApiService::class.java)
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

    val chatApiService: ChatApiService by lazy {
        retrofit.create(ChatApiService::class.java)
    }

    val notificationApiService: NotificationApiService by lazy {
        retrofit.create(NotificationApiService::class.java)
    }
}
