package com.example.buyva.features.authentication.login.viewmodel

import android.content.Context
import android.content.SharedPreferences

object UserSessionManager {
    private const val PREF_NAME = "user_session_prefs"
    private const val KEY_GUEST_MODE = "guest_mode"
    private lateinit var preferences: SharedPreferences

    fun init(context: Context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun setGuestMode(value: Boolean) {
        preferences.edit().putBoolean(KEY_GUEST_MODE, value).apply()
    }

    fun isGuest(): Boolean {
        return preferences.getBoolean(KEY_GUEST_MODE, false)
    }
}
