package com.example.expenzo.Repository

import android.util.Log
import com.example.expenzo.Model.TransactionDataModel
import com.example.expenzo.Model.TransactionResponse
import retrofit2.Response
import retrofit2.http.Body

class TransactionRepository {

    suspend fun sendingResponseToTranscationapi(@Body request: TransactionDataModel): Response<TransactionResponse> {
        Log.d("CheckUserRepo", "Sending request: $request")
        val response = RetrofitClass.apiservicesTransactionrepo.getTransactionData(request)

        if (response.isSuccessful) {
            Log.d("CheckUserRepo", "Received response: ${response.body()}")
        } else {
            Log.e("CheckUserRepo", "API Error Code: ${response.code()}")
            Log.e("CheckUserRepo", "Error Body: ${response.errorBody()?.string()}")
        }
        return response
    }

}