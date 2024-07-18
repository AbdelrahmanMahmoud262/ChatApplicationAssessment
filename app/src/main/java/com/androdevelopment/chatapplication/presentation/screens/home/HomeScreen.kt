package com.androdevelopment.chatapplication.presentation.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.androdevelopment.chatapplication.presentation.testViewModel
import com.androdevelopment.domain.entity.Chat
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import kotlin.random.Random

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    state: HomeState
) {

    val testViewModel = hiltViewModel<testViewModel>()

    LazyColumn (
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .then(modifier)
            .padding(16.dp)
    ){

        items(state.chats){
            ChatItem(chat = it)
        }

        item {
            Button(
                onClick = {
                    testViewModel.sendMessage(
                        Random.nextInt().toString(),
                        testViewModel.user2
                    )
                }
            ) {

                Text("Send")
            }
        }

    }
}

@Preview
@Composable
fun ChatItem(
    modifier: Modifier = Modifier,
    chat: Chat = Chat(
        recipientId = "123",
        recipientName = "John Doe",
        lastMessage = "Hello, how are you?",
        lastMessageDate = OffsetDateTime.now(),
        isRead = false
    )
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(8.dp)
    ){

        Column(
            modifier = Modifier
                .weight(8f),
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            Text(
                text = chat.recipientName,
                fontSize = 16.sp,
                fontWeight = FontWeight.W700,
                modifier = Modifier
                    .align(Alignment.Start)
            )

            Text(
                text = chat.lastMessage,
                fontSize = 12.sp,
                fontWeight = FontWeight.W400,
                modifier = Modifier
                    .align(Alignment.Start)
            )
        }

        Column(
            modifier = Modifier
                .weight(2f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = chat.lastMessageDate?.format(DateTimeFormatter.ofPattern("HH:mm"))?: "Unknown",
            )

            Spacer(Modifier.height(8.dp))

            if (!chat.isRead) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color.Yellow)
                        .padding(8.dp),
                )
            }
        }

    }

}