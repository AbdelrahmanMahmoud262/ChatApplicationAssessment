package com.androdevelopment.data.remote.di

import com.androdevelopment.data.remote.source.MessageDataSourceImpl
import com.androdevelopment.data.remote.source.UserDataSourceImpl
import com.androdevelopment.data.repository.source.MessageDataSource
import com.androdevelopment.data.repository.source.UserDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class SourceModule {

    @Binds
    abstract fun bindMessageDataSource(messageDataSourceImpl: MessageDataSourceImpl): MessageDataSource


    @Binds
    abstract fun bindUserDataSource(userDataSourceImpl: UserDataSourceImpl): UserDataSource
}