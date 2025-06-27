package com.example.buyva.features

import android.app.Application
import android.util.Log
import com.example.buyva.utils.sharedpreference.SharedPreferenceImpl
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            Log.e("GlobalCrash", "Uncaught: ${throwable.message}", throwable)
        }
        SharedPreferenceImpl.init(this)
    }
}
