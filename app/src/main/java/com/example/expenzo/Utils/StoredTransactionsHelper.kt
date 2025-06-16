package com.example.expenzo.Utils

import android.content.Context
import android.util.Log

class StoredTransactionsHelper(private val context: Context) {

    private val sharedPreferences = context.getSharedPreferences("upi_transactions", Context.MODE_PRIVATE)
    private val STORED_UPI_REFS_KEY = "stored_upi_refs"

    /**
     * Check if a UPI reference ID has already been processed and stored
     */
    fun isTransactionAlreadyStored(upiRefId: String): Boolean {
        if (upiRefId == "Unknown" || upiRefId.isBlank()) {
            Log.d("StoredTransactions", "UPI Ref ID is Unknown or blank, treating as new")
            return false
        }

        val storedRefs = getStoredUpiRefs()
        val isStored = storedRefs.contains(upiRefId)
        Log.d("StoredTransactions", "Checking UPI Ref: $upiRefId, Already stored: $isStored")
        return isStored
    }

    /**
     * Mark a UPI reference ID as processed and stored
     */
    fun markTransactionAsStored(upiRefId: String) {
        if (upiRefId == "Unknown" || upiRefId.isBlank()) {
            Log.w("StoredTransactions", "Not storing Unknown or blank UPI Ref ID")
            return
        }

        val storedRefs = getStoredUpiRefs().toMutableSet()
        storedRefs.add(upiRefId)

        // Keep only last 1000 transactions to prevent unlimited growth
        if (storedRefs.size > 1000) {
            val sortedRefs = storedRefs.toList()
            storedRefs.clear()
            storedRefs.addAll(sortedRefs.takeLast(800)) // Keep last 800
        }

        saveStoredUpiRefs(storedRefs)
        Log.d("StoredTransactions", "Marked UPI Ref as stored: $upiRefId")
    }

    /**
     * Get all stored UPI reference IDs
     */
    private fun getStoredUpiRefs(): Set<String> {
        val storedRefsString = sharedPreferences.getString(STORED_UPI_REFS_KEY, "")
        return if (storedRefsString.isNullOrEmpty()) {
            emptySet()
        } else {
            storedRefsString.split(",").toSet()
        }
    }

    /**
     * Save UPI reference IDs to SharedPreferences
     */
    private fun saveStoredUpiRefs(upiRefs: Set<String>) {
        val editor = sharedPreferences.edit()
        editor.putString(STORED_UPI_REFS_KEY, upiRefs.joinToString(","))
        editor.apply()
    }

    /**
     * Clear all stored UPI reference IDs (useful for testing or reset)
     */
    fun clearAllStoredTransactions() {
        val editor = sharedPreferences.edit()
        editor.remove(STORED_UPI_REFS_KEY)
        editor.apply()
        Log.d("StoredTransactions", "Cleared all stored UPI references")
    }

    /**
     * Get count of stored transactions
     */
    fun getStoredTransactionCount(): Int {
        return getStoredUpiRefs().size
    }

    /**
     * Get all stored UPI reference IDs for debugging
     */
    fun getAllStoredUpiRefs(): Set<String> {
        return getStoredUpiRefs()
    }
}
