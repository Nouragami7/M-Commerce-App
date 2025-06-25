package com.example.buyva.features.authentication.login.viewmodel

object UserSessionManager {
    private var isGuestMode: Boolean = false

    fun setGuestMode(value: Boolean) {
        isGuestMode = value
    }

    fun isGuest(): Boolean {
        return isGuestMode
    }
}
