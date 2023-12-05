@file:Suppress("DEPRECATION")

package com.example.project.onboarding

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.fragment.app.FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
import androidx.viewpager.widget.ViewPager
import com.example.project.MainActivity
import com.example.project.R
import com.example.project.onboarding.adapter.WelcomePagerAdapter

class WelcomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        if (prefs.getBoolean("firstTime", true)) {
            // Show welcome screens
            setContentView(R.layout.activity_welcome)
            val viewPager: ViewPager = findViewById(R.id.viewPager)
            val pagerAdapter = WelcomePagerAdapter(supportFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT)
            viewPager.adapter = pagerAdapter

            // Once the user has seen the welcome screens, update the preference
            prefs.edit().putBoolean("firstTime", false).apply()
        } else {
            // Skip to main activity for users who have already seen the welcome screens
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}