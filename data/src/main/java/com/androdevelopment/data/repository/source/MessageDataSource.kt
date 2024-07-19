package com.androdevelopment.data.repository.source

import com.androdevelopment.data.remote.entity.MessageEntity
import com.androdevelopment.domain.entity.Result
import kotlinx.coroutines.flow.Flow

interface MessageDataSource {

    fun getMessages(recipientId:String): Pair<Flow<List<MessageEntity>>, Flow<List<MessageEntity>>>

    fun sendMessage(message: MessageEntity,recipientId: String): Flow<Result>

}