package com.androdevelopment.data.remote.source

import android.util.Log
import com.androdevelopment.data.remote.entity.MessageEntity
import com.androdevelopment.data.remote.entity.MessageRecipient
import com.androdevelopment.data.repository.source.MessageDataSource
import com.androdevelopment.data.utlis.Constants
import com.androdevelopment.data.utlis.SharedPreferenceManger
import com.androdevelopment.domain.entity.Result
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
import javax.inject.Inject

class MessageDataSourceImpl @Inject constructor(
    private val sharedPreferenceManger: SharedPreferenceManger,
) : MessageDataSource {

    private val db = FirebaseFirestore.getInstance()
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val scopeFlow = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun getMessages(
        recipientId: String,
    ): Pair<Flow<List<MessageEntity>>,Flow<List<MessageEntity>>> = Pair(callbackFlow {
        val recipientListener = db
            .collection(Constants.MESSAGE_RECIPIENT_COLLECTION)
            .whereEqualTo(Constants.RECIPIENT_ID, recipientId)
            .whereEqualTo(Constants.CREATOR_ID, sharedPreferenceManger.userId)
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

                if (!messagesRecipients.isNullOrEmpty()) {
                    scopeFlow.launch {
                        val messageEntities = coroutineScope {
                            messagesRecipients.map { messageRecipient ->
                                async {
                                    val message =
                                        getMessage(messageRecipient.messageId).firstOrNull()

                                    message ?: MessageEntity(
                                        id = "",
                                        subject = "",
                                        creatorId = sharedPreferenceManger.userId,
                                        body = "",
                                        dateCreated = "",
                                        isRead = 0
                                    )
                                }
                            }.awaitAll()
                        }
                        send(messageEntities)
                    }
                }

            }
        awaitClose {
            recipientListener.remove()
            scopeFlow.cancel()
        }
    }
,callbackFlow {
        val currentUserListener = db
            .collection(Constants.MESSAGE_RECIPIENT_COLLECTION)
            .whereEqualTo(Constants.RECIPIENT_ID, sharedPreferenceManger.userId)
            .whereEqualTo(Constants.CREATOR_ID, recipientId)
            .addSnapshotListener { querySnapshot: QuerySnapshot?, error ->
                if (error != null) {
                    cancel(message = "Error fetching messages", cause = error)
                    return@addSnapshotListener
                }

                val messagesRecipients = querySnapshot?.documents
                    ?.mapNotNull {
                        MessageRecipient(
                            id = it.getString(Constants.ID) ?: "",
                            recipientId = it.getString(Constants.RECIPIENT_ID) ?: "",
                            messageId = it.getString("messageId") ?: "",
                            creatorId = it.getString("creatorId") ?: "",
                            isRead = (it.get("read") as Long).toInt()
                        )
                    }

                if (!messagesRecipients.isNullOrEmpty()) {
                    scope.launch {
                        val messageEntities = coroutineScope {
                            messagesRecipients.map { messageRecipient ->
                                async {
                                    val message =
                                        getMessage(messageRecipient.messageId).firstOrNull()

                                    message ?: MessageEntity(
                                        id = "",
                                        subject = "",
                                        creatorId = recipientId,
                                        body = "",
                                        dateCreated = "",
                                        isRead = 0
                                    )
                                }
                            }.awaitAll()
                        }
                        send(messageEntities)
                    }
                }
            }

        awaitClose {

            currentUserListener.remove()

            scope.cancel()

        }
    })

    override fun sendMessage(
        message: MessageEntity,
        recipientId: String,
    ): Flow<Result> = callbackFlow {
        db
            .collection(Constants.MESSAGE_COLLECTION)
            .add(message)
            .addOnSuccessListener {

                val messageRecipient = MessageRecipient(
                    recipientId = recipientId,
                    messageId = message.id,
                    creatorId = sharedPreferenceManger.userId,
                    isRead = 0
                )

                Log.e("Creating", "creating recipient")

                db.collection(Constants.MESSAGE_RECIPIENT_COLLECTION)
                    .add(messageRecipient)
                    .addOnSuccessListener {
                        trySend(Result.Success(Unit))
                        Log.e("created", "created")
                    }.addOnFailureListener {
                        trySend(Result.Error(it))
                        Log.e("error", "error")
                        cancel()
                    }

                trySend(Result.Success(Unit))
            }
            .addOnFailureListener {
                trySend(Result.Error(it))
                cancel()
            }

        awaitClose {
        }
    }

    private fun createMessageRecipient(
        message: MessageEntity,
        recipientId: String,
    ): Flow<Result> = callbackFlow {
        val messageRecipient = MessageRecipient(
            recipientId = recipientId,
            messageId = message.id,
            creatorId = sharedPreferenceManger.userId,
            isRead = 0
        )

        Log.e("Creating", "creating recipient")

        db.collection(Constants.MESSAGE_RECIPIENT_COLLECTION)
            .add(messageRecipient)
            .addOnSuccessListener {
                trySend(Result.Success(Unit))
                Log.e("created", "created")
            }.addOnFailureListener {
                trySend(Result.Error(it))
                Log.e("error", "error")
                cancel()
            }


        awaitClose {

        }
    }

    interface MessageCallback {
        fun onMessageReceived(message: MessageEntity?)
        fun onError(exception: Exception)
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
                                dateCreated = it.getString("dateCreated") ?: "",
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