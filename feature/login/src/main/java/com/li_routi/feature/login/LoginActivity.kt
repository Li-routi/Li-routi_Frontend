package com.li_routi.feature.login

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.feature.login.navigation.LoginRoute

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.WHITE, Color.WHITE),
        )
        val startAtProfileSetup = intent.getBooleanExtra(ExtraStartAtProfileSetup, false)
        setContent {
            LiroutiFrontendTheme(darkTheme = false) {
                LoginRoute(modifier = Modifier.fillMaxSize(), startAtProfileSetup = startAtProfileSetup)
            }
        }
    }

    companion object {
        /**
         * 토큰은 있지만(재로그인 불필요) 서버의 최신 `onboardingCompleted`가 false인 경우(예: 프로필
         * 설정을 마치기 전에 앱을 강제 종료) [MainActivity][com.cmc.li_routi_frontend.MainActivity]가
         * 이 값과 함께 넘어와, 로그인 화면을 건너뛰고 곧바로 프로필 설정 화면부터 시작하게 한다.
         */
        const val ExtraStartAtProfileSetup = "extra_start_at_profile_setup"

        fun createIntent(context: Context, startAtProfileSetup: Boolean = false): Intent =
            Intent(context, LoginActivity::class.java).putExtra(ExtraStartAtProfileSetup, startAtProfileSetup)
    }
}


