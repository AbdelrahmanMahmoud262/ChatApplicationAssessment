package com.androdevelopment.chatapplication.presentation.authentication.register

import androidx.lifecycle.ViewModel
import com.androdevelopment.domain.entity.User
import com.androdevelopment.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {

    fun register(user: User, onResult: (Boolean, String) -> Unit) {
        // Perform your registration logic here (e.g., network request)
        // Call onResult(true, "") for success, onResult(false, "Error message") for failure
        userRepository.insertUser(user)
        onResult(true,"")
    }


}