package com.example.expenzo.Ui.Fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.expenzo.R
import com.example.expenzo.Ui.Analytic7days
import com.example.expenzo.Ui.Analytics30days
import com.example.expenzo.databinding.FragmentManyAnalyticsFragments2Binding
import com.example.expenzo.databinding.FragmentSevenDaysReportBinding


class ManyAnalyticsFragments : Fragment() {

    private var _binding: FragmentManyAnalyticsFragments2Binding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentManyAnalyticsFragments2Binding.inflate(inflater,container,false)

        binding.card7Days.setOnClickListener {
            startActivity(Intent(requireActivity(), Analytic7days::class.java))
        }

        binding.card30Days.setOnClickListener {
            startActivity(Intent(requireActivity(), Analytics30days::class.java))
        }

        return binding.root
    }


}