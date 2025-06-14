package com.example.expenzo.Repository.Interfaces

import com.example.expenzo.Model.CheckUserDataClass
import com.example.expenzo.Model.TransactionDataModel
import com.example.expenzo.Model.TransactionResponse
import com.example.expenzo.Model.checkUserResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface TransactionApiService {
    @POST("dev/api/transactionReport")
    suspend fun getTransactionData(@Body request: TransactionDataModel): Response<TransactionResponse>
}