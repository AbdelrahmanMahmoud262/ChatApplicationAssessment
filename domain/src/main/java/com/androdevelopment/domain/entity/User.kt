package com.androdevelopment.domain.entity

data class User(
    val id:String,
    val firstName:String,
    val lastName:String,
    val email:String,
    val password:String,
    val isActive:Boolean
)
