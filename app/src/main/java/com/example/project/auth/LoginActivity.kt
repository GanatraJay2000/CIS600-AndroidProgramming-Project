package com.example.project.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.example.project.MainActivity
import com.example.project.databinding.ActivityLoginBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.auth


class LoginActivity : AppCompatActivity() {

    private val auth = Firebase.auth
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val email = binding.emailField.editText?.text
        val password = binding.passwordField.editText?.text


        binding.linkToSignUp.setOnClickListener { startActivity(Intent(this, SignUpActivity::class.java)) }


        binding.loginButton.setOnClickListener {
            if(email == null || password == null) Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            else login(email.toString(), password.toString())
        }

        //close app on back press
        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finishAffinity()
            }
        })
    }



    private fun login(email: String, password: String) {
        if(email == "" || password == "") {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            // focus email field
            binding.emailField.requestFocus()
        }
        else {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        Toast.makeText(this, "Welcome ${user?.email}", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, MainActivity::class.java))
                    } else {
                        Toast.makeText(
                            baseContext,
                            "Authentication failed.",
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                }
        }
    }
}