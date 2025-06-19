package com.example.expenzo.Repository

import android.util.Log
import com.example.expenzo.Model.TransactionDataModel7days
import com.example.expenzo.Model.TransactionResponse7days
import retrofit2.Response
import retrofit2.http.Body

class TransactionRepository7days {

    suspend fun sendingResponseToTranscationapi7days(@Body request: TransactionDataModel7days): Response<TransactionResponse7days> {
        Log.d("CheckUserRepo", "Sending request: $request")
        val response = RetrofitClass.apiservicesTransactionrepo7days.getTransactionData7days(request)

        if (response.isSuccessful) {
            Log.d("CheckUserRepo", "Received response: ${response.body()}")
        } else {
            Log.e("CheckUserRepo", "API Error Code: ${response.code()}")
            Log.e("CheckUserRepo", "Error Body: ${response.errorBody()?.string()}")
        }
        return response
    }

}