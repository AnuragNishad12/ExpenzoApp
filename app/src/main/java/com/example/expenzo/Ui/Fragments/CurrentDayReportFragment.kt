package com.example.expenzo.Ui.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.expenzo.Adapter.CurrentDayTrasactionAdapter
import com.example.expenzo.Adapter.TransactionAdapter
import com.example.expenzo.Model.FetchCurrentDayDataModel
import com.example.expenzo.Model.FetchCurrentDayDataModel30days
import com.example.expenzo.Model.FetchCurrentDayDataModel7days
import com.example.expenzo.R
import com.example.expenzo.Utils.UserDataStore
import com.example.expenzo.ViewModel.TransactionCurrentDayViewModel
import com.example.expenzo.databinding.FragmentCurrentDayReportBinding
import com.example.expenzo.databinding.FragmentReportBinding
import kotlinx.coroutines.launch


class CurrentDayReportFragment : Fragment() {

    private var _binding: FragmentCurrentDayReportBinding? = null
    private val binding get() = _binding!!

    private lateinit var transVm : TransactionCurrentDayViewModel
    private lateinit var transactionAdapter : CurrentDayTrasactionAdapter




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCurrentDayReportBinding.inflate(inflater,container,false)
        transVm = ViewModelProvider(this)[TransactionCurrentDayViewModel::class.java]

        val userDataStore = UserDataStore(requireContext())
        viewLifecycleOwner.lifecycleScope.launch {
            val uniqueName = userDataStore.getUniqueName()
            // use uniqueName here
            val request = FetchCurrentDayDataModel(userId = uniqueName.toString())
            transVm.showTransactionCurrentDayVm(request)

            Log.d("Fragment", "Unique Name: $uniqueName")
        }




        transVm = ViewModelProvider(this)[TransactionCurrentDayViewModel::class.java]

        binding.currentdayrecyclerview.layoutManager = LinearLayoutManager(requireContext())

        transVm.transactions.observe(viewLifecycleOwner) { transactionList ->
            transactionAdapter = CurrentDayTrasactionAdapter(transactionList)
            binding.currentdayrecyclerview.adapter = transactionAdapter
            binding.progressBar3.visibility = View.GONE
        }




        return binding.root
    }


}