package com.androdevelopment.domain.entity

import java.time.OffsetDateTime

data class Chat(
    val recipientId:String,
    val recipientName:String,
    val lastMessage:String,
    val lastMessageDate:OffsetDateTime,
    val isRead:Boolean
)
