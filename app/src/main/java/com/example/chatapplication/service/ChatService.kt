package com.example.chatapplication.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.example.chatapplication.data.remote.socket.SocketManager
import com.example.chatapplication.data.repository.ChatRepository
import com.example.chatapplication.data.repository.ContactApplyRepository
import com.example.chatapplication.data.repository.ContactRepository
import com.example.chatapplication.data.repository.ConversationRepository
import com.example.chatapplication.data.repository.ProfileRepository
import com.example.chatapplication.data.repository.SessionRepository
import com.example.chatapplication.notification.NotificationDispatcher
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ChatService : Service() {

    @Inject
    lateinit var sessionRepository: SessionRepository

    @Inject
    lateinit var chatRepository: ChatRepository

    @Inject
    lateinit var contactRepository: ContactRepository

    @Inject
    lateinit var contactApplyRepository: ContactApplyRepository

    @Inject
    lateinit var conversationRepository: ConversationRepository

    @Inject
    lateinit var profileRepository: ProfileRepository

    @Inject
    lateinit var notificationDispatcher: NotificationDispatcher


    @Inject
    lateinit var socketManager: SocketManager

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)


    @SuppressLint("ForegroundServiceType")
    override fun onCreate() {
        super.onCreate()
        //初始化通知信道
        notificationDispatcher.initChannels()

        //启动前台服务
        startForeground(1, notificationDispatcher.createServiceNotification())

        chatRepository.start()
        contactRepository.start()
        contactApplyRepository.start()
        conversationRepository.start()
        profileRepository.start()
        // 开始观察登录状态并连接 Socket
        serviceScope.launch {
            sessionRepository.observeUserId().distinctUntilChanged().collect { userId ->
                if (userId != null) {
                    socketManager.connect(userId)
                } else {
                    stopSelf()
                }
            }
        }
    }

    //如果服务被系统杀死 自动重启
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    //不允许被绑定
    override fun onBind(intent: Intent?): IBinder? = null

    //销毁时进行收尾
    override fun onDestroy() {
        serviceScope.cancel()
        socketManager.disconnect()
        super.onDestroy()
    }

}