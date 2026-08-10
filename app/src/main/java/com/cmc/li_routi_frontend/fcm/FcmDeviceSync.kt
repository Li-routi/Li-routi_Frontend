package com.cmc.li_routi_frontend.fcm

import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import com.li_routi.core.common.kotlin.util.ResultState
import com.li_routi.core.data.di.NotificationContainer
import com.li_routi.core.data.network.NetworkModule
import com.li_routi.core.data.notification.FcmDeviceSyncGate
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * FCM 기기 토큰을 서버 `POST/DELETE /api/notifications/devices`에 동기화한다.
 *
 * - 등록 실패 시 짧은 backoff로 재시도한다(WorkManager 없이 프로세스 내).
 * - [FcmDeviceSyncGate]로 로그아웃/탈퇴 해제와 직렬화한다.
 * - MessagingService 생명주기와 무관한 [processScope]에서 토큰 갱신을 처리한다.
 */
object FcmDeviceSync {

    private const val Tag = "FCM"
    private const val MaxAttempts = 3
    private val RetryDelaysMs = longArrayOf(0L, 1_000L, 3_000L)

    private val processScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    suspend fun registerCurrentDevice() {
        val fcmToken = fetchFcmToken() ?: return
        registerToken(fcmToken)
    }

    suspend fun onNewToken(token: String) {
        registerToken(token)
    }

    /** [LiRoutiFirebaseMessagingService] onDestroy로 취소되지 않도록 프로세스 스코프에 올린다. */
    fun enqueueOnNewToken(token: String) {
        processScope.launch { onNewToken(token) }
    }

    private suspend fun registerToken(token: String) {
        FcmDeviceSyncGate.withExclusive {
            if (!hasActiveSession()) {
                Log.d(Tag, "skip register: no access token")
                return@withExclusive
            }
            repeat(MaxAttempts) { attempt ->
                if (attempt > 0) delay(RetryDelaysMs[attempt])
                if (!hasActiveSession()) {
                    Log.d(Tag, "abort register: session ended")
                    return@withExclusive
                }
                when (val result = NotificationContainer.registerFcmDeviceUseCase(token)) {
                    is ResultState.Success -> {
                        // 응답 사이에 로그아웃됐을 수 있으므로 저장 직전 재확인
                        if (!hasActiveSession()) {
                            Log.d(Tag, "drop register result: session ended")
                            return@withExclusive
                        }
                        NotificationContainer.fcmTokenPreference.saveToken(token)
                        Log.d(Tag, "device registered")
                        return@withExclusive
                    }
                    is ResultState.Error -> {
                        Log.w(
                            Tag,
                            "device register failed (attempt ${attempt + 1}/$MaxAttempts): ${result.message}",
                        )
                    }
                    ResultState.Loading -> Unit
                }
            }
        }
    }

    private suspend fun hasActiveSession(): Boolean =
        !NetworkModule.authTokenPreference.accessTokenFlow.first().isNullOrBlank()

    private suspend fun fetchFcmToken(): String? = suspendCoroutine { cont ->
        FirebaseMessaging.getInstance().token
            .addOnSuccessListener { token -> cont.resume(token) }
            .addOnFailureListener { e ->
                Log.w(Tag, "fetch token failed", e)
                cont.resume(null)
            }
    }
}
