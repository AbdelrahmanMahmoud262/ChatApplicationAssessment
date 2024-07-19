package com.androdevelopment.data.repository.repository

import com.androdevelopment.data.remote.entity.MessageEntity
import com.androdevelopment.data.repository.source.MessageDataSource
import com.androdevelopment.domain.entity.Message
import com.androdevelopment.domain.entity.Result
import com.androdevelopment.domain.repository.MessageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class MessageRepositoryImpl @Inject constructor(
    private val dataSource: MessageDataSource,
) : MessageRepository {

    override fun getMessages(recipientId: String): Flow<List<Message>> {
        val pair = dataSource.getMessages(recipientId)

        val recipientFlow = pair.first.map { messages ->
            messages.map { message ->
                Message(
                    id = message.id,
                    subject = message.subject,
                    creatorId = message.creatorId,
                    body = message.body,
                    dateCreated = OffsetDateTime.parse(message.dateCreated),
                    isRead = message.isRead != 0
                )
            }
        }

        val creatorFlow = pair.second.map { messages ->
            messages.map { message ->
                Message(
                    id = message.id,
                    subject = message.subject,
                    creatorId = message.creatorId,
                    body = message.body,
                    dateCreated = OffsetDateTime.parse(message.dateCreated),
                    isRead = message.isRead != 0
                )
            }
        }

        return merge(recipientFlow, creatorFlow)
    }

    override fun sendMessage(message: Message, recipientId: String): Flow<Result> =
        dataSource.sendMessage(
            message.let {
                MessageEntity(
                    id = it.id,
                    subject = it.subject,
                    creatorId = it.creatorId,
                    body = it.body,
                    dateCreated = it.dateCreated.format(DateTimeFormatter.ISO_DATE_TIME),
                    isRead = if (it.isRead) 1 else 0
                )
            },
            recipientId
        )
}