package com.example.expenzo.BackgroundServices

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.expenzo.Repository.TransactionRepository30days
import com.example.expenzo.Utils.SmsHelper
import com.example.expenzo.Utils.UserDataStore

class Fetch30daysDataWorker (
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result {
        return try {
            val smsHelper = SmsHelper(applicationContext)
//            val storedHelper = StoredTransactionsHelper(applicationContext)

            val userDataStore = UserDataStore(context = applicationContext)

            val userId = userDataStore.getUniqueName() ?: "default_user"

//            val userId = "684bbadc62bc05d171ab1175"
            val allTransactions = smsHelper.getStructuredUPIData30Days(userId)

            Log.d("Worker7days", "Total transactions found: ${allTransactions.size}")

            // Filter out transactions with valid UPI Ref IDs first, then check if they're already stored
            val validTransactions = allTransactions.filter { transaction ->
                transaction.upiRefId != "Unknown" && transaction.upiRefId.isNotBlank()
            }

            Log.d("Worker7days", "Valid transactions (with UPI Ref): ${validTransactions.size}")
            Log.d("Worker7days", "Invalid transactions (Unknown UPI Ref): ${allTransactions.size - validTransactions.size}")

//            val newTransactions = validTransactions.filter { transaction ->
//                !storedHelper.isTransactionAlreadyStored(transaction.upiRefId)
//            }

            Log.d("Worker7days", "New transactions to process: ${validTransactions.size}")

            var processedCount = 0
            validTransactions.forEach { transaction ->
                try {
//                    TransactionRepository().sendingResponseToTranscationapi(transaction)
                    TransactionRepository30days().sendingResponseToTranscationapi30days(transaction)
//                    storedHelper.markTransactionAsStored(transaction.upiRefId)
                    processedCount++
                    Log.d("Worker7days", "Processed transaction with UPI Ref: ${transaction.upiRefId}")
                } catch (e: Exception) {
                    Log.e("Worker7days", "Failed to process transaction ${transaction.upiRefId}", e)
                }
            }

            Log.d("Worker7days", "Successfully processed $processedCount out of ${validTransactions.size} new transactions")
            Result.success()
        } catch (e: Exception) {
            Log.e("Worker7days", "Failed to process transactions", e)
            Result.failure()
        }
    }
}