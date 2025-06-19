package com.example.expenzo.Ui.Fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.expenzo.Adapter.TransactionAdapter
import com.example.expenzo.BackgroundServices.MyAlarmReceiver
import com.example.expenzo.Model.FetchCurrentDayDataModel
import com.example.expenzo.R
import com.example.expenzo.Utils.BeautifulCircularProgressBar
import com.example.expenzo.Utils.SmsHelper
import com.example.expenzo.Utils.StoredTransactionsHelper
import com.example.expenzo.ViewModel.TransactionCurrentDayViewModel
import com.example.expenzo.ViewModel.TrascationViewModel
import com.example.expenzo.ViewModel.TrascationViewModel7days
import com.example.expenzo.databinding.ActivityMainBinding
import com.example.expenzo.databinding.FragmentHomeFragmentsBinding


class HomeFragments : Fragment() {


    private var _binding: FragmentHomeFragmentsBinding? = null
    private val binding get() = _binding!!


    private val SMS_PERMISSION_CODE = 101
    private lateinit var viewModel: TrascationViewModel
    private lateinit var transVm : TransactionCurrentDayViewModel
    private lateinit var transactionAdapter : TransactionAdapter
    private lateinit var viewModel7days : TrascationViewModel7days


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeFragmentsBinding.inflate(inflater, container, false)


        viewModel = ViewModelProvider(this)[TrascationViewModel::class.java]
        viewModel7days = ViewModelProvider(this)[TrascationViewModel7days::class.java]

//        upiRefTextView = findViewById(R.id.upiRefTextView)
//        circularProgressBar = findViewById(R.id.circularProgressBar)


//        binding.circularProgressBar.setMaxProgress(100f)
//        binding.circularProgressBar.setStrokeWidth(20f)
//        binding.circularProgressBar.visibility = View.GONE

