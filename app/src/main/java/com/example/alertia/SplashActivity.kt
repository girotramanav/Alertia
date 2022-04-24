package com.example.alertia

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.alertia.databinding.ActivitySplashBinding

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imageView.scaleX = 0f
        binding.imageView.scaleY = 0f
        binding.imageView.animate().setDuration(500).scaleX(1.1f)
        binding.imageView.animate().setDuration(500).scaleY(1.1f).withEndAction{
            binding.imageView.animate().setDuration(200).scaleX(1f)
            binding.imageView.animate().setDuration(200).scaleY(1f)
        }

        Handler().postDelayed ({
            val intent = Intent(this, WelcomeActivity::class.java)
            startActivity(intent)
            finish()
        },3000)
    }
}