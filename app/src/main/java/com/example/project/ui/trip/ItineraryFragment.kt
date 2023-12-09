package com.example.project.ui.trip

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.project.databinding.FragmentItineraryBinding
import com.example.project.models.ItineraryDay
import com.example.project.models.Trip
import com.google.android.material.tabs.TabLayoutMediator
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ItineraryFragment : Fragment() {

    private lateinit var binding: FragmentItineraryBinding
    private lateinit var trip: Trip

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentItineraryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        trip = arguments?.getSerializable("trip") as Trip
        setupViewPagerWithTabs()
    }

    private fun setupViewPagerWithTabs() {
        val adapter = ItineraryDayAdapter(this, trip.itineraryDays)
        binding.itineraryViewPager.adapter = adapter

        TabLayoutMediator(binding.itineraryTabs, binding.itineraryViewPager) { tab, position ->
            tab.text = trip.itineraryDays[position].date?.let { formatDate(it) }
        }.attach()
    }

    private fun formatDate(date: Date): String {
        val dayFormat = SimpleDateFormat("d", Locale.US)
        val dayInMonthWithSuffix = when (val dayInMonth = dayFormat.format(date).toInt()) {
            1, 21, 31 -> "${dayInMonth}st"
            2, 22 -> "${dayInMonth}nd"
            3, 23 -> "${dayInMonth}rd"
            else -> "${dayInMonth}th"
        }
        val monthAndYearFormat = SimpleDateFormat(" MMM, yyyy", Locale.US)
        return dayInMonthWithSuffix + monthAndYearFormat.format(date)
    }
}

class ItineraryDayAdapter(fragment: Fragment, private val days: List<ItineraryDay>) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = days.size

    override fun createFragment(position: Int): Fragment {
        val day = days[position]
        return DayWiseItineraryFragment.newInstance(day)
    }
}
