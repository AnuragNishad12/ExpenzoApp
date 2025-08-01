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

    private val repository = FetchAllCurrentDayDataRepo()

    val error = MutableLiveData<String?>()
    val transactions = MutableLiveData<List<FetchOtherCurrentDataResponse>>()
    val totalAmount = MutableLiveData<Double>()
    val highestTransaction = MutableLiveData<FetchOtherCurrentDataResponse?>()
    val totalTransactionCount = MutableLiveData<Int>()
    val mostFrequentReceiver = MutableLiveData<Pair<String, Double>>() // Receiver, TotalSent

    fun showTransactionCurrentDayVm(data: FetchCurrentDayDataModel) {
        viewModelScope.launch {
            try {
                val response = repository.fetchAllCurrentDayData(data)
                if (response.isSuccessful) {
                    val userDataList = response.body()?.UserData ?: emptyList()
                    transactions.postValue(userDataList)

                    // ✅ Total Amount
                    val total = userDataList.sumOf { it.Amount?.toDoubleOrNull() ?: 0.0 }
                    totalAmount.postValue(total)

                    // ✅ Highest Transaction
                    val maxTxn = userDataList.maxByOrNull { it.Amount?.toDoubleOrNull() ?: 0.0 }
                    highestTransaction.postValue(maxTxn)

                    // ✅ Total Transactions Count
                    totalTransactionCount.postValue(userDataList.size)

                    // ✅ Most Frequent Receiver (by total sent)
                    val receiverTotals = mutableMapOf<String, Double>()
                    for (txn in userDataList) {
                        val receiver = txn.Receiver ?: continue
                        val amount = txn.Amount?.toDoubleOrNull() ?: 0.0
                        receiverTotals[receiver] = receiverTotals.getOrDefault(receiver, 0.0) + amount
                    }
                    val mostFrequent = receiverTotals.maxByOrNull { it.value }
                    mostFrequent?.let {
                        mostFrequentReceiver.postValue(Pair(it.key, it.value))
                    }

                    Log.d("Analytics", "Total = ₹$total, Max = ₹${maxTxn?.Amount}, Top Receiver = ${mostFrequent?.key}")
                } else {
                    error.postValue("Response failed with code: ${response.code()}")
                }
            } catch (e: Exception) {
                error.postValue(e.localizedMessage)
            }
        }
    }
}
