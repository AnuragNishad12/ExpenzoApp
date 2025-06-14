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

        setupObservers();


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
            val smsHelper = SmsHelper(this)

            runOnUiThread {
                circularProgressBar.setProgress(30f, animate = true)
            }

            val userId = "684bbadc62bc05d171ab1175"
            val transactionList = smsHelper.getStructuredUPIData(userId)

            runOnUiThread {
                circularProgressBar.setProgress(70f, animate = true)

                if (transactionList.isNotEmpty()) {
                    upiRefTextView.text = "Found ${transactionList.size} UPI transactions"

                    // Log the parsed data for debugging
                    transactionList.forEach { transaction ->
                        Log.d("TransactionData", "Parsed: $transaction")
                    }

                    circularProgressBar.setProgress(90f, animate = true)

                    // Send each transaction to API
                    transactionList.forEach { transaction ->
                        viewModel.transactionDataClass(transaction)
                    }

                    circularProgressBar.setProgress(100f, animate = true)
                } else {
                    upiRefTextView.text = "No UPI transactions found in SMS"
                    circularProgressBar.setProgress(100f, animate = true)
                }

                Handler(Looper.getMainLooper()).postDelayed({
                    circularProgressBar.visibility = View.GONE
                }, 2000)
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










//    private fun extractAndDisplayUPIRefs() {
//        circularProgressBar.visibility = View.VISIBLE
//        circularProgressBar.setProgress(0f, animate = false)
//        Thread {
//            val smsHelper = SmsHelper(this)
//            runOnUiThread { circularProgressBar.setProgress(50f, animate = true) }
//
//            val upiRefs: List<String> = smsHelper.getStructuredUPIData(7)
//            runOnUiThread {
//                circularProgressBar.setProgress(100f, animate = true)
//                Handler(Looper.getMainLooper()).postDelayed({
//                    circularProgressBar.visibility = View.GONE
//                }, 2000)
//                if (upiRefs.isNotEmpty()) {
//                    upiRefTextView.text = upiRefs.joinToString("\n\n")
//                } else {
//                    upiRefTextView.text = "No UPI references found in SMS"
//                }
//            }
//        }.start()
//    }

//    fun transaction(){
//
//        val transactiondata = TransactionDataModel
//
//
//    }


}