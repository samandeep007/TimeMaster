package com.example.timemaster

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.example.timemaster.databinding.ActivityRegistrationPageBinding

class RegistrationPage : AppCompatActivity() {
    lateinit var binding: ActivityRegistrationPageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationPageBinding.inflate(layoutInflater)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(binding.root)
        binding.txtLoginInHere.setOnClickListener {
            startActivity(Intent(this, LoginPage::class.java))
        }

        val sharedPreferences = getSharedPreferences("Register", MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        binding.btnSignUp.setOnClickListener {

            val name = binding.edtName.text.toString()
            val password = binding.edtPassword.text.toString()
            val email = binding.edtEmail.text.toString()

            if (name.isNotEmpty() && password.isNotEmpty() && email.isNotEmpty()) {
                editor.putString("user", name)
                editor.putString("email", email)
                editor.putString("password", password)
                editor.apply()
                startActivity(Intent(this, LoginPage::class.java))
            }
            else{
                Toast.makeText(this, "Please provide all the required details !", Toast.LENGTH_SHORT).show()
            }

        }


    }
}