package com.example.expenzo.ViewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expenzo.Model.FetchCurrentDayDataModel
import com.example.expenzo.Model.FetchCurrentDayDataResponse
import com.example.expenzo.Model.FetchOtherCurrentDataResponse
import com.example.expenzo.Repository.FetchAllCurrentDayDataRepo
import kotlinx.coroutines.launch


class TransactionCurrentDayViewModel : ViewModel() {

    private val respository = FetchAllCurrentDayDataRepo()
    val error = MutableLiveData<String?>()

    val transactions = MutableLiveData<List<FetchOtherCurrentDataResponse>>()

    fun showTransactionCurrentDayVm(data: FetchCurrentDayDataModel) {
        viewModelScope.launch {
            try {
                val response = respository.fetchAllCurrentDayData(data)
                if (response.isSuccessful) {
                    Log.d("ResponseTransactionCurrentDay","response ${response.body()}")
                    transactions.postValue(response.body()?.UserData ?: emptyList())
                } else {
                    error.postValue("Response failed with code: ${response.code()}")
                }
            } catch (e: Exception) {
                error.postValue(e.localizedMessage)
            }
        }
    }
}
