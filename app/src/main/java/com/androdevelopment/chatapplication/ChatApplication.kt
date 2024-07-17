package com.androdevelopment.chatapplication

import android.app.Application
import android.content.Context
import com.androdevelopment.data.utlis.SharedPreferenceManger
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ChatApplication: Application() {

    companion object {
        lateinit var appContext: Context
        lateinit var sharedPreferenceManger: SharedPreferenceManger
    }


    override fun onCreate() {
        super.onCreate()

        appContext = applicationContext
        sharedPreferenceManger = SharedPreferenceManger(appContext)
    }


}