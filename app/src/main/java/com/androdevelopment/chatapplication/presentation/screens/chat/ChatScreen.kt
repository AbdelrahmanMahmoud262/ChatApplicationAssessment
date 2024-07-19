package com.androdevelopment.chatapplication.presentation.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.androdevelopment.domain.entity.Message

@Composable
fun ChatScreen(
    modifier: Modifier = Modifier,
    state: ChatState,
    onSend: (String) -> Unit,
) {

    ChatScreenContent(
        modifier = modifier,
        state = state,
        onSend = {
            onSend(it)
        }
    )

}

@Preview
@Composable
fun ChatScreenContent(
    modifier: Modifier = Modifier,
    state: ChatState = ChatState(
        chatItems = listOf(
            Message(
                id = "1",
                creatorId = "1",
                body = "Hello",
            ),
            Message(
                id = "4",
                creatorId = "2",
                body = "Hello",
            ),
            Message(
                id = "3",
                creatorId = "1",
                body = "Bye",
            ),
            Message(
                id = "5",
                creatorId = "2",
                body = "newMessage",
            ),
            Message(
                id = "2",
                creatorId = "1",
                body = "NewMessage",
            ),
            Message(
                id = "6",
                creatorId = "2",
                body = "HelloAgain",
            ),
            Message(
                id = "6",
                creatorId = "2",
                body = "HelloAgain",
            ),

            ),
        recipientId = "2"
    ),
    onSend: (String) -> Unit = {},
) {

    var message by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .then(modifier)
    ) {

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(12.dp)
        ) {
            items(state.chatItems) { message ->
                if (message.creatorId == state.recipientId) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFF5F5F5))
                                .padding(8.dp)
                        ) {
                            Text(
                                text = message.body,
                                color = Color.Black,

                                )
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        horizontalAlignment = Alignment.End
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFFBC404))
                                .padding(8.dp)
                        ) {
                            Text(
                                text = message.body,
                                color = Color.Black,

                                )
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                modifier = Modifier.weight(1f),
                value = message,
                onValueChange = { message = it },
            )
            IconButton(
                onClick = {
                    if (message.isNotBlank()) {
                        onSend(message)
                    }
                }
            ) {
                Icon(Icons.AutoMirrored.Rounded.Send, contentDescription = null)
            }
        }
    }

}