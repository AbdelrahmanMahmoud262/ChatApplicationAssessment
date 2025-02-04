package com.androdevelopment.data.repository.source

import com.androdevelopment.domain.entity.Result
import com.androdevelopment.domain.entity.User
import kotlinx.coroutines.flow.Flow

interface UserDataSource {

    fun insertUser(user: User): Result

    fun getUser(id: String): Flow<User>

    fun validateUser(email: String, password: String): Flow<User?>

    fun getUsers():Flow<List<User>>

 }