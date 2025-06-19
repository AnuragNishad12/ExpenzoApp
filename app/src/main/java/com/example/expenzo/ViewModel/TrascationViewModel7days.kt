package com.example.expenzo.ViewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expenzo.Model.TransactionDataModel
import com.example.expenzo.Model.TransactionDataModel7days
import com.example.expenzo.Model.TransactionResponse
import com.example.expenzo.Model.TransactionResponse7days
import com.example.expenzo.Repository.TransactionRepository
import com.example.expenzo.Repository.TransactionRepository7days
import kotlinx.coroutines.launch
import org.json.JSONObject

class TrascationViewModel7days : ViewModel(){

    private val repository = TransactionRepository7days()
    val responseTransaction = MutableLiveData<TransactionResponse7days?>()
    val errorResponse = MutableLiveData<String?>()




    fun transactionDataClass7days(request: TransactionDataModel7days){
        viewModelScope.launch {
            try {

                val response = repository.sendingResponseToTranscationapi7days(request)

                if (response.isSuccessful){
                    responseTransaction.postValue(response.body())
                }else{
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