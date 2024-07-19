package com.androdevelopment.chatapplication.presentation.screens.chat

import com.androdevelopment.domain.entity.Message

data class ChatState(
    val chatItems:List<Message> = emptyList(),
    val recipientId:String = "",
    val isLoading:Boolean = false,
    val error:String? = null
)
