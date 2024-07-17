package com.androdevelopment.data.remote.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.Date
import java.util.UUID

@Serializable
data class MessageEntity(
    val id:String = UUID.randomUUID().toString(),
    val subject:String,
    @SerialName("creator_id") val creatorId:String,
    @SerialName("message_body") val body:String,
    @SerialName("created_date") val dateCreated: String,
    @SerialName("is_read") val isRead:Int
)