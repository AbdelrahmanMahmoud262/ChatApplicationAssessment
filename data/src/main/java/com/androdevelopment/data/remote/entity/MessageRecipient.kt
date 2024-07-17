package com.androdevelopment.data.remote.entity

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class MessageRecipient(
    val id: String = UUID.randomUUID().toString(),
    val recipientId: String,
    val messageId: String,
    val isRead: Int,
)