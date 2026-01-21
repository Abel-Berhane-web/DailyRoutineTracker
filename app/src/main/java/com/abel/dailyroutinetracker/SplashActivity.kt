package com.abel.dailyroutinetracker

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Delay for 3 seconds (3000ms) then move to Login/Main
        Handler(Looper.getMainLooper()).postDelayed({
            // Check logic to go to Login or Main is handled in LoginActivity normally, 
            // but here we just go to LoginActivity which has the check logic.
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }, 3000)
    }
}