package com.example.expenzo.Utils

import android.content.Context
import android.net.Uri
import com.example.expenzo.Model.TransactionDataModel
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException

class SmsHelper(private val context: Context) {

    fun getStructuredUPIData(userId: String, daysBack: Int = 7): List<TransactionDataModel> {
        val results = mutableListOf<TransactionDataModel>()
        val uri = Uri.parse("content://sms/inbox")

        val currentTime = System.currentTimeMillis()
        val pastTime = currentTime - daysBack * 24 * 60 * 60 * 1000L

        val projection = arrayOf("body", "date")
        val selection = "date >= ?"
        val selectionArgs = arrayOf(pastTime.toString())

        val cursor = context.contentResolver.query(
            uri, projection, selection, selectionArgs, "date DESC"
        )

        cursor?.use {
            val bodyIndex = it.getColumnIndex("body")
            while (it.moveToNext()) {
                val message = it.getString(bodyIndex)
                val upiData = parseUpiDataFromMessage(message) // This must return TransactionDataModel or compatible object
                if (upiData != null) {
                    results.add(upiData.copy(userId = userId))
                }
            }
        }

        return results
    }

    private fun parseUpiDataFromMessage(message: String): TransactionDataModel? {
        return try {
            val gson = Gson()
            val partialData = gson.fromJson(message, TransactionDataModel::class.java)
            partialData.copy(userId = "") // placeholder, will be replaced later
        } catch (e: JsonSyntaxException) {
            null
        } catch (e: Exception) {
            null
        }
    }

}