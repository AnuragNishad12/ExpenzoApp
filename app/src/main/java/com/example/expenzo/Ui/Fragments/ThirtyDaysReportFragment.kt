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
import com.example.expenzo.Adapter.Transaction30daysAdapter
import com.example.expenzo.Adapter.TransactionAdapter
import com.example.expenzo.Adapter.Trasnaction7daysAdapter
import com.example.expenzo.Model.FetchCurrentDayDataModel
import com.example.expenzo.Model.FetchCurrentDayDataModel30days
import com.example.expenzo.Model.FetchCurrentDayDataModel7days
import com.example.expenzo.R
import com.example.expenzo.Utils.UserDataStore
import com.example.expenzo.ViewModel.Fetch30daysTransViewModel
import com.example.expenzo.ViewModel.Fetch7daysTransViewModel
import com.example.expenzo.databinding.FragmentThirtyDaysReportBinding
import kotlinx.coroutines.launch


class ThirtyDaysReportFragment : Fragment() {


    private  var _binding : FragmentThirtyDaysReportBinding? =null
    private val binding get() = _binding!!
    private lateinit var transactionAdapter : Transaction30daysAdapter
    private lateinit var viewmodel :Fetch30daysTransViewModel



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentThirtyDaysReportBinding.inflate(inflater,container,false)

        viewmodel = ViewModelProvider(this)[Fetch30daysTransViewModel::class.java]

        binding.recyclerview.layoutManager = LinearLayoutManager(requireContext())

        viewmodel.transactions.observe(viewLifecycleOwner) { transactionList ->
            transactionAdapter = Transaction30daysAdapter(transactionList)
            binding.recyclerview.adapter = transactionAdapter
            binding.progressBar4.visibility = View.GONE
        }

        val userDataStore = UserDataStore(requireContext())
        viewLifecycleOwner.lifecycleScope.launch {
            val uniqueName = userDataStore.getUniqueName()
            // use uniqueName here

            val request = FetchCurrentDayDataModel30days(userId = uniqueName.toString())
            viewmodel.showTransaction30daysVm(request)

            Log.d("Fragment", "Unique Name: $uniqueName")
        }






        return binding.root
    }
}