package com.androdevelopment.domain.repository

import com.androdevelopment.domain.entity.Message
import com.androdevelopment.domain.entity.Result
import kotlinx.coroutines.flow.Flow

interface MessageRepository {

    fun getMessages(recipientId: String): Flow<List<Message>>

    fun sendMessage(message: Message,recipientId: String): Flow<Result>
}