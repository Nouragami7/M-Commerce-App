package com.example.buyva.features

import android.app.Application
import com.example.buyva.utils.sharedpreference.SharedPreferenceImpl

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        SharedPreferenceImpl.init(this)
    }
}
