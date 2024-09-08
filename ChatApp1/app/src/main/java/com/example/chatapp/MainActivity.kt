package com.example.chatapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.chatapp.chatroom.ChatActivity
import com.example.chatapp.databinding.ActivityMainBinding
import com.example.chatapp.sigin.SharedPreferencesHelper
import com.example.chatapp.sigin.SigninActivity


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)

        sharedPreferencesHelper = SharedPreferencesHelper(this)

        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { true }

        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (sharedPreferencesHelper.isLoggedIn()) {
            startActivity(Intent(this, ChatActivity::class.java))
            Log.e("Activity", "Chat Activity")
        } else {
            startActivity(Intent(this, SigninActivity::class.java))
            Log.e("Activity", "Sign In Activity")
        }
        finish()
    }
    override fun onResume() {
        super.onResume()
        if (sharedPreferencesHelper.isLoggedIn()) {
            Log.e("Logged in", "Logged in")
        } else {
            Log.e("Not Logged in", "Not Logged in")
        }
    }

}