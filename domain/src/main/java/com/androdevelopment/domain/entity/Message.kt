package com.androdevelopment.domain.entity

import java.time.OffsetDateTime
import java.util.UUID

data class Message(
    val id: String = UUID.randomUUID().toString(),
    val subject: String = "",
    val creatorId: String,
    val body: String,
    val dateCreated: OffsetDateTime = OffsetDateTime.now(),
    val isRead: Boolean = false,
)
