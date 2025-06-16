package com.example.expenzo

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.Manifest

import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View

import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.expenzo.Model.TransactionDataModel

import com.example.expenzo.Utils.BeautifulCircularProgressBar
import com.example.expenzo.Utils.SmsHelper
import com.example.expenzo.Utils.StoredTransactionsHelper
import com.example.expenzo.ViewModel.TrascationViewModel
import com.example.expenzo.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var upiRefTextView: TextView
    private lateinit var circularProgressBar: BeautifulCircularProgressBar
    private val SMS_PERMISSION_CODE = 101
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: TrascationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[TrascationViewModel::class.java]

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        upiRefTextView = findViewById(R.id.upiRefTextView)
        circularProgressBar = findViewById(R.id.circularProgressBar)

        circularProgressBar.setMaxProgress(100f)
        circularProgressBar.setStrokeWidth(20f)
        circularProgressBar.visibility = View.GONE

        setupObservers()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_SMS),
                SMS_PERMISSION_CODE
            )
        } else {
            extractAndSendUPIRefs()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == SMS_PERMISSION_CODE && grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            extractAndSendUPIRefs()
        } else {
            Toast.makeText(this, "Permission denied to read SMS", Toast.LENGTH_SHORT).show()
        }
    }

    private fun extractAndSendUPIRefs() {
        circularProgressBar.visibility = View.VISIBLE
        circularProgressBar.setProgress(0f, animate = false)

        Thread {
            try {
                val smsHelper = SmsHelper(this)
                val storedHelper = StoredTransactionsHelper(this)

                runOnUiThread {
                    circularProgressBar.setProgress(30f, animate = true)
                }

                val userId = "684bbadc62bc05d171ab1175"
                val allTransactions = smsHelper.getStructuredUPIData(userId)
                Log.d("TransactionDebug", "Total transactions found: ${allTransactions.size}")

                // Log all transactions for debugging
                allTransactions.forEachIndexed { index, transaction ->
                    Log.d("TransactionDebug", "Transaction $index: UPI Ref: ${transaction.upiRefId}, Amount: ${transaction.amount}, Receiver: ${transaction.receiver}")
                }

                // Filter only new transactions
                val newTransactions = allTransactions.filter { transaction ->
                    val isStored = storedHelper.isTransactionAlreadyStored(transaction.upiRefId)
                    Log.d("TransactionFilter", "UPI Ref ID: ${transaction.upiRefId}, Stored: $isStored")
                    !isStored
                }
                Log.d("TransactionDebug", "New transactions: ${newTransactions.size}")

                runOnUiThread {
                    if (!isFinishing) {
                        circularProgressBar.setProgress(70f, animate = true)

                        if (newTransactions.isNotEmpty()) {
                            upiRefTextView.text = "Found ${newTransactions.size} new UPI transactions"

                            // Process each new transaction
                            newTransactions.forEach { transaction ->
                                Log.d("TransactionData", "Processing New Transaction: $transaction")

                                // Only send transactions with valid UPI reference IDs
                                if (transaction.upiRefId != "Unknown" && transaction.upiRefId.isNotBlank()) {
                                    viewModel.transactionDataClass(transaction)
                                    storedHelper.markTransactionAsStored(transaction.upiRefId)
                                    Log.d("TransactionData", "Sent transaction with UPI Ref: ${transaction.upiRefId}")
                                } else {
                                    Log.w("TransactionData", "Skipping transaction with invalid UPI Ref: ${transaction.upiRefId}")
                                }
                            }
                            circularProgressBar.setProgress(100f, animate = true)
                        } else {
                            upiRefTextView.text = if (allTransactions.isEmpty()) {
                                "No UPI transactions found in SMS"
                            } else {
                                "No new UPI transactions (${allTransactions.size} already processed)"
                            }
                            circularProgressBar.setProgress(100f, animate = true)
                        }

                        // Show stored transaction count for debugging
                        val storedCount = storedHelper.getStoredTransactionCount()
                        Log.d("TransactionDebug", "Total stored transactions: $storedCount")

                        Handler(Looper.getMainLooper()).postDelayed({
                            if (!isFinishing) {
                                circularProgressBar.visibility = View.GONE
                            }
                        }, 2000)
                    }
                }
            } catch (e: Exception) {
                Log.e("ThreadError", "Error in extractAndSendUPIRefs: ${e.message}", e)
                runOnUiThread {
                    if (!isFinishing) {
                        Toast.makeText(this, "Error processing SMS: ${e.message}", Toast.LENGTH_LONG).show()
                        circularProgressBar.visibility = View.GONE
                    }
                }
            }
        }.start()
    }

    private fun setupObservers() {
        viewModel.responseTransaction.observe(this) { response ->
            response?.let {
                Log.d("MainActivity", "API Success: ${it.message}")
                Toast.makeText(this, "Transaction sent successfully", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.errorResponse.observe(this) { error ->
            error?.let {
                Log.e("MainActivity", "API Error: $it")
                Toast.makeText(this, "Error: $it", Toast.LENGTH_SHORT).show()
            }
        }
    }
}