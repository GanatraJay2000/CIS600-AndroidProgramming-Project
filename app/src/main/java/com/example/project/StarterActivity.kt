package com.example.project

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import android.widget.ImageView

class StarterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_starter)

        val imageView = findViewById<ImageView>(R.id.animationImageView)
        val rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate_around_center)
        imageView.startAnimation(rotateAnimation)

        Handler(Looper.getMainLooper()).postDelayed({
            imageView.clearAnimation()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, 3000)
    }
}