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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.cmc.li_routi_frontend.fcm.FcmNotificationPresenter
import com.cmc.li_routi_frontend.navigation.AppNavHost
import com.li_routi.core.data.preference.AuthTokenPreference
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.domain.notification.NotificationNavigationTarget
import com.li_routi.core.domain.notification.resolveNotificationNavigationTarget
import com.li_routi.feature.login.LoginActivity

/**
 * 앱 전체를 하나로 묶는 진입점이자 매니페스트 launcher. [AppNavHost]를 통해 각 feature 화면을 연결한다.
 *
 * 저장된 서비스 토큰이 없으면(비로그인 상태) [LoginActivity]로 보내고 자신은 종료한다.
 */
class MainActivity : ComponentActivity() {

    private val requestNotificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { /* no-op */ }

    private var pendingNotificationTarget by mutableStateOf<NotificationNavigationTarget?>(null)

    /** [pendingNotificationTarget]이 같은 값이라도(예: 동일 타입 알림 재수신) 매번 새로 처리시키기 위한 토큰. */
    private var pendingNotificationToken by mutableLongStateOf(0L)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (AuthTokenPreference(applicationContext).getAccessTokenBlocking().isNullOrBlank()) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        FcmNotificationPresenter.ensureChannel(this)
        requestPostNotificationsIfNeeded()
        consumeNotificationIntent(intent)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.WHITE, Color.WHITE),
        )
        setContent {
            LiroutiFrontendTheme {
                AppNavHost(
                    modifier = Modifier.fillMaxSize(),
                    pendingNotificationTarget = pendingNotificationTarget,
                    pendingNotificationToken = pendingNotificationToken,
                    onPendingNotificationConsumed = { pendingNotificationTarget = null },
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        consumeNotificationIntent(intent)
    }

    /** 알림을 탭해서 실행/재진입한 경우 [FcmNotificationPresenter]가 실어 보낸 이동 정보를 읽는다. */
    private fun consumeNotificationIntent(intent: Intent) {
        val type = intent.getStringExtra(FcmNotificationPresenter.ExtraNotificationType) ?: return
        val referenceId = intent
            .getLongExtra(FcmNotificationPresenter.ExtraNotificationReferenceId, -1L)
            .takeIf { it >= 0 }
        pendingNotificationTarget = resolveNotificationNavigationTarget(type, referenceId) ?: return
        pendingNotificationToken++
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
