package com.androdevelopment.chatapplication.presentation.authentication.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androdevelopment.data.utlis.SharedPreferenceManger
import com.androdevelopment.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: UserRepository,
    private val sharedPreferenceManger: SharedPreferenceManger
) : ViewModel() {

        fun login(email: String, password: String, onResult: (Boolean) -> Unit) {
            // Perform your login logic here (e.g., network request)
            // Call onResult(true) for success, onResult(false) for failure
            viewModelScope.launch {
                repository.validateUser(email, password)
                    .collect{
                        if (it != null) {
                            onResult(true)
                            sharedPreferenceManger.userId = it.id
                            sharedPreferenceManger.username = it.firstName + " " + it.lastName
                            sharedPreferenceManger.isLoggedIn = true
                            sharedPreferenceManger.userEmail = it.email
                        } else {
                            onResult(false)
                            sharedPreferenceManger.clearUserData()
                        }
                    }
            }
        }



}