package com.example.chatapp

import android.app.usage.UsageEvents


data class Contact(
    val name: String,
    val number: String,
//    val profilePictureUri: String,
    var events: List<UsageEvents.Event>? = null
)
