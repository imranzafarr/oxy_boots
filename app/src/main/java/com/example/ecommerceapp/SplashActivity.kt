package com.example.ecommerceapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)  // Set the layout for the splash screen

        // Adjusts padding to prevent UI elements from overlapping with system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Delays the transition to the main activity for 3 seconds
        Handler(Looper.getMainLooper()).postDelayed({
            // Create an intent to navigate to the MainActivity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent) // Start the MainActivity
            finish()  // Close the SplashActivity so it doesn't stay in the back stack
        },3000)  // 3000 milliseconds (3 seconds) delay
    }
}