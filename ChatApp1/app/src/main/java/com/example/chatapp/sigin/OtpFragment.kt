package com.example.chatapp.sigin

import CameraFragment
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.chatapp.R
import com.example.chatapp.chatroom.ChatActivity
import com.example.chatapp.databinding.FragmentOtpBinding

class OtpFragment : Fragment() {

    private lateinit var binding: FragmentOtpBinding
    private lateinit var sharedPreferencesHelper: SharedPreferencesHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOtpBinding.inflate(inflater, container, false)
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

        binding.otpVerify.visibility = View.VISIBLE
        binding.popup.visibility = View.GONE
        binding.greenTick.visibility = View.GONE
        binding.textSuccessful.visibility = View.GONE
        binding.verifyButton.setOnClickListener {
            val enteredOtp = binding.otp.text.toString().trim()
            if (enteredOtp == "1234") {
                popupVerified()
            } else {
                if (enteredOtp.isEmpty()) {
                    Toast.makeText(requireContext(), "Please enter the OTP", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Incorrect OTP", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun popupVerified() {
        binding.popup.visibility = View.VISIBLE
        binding.greenTick.visibility = View.VISIBLE
        binding.textSuccessful.visibility = View.VISIBLE
        Handler(Looper.getMainLooper()).postDelayed({
            binding.popup.visibility = View.GONE
            binding.greenTick.visibility = View.GONE
            binding.textSuccessful.visibility = View.GONE


            val cameraFragment = CameraFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.otp_fragment, cameraFragment)
                .commitAllowingStateLoss()
        }, 1000)
    }
}



