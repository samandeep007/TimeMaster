package com.example.timemaster

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.timemaster.databinding.ActivityLoginPageBinding

class LoginPage : AppCompatActivity() {
    lateinit var binding: ActivityLoginPageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginPageBinding.inflate(layoutInflater)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(binding.root)
        binding.txtRegisterNow.setOnClickListener {
            startActivity(Intent(this, RegistrationPage::class.java))
        }

        val sharedPreferences = getSharedPreferences("Register", MODE_PRIVATE)
        val spName = sharedPreferences.getString("user", "")
        val spPassword = sharedPreferences.getString("password", "")


        binding.btnLogin.setOnClickListener {

            val name = binding.edtName.text.toString()
            val password = binding.edtPassword.text.toString()

            if (name.isNotEmpty() && password.isNotEmpty()) {
                if (name == spName) {
                    if (password == spPassword) {
                        val editor = sharedPreferences.edit()
                        editor.putBoolean("isLoggedIn", true)
                        editor.apply()
                        startActivity(Intent(this, MainActivity::class.java))
                    } else {
                        Toast.makeText(this, "Please enter valid password", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    if (name.isEmpty()) {
                        Toast.makeText(this, "Please enter username.", Toast.LENGTH_SHORT).show()
                    } else if (name != spName) {
                        Toast.makeText(this, "Please enter valid username.", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }
    }

}