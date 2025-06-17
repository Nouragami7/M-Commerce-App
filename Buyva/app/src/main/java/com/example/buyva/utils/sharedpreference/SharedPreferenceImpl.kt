package com.example.buyva.utils.sharedpreference

import android.content.Context
import android.util.Log

object SharedPreferenceImpl : SharedPreference {

    private const val PREF_NAME = "shared_pref"

    override fun saveToSharedPreference(context: Context, key: String, value: String) {
        Log.d("TAG", "saveToSharedPreference: $value")
        val sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sharedPref.edit().apply {
            putString(key, value)
            apply()
        }
    }

    override fun getFromSharedPreference(context: Context, key: String): String? {
        val sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPref.getString(key, null)
    }

    override fun deleteSharedPreference(context: Context, key: String) {
        val sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sharedPref.edit().apply {
            remove(key)
            apply()
        }
    }
}
