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
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.expenzo.Adapter.TransactionAdapter
import com.example.expenzo.BackgroundServices.AlarmManager7days
import com.example.expenzo.BackgroundServices.MyAlarmReceiver
import com.example.expenzo.Model.FetchCurrentDayDataModel
import com.example.expenzo.Model.FetchCurrentDayDataModel30days
import com.example.expenzo.Model.FetchCurrentDayDataModel7days
import com.example.expenzo.Model.TransactionDataModel30days
import com.example.expenzo.R
import com.example.expenzo.Utils.BeautifulCircularProgressBar
import com.example.expenzo.Utils.SmsHelper
import com.example.expenzo.Utils.StoredTransactionsHelper
import com.example.expenzo.Utils.UserDataStore
import com.example.expenzo.ViewModel.Fetch30daysTransViewModel
import com.example.expenzo.ViewModel.Fetch7daysTransViewModel
import com.example.expenzo.ViewModel.TransactionCurrentDayViewModel
import com.example.expenzo.ViewModel.TrascationViewModel
import com.example.expenzo.ViewModel.TrascationViewModel30days
import com.example.expenzo.ViewModel.TrascationViewModel7days
import com.example.expenzo.databinding.ActivityMainBinding
import com.example.expenzo.databinding.FragmentHomeFragmentsBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class HomeFragments : Fragment() {


    private var _binding: FragmentHomeFragmentsBinding? = null
    private val binding get() = _binding!!


    private val SMS_PERMISSION_CODE = 101
    private lateinit var viewModel: TrascationViewModel
    private lateinit var transVm : TransactionCurrentDayViewModel
    private lateinit var transactionAdapter : TransactionAdapter
    private lateinit var viewModel7days : TrascationViewModel7days
    private lateinit var viewModel30days : TrascationViewModel30days
    private lateinit var days7viewModel : Fetch7daysTransViewModel
    private lateinit var days30viewmodel : Fetch30daysTransViewModel
    private lateinit var userDataStore: UserDataStore


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeFragmentsBinding.inflate(inflater, container, false)


        viewModel = ViewModelProvider(this)[TrascationViewModel::class.java]
        viewModel7days = ViewModelProvider(this)[TrascationViewModel7days::class.java]
        days7viewModel = ViewModelProvider(this)[Fetch7daysTransViewModel::class.java]
        viewModel30days = ViewModelProvider(this)[TrascationViewModel30days::class.java]
        days30viewmodel = ViewModelProvider(this)[Fetch30daysTransViewModel::class.java]
        userDataStore = UserDataStore(requireContext())


        setupObservers()
        scheduleAlarmFor1155PM()
        scheduleAlarmFor1205PM()
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_SMS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireContext() as Activity,
                arrayOf(Manifest.permission.READ_SMS),
                SMS_PERMISSION_CODE
            )
        } else {
            startExtractProcess()
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
        val userDataStore = UserDataStore(requireContext())
        viewLifecycleOwner.lifecycleScope.launch {
            val uniqueName = userDataStore.getUniqueName()
            // use uniqueName here
            val request = FetchCurrentDayDataModel(userId = uniqueName.toString())
            transVm.showTransactionCurrentDayVm(request)
            val request7days = FetchCurrentDayDataModel7days(userId = uniqueName.toString())
            days7viewModel.showTransaction7daysVm(request7days)

            val request30days = FetchCurrentDayDataModel30days(userId = uniqueName.toString())
            days30viewmodel.showTransaction30daysVm(request30days)


            Log.d("Fragment", "Unique Name: $uniqueName")
        }




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



        days7viewModel.mostFrequentReceiver.observe(viewLifecycleOwner) {
            val (receiver, totalSent) = it
            binding.cdFrequentPayeeValue.text = "$receiver"
            binding.cdFrequentPayeeAmount.text = "$totalSent"
        }

        /*....................................7days.....................................*/

        days7viewModel.totalAmount.observe(viewLifecycleOwner) {
            binding.sdTotalSpendingValue.text = "₹${it}"
        }

        days7viewModel.highestTransaction.observe(viewLifecycleOwner) {
            val name = it?.Receiver ?: "No Data"
            val amount = it?.Amount ?: "0.00"
            binding.sdHighestTransactionValue.text = "₹$amount"
            binding.sdHighestTransactionTo.text = "$name"
        }

        days7viewModel.totalTransactionCount.observe(viewLifecycleOwner) {
            binding.sdTotalTransactionsValue.text = "$it"
        }

        days7viewModel.mostFrequentReceiver.observe(viewLifecycleOwner) {
            val (receiver, totalSent) = it
            binding.sdFrequentPayeeValue.text = "$receiver"
            binding.sdFrequentPayeeAmount.text = "$totalSent"
        }

        /*....................................7days.....................................*/


        /*....................................30days.....................................*/
        days30viewmodel.totalAmount.observe(viewLifecycleOwner) {
            binding.tdTotalSpendingValue.text = "₹${it}"
        }

        days30viewmodel.highestTransaction.observe(viewLifecycleOwner) {
            val name = it?.Receiver ?: "No Data"
            val amount = it?.Amount ?: "0.00"
            binding.tdHighestTransactionValue.text = "₹$amount"
            binding.tdHighestTransactionTo.text = "$name"
        }

        days30viewmodel.totalTransactionCount.observe(viewLifecycleOwner) {
            binding.tdTotalTransactionsValue.text = "$it"
        }

        days30viewmodel.mostFrequentReceiver.observe(viewLifecycleOwner) {
            val (receiver, totalSent) = it
            binding.tdFrequentPayeeValue.text = "$receiver"
            binding.tdFrequentPayeeAmount.text = "$totalSent"
        }



        /*....................................30days.....................................*/
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
            startExtractProcess()
        } else {
            Toast.makeText(requireContext(), "Permission denied to read SMS", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("SuspiciousIndentation")
    private fun extractAndSendUPIRefs() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val smsHelper = SmsHelper(requireContext())
                val userDataStore = UserDataStore(requireContext())

                // Fix: Properly call the suspend function and handle null case
                val userId = userDataStore.getUniqueName() ?: "default_user"
                Log.d("Fragment", "Unique Name: $userId")

                val allTransactions = smsHelper.getStructuredUPIData(userId)
                Log.d("TransactionDebug", "Total transactions found: ${allTransactions.size}")

                // Process transactions
                val validTransactions = allTransactions.filter {
                    it.upiRefId != "Unknown" && it.upiRefId.isNotBlank()
                }

                withContext(Dispatchers.Main) {
                    if (activity is AppCompatActivity && activity?.isFinishing != true) {
                        if (validTransactions.isNotEmpty()) {
                            binding.upiRefTextView.text = "Found ${validTransactions.size} new UPI transactions"
                            validTransactions.forEach { transaction ->
                                viewModel.transactionDataClass(transaction)
                                Log.d("TransactionData", "Sent transaction with UPI Ref: ${transaction.upiRefId}")
                            }
                        } else {
                            binding.upiRefTextView.text = "No valid UPI transactions found"
                        }

                        Handler(Looper.getMainLooper()).postDelayed({
                            if (activity is AppCompatActivity && activity?.isFinishing != true) {

                            }
                        }, 2000)
                    }
                }
            } catch (e: Exception) {
                Log.e("CoroutineError", "Error in extractAndSendUPIRefs: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Errorinhomefrag: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }


    /* .....................................Function for 7 days.......................................... */
//    @SuppressLint("SuspiciousIndentation")
//    private fun extractAndSendUPIRefs7days() {


    private fun extractAndSendUPIRefs7days() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val smsHelper = SmsHelper(requireContext())
//                val storedHelper = StoredTransactionsHelper(requireContext())

                val userDataStore = UserDataStore(requireContext())

                val userId = userDataStore.getUniqueName() ?: "default_user"
//                val userId = "684bbadc62bc05d171ab1175" // You can replace this with userDataStore.getUniqueName() if needed

                val allTransactions = smsHelper.getStructuredUPIData7Days(userId)
                Log.d("TransactionDebug", "Total transactions found: ${allTransactions.size}")

                allTransactions.forEachIndexed { index, transaction ->
                    Log.d("TransactionDebug", "Transaction $index: UPI Ref: ${transaction.upiRefId}, Amount: ${transaction.amount}, Receiver: ${transaction.receiver}")
                }


                Log.d("TransactionDebug", "New transactions: ${allTransactions.size}")

                withContext(Dispatchers.Main) {
                    if (activity is AppCompatActivity && activity?.isFinishing != true) {
                        if (allTransactions.isNotEmpty()) {
                            binding.upiRefTextView.text = "Found ${allTransactions.size} new UPI transactions"

                            allTransactions.forEach { transaction ->
                                Log.d("TransactionData", "Processing New Transaction: $transaction")

                                if (transaction.upiRefId != "Unknown" && transaction.upiRefId.isNotBlank()) {
                                    viewModel7days.transactionDataClass7days(transaction)
//                                    storedHelper.markTransactionAsStored(transaction.upiRefId)
                                    Log.d("TransactionData", "Sent transaction with UPI Ref: ${transaction.upiRefId}")
                                } else {
                                    Log.w("TransactionData", "Skipping transaction with invalid UPI Ref: ${transaction.upiRefId}")
                                }
                            }
                        } else {
                            binding.upiRefTextView.text = if (allTransactions.isEmpty()) {
                                "No UPI transactions found in SMS"
                            } else {
                                "No new UPI transactions (${allTransactions.size} already processed)"
                            }
                        }

//                        val storedCount = storedHelper.getStoredTransactionCount()
//                        Log.d("TransactionDebug", "Total stored transactions: $storedCount")

                        Handler(Looper.getMainLooper()).postDelayed({
                            if (activity is AppCompatActivity && activity?.isFinishing != true) {
                                // binding.circularProgressBar.visibility = View.GONE
                            }
                        }, 2000)
                    }
                }
            } catch (e: Exception) {
                Log.e("CoroutineError", "Error in extractAndSendUPIRefs7days: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    if (activity is AppCompatActivity && activity?.isFinishing != true) {
                        Toast.makeText(requireContext(), "Error processing SMS: ${e.message}", Toast.LENGTH_LONG).show()
                        // binding.circularProgressBar.visibility = View.GONE
                    }
                }
            }
        }
    }




    fun scheduleAlarmFor1205PM() {
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {

                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                startActivity(intent)
                Log.w("MainActivityWorker7days", "Requesting permission to schedule exact alarms.")
                return
            }
        }

        val intent = Intent(requireActivity(), AlarmManager7days::class.java)
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
//
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

        Log.d("MainActivityWorker7days", "Alarm scheduled for: ${calendar.time}")
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

    /*.............................30Days..............................*/


    @SuppressLint("SuspiciousIndentation")
    private fun extractAndSendUPIRefs30days() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val smsHelper = SmsHelper(requireContext())
                val storedHelper = StoredTransactionsHelper(requireContext())

                val userDataStore = UserDataStore(requireContext())

                val userId = userDataStore.getUniqueName() ?: "default_user"
