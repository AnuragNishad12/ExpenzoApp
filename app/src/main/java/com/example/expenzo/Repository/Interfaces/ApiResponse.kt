package com.example.expenzo.Repository.Interfaces

import com.example.expenzo.Model.CreateUserResponse
import com.example.expenzo.Model.Users
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiResponse {
    @POST("dev/api/CreateUser")
    suspend fun createUser(@Body request: Users): Response<CreateUserResponse>

}