package com.androdevelopment.chatapplication.presentation

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androdevelopment.chatapplication.ui.theme.ChatApplicationTheme
import com.androdevelopment.data.utlis.SharedPreferenceManger
import com.androdevelopment.domain.entity.Message
import com.androdevelopment.domain.entity.Result
import com.androdevelopment.domain.repository.MessageRepository
import com.androdevelopment.domain.repository.UserRepository
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.OffsetDateTime
import java.util.UUID
import javax.inject.Inject
import kotlin.random.Random


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ChatApplicationTheme {
                ChatApp()
            }
        }
    }
}

@HiltViewModel
class testViewModel @Inject constructor(
    private val repository: MessageRepository,
    private val userRepository: UserRepository,
    private val sharedPreferenceManger: SharedPreferenceManger
):ViewModel(){

    val user2 = "fc752be8-cde0-4567-897d-8495e2e0b9f1"
    val user1 = "b9010456-ead9-429f-aad7-5c79e4179ecf"

    var messages by mutableStateOf<List<Message>>(emptyList())
        private set

    init {

        viewModelScope.launch {

//            sharedPreferenceManger.userId = user2

            getMessages()

        }

    }

    fun sendMessage(message:String,recipientId:String){
        viewModelScope.launch {
            repository.sendMessage(
                Message(
                    id = UUID.randomUUID().toString(),
                    subject = "Test",
                    creatorId = sharedPreferenceManger.userId,
                    body = message,
                    dateCreated = OffsetDateTime.now(),
                    isRead = false
                ),
                recipientId
            ).collect{
                when(it){
                    is Result.Error -> {
                        Log.e("error","error",it.exception)
                    }
                    is Result.Success<*> -> {
                        Log.e("success","success")
                    }
                }
            }
        }
    }

    private fun getMessages(){
        viewModelScope.launch {
            repository.getMessages(
                user2
            ).collect {
                messages = it
                Log.e("messages",it.toString())
            }
        }
    }
}