package com.androdevelopment.chatapplication.di

import com.androdevelopment.chatapplication.ChatApplication
import com.androdevelopment.data.utlis.SharedPreferenceManger
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    fun provideSharedPreferenceManger(): SharedPreferenceManger = ChatApplication.sharedPreferenceManger
}