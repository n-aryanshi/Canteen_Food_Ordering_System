package com.example.foodease

import android.content.Intent
import android.os.Bundle
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash_screen)
        //this activity will open and will finish after holding 3 sec on the screen and then after 3 sec another activity will appear
        android.os.Handler(Looper.getMainLooper()).postDelayed({
            val intent  = Intent(this, StartActivity::class.java)
            startActivity(intent)
            finish()
        },3000)
    }
}