package com.androdevelopment.chatapplication.presentation.screens.home

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androdevelopment.data.utlis.SharedPreferenceManger
import com.androdevelopment.domain.entity.Chat
import com.androdevelopment.domain.repository.HomeRepository
import com.androdevelopment.domain.repository.MessageRepository
import com.androdevelopment.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: HomeRepository,
    private val userRepository: UserRepository,
    private val sharedPreferenceManger: SharedPreferenceManger
) : ViewModel() {

    var state by mutableStateOf(HomeState())
        private set

    init {
        getChats()
        getUsers()
    }

    private fun getChats() {
        viewModelScope.launch {
            repository.getChats().collect{
                Log.e("homeviewmodel",it.toString())
                state = state.copy(chats = it)
            }
        }
    }

    private fun getUsers(){
        viewModelScope.launch {
            userRepository.getUsers().collect{
                state = state.copy(users = it.filter { it.id != sharedPreferenceManger.userId })
            }
        }
    }
}