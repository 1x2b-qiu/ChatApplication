package com.example.chatapplication.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.chatapplication.MainActivity
import com.example.chatapplication.R
import com.example.chatapplication.notification.model.NotificationMessage
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationDispatcher @Inject constructor(
    @ApplicationContext private val context: Context,
    private val notificationPermissionManager: NotificationPermissionManager
) {
    private val manager: NotificationManager
        get() = context.getSystemService(NotificationManager::class.java)

    //初始化通知信道
    fun initChannels() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        //前台服务信道
        val foregroundServiceChannel = NotificationChannel(
            "foreground_service", "前台服务", NotificationManager.IMPORTANCE_LOW
        ).apply {
            setShowBadge(false)
            enableVibration(false)
            setSound(null, null)
        }

        //消息通知信道
        val messageChannel = NotificationChannel(
            "message", "新消息通知", NotificationManager.IMPORTANCE_HIGH
        ).apply {
            enableVibration(true)   //允许震动
        }

        manager.createNotificationChannel(foregroundServiceChannel)
        manager.createNotificationChannel(messageChannel)
    }


    fun createServiceNotification(): Notification {
        return NotificationCompat.Builder(context, "foreground_service")
            .setContentTitle("服务已启动")
            .setContentText("正在实时接收新消息")
            .setSmallIcon(R.drawable.ic_rose)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }


    fun showMessage(notificationMessage: NotificationMessage) {
        if (!notificationPermissionManager.hasPermission()) return

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra("conversationId", notificationMessage.conversationId)
            putExtra("targetName", notificationMessage.targetName)
            putExtra("targetId", notificationMessage.targetId)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationMessage.conversationId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, "message")
            .setSmallIcon(R.drawable.ic_rose)
            .setContentTitle(notificationMessage.targetName)
            .setContentText(notificationMessage.content)
            .setStyle(NotificationCompat.BigTextStyle().bigText(notificationMessage.content))
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(Notification.DEFAULT_ALL)
            .setAutoCancel(true)
            .setWhen(notificationMessage.timestamp)
            .setShowWhen(true)
            .setGroup("chat_${notificationMessage.conversationId}")
            .setContentIntent(pendingIntent)
            .build()
        manager.notify(notificationMessage.conversationId.hashCode(), notification)
    }
}