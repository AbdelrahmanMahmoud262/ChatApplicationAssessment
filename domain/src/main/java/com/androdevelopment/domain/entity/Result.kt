package com.androdevelopment.domain.entity

sealed class Result {

    data class Success<T>(val data: T) : Result()

    data class Error(val exception: Exception) : Result()

}