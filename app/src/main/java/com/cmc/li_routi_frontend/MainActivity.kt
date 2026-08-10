package com.cmc.li_routi_frontend

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.cmc.li_routi_frontend.fcm.FcmNotificationPresenter
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

    private val requestNotificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { /* no-op */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (AuthTokenPreference(applicationContext).getAccessTokenBlocking().isNullOrBlank()) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        FcmNotificationPresenter.ensureChannel(this)
        requestPostNotificationsIfNeeded()

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.WHITE, Color.WHITE),
        )
        setContent {
            LiroutiFrontendTheme {
                AppNavHost(modifier = Modifier.fillMaxSize())
            }
        }
    }

    private fun requestPostNotificationsIfNeeded() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
        val granted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS,
        ) == PackageManager.PERMISSION_GRANTED
        if (!granted) {
            requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}
