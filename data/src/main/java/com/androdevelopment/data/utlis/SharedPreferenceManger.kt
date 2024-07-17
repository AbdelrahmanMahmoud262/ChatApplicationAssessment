package com.androdevelopment.data.utlis

import android.content.Context
import android.content.SharedPreferences

class SharedPreferenceManger(
    context: Context,
) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(
            sharedPreferencesKey,
            Context.MODE_PRIVATE
        )
    private val editor = sharedPreferences.edit()

    var username: String
        get() = getString(userNameKey)
        set(value) {
            editor.putString(userNameKey, value).apply()
        }

    var userEmail: String
        get() = getString(userEmailKey)
        set(value) {
            editor.putString(userEmailKey, value).apply()
        }
    var userId: String
        get() = getString(userIdKey)
        set(value) {
            editor.putString(userIdKey, value).apply()
        }

    var isLoggedIn: Boolean
        get() = sharedPreferences.getBoolean(isLoggedInKey, false)
        set(value) {
            editor.putBoolean(isLoggedInKey, value).apply()
        }


    var firebaseToken: String
        get() = getString(fcmToken)
        set(value) {
            editor.putString(fcmToken, value).apply()
        }

    private fun getString(key: String): String {
        sharedPreferences.getString(key, "").let { s ->
            return if (s.isNullOrBlank())
                ""
            else
                s
        }
    }


    private fun getInt(key: String): Int {
        sharedPreferences.getInt(key, 0).let { s ->
            return s ?: 0
        }
    }

    fun clearUserData() {
        username = ""
        userEmail = ""
        userId = ""
        isLoggedIn = false
    }


//    fun saveUserSession(loginResponse: LoginResponse) {
//        userToken = loginResponse.token!!
//        userName = loginResponse.name!!
//        userEmail = loginResponse.email!!
//    }

    companion object {
        private const val sharedPreferencesKey = "USERDATA"
        private const val languageKey = "en"
        private const val userTokenKey = "API_TOKEN"
        private const val userNameKey = "USERNAME"
        private const val userIdKey = "USERID"
        private const val userEmailKey = "USEREMAIL"
        private const val isLoggedInKey = "LOGIN"
        private const val isActiveKey = "ACTIVE"
        private const val fcmToken = "FCMTOKEN"
    }
}