package com.example.expenzo.Repository

import android.util.Log
import com.example.expenzo.Model.TransactionDeleteModel
import com.example.expenzo.Model.TransactionDeleteReponse
import retrofit2.Response
import retrofit2.http.Body

class TransactionDeleteRepo {
    suspend fun deleteTransactionApi(@Body request:TransactionDeleteModel): Response<TransactionDeleteReponse>{
        val response = RetrofitClass.apiServicesTranscationData.deleteTransData(request)
        if (response.isSuccessful) {
            Log.d("CheckUserRepo", "Received response: ${response.body()}")
        } else {
            Log.e("CheckUserRepo", "API Error Code: ${response.code()}")
            Log.e("CheckUserRepo", "Error Body: ${response.errorBody()?.string()}")
        }

        return response
    }
}