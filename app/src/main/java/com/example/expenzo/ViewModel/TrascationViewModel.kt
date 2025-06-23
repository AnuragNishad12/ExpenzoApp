package com.example.expenzo.ViewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expenzo.Model.TransactionDataModel
import com.example.expenzo.Model.TransactionResponse
import com.example.expenzo.Repository.TransactionRepository
import kotlinx.coroutines.launch
import org.json.JSONObject


class TrascationViewModel : ViewModel(){


    private val repository = TransactionRepository()
    val responseTransaction = MutableLiveData<TransactionResponse?>()
    val errorResponse = MutableLiveData<String?>()

    fun transactionDataClass(request: TransactionDataModel) {
        viewModelScope.launch {
            try {
                val response = repository.sendingResponseToTranscationapi(request)

                if (response.isSuccessful) {
                    responseTransaction.postValue(response.body())
                } else if (response.code() == 409) {
                    // Handle duplicate transaction - not an error, just already exists
                    Log.d("TransactionVM", "Transaction already exists for UPI Ref: ${request.upiRefId}")
                    // Create a success response for duplicate (optional)

//                    responseTransaction.postValue(Transaction already exists")
                    // Don't post to errorResponse for duplicates
                } else {
                    val errorBody = response.errorBody()?.string()

                    if (errorBody.isNullOrEmpty()) {
                        errorResponse.postValue("Error: ${response.code()} - ${response.message()}")
                    } else {
                        try {
                            val errorJson = JSONObject(errorBody)
                            val message = errorJson.optString("message", "Unknown error")
                            errorResponse.postValue("Error: $message")
                            Log.d("TransactionVM", "API Error: $message")
                        } catch (jsonException: Exception) {
                            // If error body is not valid JSON
                            errorResponse.postValue("Error: ${response.code()} - $errorBody")
                            Log.e("TransactionVM", "Failed to parse error JSON", jsonException)
                        }
                    }
                }
            } catch (e: Exception) {
                errorResponse.postValue("Exception: ${e.localizedMessage}")
                Log.e("TransactionVM", "Exception in transaction call", e)
            }
        }
    }
}