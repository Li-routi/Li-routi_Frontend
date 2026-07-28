package com.li_routi.feature.login

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.feature.login.screen.LoginScreen

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LiroutiFrontendTheme {
                LoginScreen(
                    onKakaoLoginClick = ::goToHome,
                    onGoogleLoginClick = ::goToHome,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }

    /**
     * feature:login은 app 모듈을 의존하지 않으므로 MainActivity를 직접 참조하지 않고,
     * 앱의 launcher 인텐트로 홈 화면(AppNavHost)에 진입한다.
     */
    private fun goToHome() {
        packageManager.getLaunchIntentForPackage(packageName)?.let(::startActivity)
        finish()
    }
}
