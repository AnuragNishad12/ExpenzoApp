package com.example.expenzo.Ui.Fragments

import android.animation.ObjectAnimator
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.expenzo.R
import com.example.expenzo.databinding.FragmentHomeFragmentsBinding
import com.example.expenzo.databinding.FragmentReportBinding


class ReportFragment : Fragment() {

    private var _binding: FragmentReportBinding? = null
    private val binding get() = _binding!!

    private var currentSelectedCard = 0 // 0: Current, 1: 7 Days, 2: 30 Days

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentReportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewPager()
        setupClickListeners()

        // Set initial selection
        selectCard(0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupViewPager() {
        val adapter = ReportPagerAdapter(this)
        binding.viewPagerReport.adapter = adapter

        // Synchronize ViewPager with card selection
        binding.viewPagerReport.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                selectCard(position)
            }
        })
    }

    private fun setupClickListeners() {
        binding.cardCurrent.setOnClickListener {
            animateCardClick(binding.cardCurrent)
            selectCard(0)
            binding.viewPagerReport.currentItem = 0
        }

        binding.cardSeven.setOnClickListener {
            animateCardClick(binding.cardSeven)
            selectCard(1)
            binding.viewPagerReport.currentItem = 1
        }

        binding.cardThirty.setOnClickListener {
            animateCardClick(binding.cardThirty)
            selectCard(2)
            binding.viewPagerReport.currentItem = 2
        }
    }

    private fun selectCard(cardIndex: Int) {
        if (currentSelectedCard == cardIndex) return

        currentSelectedCard = cardIndex

        // Reset all cards to inactive state
        resetCardAppearance(binding.cardCurrent, binding.currentDayText, binding.currentDayIcon, binding.currentDayIndicator)
        resetCardAppearance(binding.cardSeven, binding.sevenDaysText, binding.sevenDaysIcon, binding.sevenDaysIndicator)
        resetCardAppearance(binding.cardThirty, binding.thirtyDaysText, binding.thirtyDaysIcon, binding.thirtyDaysIndicator)

        // Activate selected card
        when (cardIndex) {
            0 -> activateCard(binding.cardCurrent, binding.currentDayText, binding.currentDayIcon, binding.currentDayIndicator)
            1 -> activateCard(binding.cardSeven, binding.sevenDaysText, binding.sevenDaysIcon, binding.sevenDaysIndicator)
            2 -> activateCard(binding.cardThirty, binding.thirtyDaysText, binding.thirtyDaysIcon, binding.thirtyDaysIndicator)
        }
    }

    private fun activateCard(card: CardView, textView: TextView, icon: ImageView, indicator: View) {
        // Animate card selection
        animateCardSelection(card, true)

        // Update colors
        card.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.card_selected))
        textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.orange_light))
        icon.imageTintList = ContextCompat.getColorStateList(requireContext(), R.color.orange_primary)

        // Show and animate indicator
        indicator.visibility = View.VISIBLE
        indicator.setBackgroundResource(R.drawable.indicator_background)
        animateIndicator(indicator, true)
    }

    private fun resetCardAppearance(card: CardView, textView: TextView, icon: ImageView, indicator: View) {
        // Animate card deselection
        animateCardSelection(card, false)

        // Reset colors
        card.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.card_background))
        textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary))
        icon.imageTintList = ContextCompat.getColorStateList(requireContext(), R.color.secondary_color)

        // Hide indicator
        indicator.visibility = View.INVISIBLE
        animateIndicator(indicator, false)
    }

    private fun animateCardClick(card: CardView) {
        // Scale animation for click feedback
        val scaleDown = ObjectAnimator.ofFloat(card, "scaleX", 1.0f, 0.95f).apply {
            duration = 100
        }
        val scaleUp = ObjectAnimator.ofFloat(card, "scaleX", 0.95f, 1.0f).apply {
            duration = 100
            startDelay = 100
        }

        val scaleDownY = ObjectAnimator.ofFloat(card, "scaleY", 1.0f, 0.95f).apply {
            duration = 100
        }
        val scaleUpY = ObjectAnimator.ofFloat(card, "scaleY", 0.95f, 1.0f).apply {
            duration = 100
            startDelay = 100
        }

        scaleDown.start()
        scaleUp.start()
        scaleDownY.start()
        scaleUpY.start()
    }

    private fun animateCardSelection(card: CardView, isSelected: Boolean) {
        val elevation = if (isSelected) 8f else 2f
        val scale = if (isSelected) 1.02f else 1.0f

        ObjectAnimator.ofFloat(card, "cardElevation", card.cardElevation, elevation).apply {
            duration = 200
            start()
        }

        ObjectAnimator.ofFloat(card, "scaleX", card.scaleX, scale).apply {
            duration = 200
            start()
        }

        ObjectAnimator.ofFloat(card, "scaleY", card.scaleY, scale).apply {
            duration = 200
            start()
        }
    }

    private fun animateIndicator(indicator: View, show: Boolean) {
        val alpha = if (show) 1.0f else 0.0f
        val scaleX = if (show) 1.0f else 0.0f

        ObjectAnimator.ofFloat(indicator, "alpha", indicator.alpha, alpha).apply {
            duration = 300
            start()
        }

        ObjectAnimator.ofFloat(indicator, "scaleX", indicator.scaleX, scaleX).apply {
            duration = 300
            start()
        }
    }
}

// ReportPagerAdapter.kt
class ReportPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> CurrentDayReportFragment()
            1 -> SevenDaysReportFragment()
            2 -> ThirtyDaysReportFragment()
            else -> CurrentDayReportFragment()
        }
    }
}