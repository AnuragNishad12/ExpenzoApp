package com.example.expenzo.Repository.Interfaces

import com.example.expenzo.Model.TransactionDataModel30days
import com.example.expenzo.Model.TransactionResponse30days
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface TransactionApiService30days {
    @POST("dev/api/ThirtyDayModel")
    suspend fun getTransactionData30days(@Body request: TransactionDataModel30days): Response<TransactionResponse30days>
}