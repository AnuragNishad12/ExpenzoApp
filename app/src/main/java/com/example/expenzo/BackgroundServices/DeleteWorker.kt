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
            Log.e("DeleteWorker", "Exception in doWork: ${e.message}", e)
            Result.failure()
        }
    }

    private suspend fun deleteDataFor1Day() {
        try {
            val repository = TransactionDeleteRepo()
            val request = TransactionDeleteModel("684bbadc62bc05d171ab1175")

            Log.d("DeleteWorker", "Making delete request for userId: ${request.userId}")

            val response = repository.deleteTransactionApi(request)

            if (response.isSuccessful) {
                val body = response.body()
                Log.d("DeleteWorker", "Delete successful: status=${body?.status}, message=${body?.message}, deletedCount=${body?.deletedCount}")
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("DeleteWorker", "Delete failed: Code=${response.code()}, Error=$errorBody")
            }
        } catch (e: Exception) {
            Log.e("DeleteWorker", "Network error: ${e.message}", e)
            throw e
        }
    }
}