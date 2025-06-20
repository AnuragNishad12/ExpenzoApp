package com.example.expenzo.ViewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expenzo.Model.FetchCurrentDayDataModel7days
import com.example.expenzo.Model.FetchOtherCurrentDataResponse7days
import com.example.expenzo.Repository.Fetch7DayDataRepo
import kotlinx.coroutines.launch

class Fetch7daysTransViewModel : ViewModel() {

    private val repository = Fetch7DayDataRepo()

    val error = MutableLiveData<String?>()
    val transactions = MutableLiveData<List<FetchOtherCurrentDataResponse7days>>()
    val totalAmount = MutableLiveData<Double>()
    val highestTransaction = MutableLiveData<FetchOtherCurrentDataResponse7days?>()
    val totalTransactionCount = MutableLiveData<Int>()
    val mostFrequentReceiver = MutableLiveData<Pair<String, Double>>() // Receiver, TotalSent

    // ✅ NEW: Daily transaction totals with dates
    val dailyTransactionTotals = MutableLiveData<List<Pair<String, Double>>>() // Date, Amount

    fun showTransaction7daysVm(data: FetchCurrentDayDataModel7days) {
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

                    // ✅ NEW: Calculate daily totals
                    val dailyTotals = mutableMapOf<String, Double>()
                    for (txn in userDataList) {
                        val date = txn.Date ?: continue // Assuming your transaction has a Date field
                        val amount = txn.Amount?.toDoubleOrNull() ?: 0.0
                        dailyTotals[date] = dailyTotals.getOrDefault(date, 0.0) + amount
                    }

                    // Sort by date and convert to list of pairs
                    val sortedDailyTotals = dailyTotals.toList().sortedBy { it.first }
                    dailyTransactionTotals.postValue(sortedDailyTotals)

                    Log.d("Analytics", "Total = ₹$total, Max = ₹${maxTxn?.Amount}, Top Receiver = ${mostFrequent?.key}")
                    Log.d("Analytics", "Daily Totals = $sortedDailyTotals")
                } else {
                    error.postValue("Response failed with code: ${response.code()}")
                }
            } catch (e: Exception) {
                error.postValue(e.localizedMessage)
            }
        }
    }
}