        setupObservers()
        scheduleAlarmFor1155PM()
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_SMS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireContext() as Activity,
                arrayOf(Manifest.permission.READ_SMS),
                SMS_PERMISSION_CODE
            )
        } else {
            extractAndSendUPIRefs()
            extractAndSendUPIRefs7days();
        }
        transVm = ViewModelProvider(this)[TransactionCurrentDayViewModel::class.java]

        binding.transactionRecycler.layoutManager = LinearLayoutManager(requireContext())

        transVm.transactions.observe(viewLifecycleOwner) { transactionList ->
            transactionAdapter = TransactionAdapter(transactionList)
            binding.transactionRecycler.adapter = transactionAdapter
            binding.progressBar.visibility = View.GONE
        }


        transVm.error.observe(viewLifecycleOwner) {
            Log.d("UnkwonError","error ${it}")
            Toast.makeText(requireContext(), it ?: "Unknown error", Toast.LENGTH_SHORT).show()
            binding.progressBar.visibility = View.GONE
        }

        transVm.totalAmount.observe(viewLifecycleOwner) { total ->
            binding.totalamountcurrent.text = "Total: ₹$total"
            binding.progressBar.visibility = View.GONE
        }

        val request = FetchCurrentDayDataModel(userId = "684bbadc62bc05d171ab1175")
        transVm.showTransactionCurrentDayVm(request)

        transVm.totalAmount.observe(viewLifecycleOwner) {
            binding.cdTotalSpendingValue.text = "₹${it}"
        }

        transVm.highestTransaction.observe(viewLifecycleOwner) {
            val name = it?.Receiver ?: "No Data"
            val amount = it?.Amount ?: "0.00"
            binding.cdHighestTransactionValue.text = "₹$amount"
            binding.cdHighestTransactionTo.text = "$name"
        }

        transVm.totalTransactionCount.observe(viewLifecycleOwner) {
            binding.cdTotalTransactionsValue.text = "$it"
        }

        transVm.mostFrequentReceiver.observe(viewLifecycleOwner) {
            val (receiver, totalSent) = it
            binding.cdFrequentPayeeValue.text = "$receiver"
            binding.cdFrequentPayeeAmount.text = "$totalSent"
        }





        return binding.root
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
            extractAndSendUPIRefs7days()
        } else {
            Toast.makeText(requireContext(), "Permission denied to read SMS", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("SuspiciousIndentation")
    private fun extractAndSendUPIRefs() {
//        binding.circularProgressBar.visibility = View.VISIBLE
//        binding.circularProgressBar.setProgress(0f, animate = false)

        Thread {
            try {
                val smsHelper = SmsHelper(requireContext())
                val storedHelper = StoredTransactionsHelper(requireContext())

                requireActivity().runOnUiThread {
//                    binding.circularProgressBar.setProgress(30f, animate = true)
                }

                val userId = "684bbadc62bc05d171ab1175"
                val allTransactions = smsHelper.getStructuredUPIData(userId)
                Log.d("TransactionDebug", "Total transactions found: ${allTransactions.size}")

                // Log all transactions for debugging
                allTransactions.forEachIndexed { index, transaction ->
                    Log.d("TransactionDebug", "Transaction $index: UPI Ref: ${transaction.upiRefId}, Amount: ${transaction.amount}, Receiver: ${transaction.receiver}")
                }


                val newTransactions = allTransactions.filter { transaction ->
                    val isStored = storedHelper.isTransactionAlreadyStored(transaction.upiRefId)
                    Log.d("TransactionFilter", "UPI Ref ID: ${transaction.upiRefId}, Stored: $isStored")
                    !isStored
                }
                Log.d("TransactionDebug", "New transactions: ${newTransactions.size}")

                requireActivity().runOnUiThread {
                    if (activity is AppCompatActivity && activity?.isFinishing != true) {
//                        binding.circularProgressBar.setProgress(70f, animate = true)

                        if (newTransactions.isNotEmpty()) {
                            binding.upiRefTextView.text = "Found ${newTransactions.size} new UPI transactions"


                            newTransactions.forEach { transaction ->
                                Log.d("TransactionData", "Processing New Transaction: $transaction")


                                if (transaction.upiRefId != "Unknown" && transaction.upiRefId.isNotBlank()) {
                                    viewModel.transactionDataClass(transaction)
                                    storedHelper.markTransactionAsStored(transaction.upiRefId)
                                    Log.d("TransactionData", "Sent transaction with UPI Ref: ${transaction.upiRefId}")
                                } else {
                                    Log.w("TransactionData", "Skipping transaction with invalid UPI Ref: ${transaction.upiRefId}")
                                }
                            }
//                            binding.circularProgressBar.setProgress(100f, animate = true)
                        } else {
                            binding.upiRefTextView.text = if (allTransactions.isEmpty()) {
                                "No UPI transactions found in SMS"
                            } else {
                                "No new UPI transactions (${allTransactions.size} already processed)"
                            }
//                            binding.circularProgressBar.setProgress(100f, animate = true)
                        }


                        val storedCount = storedHelper.getStoredTransactionCount()
                        Log.d("TransactionDebug", "Total stored transactions: $storedCount")

                        Handler(Looper.getMainLooper()).postDelayed({

                            if (activity is AppCompatActivity && activity?.isFinishing != true) {
//                                binding.circularProgressBar.visibility = View.GONE
                            }
                        }, 2000)
                    }
                }
            } catch (e: Exception) {
                Log.e("ThreadError", "Error in extractAndSendUPIRefs: ${e.message}", e)
                requireActivity().runOnUiThread {

                    if (activity is AppCompatActivity && activity?.isFinishing != true) {
                        Toast.makeText(requireContext(), "Error processing SMS: ${e.message}", Toast.LENGTH_LONG).show()
//                        binding.circularProgressBar.visibility = View.GONE
                    }
                }
            }
        }.start()
    }

    /* .....................................Function for 7 days.......................................... */
    @SuppressLint("SuspiciousIndentation")
    private fun extractAndSendUPIRefs7days() {
//        binding.circularProgressBar.visibility = View.VISIBLE
//        binding.circularProgressBar.setProgress(0f, animate = false)

        Thread {
            try {
                val smsHelper = SmsHelper(requireContext())
                val storedHelper = StoredTransactionsHelper(requireContext())

                requireActivity().runOnUiThread {
//                    binding.circularProgressBar.setProgress(30f, animate = true)
                }

                val userId = "684bbadc62bc05d171ab1175"
                val allTransactions = smsHelper.getStructuredUPIData7Days(userId)
                Log.d("TransactionDebug", "Total transactions found: ${allTransactions.size}")

                // Log all transactions for debugging
                allTransactions.forEachIndexed { index, transaction ->
                    Log.d("TransactionDebug", "Transaction $index: UPI Ref: ${transaction.upiRefId}, Amount: ${transaction.amount}, Receiver: ${transaction.receiver}")
                }


                val newTransactions = allTransactions.filter { transaction ->
                    val isStored = storedHelper.isTransactionAlreadyStored(transaction.upiRefId)
                    Log.d("TransactionFilter", "UPI Ref ID: ${transaction.upiRefId}, Stored: $isStored")
                    !isStored
                }
                Log.d("TransactionDebug", "New transactions: ${newTransactions.size}")

                requireActivity().runOnUiThread {
                    if (activity is AppCompatActivity && activity?.isFinishing != true) {
//                        binding.circularProgressBar.setProgress(70f, animate = true)

                        if (newTransactions.isNotEmpty()) {
                            binding.upiRefTextView.text = "Found ${newTransactions.size} new UPI transactions"


                            newTransactions.forEach { transaction ->
                                Log.d("TransactionData", "Processing New Transaction: $transaction")


                                if (transaction.upiRefId != "Unknown" && transaction.upiRefId.isNotBlank()) {
                                    viewModel7days.transactionDataClass7days(transaction)
                                    storedHelper.markTransactionAsStored(transaction.upiRefId)
                                    Log.d("TransactionData", "Sent transaction with UPI Ref: ${transaction.upiRefId}")
                                } else {
                                    Log.w("TransactionData", "Skipping transaction with invalid UPI Ref: ${transaction.upiRefId}")
                                }
                            }
//                            binding.circularProgressBar.setProgress(100f, animate = true)
                        } else {
                            binding.upiRefTextView.text = if (allTransactions.isEmpty()) {
                                "No UPI transactions found in SMS"
                            } else {
                                "No new UPI transactions (${allTransactions.size} already processed)"
                            }
//                            binding.circularProgressBar.setProgress(100f, animate = true)
                        }


                        val storedCount = storedHelper.getStoredTransactionCount()
                        Log.d("TransactionDebug", "Total stored transactions: $storedCount")

                        Handler(Looper.getMainLooper()).postDelayed({

                            if (activity is AppCompatActivity && activity?.isFinishing != true) {
//                                binding.circularProgressBar.visibility = View.GONE
                            }
                        }, 2000)
                    }
                }
            } catch (e: Exception) {
                Log.e("ThreadError", "Error in extractAndSendUPIRefs: ${e.message}", e)
                requireActivity().runOnUiThread {

                    if (activity is AppCompatActivity && activity?.isFinishing != true) {
                        Toast.makeText(requireContext(), "Error processing SMS: ${e.message}", Toast.LENGTH_LONG).show()
//                        binding.circularProgressBar.visibility = View.GONE
                    }
                }
            }
        }.start()
    }





    /* .....................................Function for 7 days.......................................... */

    private fun setupObservers() {
        viewModel.responseTransaction.observe(requireActivity()) { response ->
            response?.let {
                Log.d("MainActivity", "API Success: ${it.message}")
                Toast.makeText(requireContext(), "Transaction sent successfully", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.errorResponse.observe(requireActivity()) { error ->
            error?.let {
                Log.e("MainActivity", "API Error: $it")
                Toast.makeText(requireContext(), "Error: $it", Toast.LENGTH_SHORT).show()
            }
        }
    }


    @SuppressLint("ScheduleExactAlarm")
    fun scheduleAlarmFor1155PM() {
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {

                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                startActivity(intent)
                Log.w("MainActivity", "Requesting permission to schedule exact alarms.")
                return
            }
        }

        val intent = Intent(requireActivity(), MyAlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            requireActivity(),
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 55)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)


            if (before(Calendar.getInstance())) {
                add(Calendar.DATE, 1)
            }
        }

//        val calendar = Calendar.getInstance().apply {
//            timeInMillis = System.currentTimeMillis()
//            add(Calendar.MINUTE, 1) // 1 minute from now
//            set(Calendar.SECOND, 0)
//            set(Calendar.MILLISECOND, 0)
//        }

        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )

        Log.d("MainActivity", "Alarm scheduled for: ${calendar.time}")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}