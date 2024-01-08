package com.example.michellinexpress.Apis.Models

data class LoginRequest(
    val user: String,
    val password: String,
    val device: String
)