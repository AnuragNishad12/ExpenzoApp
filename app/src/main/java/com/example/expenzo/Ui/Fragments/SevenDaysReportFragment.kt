package com.example.expenzo.Ui.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.expenzo.Adapter.TransactionAdapter
import com.example.expenzo.Adapter.Trasnaction7daysAdapter
import com.example.expenzo.Model.FetchCurrentDayDataModel
import com.example.expenzo.Model.FetchCurrentDayDataModel7days
import com.example.expenzo.R
import com.example.expenzo.Utils.UserDataStore
import com.example.expenzo.ViewModel.Fetch7daysTransViewModel
import com.example.expenzo.databinding.FragmentHomeFragmentsBinding
import com.example.expenzo.databinding.FragmentSevenDaysReportBinding
import kotlinx.coroutines.launch


class SevenDaysReportFragment : Fragment() {

    private var _binding: FragmentSevenDaysReportBinding? = null
    private val binding get() = _binding!!
    private lateinit var transactionAdapter : Trasnaction7daysAdapter
    private lateinit var viewmodel :Fetch7daysTransViewModel



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSevenDaysReportBinding.inflate(inflater, container, false)

        viewmodel = ViewModelProvider(this)[Fetch7daysTransViewModel::class.java]

        binding.recyclerview.layoutManager = LinearLayoutManager(requireContext())

        viewmodel.transactions.observe(viewLifecycleOwner) { transactionList ->
            transactionAdapter = Trasnaction7daysAdapter(transactionList)
            binding.recyclerview.adapter = transactionAdapter
            binding.progressBar2.visibility = View.GONE
        }


//        viewmodel.error.observe(viewLifecycleOwner) {
//            Log.d("UnkwonError","error ${it}")
//            Toast.makeText(requireContext(), it ?: "Unknown error", Toast.LENGTH_SHORT).show()
//            binding.progressBar.visibility = View.GONE
//        }
//
//        viewmodel.totalAmount.observe(viewLifecycleOwner) { total ->
//            binding.totalamountcurrent.text = "Total: ₹$total"
//            binding.progressBar.visibility = View.GONE
//        }


        val userDataStore = UserDataStore(requireContext())
        viewLifecycleOwner.lifecycleScope.launch {
            val userId = userDataStore.getUniqueName() ?: "default_user"
            // use uniqueName here

            val request = FetchCurrentDayDataModel7days(userId = userId)
            viewmodel.showTransaction7daysVm(request)

            Log.d("Fragment", "Unique Name: $userId")
        }



//        viewmodel.totalAmount.observe(viewLifecycleOwner) {
//            binding.cdTotalSpendingValue.text = "₹${it}"
//        }
//
//        viewmodel.highestTransaction.observe(viewLifecycleOwner) {
//            val name = it?.Receiver ?: "No Data"
//            val amount = it?.Amount ?: "0.00"
//            binding.cdHighestTransactionValue.text = "₹$amount"
//            binding.cdHighestTransactionTo.text = "$name"
//        }
//
//        viewmodel.totalTransactionCount.observe(viewLifecycleOwner) {
//            binding.cdTotalTransactionsValue.text = "$it"
//        }
//
//        viewmodel.mostFrequentReceiver.observe(viewLifecycleOwner) {
//            val (receiver, totalSent) = it
//            binding.cdFrequentPayeeValue.text = "$receiver"
//            binding.cdFrequentPayeeAmount.text = "$totalSent"
//        }









        return binding.root
    }


}