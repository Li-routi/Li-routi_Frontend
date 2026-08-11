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
import androidx.lifecycle.lifecycleScope
import com.cmc.li_routi_frontend.fcm.FcmNotificationPresenter
import com.cmc.li_routi_frontend.navigation.AppNavHost
import com.li_routi.core.common.kotlin.util.ResultState
import com.li_routi.core.data.di.AuthContainer
import com.li_routi.core.data.preference.AuthTokenPreference
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.domain.notification.NotificationNavigationTarget
import com.li_routi.core.domain.notification.resolveNotificationNavigationTarget
import com.li_routi.feature.login.LoginActivity
import kotlinx.coroutines.launch

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

        lifecycleScope.launch {
            // 토큰은 있어도 프로필 설정(온보딩)을 마치기 전에 앱이 강제 종료됐을 수 있다 — 서버의
            // 최신 onboardingCompleted를 다시 확인해, 미완료면 곧장 홈으로 들어가지 않고 프로필
            // 설정 화면으로 되돌린다. 조회 실패(네트워크 등)로 사용자를 홈에서 막지는 않는다.
            val myInfo = AuthContainer.getMyInfoUseCase()
            if (myInfo is ResultState.Success && !myInfo.data.onboardingCompleted) {
                startActivity(LoginActivity.createIntent(this@MainActivity, startAtProfileSetup = true))
                finish()
                return@launch
            }
            proceedToHome()
        }
    }

    private fun proceedToHome() {
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

    /**
     * 알림을 탭해서 실행/재진입한 경우 이동 정보를 읽는다.
     *
     * 앱이 포그라운드일 때는 [FcmNotificationPresenter]가 만든 커스텀 extras
     * ([FcmNotificationPresenter.ExtraNotificationType] 등)를 쓴다. 반면 백그라운드/종료
     * 상태에서는 시스템이 알림 payload로 직접 알림을 띄우고 기본 launcher 액티비티를
     * 여는데, 이때는 우리 커스텀 extras 없이 FCM data payload의 raw 키("type",
     * "referenceId")가 그대로 인텐트 extras로 들어온다. 그래서 커스텀 extras가 없으면
     * raw 키로 한 번 더 시도한다.
     */
    private fun consumeNotificationIntent(intent: Intent) {
        val type = intent.getStringExtra(FcmNotificationPresenter.ExtraNotificationType)
            ?: intent.getStringExtra("type")
            ?: return
        val referenceId = intent
            .getLongExtra(FcmNotificationPresenter.ExtraNotificationReferenceId, -1L)
            .takeIf { it >= 0 }
            ?: intent.getStringExtra("referenceId")?.toLongOrNull()
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
