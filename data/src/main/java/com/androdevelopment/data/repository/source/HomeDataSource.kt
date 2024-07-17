package com.androdevelopment.data.repository.source

import com.androdevelopment.data.remote.entity.ChatEntity
import kotlinx.coroutines.flow.Flow

interface HomeDataSource {

    fun getChats(): Flow<List<ChatEntity>>

}