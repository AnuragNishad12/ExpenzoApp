package com.example.expenzo.Utils

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.expenzo.Model.TransactionDataModel
import com.example.expenzo.Model.TransactionDataModel7days
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

                // Log the SMS message for debugging
                Log.d("SmsHelper", "Processing SMS: $message")

                // Check if message contains UPI keywords
                if (isUPIMessage(message)) {
                    val upiData = parseUpiDataFromMessage(message, dateMillis, userId)
                    if (upiData != null) {
                        results.add(upiData)
                        Log.d("SmsHelper", "Successfully parsed UPI data: $upiData")
                    } else {
                        Log.w("SmsHelper", "Failed to parse UPI data from message: $message")
                    }
                }
            }
        }

        return results
    }


    fun getStructuredUPIData7Days(userId: String, daysBack: Int = 7): List<TransactionDataModel7days> {
        val results = mutableListOf<TransactionDataModel7days>()
        val uri = Uri.parse("content://sms/inbox")

        // Calculate start time: X days ago at 00:00:00
        val calendarStart = Calendar.getInstance()
        calendarStart.add(Calendar.DAY_OF_YEAR, -daysBack)
        calendarStart.set(Calendar.HOUR_OF_DAY, 0)
        calendarStart.set(Calendar.MINUTE, 0)
        calendarStart.set(Calendar.SECOND, 0)
        calendarStart.set(Calendar.MILLISECOND, 0)
        val startMillis = calendarStart.timeInMillis

        // Calculate end time: yesterday at 23:59:59.999 (excludes current day completely)
        val calendarEnd = Calendar.getInstance()
        calendarEnd.add(Calendar.DAY_OF_YEAR, -1)  // Go back 1 day from today
        calendarEnd.set(Calendar.HOUR_OF_DAY, 23)
        calendarEnd.set(Calendar.MINUTE, 59)
        calendarEnd.set(Calendar.SECOND, 59)
        calendarEnd.set(Calendar.MILLISECOND, 999)
        val endMillis = calendarEnd.timeInMillis

        // Debug logging for time boundaries
        Log.d("SmsHelper", "Fetching SMS data from ${Date(startMillis)} to ${Date(endMillis)}")
        Log.d("SmsHelper", "Start millis: $startMillis, End millis: $endMillis")

        val projection = arrayOf("body", "date")
        val selection = "date >= ? AND date <= ?"  // Changed to <= since we're using precise end time
        val selectionArgs = arrayOf(startMillis.toString(), endMillis.toString())

        val cursor = context.contentResolver.query(
            uri, projection, selection, selectionArgs, "date DESC"
        )

        cursor?.use {
            val bodyIndex = it.getColumnIndex("body")
            val dateIndex = it.getColumnIndex("date")

            if (bodyIndex == -1 || dateIndex == -1) {
                Log.e("SmsHelper", "Failed to get column indices for SMS data")
                return results
            }

            var processedCount = 0
            var upiCount = 0

            while (it.moveToNext()) {
                val message = it.getString(bodyIndex)
                val dateMillis = it.getLong(dateIndex)
                processedCount++

                Log.d("SmsHelper", "Processing SMS #$processedCount: Date=${Date(dateMillis)}")
                Log.v("SmsHelper", "SMS Content: $message")

                if (isUPIMessage(message)) {
                    upiCount++
                    val upiData = parseUpiDataFromMessage7days(message, dateMillis, userId)
                    if (upiData != null) {
                        results.add(upiData)
                        Log.d("SmsHelper", "Successfully parsed UPI data #${results.size}: $upiData")
                    } else {
                        Log.w("SmsHelper", "Failed to parse UPI data from message: $message")
                    }
                }
            }

            Log.i("SmsHelper", "SMS Processing Summary: Total SMS=$processedCount, UPI SMS=$upiCount, Parsed UPI=${results.size}")
        } ?: run {
            Log.e("SmsHelper", "Failed to query SMS content resolver")
        }

        return results
    }



    private fun isUPIMessage(message: String): Boolean {
        val upiKeywords = listOf(
            "UPI", "upi", "transaction", "debited", "credited",
            "sent", "received", "paid", "Rs.", "₹", "INR",
            "UTR", "Ref", "Reference", "IMPS", "NEFT", "RTGS"
        )
        return upiKeywords.any { message.contains(it, ignoreCase = true) }
    }

    private fun parseUpiDataFromMessage(
        message: String,
        dateMillis: Long,
        userId: String
    ): TransactionDataModel? {
        return try {
            // Parse different UPI message formats
            val amount = extractAmount(message)
            val receiver = extractReceiver(message)
            val bank = extractBank(message)
            val account = extractAccount(message)
            val upiRefId = extractUPIRef(message)
            val date = formatDate(dateMillis)

            // Log extracted data for debugging
            Log.d(
                "SmsHelper",
                "Extracted - Amount: $amount, Receiver: $receiver, Bank: $bank, Account: $account, UPI Ref: $upiRefId"
            )

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
                Log.w("SmsHelper", "Missing required fields - Amount: $amount, Receiver: $receiver")
                null
            }
        } catch (e: Exception) {
            Log.e("SmsHelper", "Error parsing UPI message: ${e.message}")
            null
        }
    }


    private fun parseUpiDataFromMessage7days(
        message: String,
        dateMillis: Long,
        userId: String
    ): TransactionDataModel7days? {
        return try {
            // Parse different UPI message formats
            val amount = extractAmount(message)
            val receiver = extractReceiver(message)
            val bank = extractBank(message)
            val account = extractAccount(message)
            val upiRefId = extractUPIRef(message)
            val date = formatDate(dateMillis)

            // Log extracted data for debugging
            Log.d(
                "SmsHelper",
                "Extracted - Amount: $amount, Receiver: $receiver, Bank: $bank, Account: $account, UPI Ref: $upiRefId"
            )

            if (amount.isNotEmpty() && receiver.isNotEmpty()) {
                TransactionDataModel7days(
                    userId = userId,
                    account = account,
                    amount = amount,
                    bank = bank,
                    date = date,
                    receiver = receiver,
                    upiRefId = upiRefId
                )
            } else {
                Log.w("SmsHelper", "Missing required fields - Amount: $amount, Receiver: $receiver")
                null
            }
        } catch (e: Exception) {
            Log.e("SmsHelper", "Error parsing UPI message: ${e.message}")
            null
        }
    }

    private fun extractAmount(message: String): String {
        // Enhanced patterns to match amounts like Rs.100, ₹100, INR 100, etc.
        val patterns = listOf(
            "Rs\\.?\\s*(\\d+(?:[,.]\\d+)*(?:\\.\\d{2})?)",
            "₹\\s*(\\d+(?:[,.]\\d+)*(?:\\.\\d{2})?)",
            "INR\\s*(\\d+(?:[,.]\\d+)*(?:\\.\\d{2})?)",
            "amount\\s+Rs\\.?\\s*(\\d+(?:[,.]\\d+)*(?:\\.\\d{2})?)",
            "amount\\s+₹\\s*(\\d+(?:[,.]\\d+)*(?:\\.\\d{2})?)",
            "of\\s+Rs\\.?\\s*(\\d+(?:[,.]\\d+)*(?:\\.\\d{2})?)",
            "of\\s+₹\\s*(\\d+(?:[,.]\\d+)*(?:\\.\\d{2})?)"
        )

        for (pattern in patterns) {
            val regex = Regex(pattern, RegexOption.IGNORE_CASE)
            val match = regex.find(message)
            if (match != null) {
                val amount = match.groupValues[1].replace(",", "")
                Log.d("SmsHelper", "Found amount: $amount using pattern: $pattern")
                return amount
            }
        }
        Log.w("SmsHelper", "No amount found in message")
        return ""
    }

    private fun extractReceiver(message: String): String {
        // Enhanced patterns for receiver names in UPI messages based on actual format
        val patterns = listOf(
            // Pattern for "to stk-782780165@okbizaxis" format
            "to\\s+([A-Za-z0-9@.-]+)\\s+on",
            // Pattern for "to coolboyjunny@axl on" format
            "to\\s+([A-Za-z0-9@.-]+)\\s+on",
            // Other common patterns
            "sent to\\s+([A-Za-z0-9\\s@.-]+)",
            "paid to\\s+([A-Za-z0-9\\s@.-]+)",
            "transferred to\\s+([A-Za-z0-9\\s@.-]+)",
            "to\\s+([A-Za-z0-9\\s@.-]+)\\s+via",
            "to\\s+([A-Za-z0-9\\s@.-]+)\\s+UPI",
            "VPA\\s+([A-Za-z0-9@.-]+)",
            "UPI ID\\s+([A-Za-z0-9@.-]+)"
        )

        for (pattern in patterns) {
            val regex = Regex(pattern, RegexOption.IGNORE_CASE)
            val match = regex.find(message)
            if (match != null) {
                val receiver = match.groupValues[1].trim()
                Log.d("SmsHelper", "Found receiver: $receiver using pattern: $pattern")
                return receiver
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
                    val receiver = words[0].trim()
                    Log.d("SmsHelper", "Found receiver (fallback): $receiver")
                    return receiver
                }
            }
        }

        Log.w("SmsHelper", "No receiver found in message")
        return "Unknown"
    }

    // Add these methods inside the SmsHelper class

    private fun extractBank(message: String): String {
        // Pattern for bank name like "Kotak Bank AC" or similar
        val patterns = listOf(
            "from\\s+([A-Za-z\\s]+)\\s+AC",
            "by\\s+([A-Za-z\\s]+)\\s+Bank",
            "via\\s+([A-Za-z\\s]+)\\s+Bank"
        )

        for (pattern in patterns) {
            val regex = Regex(pattern, RegexOption.IGNORE_CASE)
            val match = regex.find(message)
            if (match != null) {
                val bank = match.groupValues[1].trim()
                Log.d("SmsHelper", "Found bank: $bank using pattern: $pattern")
                return bank
            }
        }
        Log.w("SmsHelper", "No bank found in message")
        return "Unknown"
    }

    private fun extractAccount(message: String): String {
        // Pattern for account number or identifier like "X3957"
        val patterns = listOf(
            "from\\s+[A-Za-z\\s]+\\s+AC\\s+([A-Za-z0-9]+)",
            "AC\\s+([A-Za-z0-9]+)"
        )

        for (pattern in patterns) {
            val regex = Regex(pattern, RegexOption.IGNORE_CASE)
            val match = regex.find(message)
            if (match != null) {
                val account = match.groupValues[1].trim()
                Log.d("SmsHelper", "Found account: $account using pattern: $pattern")
                return account
            }
        }
        Log.w("SmsHelper", "No account found in message")
        return "Unknown"
    }

    private fun extractUPIRef(message: String): String {
        // Pattern for UPI Reference like "UPI Ref 547454447057" or "Ref 290667420543"
        val patterns = listOf(
            "UPI\\s+Ref\\s+(\\d+)",
            "Ref\\s+(\\d+)",
            "Reference\\s+(\\d+)"
        )

        for (pattern in patterns) {
            val regex = Regex(pattern, RegexOption.IGNORE_CASE)
            val match = regex.find(message)
            if (match != null) {
                val upiRefId = match.groupValues[1].trim()
                Log.d("SmsHelper", "Found UPI Ref: $upiRefId using pattern: $pattern")
                return upiRefId
            }
        }
        Log.w("SmsHelper", "No UPI Ref found in message")
        return "Unknown"
    }

    private fun formatDate(dateMillis: Long): String {
        // Convert timestamp to readable date format (e.g., "16-06-25")
        val date = Date(dateMillis)
        val formatter = SimpleDateFormat("dd-MM-yy", Locale.getDefault())
        return formatter.format(date)
    }



}