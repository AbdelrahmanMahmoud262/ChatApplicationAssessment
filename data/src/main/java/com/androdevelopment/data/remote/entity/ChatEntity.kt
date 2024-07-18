package com.androdevelopment.data.remote.entity

data class ChatEntity(
    val recipientId: String,
    val recipientName: String,
    val lastMessage: String,
    val lastMessageDate: String?,
    val isRead: Boolean,
)