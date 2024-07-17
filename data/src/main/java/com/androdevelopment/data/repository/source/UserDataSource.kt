package com.androdevelopment.data.repository.source

import com.androdevelopment.domain.entity.Result
import com.androdevelopment.domain.entity.User

interface UserDataSource {

    fun insertUser(user: User): Result
}