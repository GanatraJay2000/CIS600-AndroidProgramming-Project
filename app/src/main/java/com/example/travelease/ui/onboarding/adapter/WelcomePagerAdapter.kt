@file:Suppress("DEPRECATION")

package com.example.travelease.ui.onboarding.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.travelease.ui.onboarding.fragments.WelcomeFragment1
import com.example.travelease.ui.onboarding.fragments.WelcomeFragment2
import com.example.travelease.ui.onboarding.fragments.WelcomeFragment3

@Suppress("DEPRECATION")
class WelcomePagerAdapter(fm: FragmentManager, behavior: Int) : FragmentPagerAdapter(fm, behavior) {
    private val pages = listOf(
        WelcomeFragment1(),
        WelcomeFragment2(),
        WelcomeFragment3()
    )

    override fun getCount(): Int = pages.size

    override fun getItem(position: Int): Fragment {
        return pages[position]
    }
}