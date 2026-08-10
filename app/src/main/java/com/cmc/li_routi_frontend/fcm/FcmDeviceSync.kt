package com.cmc.li_routi_frontend.fcm

import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import com.li_routi.core.common.kotlin.util.ResultState
import com.li_routi.core.data.di.NotificationContainer
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * FCM 기기 토큰을 서버 `POST/DELETE /api/notifications/devices`에 동기화한다.
 */
object FcmDeviceSync {

    private const val Tag = "FCM"

    suspend fun registerCurrentDevice() {
        val fcmToken = fetchFcmToken() ?: return
        registerToken(fcmToken)
    }

    suspend fun onNewToken(token: String) {
        registerToken(token)
    }

    private suspend fun registerToken(token: String) {
        when (val result = NotificationContainer.registerFcmDeviceUseCase(token)) {
            is ResultState.Success -> {
                NotificationContainer.fcmTokenPreference.saveToken(token)
                Log.d(Tag, "device registered")
            }
            is ResultState.Error -> Log.w(Tag, "device register failed: ${result.message}")
            ResultState.Loading -> Unit
        }
    }

    private suspend fun fetchFcmToken(): String? = suspendCoroutine { cont ->
        FirebaseMessaging.getInstance().token
            .addOnSuccessListener { token -> cont.resume(token) }
            .addOnFailureListener { e ->
                Log.w(Tag, "fetch token failed", e)
                cont.resume(null)
            }
    }
}