//                val userId = "684bbadc62bc05d171ab1175" // You can replace this with value from UserDataStore if needed

                val allTransactions = smsHelper.getStructuredUPIData30Days(userId)
                Log.d("TransactionDebug", "Total transactions found: ${allTransactions.size}")

                allTransactions.forEachIndexed { index, transaction ->
                    Log.d("TransactionDebug", "Transaction $index: UPI Ref: ${transaction.upiRefId}, Amount: ${transaction.amount}, Receiver: ${transaction.receiver}")
                }


                Log.d("TransactionDebug", "New transactions: ${allTransactions.size}")

                withContext(Dispatchers.Main) {
                    if (activity is AppCompatActivity && activity?.isFinishing != true) {

                        if (allTransactions.isNotEmpty()) {
                            binding.upiRefTextView.text = "Found ${allTransactions.size} new UPI transactions"

                            allTransactions.forEach { transaction ->
                                Log.d("TransactionData", "Processing New Transaction: $transaction")

                                if (transaction.upiRefId != "Unknown" && transaction.upiRefId.isNotBlank()) {
                                    viewModel30days.transactionDataClass30days(transaction)
//                                    storedHelper.markTransactionAsStored(transaction.upiRefId)
                                    Log.d("TransactionData", "Sent transaction with UPI Ref: ${transaction.upiRefId}")
                                } else {
                                    Log.w("TransactionData", "Skipping transaction with invalid UPI Ref: ${transaction.upiRefId}")
                                }
                            }
                        } else {
                            binding.upiRefTextView.text = if (allTransactions.isEmpty()) {
                                "No UPI transactions found in SMS"
                            } else {
                                "No new UPI transactions (${allTransactions.size} already processed)"
                            }
                        }


                        Handler(Looper.getMainLooper()).postDelayed({
                            if (activity is AppCompatActivity && activity?.isFinishing != true) {
                                // binding.circularProgressBar.visibility = View.GONE
                            }
                        }, 2000)
                    }
                }
            } catch (e: Exception) {
                Log.e("CoroutineError", "Error in extractAndSendUPIRefs30days: ${e.message}", e)
                withContext(Dispatchers.Main) {
                    if (activity is AppCompatActivity && activity?.isFinishing != true) {
                        Toast.makeText(requireContext(), "Error processing SMS: ${e.message}", Toast.LENGTH_LONG).show()
                        // binding.circularProgressBar.visibility = View.GONE
                    }
                }
            }
        }
    }



    /*.............................30Days..............................*/


    fun startExtractProcess() {
        lifecycleScope.launch {
            extractAndSendUPIRefs()
            delay(1500) // 1.5 seconds

            extractAndSendUPIRefs7days()
            delay(1500) // 1.5 seconds

            extractAndSendUPIRefs30days()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}