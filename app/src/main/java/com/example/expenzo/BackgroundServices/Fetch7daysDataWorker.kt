package com.example.expenzo.BackgroundServices

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.expenzo.Repository.TransactionRepository7days
import com.example.expenzo.Utils.SmsHelper
import com.example.expenzo.Utils.StoredTransactionsHelper

class Fetch7daysDataWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result {
        return try {
            val smsHelper = SmsHelper(applicationContext)
            val storedHelper = StoredTransactionsHelper(applicationContext)

            val userId = "684bbadc62bc05d171ab1175"
            val allTransactions = smsHelper.getStructuredUPIData7Days(userId)

            Log.d("Worker", "Total transactions found: ${allTransactions.size}")

            val newTransactions = allTransactions.filter { transaction ->
                !storedHelper.isTransactionAlreadyStored(transaction.upiRefId)
            }

            newTransactions.forEach { transaction ->
                if (transaction.upiRefId != "Unknown" && transaction.upiRefId.isNotBlank()) {
                    TransactionRepository7days().sendingResponseToTranscationapi7days(transaction)
                    storedHelper.markTransactionAsStored(transaction.upiRefId)
                    Log.d("Worker", "Processed transaction with UPI Ref: ${transaction.upiRefId}")
                } else {
                    Log.w("Worker", "Skipping invalid transaction: ${transaction.upiRefId}")
                }
            }

            Log.d("Worker", "Finished processing ${newTransactions.size} new transactions")
            Result.success()
        } catch (e: Exception) {
            Log.e("WorkerError", "Failed to process transactions", e)
            Result.failure()
        }
    }
}

