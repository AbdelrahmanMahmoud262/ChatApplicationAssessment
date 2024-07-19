package com.androdevelopment.chatapplication.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.androdevelopment.chatapplication.ChatApplication
import com.androdevelopment.chatapplication.presentation.authentication.login.LoginScreen
import com.androdevelopment.chatapplication.presentation.authentication.register.RegistrationScreen
import com.androdevelopment.chatapplication.presentation.screens.chat.ChatScreen
import com.androdevelopment.chatapplication.presentation.screens.chat.ChatViewModel
import com.androdevelopment.chatapplication.presentation.screens.home.HomeScreen
import com.androdevelopment.chatapplication.presentation.screens.home.HomeViewModel
import com.androdevelopment.data.utlis.Constants
import com.androdevelopment.data.utlis.SharedPreferenceManger

@Composable
fun ChatNavigation(modifier: Modifier = Modifier) {

    val navController = rememberNavController()
    val sharedPreferenceManger = SharedPreferenceManger(ChatApplication.appContext)

    NavHost(
        navController = navController,
        startDestination = if (sharedPreferenceManger.isLoggedIn) Screen.Home.route else Screen.Login.route,
        modifier = modifier
    ) {

        composable(Screen.Home.route) {
            val viewmodel = hiltViewModel<HomeViewModel>()
            HomeScreen(
                modifier = modifier,
                state = viewmodel.state,
                navController = navController
            )
        }

        composable(
           route =  "${Screen.Chat.route}/{${Constants.RECIPIENT_ID}}"
        ) {
            val viewModel = hiltViewModel<ChatViewModel>()
            ChatScreen(
                modifier = modifier,
                state = viewModel.state,
                onSend = {
                    viewModel.sendMessage(it)
                }
            )
        }

        composable(
            route = Screen.Login.route
        ){
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route)
                },
                onRegisterClick = {
                    navController.navigate(Screen.Registration.route)
                }
            )
        }

        composable(
            route = Screen.Registration.route
        ){
            RegistrationScreen(
                onRegistrationSuccess = {
                    navController.navigate(Screen.Login.route)
                }
            )
        }
    }
}