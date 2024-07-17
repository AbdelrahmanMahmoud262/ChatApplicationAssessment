package com.androdevelopment.domain.repository

import com.androdevelopment.domain.entity.Chat
import kotlinx.coroutines.flow.Flow


interface HomeRepository {

    fun getChats(): Flow<List<Chat>>
}