package com.androdevelopment.data.remote.source

import android.util.Log
import com.androdevelopment.data.remote.entity.ChatEntity
import com.androdevelopment.data.remote.entity.MessageEntity
import com.androdevelopment.data.remote.entity.MessageRecipient
import com.androdevelopment.data.repository.source.HomeDataSource
import com.androdevelopment.data.repository.source.MessageDataSource
import com.androdevelopment.data.repository.source.UserDataSource
import com.androdevelopment.data.utlis.Constants
import com.androdevelopment.data.utlis.SharedPreferenceManger
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class HomeDataSourceImpl @Inject constructor(
    private val sharedPreferenceManger: SharedPreferenceManger,
    private val messageDataSource: MessageDataSource,
    private val userDataSource: UserDataSource,
) : HomeDataSource {

    private val db = FirebaseFirestore.getInstance()
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun getChats(): Flow<List<ChatEntity>> = callbackFlow {
        val recipientListener = db
            .collection(Constants.MESSAGE_RECIPIENT_COLLECTION)
            .whereEqualTo(Constants.CREATOR_ID, sharedPreferenceManger.userId)
            .addSnapshotListener { querySnapshot: QuerySnapshot?, error ->
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
                    ?.distinctBy { it.creatorId }
                    ?.distinctBy { it.recipientId }

                if (!messagesRecipients.isNullOrEmpty()) {
                    scope.launch {
                        val chatEntities = coroutineScope {
                            messagesRecipients.map { messageRecipient ->
                                async {
                                    val message =
                                        getMessage(messageRecipient.messageId).firstOrNull()

                                    val user = userDataSource.getUser(messageRecipient.recipientId).firstOrNull()

                                    ChatEntity(
                                        recipientId = messageRecipient.recipientId,
                                        recipientName = user?.firstName?: "",
                                        lastMessage = message?.body?:"",
                                        lastMessageDate = message?.dateCreated?.let {
                                            OffsetDateTime.parse(it).format(
                                                DateTimeFormatter.ISO_DATE_TIME
                                            )
                                        },
                                        isRead = messageRecipient.isRead == 1
                                    )
                                }
                            }.awaitAll()
                        }
                        send(chatEntities)
                    }
                }else{
                    scope.launch {
                        send(emptyList())
                    }
                }


            }

        val currentUserListener = db
            .collection(Constants.MESSAGE_RECIPIENT_COLLECTION)
            .whereEqualTo(Constants.RECIPIENT_ID, sharedPreferenceManger.userId)
            .addSnapshotListener { querySnapshot: QuerySnapshot?, error ->
                if (error != null) {
                    cancel(message = "Error fetching messages", cause = error)
                    return@addSnapshotListener
                }

                val messagesRecipients = querySnapshot?.documents
                    ?.map {
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
                    ?.map { messageRecipient ->
                        ChatEntity(
                            recipientId = messageRecipient.recipientId,
                            recipientName = messageRecipient.recipientId,
                            lastMessage = messageRecipient.messageId,
                            lastMessageDate = messageRecipient.messageId,
                            isRead = messageRecipient.isRead == 1
                        )
                    }

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


    fun getMessage(messageId: String) = callbackFlow {
        Log.e("getMessage", "getting a Message")

        db.collection(Constants.MESSAGE_COLLECTION)
            .whereEqualTo(Constants.ID, messageId)
            .limit(1)
            .get()
            .addOnSuccessListener { querySnapshot ->
                Log.e("success", "success $messageId")
                querySnapshot?.documents?.firstNotNullOfOrNull {
                    Log.e("converting", "converting ${it.get("id")}")
                    scope.launch {
                        send(
                            MessageEntity(
                                id = it.getString(Constants.ID) ?: "",
                                subject = it.getString("subject") ?: "",
                                creatorId = it.getString("creatorId") ?: "",
                                body = it.getString("body") ?: "",
                                dateCreated = (it.get("dateCreated") ?: "").toString(),
                                isRead = (it.get("read") as Long).toInt()
                            )
                        )
                    }
                }
//                callback.onMessageReceived(item) // Notify the callback with the result
            }
            .addOnFailureListener { exception ->
                Log.e("error", "error getting message $messageId", exception)
//                callback.onError(exception) // Notify the callback with the error
            }

        awaitClose {

        }
    }

}