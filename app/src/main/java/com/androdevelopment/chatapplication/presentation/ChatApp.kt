package com.androdevelopment.chatapplication.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.androdevelopment.chatapplication.presentation.navigation.ChatNavigation

@Composable
fun ChatApp(modifier: Modifier = Modifier) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) { innerPadding ->
        ChatNavigation(
            modifier.padding(innerPadding)
        )
    }
}