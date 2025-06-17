package com.example.expenzo.Repository


import com.example.expenzo.Model.TransactionDeleteModel
import com.example.expenzo.Model.TransactionDeleteResponse
import retrofit2.Response

class TransactionDeleteRepo {

    private val apiService = RetrofitClass.apiServicesTransactionData
    suspend fun deleteTransactionApi(request: TransactionDeleteModel): Response<TransactionDeleteResponse> {
        return apiService.deleteTransData(request)
    }

//    suspend fun deleteTransactionApi(@Body request:TransactionDeleteModel): Response<TransactionDeleteReponse>{
//        val response = RetrofitClass.apiServicesTranscationData.deleteTransData(request)
//        if (response.isSuccessful) {
//            Log.d("TransactionDeleteRepo", "Sending request with: $request")
//            Log.d("apiServicesTranscationData", "Received response: ${response.body()}")
//        } else {
//            Log.e("apiServicesTranscationData", "API Error Code: ${response.code()}")
//            Log.e("apiServicesTranscationData", "Error Body: ${response.errorBody()?.string()}")
//        }
//
//        return response
//    }
}