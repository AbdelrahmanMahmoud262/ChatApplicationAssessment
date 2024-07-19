package com.androdevelopment.chatapplication.presentation.screens.home

import com.androdevelopment.domain.entity.Chat
import com.androdevelopment.domain.entity.User

data class HomeState(
    val chats: List<Chat> = emptyList(),
    val users:List<User> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)