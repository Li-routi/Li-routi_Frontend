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
        val initialNickname = intent.getStringExtra(ExtraInitialNickname)
        setContent {
            LiroutiFrontendTheme(darkTheme = false) {
                LoginRoute(
                    modifier = Modifier.fillMaxSize(),
                    startAtProfileSetup = startAtProfileSetup,
                    initialNickname = initialNickname,
                )
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

        /**
         * [MainActivity][com.cmc.li_routi_frontend.MainActivity]가 이미 조회해 둔 내 정보의
         * 닉네임(소셜 프로필 기반 초기값)을 프로필 설정 화면 입력창에 미리 채워주기 위해 넘긴다.
         */
        const val ExtraInitialNickname = "extra_initial_nickname"

        fun createIntent(
            context: Context,
            startAtProfileSetup: Boolean = false,
            initialNickname: String? = null,
        ): Intent =
            Intent(context, LoginActivity::class.java)
                .putExtra(ExtraStartAtProfileSetup, startAtProfileSetup)
                .putExtra(ExtraInitialNickname, initialNickname)
    }
}


