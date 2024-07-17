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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
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
    ): Flow<List<MessageEntity>> = callbackFlow {
        val recipientListener = db
            .collection(Constants.MESSAGE_RECIPIENT_COLLECTION)
            .whereEqualTo(Constants.RECIPIENT_ID, recipientId)
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
                            messageId = it.getString("messageId") ?: "",
                            isRead = (it.get("read") as Long).toInt()
                        )
                    }

                if (!messagesRecipients.isNullOrEmpty()) {
                    val messageIds = messagesRecipients.map { it.messageId }
                    Log.e("ids", messageIds.toString())
                    val messages = mutableListOf<MessageEntity>()
                    messageIds.forEach { id ->
                        getMessage(id,object:MessageCallback{
                            override fun onMessageReceived(message: MessageEntity?) {
                                if (message != null) {
                                    messages.add(message)
                                }

                                if (messages.size == messageIds.size){
                                    scope.launch {
                                        send(messages)
                                    }
                                }
                            }
                            override fun onError(exception: Exception) {
                                cancel(message = "Error fetching messages", cause = exception)
                            }
                        })
                    }

                    Log.e("messagesDataSource", messages.toString())

                } else {
                    Log.e("empty 2222", "ermkfawel;rjkfas;dlf")
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
                    ?.mapNotNull {
                        MessageRecipient(
                            id = it.getString(Constants.ID) ?: "",
                            recipientId = it.getString(Constants.RECIPIENT_ID) ?: "",
                            messageId = it.getString("messageId") ?: "",
                            isRead = (it.get("read") as Long).toInt()
                        )
                    }

                if (!messagesRecipients.isNullOrEmpty()) {
                    val messageIds = messagesRecipients.map { it.messageId }
                    Log.e("ids", messageIds.toString())
                    val messages = mutableListOf<MessageEntity>()
                    messageIds.forEach { id ->
                        getMessage(id,object:MessageCallback{
                            override fun onMessageReceived(message: MessageEntity?) {
                                if (message != null) {
                                    messages.add(message)
                                }

                                if (messages.size == messageIds.size){
                                    scope.launch {
                                        send(messages)
                                    }
                                }
                            }
                            override fun onError(exception: Exception) {
                                cancel(message = "Error fetching messages", cause = exception)
                            }
                        })
                    }
                } else {
                    Log.e("emtpy", "empty")
                }
            }

        awaitClose {

            recipientListener.remove()
            currentUserListener.remove()

            scope.cancel()

        }
    }

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

    private fun getMessage(messageId: String, callback: MessageCallback) {
        Log.e("getMessage", "getting a Message")

        db.collection(Constants.MESSAGE_COLLECTION)
            .whereEqualTo(Constants.ID, messageId)
            .limit(1)
            .get()
            .addOnSuccessListener { querySnapshot ->
                Log.e("success", "success $messageId")
                val item = querySnapshot?.documents?.mapNotNull {
                    Log.e("converting", "converting ${it.get("id")}")
                    MessageEntity(
                        id = it.getString(Constants.ID) ?: "",
                        subject = it.getString("subject") ?: "",
                        creatorId = it.getString("creatorId") ?: "",
                        body = it.getString("body") ?: "",
                        dateCreated = (it.get("dateCreated") ?: "").toString(),
                        isRead = (it.get("read") as Long).toInt()
                    )
                }?.firstOrNull()
                callback.onMessageReceived(item) // Notify the callback with the result
            }
            .addOnFailureListener { exception ->
                Log.e("error", "error getting message $messageId", exception)
                callback.onError(exception) // Notify the callback with the error
            }
    }

}