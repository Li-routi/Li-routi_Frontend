package com.cmc.li_routi_frontend

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.cmc.li_routi_frontend.navigation.AppNavHost
import com.li_routi.core.data.preference.AuthTokenPreference
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.feature.login.LoginActivity

/**
 * 앱 전체를 하나로 묶는 진입점이자 매니페스트 launcher. [AppNavHost]를 통해 각 feature 화면을 연결한다.
 *
 * 저장된 서비스 토큰이 없으면(비로그인 상태) [LoginActivity]로 보내고 자신은 종료한다.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (AuthTokenPreference(applicationContext).getAccessTokenBlocking().isNullOrBlank()) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        enableEdgeToEdge()
        setContent {
            LiroutiFrontendTheme {
                AppNavHost(modifier = Modifier.fillMaxSize())
            }
        }
    }
}
