package com.example.expenzo.Ui

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.expenzo.Model.FetchCurrentDayDataModel30days
import com.example.expenzo.Model.FetchCurrentDayDataModel7days
import com.example.expenzo.R
import com.example.expenzo.Utils.UserDataStore
import com.example.expenzo.ViewModel.Fetch30daysTransViewModel
import com.example.expenzo.ViewModel.Fetch7daysTransViewModel
import com.example.expenzo.databinding.ActivityAnalytics30daysBinding
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import kotlinx.coroutines.launch

class Analytics30days : AppCompatActivity() {

    private lateinit var binding: ActivityAnalytics30daysBinding
    private lateinit var viewModel: Fetch30daysTransViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAnalytics30daysBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[Fetch30daysTransViewModel::class.java]

        setupObservers()
        setupBarChart()

        val userDataStore = UserDataStore(this)

        lifecycleScope.launch {
            val userId = userDataStore.getUniqueName() ?: "default_user"
            // use uniqueName here

            val fetchData = FetchCurrentDayDataModel30days(userId = userId) // Initialize with your required parameters
            viewModel.showTransaction30daysVm(fetchData)

            Log.d("Fragment", "Unique Name: $userId")
        }



    }


    private fun setupObservers() {
        // Observe daily transaction totals and update bar chart
        viewModel.dailyTransactionTotals.observe(this) { dailyTotals ->
            Log.d("Analytics", "Observer triggered with data: $dailyTotals")
            updateBarChart(dailyTotals)
        }

        // Observe other data for displaying in cards
        viewModel.totalAmount.observe(this) { total ->
            binding.tvTotalAmount.text = "₹${String.format("%.2f", total)}"
        }

        viewModel.totalTransactionCount.observe(this) { count ->
            binding.tvTransactionCount.text = count.toString()
        }

        viewModel.highestTransaction.observe(this) { highest ->
            binding.tvHighestAmount.text = "₹${highest?.Amount ?: "0"}"
        }

        viewModel.mostFrequentReceiver.observe(this) { receiver ->
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




}