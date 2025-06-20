package com.example.expenzo.Ui.Fragments

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.expenzo.Model.FetchCurrentDayDataModel7days
import com.example.expenzo.R
import com.example.expenzo.ViewModel.Fetch7daysTransViewModel
import com.example.expenzo.databinding.FragmentAnalyticBinding
import com.example.expenzo.databinding.FragmentHomeFragmentsBinding
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class AnalyticFragment : Fragment() {

    private var _binding: FragmentAnalyticBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: Fetch7daysTransViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAnalyticBinding.inflate(inflater, container, false)

        // Initialize ViewModel
        viewModel = ViewModelProvider(this)[Fetch7daysTransViewModel::class.java]

        setupObservers()
        setupBarChart()

        // Trigger data fetching - replace with your actual data model
        val fetchData = FetchCurrentDayDataModel7days(userId = "684bbadc62bc05d171ab1175") // Initialize with your required parameters
        viewModel.showTransaction7daysVm(fetchData)

        return binding.root
    }

    private fun setupObservers() {
        // Observe daily transaction totals and update bar chart
        viewModel.dailyTransactionTotals.observe(viewLifecycleOwner) { dailyTotals ->
            Log.d("Analytics", "Observer triggered with data: $dailyTotals")
            updateBarChart(dailyTotals)
        }

        // Observe other data for displaying in cards
        viewModel.totalAmount.observe(viewLifecycleOwner) { total ->
            binding.tvTotalAmount.text = "₹${String.format("%.2f", total)}"
        }

        viewModel.totalTransactionCount.observe(viewLifecycleOwner) { count ->
            binding.tvTransactionCount.text = count.toString()
        }

        viewModel.highestTransaction.observe(viewLifecycleOwner) { highest ->
            binding.tvHighestAmount.text = "₹${highest?.Amount ?: "0"}"
        }

        viewModel.mostFrequentReceiver.observe(viewLifecycleOwner) { receiver ->
            binding.tvMostFrequentReceiver.text = receiver?.first ?: "N/A"
        }
    }

    private fun setupBarChart() {
        binding.barChart.apply {
            description.isEnabled = false
            setTouchEnabled(true)
            isDragEnabled = true
            setScaleEnabled(true)
            setPinchZoom(false)
            setDrawBarShadow(false)
            setDrawValueAboveBar(true)

            // Customize appearance
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                granularity = 1f
                labelCount = 7
                textColor = Color.GRAY
                textSize = 10f
            }

            axisLeft.apply {
                setDrawGridLines(true)
                gridColor = Color.LTGRAY
                textColor = Color.GRAY
                textSize = 10f
            }

            axisRight.isEnabled = false
            legend.isEnabled = false

            // Animation
            animateY(1200, Easing.EaseInOutQuart)
        }
    }

    private fun updateBarChart(dailyTotals: List<Pair<String, Double>>) {
        Log.d("Analytics", "updateBarChart called with data: $dailyTotals")

        if (dailyTotals.isEmpty()) {
            Log.d("Analytics", "No data to display in chart")
            return
        }

        val entries = ArrayList<BarEntry>()
        val labels = ArrayList<String>()

        dailyTotals.forEachIndexed { index, (date, amount) ->
            entries.add(BarEntry(index.toFloat(), amount.toFloat()))
            labels.add(date) // Just use the date as is
            Log.d("Analytics", "Added entry: Index=$index, Date=$date, Amount=$amount")
        }

        val dataSet = BarDataSet(entries, "Daily Transactions").apply {
            colors = listOf(
                Color.parseColor("#FF6B6B"), // Red
                Color.parseColor("#4ECDC4"), // Teal
                Color.parseColor("#45B7D1"), // Blue
                Color.parseColor("#96CEB4"), // Green
                Color.parseColor("#FECA57"), // Yellow
                Color.parseColor("#FF9FF3"), // Pink
                Color.parseColor("#54A0FF")  // Light Blue
            )
            valueTextColor = Color.BLACK
            valueTextSize = 12f
            setDrawValues(true)
        }

        val barData = BarData(dataSet).apply {
            barWidth = 0.6f
        }

        binding.barChart.apply {
            data = barData
            xAxis.valueFormatter = IndexAxisValueFormatter(labels)
            invalidate() // Refresh chart
        }

        Log.d("Analytics", "Chart updated successfully with ${entries.size} entries")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}