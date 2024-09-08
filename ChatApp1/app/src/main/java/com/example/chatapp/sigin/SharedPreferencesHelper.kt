package com.example.chatapp.sigin

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

class SharedPreferencesHelper(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

    fun isLoggedIn(): Boolean {
        Log.e("SharedPreferencesHelper", "isLoggedIn: ${sharedPreferences.getBoolean("isLoggedIn", false)}")
        return sharedPreferences.getBoolean("isLoggedIn", false)
    }

    fun setLoggedIn(isLoggedIn: Boolean) {
        sharedPreferences.edit().putBoolean("isLoggedIn", isLoggedIn).apply()
        Log.e("SharedPreferencesHelper", "setLoggedIn: $isLoggedIn")

    }
}