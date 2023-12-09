package com.example.project.auth

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import com.example.project.MainActivity
import com.example.project.R
import com.example.project.databinding.ActivityLoginBinding
import com.example.project.databinding.ActivitySignUpBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.auth

class SignUpActivity : AppCompatActivity() {

    private val auth = Firebase.auth
    private lateinit var binding: ActivitySignUpBinding

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val name = binding.nameField.editText?.text
        val email = binding.emailField.editText?.text
        val password = binding.passwordField.editText?.text

        binding.linkToLogin.setOnClickListener { startActivity(Intent(this, LoginActivity::class.java)) }

        binding.signupButton.setOnClickListener {
            //check if editText fields are empty
            if(name == null || email == null || password == null) Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            else signup(name.toString(), email.toString(), password.toString())
        }

        //close app on back press
        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finishAffinity()
            }
        })
    }

    private fun signup(name: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(name)
                        .build()
                    user?.updateProfile(profileUpdates)
                        ?.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(this, "Welcome ${user?.displayName}", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this, MainActivity::class.java))
                            }else{
                                Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
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

