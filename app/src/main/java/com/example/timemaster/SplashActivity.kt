package com.example.timemaster

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import com.example.timemaster.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {
    var handler: Handler? = null
    var runnable: Runnable? = null
    var deviceLocale = ""
    lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        redirectToNewScreen()


    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (handler != null && runnable != null) handler!!.removeCallbacks(runnable!!)
    }

    private fun redirectToNewScreen() {
        handler = Handler()
        runnable = Runnable {
//            !TextUtils.isEmpty(getSharedPreferences("Register", MODE_PRIVATE).getString("user",""))
            val sharedPreferences = getSharedPreferences("Register", MODE_PRIVATE)
            val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
            if (isLoggedIn) {
                startActivity(
                    Intent(this@SplashActivity, MainActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                )
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            } else {
                startActivity(
                    Intent(this@SplashActivity, LoginPage::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                )
                overridePendingTransition(R.anim.splash_fade_in, R.anim.splash_fade_out)
            }
        }
        handler!!.postDelayed(runnable!!, 2000)
    }


}