@file:Suppress("DEPRECATION")

package com.example.travelease

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.preference.PreferenceManager
import android.view.animation.AnimationUtils
import android.widget.ImageView
import com.example.travelease.ui.onboarding.WelcomeActivity


class StarterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_starter)

        val imageView = findViewById<ImageView>(R.id.animationImageView)
        val rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate_around_center)
        imageView.startAnimation(rotateAnimation)

        // Determine if this is the first time running the app
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val isFirstRun = prefs.getBoolean("isFirstRun", true)

        Handler(Looper.getMainLooper()).postDelayed({
            imageView.clearAnimation()
            if (isFirstRun) {
                // Update the isFirstRun flag so this path is not taken again
                prefs.edit().putBoolean("isFirstRun", false).apply()

                // Go to WelcomeActivity since this is the first run
                val welcomeIntent = Intent(this, WelcomeActivity::class.java)
                startActivity(welcomeIntent)
            } else {
                // Go to MainActivity since this is not the first run
                val mainIntent = Intent(this, MainActivity::class.java)
                startActivity(mainIntent)
            }
            finish()
        }, 1500)
    }
}

/*
class StarterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_starter)

        val imageView = findViewById<ImageView>(R.id.animationImageView)
        val rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate_around_center)
        imageView.startAnimation(rotateAnimation)

        Handler(Looper.getMainLooper()).postDelayed({
            imageView.clearAnimation()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }, 3000)
    }
}*/
