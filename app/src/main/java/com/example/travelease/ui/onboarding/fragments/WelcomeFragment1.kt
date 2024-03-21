package com.example.travelease.ui.onboarding.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.viewpager.widget.ViewPager
import com.example.travelease.MainActivity
import com.example.travelease.R



class WelcomeFragment1 : Fragment() {
    private lateinit var skipText: TextView
    private lateinit var next: ImageButton
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_welcome1, container, false)
        skipText = view.findViewById(R.id.skipText)
        next = view.findViewById(R.id.btn_welcomeP1)

        skipText.setOnClickListener {
            // Define the click behavior here
            // For example, you can start the MainActivity like this:
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
        }

        val viewPager = requireActivity().findViewById<ViewPager>(R.id.viewPager)
        next.setOnClickListener {
            // Navigate to the next fragment (WelcomeFragment2)
            viewPager.currentItem = viewPager.currentItem + 1
        }

        return view
    }

    companion object {

    }
}