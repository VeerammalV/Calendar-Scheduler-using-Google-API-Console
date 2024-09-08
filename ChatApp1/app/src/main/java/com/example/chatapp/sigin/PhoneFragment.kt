package com.example.chatapp.sigin

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.chatapp.R
import com.example.chatapp.chatroom.ChatActivity
import com.example.chatapp.databinding.FragmentPhoneBinding

class PhoneFragment : Fragment() {

    private lateinit var binding: FragmentPhoneBinding
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPhoneBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferencesHelper = SharedPreferencesHelper(requireContext())
        if (sharedPreferencesHelper.isLoggedIn()) {
            startActivity(Intent(requireContext(), ChatActivity::class.java))
            requireActivity().finish()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.generateOtp.setOnClickListener {
        if (isValidPhoneNumber()) {
            numberVerified()
        }
      }
    }

private fun numberVerified() {
    val otpFragment = OtpFragment()
    requireActivity().supportFragmentManager.beginTransaction()
        .replace(R.id.otp_fragment, otpFragment)
        .commit()
}

private fun isValidPhoneNumber(): Boolean {
    val phoneNumber = binding.mobileNumber.text.toString().trim()
    return when {
        phoneNumber.isEmpty() -> {
            binding.mobileNumber.error = "Please enter Phone Number"
            false
        }
        phoneNumber.length != 10 -> {
            binding.mobileNumber.error = "Please enter a valid 10-digit Phone Number"
            false
        }
        else -> true
    }
  }
}