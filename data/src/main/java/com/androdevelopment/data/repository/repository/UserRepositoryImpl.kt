package com.androdevelopment.data.repository.repository

import com.androdevelopment.data.repository.source.UserDataSource
import com.androdevelopment.domain.entity.Result
import com.androdevelopment.domain.entity.User
import com.androdevelopment.domain.repository.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val dataSource: UserDataSource
):UserRepository {

    override fun insertUser(user: User): Result  = dataSource.insertUser(user)
}