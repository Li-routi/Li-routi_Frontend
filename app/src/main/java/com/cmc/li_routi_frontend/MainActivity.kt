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
import androidx.compose.runtime.CompositionLocalProvider
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
import com.li_routi.core.data.home.HomeContentPrefetcher
import com.li_routi.core.data.preference.AuthTokenPreference
import com.li_routi.core.common.ui.payment.LocalPaymentLauncher
import com.li_routi.core.common.ui.payment.LocalPaymentResultHandlerSetter
import com.li_routi.core.common.ui.payment.PaymentLauncher
import com.li_routi.core.data.di.NotificationContainer
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import io.portone.sdk.android.PortOne
import io.portone.sdk.android.payment.PaymentCallback
import io.portone.sdk.android.type.response.PaymentResponse
import com.li_routi.core.domain.notification.NotificationNavigationTarget
import com.li_routi.core.domain.notification.resolveNotificationNavigationTarget
import com.li_routi.feature.login.LoginActivity
import com.li_routi.feature.login.screen.LoadingScreen
import kotlinx.coroutines.launch

/**
 * 앱 전체를 하나로 묶는 진입점이자 매니페스트 launcher. [AppNavHost]를 통해 각 feature 화면을 연결한다.
 *
 * 저장된 서비스 토큰이 없으면(비로그인 상태) [LoginActivity]로 보내고 자신은 종료한다.
 */
class MainActivity : ComponentActivity() {

    private val requestNotificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { /* no-op */ }

    /** 결제 결과를 받을 콜백. 화면이 붙기 전에 정해져 있어야 해서 여기서 들고 있음 */
    private var paymentResultHandler: ((PaymentResponse, Boolean) -> Unit)? = null

    /**
     * 포트원 SDK는 결과 런처를 Activity가 STARTED 되기 전에 등록해야 함 —
     * Composable 안에서 만들면 이미 RESUMED라 등록이 거부됨. 그래서 여기서 미리 만들어 둠
     */
    private val paymentLauncher = PortOne.registerForPaymentActivity(
        this,
        callback = object : PaymentCallback {
            // code가 null이면 결제 성공
            override fun onSuccess(response: PaymentResponse) {
                paymentResultHandler?.invoke(response, true)
            }

            override fun onFail(response: PaymentResponse) {
                paymentResultHandler?.invoke(response, false)
            }
        },
    )

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
                startActivity(
                    LoginActivity.createIntent(
                        this@MainActivity,
                        startAtProfileSetup = true,
                        initialNickname = myInfo.data.nickname,
                    ),
                )
                finish()
                return@launch
            }
            // 토큰이 남아있어 로그인/로딩 화면을 거치지 않고 앱이 곧장 여기로 들어온 경우에도,
            // 로그인 경로(LoginRoute)와 동일하게 로딩화면을 잠깐 띄우고 그 뒤에서 홈 데이터/이미지를
            // 미리 받아둔다 — 안 그러면 이 경로만 홈 화면 자체의 로딩 스피너/이미지 깜빡임을 그대로
            // 겪는다.
            setContent {
                LiroutiFrontendTheme {
                    LoadingScreen()
                }
            }
            HomeContentPrefetcher.prefetchWithMinDuration()
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
                CompositionLocalProvider(
                    LocalPaymentLauncher provides PaymentLauncher { request ->
                        PortOne.requestPayment(this, request, paymentLauncher)
                    },
                    LocalPaymentResultHandlerSetter provides { handler ->
                        paymentResultHandler = handler
                    },
                ) {
                    AppNavHost(
                        modifier = Modifier.fillMaxSize(),
                        pendingNotificationTarget = pendingNotificationTarget,
                        pendingNotificationToken = pendingNotificationToken,
                        onPendingNotificationConsumed = { pendingNotificationTarget = null },
                    )
                }
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
        markNotificationReadFromIntent(intent)
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

    /**
     * 시스템 알림을 탭해 앱에 진입한 경우 서버의 알림 목록도 읽음 처리한다.
     *
     * 포그라운드에서 직접 만든 알림은 숫자 extra를 사용하고, 백그라운드에서 FCM이 만든 알림은
     * data payload의 문자열 extra를 사용한다. 서버 읽음 API는 멱등이므로 같은 Intent가 다시
     * 전달돼도 최초 읽은 시각은 유지된다.
     */
    private fun markNotificationReadFromIntent(intent: Intent) {
        val notificationId = intent
            .getLongExtra(FcmNotificationPresenter.ExtraNotificationId, -1L)
            .takeIf { it > 0 }
            ?: intent.getStringExtra("notificationId")?.toLongOrNull()?.takeIf { it > 0 }
            ?: return
        lifecycleScope.launch {
            NotificationContainer.markNotificationReadUseCase(notificationId)
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
