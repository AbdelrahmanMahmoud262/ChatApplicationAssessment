package com.androdevelopment.data.remote.source

import android.util.Log
import com.androdevelopment.data.remote.entity.ChatEntity
import com.androdevelopment.data.remote.entity.MessageEntity
import com.androdevelopment.data.remote.entity.MessageRecipient
import com.androdevelopment.data.remote.source.MessageDataSourceImpl.MessageCallback
import com.androdevelopment.data.repository.source.HomeDataSource
import com.androdevelopment.data.repository.source.MessageDataSource
import com.androdevelopment.data.utlis.Constants
import com.androdevelopment.data.utlis.SharedPreferenceManger
import com.androdevelopment.domain.entity.Chat
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class HomeDataSourceImpl @Inject constructor(
    private val sharedPreferenceManger: SharedPreferenceManger,
    private val messageDataSource: MessageDataSource,
) : HomeDataSource {

    private val db = FirebaseFirestore.getInstance()
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun getChats(): Flow<List<ChatEntity>> = callbackFlow {
        val recipientListener = db
            .collection(Constants.MESSAGE_RECIPIENT_COLLECTION)
            .whereEqualTo(Constants.CREATOR_ID, sharedPreferenceManger.userId)
            .addSnapshotListener { querySnapshot: QuerySnapshot?, error ->
                Log.e("getting", " gettttingintntingg")
                if (error != null) {
                    cancel(message = "Error fetching messages", cause = error)
                    return@addSnapshotListener
                }

                val messagesRecipients = querySnapshot?.documents
                    ?.map {
                        Log.e("Converting", it.getString("id").toString())
                        MessageRecipient(
                            id = it.getString(Constants.ID) ?: "",
                            recipientId = it.getString(Constants.RECIPIENT_ID) ?: "",
                            creatorId = it.getString("creatorId") ?: "",
                            messageId = it.getString("messageId") ?: "",
                            isRead = (it.get("read") as Long).toInt()
                        )
                    }
                    ?.distinctBy { it.recipientId }
                    ?.distinctBy { it.creatorId }
                    ?.map {messageRecipient->
                        ChatEntity(
                            recipientId = messageRecipient.recipientId,
                            recipientName = messageRecipient.recipientId,
                            lastMessage = messageRecipient.messageId,
                            lastMessageDate = OffsetDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME),
                            isRead = messageRecipient.isRead == 1
                        )
                    }

                scope.launch {
                    if (!messagesRecipients.isNullOrEmpty()) {
                        send(messagesRecipients)
                    }
                }


            }

        val currentUserListener = db
            .collection(Constants.MESSAGE_RECIPIENT_COLLECTION)
            .whereEqualTo(Constants.RECIPIENT_ID, sharedPreferenceManger.userId)
            .addSnapshotListener { querySnapshot: QuerySnapshot?, error ->
                Log.e("getting", " gettttingintntingg")
                if (error != null) {
                    cancel(message = "Error fetching messages", cause = error)
                    return@addSnapshotListener
                }

                val messagesRecipients = querySnapshot?.documents
                    ?.map {
                        Log.e("Converting", it.getString("id").toString())
                        MessageRecipient(
                            id = it.getString(Constants.ID) ?: "",
                            recipientId = it.getString(Constants.RECIPIENT_ID) ?: "",
                            creatorId = it.getString("creatorId") ?: "",
                            messageId = it.getString("messageId") ?: "",
                            isRead = (it.get("read") as Long).toInt()
                        )
                    }
                    ?.distinctBy { it.recipientId }
                    ?.distinctBy { it.creatorId }
                    ?.map {messageRecipient->
                        ChatEntity(
                            recipientId = messageRecipient.recipientId,
                            recipientName = messageRecipient.recipientId,
                            lastMessage = messageRecipient.messageId,
                            lastMessageDate = messageRecipient.messageId,
                            isRead = messageRecipient.isRead == 1
                        )
                    }

                Log.e("messagesDataSource", messagesRecipients.toString())

                scope.launch {
                    if (!messagesRecipients.isNullOrEmpty()) {
                        send(messagesRecipients)
                    }
                }
            }

        awaitClose {

            recipientListener.remove()
            currentUserListener.remove()

            scope.cancel()

        }
    }
}