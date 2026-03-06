package com.example.chatapplication.data.remote.socket

import android.util.Log
import com.example.chatapplication.data.remote.socket.dto.ContactAcceptedDto
import com.example.chatapplication.data.remote.socket.dto.ContactProfileUpdatedDto
import com.example.chatapplication.data.remote.socket.dto.MessageDto
import com.google.gson.Gson
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SocketManager @Inject constructor(
    private val gson: Gson
) {
    private var socket: Socket? = null
    private var currentUserId: String? = null

    private val _eventFlow = MutableSharedFlow<SocketEvent>(
        replay = 50,
        extraBufferCapacity = 64, // 💡 增加缓冲区，防止离线消息太多时溢出
        onBufferOverflow = BufferOverflow.SUSPEND // 💡 缓冲区满时挂起而非丢弃
    )

    val eventFlow: SharedFlow<SocketEvent> = _eventFlow.asSharedFlow()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    init {
        try {
            //连接前初始化配置
            val opts = IO.Options().apply {
                forceNew = true
                reconnection = true
            }
            socket = IO.socket("http://121.43.25.186:8080", opts)

            //连接前开始监听
            socket?.on(Socket.EVENT_CONNECT) {
                //发送用户Id到服务器
                currentUserId?.let { userId ->
                    socket?.emit("join", userId)
                }
                scope.launch {
                    _eventFlow.emit(SocketEvent.Connected)
                }
            }

            initSocketEvents()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initSocketEvents() {
        onEvent("contact_accepted")
        onEvent("contact_profile_updated")
        onEvent("message")
    }

    fun sendMessage(message: MessageDto) {
        if (socket?.connected() == true) {
            val json = gson.toJson(message)
            socket?.emit("message", json)
        } else {
            Log.w("SocketManager", "发送消息失败：Socket 未连接")
        }
    }


    fun connect(userId: String) {
        this.currentUserId = userId
        if (socket?.connected() == true) {
            // 如果已经连上了，手动补发一次 join
            socket?.emit("join", userId)
            return
        }
        try {
            socket?.connect()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun disconnect() {
        socket?.disconnect()
    }

    fun onEvent(event: String) {
        socket?.on(event) { args ->
            val json = args[0].toString()
            scope.launch {
                when (event) {
                    "contact_accepted" -> {
                        val dto = gson.fromJson(json, ContactAcceptedDto::class.java)
                        _eventFlow.emit(SocketEvent.ContactAccepted(dto))
                    }

                    "contact_profile_updated" -> {
                        val dto = gson.fromJson(json, ContactProfileUpdatedDto::class.java)
                        _eventFlow.emit(SocketEvent.ContactProfileUpdated(dto))
                    }

                    "message" -> {
                        val dto = gson.fromJson(json, MessageDto::class.java)
                        _eventFlow.emit(SocketEvent.MessageReceived(dto))
                    }
                }
            }
        }
    }
}