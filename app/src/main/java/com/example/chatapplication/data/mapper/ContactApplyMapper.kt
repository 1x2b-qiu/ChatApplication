package com.example.chatapplication.data.mapper

import com.example.chatapplication.data.local.entity.ContactApplyEntity
import com.example.chatapplication.data.model.Contact
import com.example.chatapplication.data.model.ContactApply

fun ContactApply.toEntity(ownerId: String): ContactApplyEntity = ContactApplyEntity(
    applyId = this.applyId,
    contactId = this.contact.id,
    ownerId = ownerId,
    name = this.contact.name,
    avatar = this.contact.avatar ?: "",
    phone = this.contact.phone,
    gender = this.contact.gender,
    status = this.status,
    message = this.message,
    createdAt = this.createdAt,
    handledAt = this.handledAt,
    isMySent = this.isMySent
)


fun ContactApplyEntity.toDomain(): ContactApply = ContactApply(
    applyId = this.applyId,
    contact = Contact(
        id = this.contactId,
        name = this.name,
        avatar = this.avatar,
        phone = this.phone,
        gender = this.gender
    ),
    status = this.status,
    message = this.message,
    createdAt = this.createdAt,
    handledAt = this.handledAt,
    isMySent = this.isMySent
)