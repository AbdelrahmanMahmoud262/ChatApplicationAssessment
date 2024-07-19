package com.androdevelopment.domain.repository

import com.androdevelopment.domain.entity.Result
import com.androdevelopment.domain.entity.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    fun insertUser(user: User):Result

    fun validateUser(email: String, password: String): Flow<User?>
    fun getUsers(): Flow<List<User>>
}