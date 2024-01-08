package com.example.michellinexpress.Apis.Interface
import com.example.michellinexpress.Apis.Models.LoginRequest
import com.example.michellinexpress.Apis.Response.LoginResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {

    @POST("webAppLogin.ashx")
    fun login(
        @Body requestBody: LoginRequest,
        @Header("Authorization") authorizationHeader: String = "EvK2sH7iA0bT1vS4kQ5jC6pI6xrM4mF0pM2pQ4aY0pT4dS6jP4jBA"
    ): Call<LoginResponse>
}