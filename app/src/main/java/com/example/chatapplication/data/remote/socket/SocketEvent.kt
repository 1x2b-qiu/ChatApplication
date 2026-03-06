package com.example.chatapplication.data.remote.socket

import com.example.chatapplication.data.remote.socket.dto.ContactAcceptedDto
import com.example.chatapplication.data.remote.socket.dto.ContactProfileUpdatedDto
import com.example.chatapplication.data.remote.socket.dto.MessageDto

sealed class SocketEvent {
    object Connected : SocketEvent()
    data class ContactAccepted(val data: ContactAcceptedDto) : SocketEvent()
    data class ContactProfileUpdated(val data: ContactProfileUpdatedDto) : SocketEvent()
    data class MessageReceived(val data: MessageDto) : SocketEvent()
}
