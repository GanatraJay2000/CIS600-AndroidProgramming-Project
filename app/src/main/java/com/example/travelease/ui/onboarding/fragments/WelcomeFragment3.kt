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
import androidx.viewpager2.widget.ViewPager2
import com.example.travelease.MainActivity
import com.example.travelease.R

class WelcomeFragment3 : Fragment() {

    private lateinit var skipText: TextView
    private lateinit var next: ImageButton
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_welcome3, container, false)
        skipText = view.findViewById(R.id.skipText3)
        next = view.findViewById(R.id.btn_welcomeP3)

        skipText.setOnClickListener {
            // Define the click behavior here
            // For example, you can start the MainActivity like this:
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
        }

        val viewPager = requireActivity().findViewById<ViewPager>(R.id.viewPager)
        next.setOnClickListener {
            // Navigate to the next fragment (WelcomeFragment2)
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
        }

        return view
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            WelcomeFragment3().apply {
                arguments = Bundle().apply {
                }
            }
    }
}