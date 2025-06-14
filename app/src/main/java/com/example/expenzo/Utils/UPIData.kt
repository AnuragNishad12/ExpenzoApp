package com.example.expenzo.Utils

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.expenzo.Model.TransactionDataModel
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class SmsHelper(private val context: Context) {

    fun getStructuredUPIData(userId: String, daysBack: Int = 7): List<TransactionDataModel> {
        val results = mutableListOf<TransactionDataModel>()
        val uri = Uri.parse("content://sms/inbox")

        // Get today's date at 00:00:00 (start of today)
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfToday = calendar.timeInMillis

        val projection = arrayOf("body", "date")
        val selection = "date >= ?"
        val selectionArgs = arrayOf(startOfToday.toString())

        val cursor = context.contentResolver.query(
            uri, projection, selection, selectionArgs, "date DESC"
        )

        cursor?.use {
            val bodyIndex = it.getColumnIndex("body")
            val dateIndex = it.getColumnIndex("date")

            while (it.moveToNext()) {
                val message = it.getString(bodyIndex)
                val dateMillis = it.getLong(dateIndex)

                // Check if message contains UPI keywords
                if (isUPIMessage(message)) {
                    val upiData = parseUpiDataFromMessage(message, dateMillis, userId)
                    if (upiData != null) {
                        results.add(upiData)
                    }
                }
            }
        }

        return results
    }

    private fun isUPIMessage(message: String): Boolean {
        val upiKeywords = listOf(
            "UPI", "upi", "transaction", "debited", "credited",
            "sent", "received", "paid", "Rs.", "₹", "INR",
            "UTR", "Ref", "Reference"
        )
        return upiKeywords.any { message.contains(it, ignoreCase = true) }
    }

    private fun parseUpiDataFromMessage(message: String, dateMillis: Long, userId: String): TransactionDataModel? {
        return try {
            // Parse different UPI message formats
            val amount = extractAmount(message)
            val receiver = extractReceiver(message)
            val bank = extractBank(message)
            val account = extractAccount(message)
            val upiRefId = extractUPIRef(message)
            val date = formatDate(dateMillis)

            if (amount.isNotEmpty() && receiver.isNotEmpty()) {
                TransactionDataModel(
                    userId = userId,
                    account = account,
                    amount = amount,
                    bank = bank,
                    date = date,
                    receiver = receiver,
                    upiRefId = upiRefId
                )
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("SmsHelper", "Error parsing UPI message: ${e.message}")
            null
        }
    }

    private fun extractAmount(message: String): String {
        // Pattern to match amounts like Rs.100, ₹100, INR 100, etc.
        val patterns = listOf(
            "Rs\\.?\\s*(\\d+(?:\\.\\d{2})?)",
            "₹\\s*(\\d+(?:\\.\\d{2})?)",
            "INR\\s*(\\d+(?:\\.\\d{2})?)",
            "amount\\s+Rs\\.?\\s*(\\d+(?:\\.\\d{2})?)",
            "amount\\s+₹\\s*(\\d+(?:\\.\\d{2})?)"
        )

        for (pattern in patterns) {
            val regex = Regex(pattern, RegexOption.IGNORE_CASE)
            val match = regex.find(message)
            if (match != null) {
                return match.groupValues[1]
            }
        }
        return ""
    }

    private fun extractReceiver(message: String): String {
        // Common patterns for receiver names in UPI messages
        val patterns = listOf(
            "to\\s+([A-Za-z\\s]+)\\s+on",
            "sent to\\s+([A-Za-z\\s]+)",
            "paid to\\s+([A-Za-z\\s]+)",
            "transferred to\\s+([A-Za-z\\s]+)",
            "to\\s+([A-Za-z\\s]+)\\s+via"
        )

        for (pattern in patterns) {
            val regex = Regex(pattern, RegexOption.IGNORE_CASE)
            val match = regex.find(message)
            if (match != null) {
                return match.groupValues[1].trim()
            }
        }

        // If no pattern matches, try to extract name after common keywords
        val fallbackPatterns = listOf("to ", "sent ", "paid ")
        for (keyword in fallbackPatterns) {
            val index = message.indexOf(keyword, ignoreCase = true)
            if (index != -1) {
                val afterKeyword = message.substring(index + keyword.length)
                val words = afterKeyword.split(" ")
                if (words.isNotEmpty()) {
                    return words[0].trim()
                }
            }
        }

        return "Unknown"
    }

    private fun extractBank(message: String): String {
        // Common bank names in UPI messages
        val banks = listOf(
            "SBI", "HDFC", "ICICI", "Axis", "Kotak", "Yes Bank", "PNB",
            "Bank of Baroda", "Canara", "Union Bank", "IDBI", "IndusInd",
            "Paytm", "PhonePe", "GPay", "Google Pay", "BHIM"
        )

        for (bank in banks) {
            if (message.contains(bank, ignoreCase = true)) {
                return bank
            }
        }

        // Try to extract bank from UPI ID pattern
        val upiPattern = Regex("@([a-zA-Z]+)", RegexOption.IGNORE_CASE)
        val match = upiPattern.find(message)
        if (match != null) {
            return match.groupValues[1].uppercase()
        }

        return "Unknown"
    }

    private fun extractAccount(message: String): String {
        // Pattern to match account numbers (typically 10-16 digits)
        val accountPattern = Regex("a/c\\s*[x*]*\\s*(\\d{4,6})", RegexOption.IGNORE_CASE)
        val match = accountPattern.find(message)
        if (match != null) {
            return "****" + match.groupValues[1]
        }

        // Look for masked account numbers
        val maskedPattern = Regex("([x*]+\\d{4,6})", RegexOption.IGNORE_CASE)
        val maskedMatch = maskedPattern.find(message)
        if (maskedMatch != null) {
            return maskedMatch.groupValues[1]
        }

        return "Unknown"
    }

    private fun extractUPIRef(message: String): String {
        // Common UPI reference patterns
        val patterns = listOf(
            "UPI Ref No[.:]*\\s*([A-Za-z0-9]+)",
            "UTR[.:]*\\s*([A-Za-z0-9]+)",
            "Ref No[.:]*\\s*([A-Za-z0-9]+)",
            "Reference[.:]*\\s*([A-Za-z0-9]+)",
            "Transaction ID[.:]*\\s*([A-Za-z0-9]+)"
        )

        for (pattern in patterns) {
            val regex = Regex(pattern, RegexOption.IGNORE_CASE)
            val match = regex.find(message)
            if (match != null) {
                return match.groupValues[1]
            }
        }

        return "Unknown"
    }

    private fun formatDate(dateMillis: Long): String {
        val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
        return sdf.format(Date(dateMillis))
    }
}