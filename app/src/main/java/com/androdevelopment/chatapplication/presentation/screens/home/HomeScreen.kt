package com.androdevelopment.chatapplication.presentation.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.androdevelopment.chatapplication.presentation.navigation.Screen
import com.androdevelopment.domain.entity.Chat
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    state: HomeState,
    navController: NavController,
) {

    var showBottomSheet by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .then(modifier)
                .padding(16.dp)
        ) {

            items(state.chats) {
                ChatItem(
                    chat = it,
                    onChatClick = {
                        navController.navigate("${Screen.Chat.route}/${it.recipientId}")
                    }
                )
            }

        }

        FloatingActionButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            onClick = {
                showBottomSheet = true
            }
        ) {
            Icon(Icons.Rounded.Add, contentDescription = null)
        }
    }

    BottomSheet(
        isBottomSheetOpen = showBottomSheet,
        onDismiss = {
            showBottomSheet = false
        },
        onChatClick = {
            navController.navigate("${Screen.Chat.route}/${it.recipientId}")
        },
        state = state
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheet(
    modifier: Modifier = Modifier,
    isBottomSheetOpen: Boolean = true,
    onDismiss: () -> Unit = {},
    onChatClick: (Chat) -> Unit = {},
    state: HomeState,
) {

    if (isBottomSheetOpen) {

        ModalBottomSheet(
            onDismissRequest = {
                onDismiss()
            },
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.75f)
                .then(modifier)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                items(state.users) { user ->
                    ChatItem(
                        chat = user.let {
                            Chat(
                                recipientId = it.id,
                                recipientName = it.firstName + " " + it.lastName,
                                lastMessage = "",
                                lastMessageDate = OffsetDateTime.now(),
                                isRead = false
                            )
                        },
                        onChatClick = {
                            onChatClick(it)
                        }
                    )
                }
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
    ),
    onChatClick: (chatItem: Chat) -> Unit = {},
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(8.dp)
            .clickable {
                onChatClick(chat)
            }
    ) {

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
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
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
                text = chat.lastMessageDate?.format(DateTimeFormatter.ofPattern("HH:mm"))
                    ?: "Unknown",
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