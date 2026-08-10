package com.cmc.li_routi_frontend.fcm

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class LiRoutiFirebaseMessagingService : FirebaseMessagingService() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        FcmNotificationPresenter.ensureChannel(this)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "onNewToken")
        serviceScope.launch {
            FcmDeviceSync.onNewToken(token)
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d(
            "FCM",
            "message: ${message.notification?.title} / ${message.notification?.body} / data=${message.data}",
        )
        // 포그라운드에서는 시스템이 자동 표시하지 않으므로 직접 트레이에 올린다.
        FcmNotificationPresenter.show(this, message)
    }

    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
    }
}
