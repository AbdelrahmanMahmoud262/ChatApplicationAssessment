package com.androdevelopment.domain.entity

import java.time.OffsetDateTime

data class Message(
    val id: String,
    val subject: String,
    val creatorId: String,
    val body: String,
    val dateCreated: OffsetDateTime,
    val isRead: Boolean,
)
