package com.cmc.li_routi_frontend

import android.app.Application
import com.cmc.li_routi_frontend.fcm.FcmDeviceSync
import com.cmc.li_routi_frontend.fcm.FcmNotificationPresenter
import com.kakao.sdk.common.KakaoSdk
import com.li_routi.core.data.network.NetworkModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class LiRoutiApplication : Application() {

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        NetworkModule.init(this)
        KakaoSdk.init(this, BuildConfig.KAKAO_APP_ID)
        FcmNotificationPresenter.ensureChannel(this)

        // 로그인 세션이 생기거나 앱 기동 시 이미 로그인되어 있으면 FCM 기기 토큰을 서버에 등록한다.
        applicationScope.launch {
            NetworkModule.authTokenPreference.accessTokenFlow
                .distinctUntilChanged()
                .collect { accessToken ->
                    if (!accessToken.isNullOrBlank()) {
                        FcmDeviceSync.registerCurrentDevice()
                    }
                }
        }
    }
}
