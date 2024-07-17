package com.androdevelopment.chatapplication.presentation.navigation

sealed class Screen (val route:String){

    data object Home:Screen("home")
    data object Chat:Screen("chat")
}