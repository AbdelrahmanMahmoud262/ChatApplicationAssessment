package com.androdevelopment.chatapplication.presentation.screens.chat

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androdevelopment.data.utlis.Constants
import com.androdevelopment.data.utlis.SharedPreferenceManger
import com.androdevelopment.domain.entity.Message
import com.androdevelopment.domain.entity.Result
import com.androdevelopment.domain.repository.MessageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val messageRepository: MessageRepository,
    private val sharedPreferenceManger: SharedPreferenceManger,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    val recipientId = savedStateHandle.get<String>(Constants.RECIPIENT_ID)

    var state by mutableStateOf(ChatState())
        private set

    init {
        state = state.copy(recipientId = recipientId.toString())
        getMessages()
    }

    private fun getMessages() {
        viewModelScope.launch {
            messageRepository.getMessages(recipientId.toString())
                .collect { messageList ->
                    Log.e("messages",messageList.map { it.body }.toString())
                    state = state.copy(chatItems = messageList.sortedBy { it.dateCreated })
                }
        }
    }

    fun sendMessage(message: String) {
        viewModelScope.launch {
            messageRepository.sendMessage(
                message = Message(
                    id = UUID.randomUUID().toString(),
                    creatorId = sharedPreferenceManger.userId,
                    body = message,
                ),
                recipientId = recipientId.toString(),
            ).collect {
                when(it){
                    is Result.Error -> {
                        Log.e("ChatViewModel","error",it.exception)
                    }
                    is Result.Success<*> -> {
                        Log.e("ChatViewModel","success")
                    }
                }
            }
        }
    }
}