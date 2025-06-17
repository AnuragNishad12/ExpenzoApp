package com.example.expenzo.Repository.Interfaces

import com.example.expenzo.Model.TransactionDeleteModel
import com.example.expenzo.Model.TransactionDeleteResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface TransactionDeleteApiServices {
    @POST("dev/api/removecurrentdata")
    suspend fun deleteTransData(@Body request: TransactionDeleteModel): Response<TransactionDeleteResponse>
}