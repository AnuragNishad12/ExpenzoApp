package com.example.expenzo.Repository.Interfaces

import com.example.expenzo.Model.CheckUserDataClass
import com.example.expenzo.Model.checkUserResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiServiceCheckUser {
    @POST("dev/api/checkUserAlready")
    suspend fun checkUserCrediential(@Body request: CheckUserDataClass): Response<checkUserResponse>
}