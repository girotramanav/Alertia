package com.example.alertia

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.example.alertia.databinding.ActivitySplashBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySplashBinding
    private lateinit var mAuth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mAuth = Firebase.auth
        val currentUser = mAuth.currentUser

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
            val intent : Intent = if(currentUser==null)
                Intent(this, WelcomeActivity::class.java)
            else
                Intent(this,MapsActivity::class.java)
            startActivity(intent)
            finish()
        },3000)
    }
}