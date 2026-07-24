package com.cmc.li_routi_frontend

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.cmc.li_routi_frontend.navigation.AppNavHost
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme

/**
 * 앱 전체를 하나로 묶는 진입점. [AppNavHost]를 통해 각 feature 화면을 연결한다.
 *
 * 아직 매니페스트 launcher는 [com.li_routi.feature.login.LoginActivity]로 유지 중이라,
 * 이 Activity는 Android Studio에서 직접 실행 구성으로 지정해야 켜진다.
 * 로그인 플로우가 [AppNavHost]에 편입되면 launcher를 이쪽으로 옮긴다.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LiroutiFrontendTheme {
                AppNavHost(modifier = Modifier.fillMaxSize())
            }
        }
    }
}
