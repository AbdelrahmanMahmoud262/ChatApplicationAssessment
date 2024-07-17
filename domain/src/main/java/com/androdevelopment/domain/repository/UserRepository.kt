package com.androdevelopment.domain.repository

import com.androdevelopment.domain.entity.Result
import com.androdevelopment.domain.entity.User

interface UserRepository {

    fun insertUser(user: User):Result
}