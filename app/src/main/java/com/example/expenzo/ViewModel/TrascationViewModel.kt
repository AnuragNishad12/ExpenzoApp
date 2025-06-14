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


    fun transactionDataClass(request:TransactionDataModel){
        viewModelScope.launch {
            try {

                val response = repository.sendingResponseToTranscationapi(request)

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