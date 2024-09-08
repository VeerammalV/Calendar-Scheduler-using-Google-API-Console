package com.example.chatapp.sigin

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.chatapp.R
import com.example.chatapp.chatroom.ChatActivity
import com.example.chatapp.databinding.SigninMainBinding

class SigninActivity : AppCompatActivity() {
    private lateinit var binding: SigninMainBinding
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = SigninMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferencesHelper = SharedPreferencesHelper(this)

        if (sharedPreferencesHelper.isLoggedIn()) {
            startActivity(Intent(this, ChatActivity::class.java))
            finish()
            return
        }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().add(R.id.otp_fragment, PhoneFragment()).commitAllowingStateLoss()
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.signin) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
