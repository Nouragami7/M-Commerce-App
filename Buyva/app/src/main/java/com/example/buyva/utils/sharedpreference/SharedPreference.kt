package com.example.buyva.utils.sharedpreference

import android.content.Context

interface SharedPreference {
    fun saveToSharedPreference(context: Context, key: String, value: String)
    fun getFromSharedPreference(context: Context, key: String): String?
    fun deleteSharedPreference(context: Context, key: String)
}