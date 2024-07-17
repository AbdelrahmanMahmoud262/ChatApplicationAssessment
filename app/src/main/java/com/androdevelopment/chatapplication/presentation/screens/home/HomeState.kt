package com.androdevelopment.chatapplication.presentation.screens.home

import com.androdevelopment.domain.entity.Chat

data class HomeState(
    val chats: List<Chat> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)