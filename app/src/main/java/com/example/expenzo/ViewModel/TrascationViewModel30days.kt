package com.example.expenzo.ViewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expenzo.Model.TransactionDataModel30days
import com.example.expenzo.Model.TransactionResponse30days
import com.example.expenzo.Repository.TransactionRepository30days
import kotlinx.coroutines.launch
import org.json.JSONObject

class TrascationViewModel30days : ViewModel(){

    private val repository = TransactionRepository30days()
    val responseTransaction = MutableLiveData<TransactionResponse30days?>()
    val errorResponse = MutableLiveData<String?>()




    fun transactionDataClass30days(request: TransactionDataModel30days){
        viewModelScope.launch {
            try {

                val response = repository.sendingResponseToTranscationapi30days(request)

                if (response.isSuccessful){
                    responseTransaction.postValue(response.body())
                }else if (response.code() == 409) {
                    // Handle duplicate transaction - not an error, just already exists
                    Log.d("TransactionVM", "Transaction already exists for UPI Ref: ${request.upiRefId}")
                    // Create a success response for duplicate (optional)

//                    responseTransaction.postValue(Transaction already exists")
                    // Don't post to errorResponse for duplicates
                }
                else{
                    val errorBody = response.errorBody()?.string()
                    val errorJson = JSONObject(errorBody ?: "{}")
                    val message = errorJson.optString("message", "Unknown error")
                    errorResponse.postValue("Error: $message")
                    Log.d("UniqueName","name ${message}")
                }
            } catch (e: Exception) {
                errorResponse.postValue("Exception: ${e.localizedMessage}")
            }
        }
    }
}