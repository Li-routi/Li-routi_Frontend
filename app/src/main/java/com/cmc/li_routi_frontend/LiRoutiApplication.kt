package com.cmc.li_routi_frontend

import android.app.Application
import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import com.kakao.sdk.common.KakaoSdk
import com.li_routi.core.data.network.NetworkModule

class LiRoutiApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        NetworkModule.init(this)
        KakaoSdk.init(this, BuildConfig.KAKAO_APP_ID)

        // 백엔드 API 테스트용으로 현재 기기의 FCM 토큰을 로그로 확인한다.
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("FCM", "token: ${task.result}")
            }
        }
    }
}
