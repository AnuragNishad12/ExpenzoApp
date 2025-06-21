package com.example.expenzo.Ui.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.expenzo.Adapter.CurrentDayTrasactionAdapter
import com.example.expenzo.Adapter.TransactionAdapter
import com.example.expenzo.Model.FetchCurrentDayDataModel
import com.example.expenzo.R
import com.example.expenzo.ViewModel.TransactionCurrentDayViewModel
import com.example.expenzo.databinding.FragmentCurrentDayReportBinding
import com.example.expenzo.databinding.FragmentReportBinding


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

        val request = FetchCurrentDayDataModel(userId = "684bbadc62bc05d171ab1175")
        transVm.showTransactionCurrentDayVm(request)

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