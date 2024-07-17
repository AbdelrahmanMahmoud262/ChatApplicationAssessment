package com.androdevelopment.data.repository.di

import com.androdevelopment.data.repository.repository.HomeRepositoryImpl
import com.androdevelopment.data.repository.repository.MessageRepositoryImpl
import com.androdevelopment.data.repository.repository.UserRepositoryImpl
import com.androdevelopment.domain.repository.HomeRepository
import com.androdevelopment.domain.repository.MessageRepository
import com.androdevelopment.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindMessageRepository(messageRepositoryImpl: MessageRepositoryImpl): MessageRepository

    @Binds
    abstract fun bindUserRepository(userRepositoryImpl: UserRepositoryImpl): UserRepository

    @Binds
    abstract fun bindHomeRepository(homeRepositoryImpl: HomeRepositoryImpl): HomeRepository

}