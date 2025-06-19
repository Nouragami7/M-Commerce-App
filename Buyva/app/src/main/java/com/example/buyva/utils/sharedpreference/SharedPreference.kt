package com.example.buyva.utils.sharedpreference

import android.content.Context
import com.example.buyva.data.model.CustomerData

interface SharedPreference {
    fun saveToSharedPreference(context: Context, key: String, value: String)
    fun getFromSharedPreference(context: Context, key: String): String?
    fun deleteSharedPreference(context: Context, key: String)

    fun saveCustomer(context: Context, id: String, email: String, firstName: String, lastName: String)
    fun getCustomer(context: Context): CustomerData?
    fun deleteCustomer(context: Context)
}
