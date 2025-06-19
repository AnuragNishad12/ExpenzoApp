package com.example.expenzo.Repository.Interfaces


import com.example.expenzo.Model.TransactionDataModel7days
import com.example.expenzo.Model.TransactionResponse7days
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface TransactionApiService7days {
    @POST("dev/api/SevenDayData")
    suspend fun getTransactionData7days(@Body request: TransactionDataModel7days): Response<TransactionResponse7days>
}