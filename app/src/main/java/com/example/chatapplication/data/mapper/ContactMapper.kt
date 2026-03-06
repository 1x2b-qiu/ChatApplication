package com.example.chatapplication.data.mapper

import com.example.chatapplication.data.local.entity.ContactEntity
import com.example.chatapplication.data.remote.retrofit.response.contact.ContactResponse
import com.example.chatapplication.data.model.Contact
import com.example.chatapplication.data.model.Gender

fun Contact.toEntity(ownerId: String): ContactEntity = ContactEntity(
    id = this.id,
    ownerId = ownerId,
    name = this.name,
    avatar = this.avatar,
    phone = this.phone,
    gender = this.gender,
    remarks = this.remarks
)

fun ContactEntity.toDomain(): Contact = Contact(
    id = this.id,
    name = this.name,
    avatar = this.avatar,
    phone = this.phone,
    gender = this.gender,
    remarks = this.remarks
)


fun ContactResponse.toDomain(): Contact = Contact(
    id = this.id,
    name = this.name,
    avatar = this.avatar,
    phone = this.phone,
    gender = Gender.fromString(this.gender)
)