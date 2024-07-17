package com.androdevelopment.data.repository.repository

import com.androdevelopment.data.repository.source.HomeDataSource
import com.androdevelopment.domain.entity.Chat
import com.androdevelopment.domain.repository.HomeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.OffsetDateTime
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(
    private val dataSource: HomeDataSource
):HomeRepository {

    override fun getChats(): Flow<List<Chat>> = dataSource.getChats().map { chatList->
        chatList.map {chatEntity ->
            Chat(
                recipientId = chatEntity.recipientId,
                recipientName = chatEntity.recipientName,
                lastMessage = chatEntity.lastMessage,
                lastMessageDate = OffsetDateTime.parse(chatEntity.lastMessageDate),
                isRead = chatEntity.isRead
            )
        }
    }
}