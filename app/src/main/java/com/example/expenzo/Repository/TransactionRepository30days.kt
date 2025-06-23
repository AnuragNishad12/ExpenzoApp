package com.example.expenzo.Repository

import android.util.Log
import com.example.expenzo.Model.TransactionDataModel30days
import com.example.expenzo.Model.TransactionResponse30days
import retrofit2.Response
import retrofit2.http.Body

class TransactionRepository30days {

    suspend fun sendingResponseToTranscationapi30days(@Body request: TransactionDataModel30days): Response<TransactionResponse30days> {
        Log.d("CheckUserRepo30days", "Sending request: $request")
        val response = RetrofitClass.apiservicesTransactionrepo30days.getTransactionData30days(request)

        if (response.isSuccessful) {
            Log.d("CheckUserRepo30days", "Received response: ${response.body()}")
        } else {
            Log.e("CheckUserRepo30days", "API Error Code: ${response.code()}")
            Log.e("CheckUserRepo30days", "Error Body: ${response.errorBody()?.string()}")
        }
        return response
    }


}