package com.cmc.li_routi_frontend.fcm

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.cmc.li_routi_frontend.MainActivity
import com.cmc.li_routi_frontend.R
import com.google.firebase.messaging.RemoteMessage
import java.util.concurrent.atomic.AtomicInteger

/**
 * FCM 포그라운드 메시지를 시스템 트레이에 표시한다.
 * (포그라운드에서는 시스템이 notification payload를 자동 표시하지 않는다.)
 */
object FcmNotificationPresenter {

    const val ChannelId = "lirouti_default"
    /** [message.data]에서 알림 이벤트 타입을 읽는 키. 서버 알림 목록 API의 `type` 필드와 동일한 값이 온다고 가정한다. */
    const val ExtraNotificationType = "notification_type"
    /** [message.data]에서 대상 id를 읽는 키. 서버 알림 목록 API의 `referenceId`와 동일하다고 가정한다. */
    const val ExtraNotificationReferenceId = "notification_reference_id"
    private const val ChannelName = "리루티 알림"
    private val nextNotificationId = AtomicInteger(1)

    fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = context.getSystemService(NotificationManager::class.java) ?: return
        val existing = manager.getNotificationChannel(ChannelId)
        if (existing != null) return
        manager.createNotificationChannel(
            NotificationChannel(
                ChannelId,
                ChannelName,
                NotificationManager.IMPORTANCE_DEFAULT,
            ),
        )
    }

    fun show(context: Context, message: RemoteMessage) {
        if (!canPostNotifications(context)) return

        ensureChannel(context)
        val title = message.notification?.title
            ?: message.data["title"]
            ?: context.getString(R.string.app_name)
        val body = message.notification?.body
            ?: message.data["body"]
            ?: return

        val id = nextNotificationId.getAndIncrement()

        val launchIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                Intent.FLAG_ACTIVITY_SINGLE_TOP
            // id를 data에 실어 알림마다 유효(distinct)한 PendingIntent가 되게 한다.
            // 그렇지 않으면 request code가 0으로 같아 FLAG_UPDATE_CURRENT가 extras까지
            // 최신 알림 것으로 덮어써서, 예전 알림을 눌러도 최신 알림의 대상으로 이동해버린다.
            data = Uri.parse("lirouti://notification/$id")
            // MainActivity가 어느 화면으로 이동할지 판단하는 데 쓴다. resolveNotificationNavigationTarget 참고.
            putExtra(ExtraNotificationType, message.data["type"])
            putExtra(ExtraNotificationReferenceId, message.data["referenceId"]?.toLongOrNull() ?: -1L)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            id,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val notification = NotificationCompat.Builder(context, ChannelId)
            // adaptive mipmap(ic_launcher)은 알림 small icon으로 사용 불가 → 단색 drawable
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        val tag = message.messageId?.takeIf { it.isNotEmpty() } ?: "fcm"
        runCatching {
            NotificationManagerCompat.from(context).notify(tag, id, notification)
        }
    }

    private fun canPostNotifications(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS,
        ) == PackageManager.PERMISSION_GRANTED
    }
}
