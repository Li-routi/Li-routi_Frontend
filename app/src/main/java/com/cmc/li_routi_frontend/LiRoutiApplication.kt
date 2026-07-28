package com.cmc.li_routi_frontend

import android.app.Application
import com.kakao.sdk.common.KakaoSdk
import com.li_routi.core.data.network.NetworkModule

class LiRoutiApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        NetworkModule.init(this)
        KakaoSdk.init(this, BuildConfig.KAKAO_APP_ID)
    }
}
