package com.androdevelopment.chatapplication.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.androdevelopment.chatapplication.presentation.screens.chat.ChatScreen
import com.androdevelopment.chatapplication.presentation.screens.home.HomeScreen
import com.androdevelopment.chatapplication.presentation.screens.home.HomeViewModel

@Composable
fun ChatNavigation(modifier: Modifier = Modifier) {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {

        composable(Screen.Home.route) {
            val viewmodel = hiltViewModel<HomeViewModel>()
            HomeScreen(
                modifier = modifier,
                state = viewmodel.state
            )
        }

        composable(Screen.Chat.route) {
            ChatScreen(
                modifier = modifier
            )
        }
    }
}