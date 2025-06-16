package com.example.expenzo.BackgroundServices

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.expenzo.Model.TransactionDeleteModel
import com.example.expenzo.Repository.TransactionDeleteRepo


class DeleteWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            deleteDataFor1Day()
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }

    private suspend fun deleteDataFor1Day() {
        val repository = TransactionDeleteRepo()
        val request = TransactionDeleteModel("684bbadc62bc05d171ab1175")
        val response = repository.deleteTransactionApi(request)
        if (response.isSuccessful) {
            Log.d("DeleteWorker", "Delete successful: ${response.body()}")
        } else {
            Log.e("DeleteWorker", "Delete failed: ${response.errorBody()?.string()}")
        }
    }
}
