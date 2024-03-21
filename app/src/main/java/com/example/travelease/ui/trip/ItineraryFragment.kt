package com.example.travelease.ui.trip

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.travelease.R
import com.example.travelease.databinding.FragmentItineraryBinding
import com.example.travelease.helper.dialogs.addPlace.AddPlaceToDayFragment
import com.example.travelease.helper.dialogs.search.SearchBottomSheetFragment
import com.example.travelease.model.ItineraryDay
import com.example.travelease.model.Trip
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
        val adapter = ItineraryDayAdapter(this, trip.itineraryDays!!)
        binding.itineraryViewPager.adapter = adapter
        binding.addPlaceToDayButton.setOnClickListener {
            val addPlaceToDayFragment = AddPlaceToDayFragment()
            addPlaceToDayFragment.show(parentFragmentManager, addPlaceToDayFragment.tag)
            val searchBottomSheetFragment = SearchBottomSheetFragment()
            searchBottomSheetFragment.show(parentFragmentManager, searchBottomSheetFragment.tag)
            navigateToDestination(R.id.nav_trip)
        }
        TabLayoutMediator(binding.itineraryTabs, binding.itineraryViewPager) { tab, position ->
            tab.text = formatDate(trip.itineraryDays!![position].date)
        }.attach()
    }

    private fun formatDate(date: Date): String {
        val dayFormat = SimpleDateFormat("d", Locale.US)
        val dayInMonth = dayFormat.format(date).toInt()
        val dayInMonthWithSuffix = when (dayInMonth) {
            1, 21, 31 -> "${dayInMonth}st"
            2, 22 -> "${dayInMonth}nd"
            3, 23 -> "${dayInMonth}rd"
            else -> "${dayInMonth}th"
        }
        val monthAndYearFormat = SimpleDateFormat(" MMM, yyyy", Locale.US)
        return dayInMonthWithSuffix + monthAndYearFormat.format(date)
    }

    fun navigateToDestination(destinationId: Int) {
        val navController = findNavController()
        val currentDestination = navController.currentDestination?.id

        if (currentDestination != destinationId) {
            val navOptions = NavOptions.Builder()
                .setPopUpTo(navController.graph.startDestinationId, false)
                .setEnterAnim(R.anim.slide_in_right)
                .setExitAnim(R.anim.slide_out_left)
                .build()
            navController.navigate(destinationId, null, navOptions)
        }
    }
}


class ItineraryDayAdapter(fragment: Fragment, private val days: List<ItineraryDay>) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = days.size

    override fun createFragment(position: Int): Fragment {
        val day = days[position]
        return DayWiseItineraryFragment.newInstance(day)
    }
}
