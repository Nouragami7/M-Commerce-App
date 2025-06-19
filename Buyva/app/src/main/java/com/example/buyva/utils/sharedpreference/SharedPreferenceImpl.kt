package com.example.buyva.utils.sharedpreference

import android.content.Context
import com.example.buyva.data.model.CustomerData

object SharedPreferenceImpl : SharedPreference {

    private const val PREF_NAME = "shared_pref"
    private const val KEY_CUSTOMER_ID = "customer_id"
    private const val KEY_CUSTOMER_EMAIL = "customer_email"
    private const val KEY_CUSTOMER_FIRST_NAME = "customer_first_name"
    private const val KEY_CUSTOMER_LAST_NAME = "customer_last_name"

    override fun saveToSharedPreference(context: Context, key: String, value: String) {
        val sharedPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
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
        with(sharedPref.edit()) {
            remove(key)
            apply()
        }
    }

    override fun saveCustomer(context: Context, id: String, email: String, firstName: String, lastName: String) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit().apply {
            putString(KEY_CUSTOMER_ID, id)
            putString(KEY_CUSTOMER_EMAIL, email)
            putString(KEY_CUSTOMER_FIRST_NAME, firstName)
            putString(KEY_CUSTOMER_LAST_NAME, lastName)
            apply()
        }
    }

    override fun getCustomer(context: Context): CustomerData? {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val id = prefs.getString(KEY_CUSTOMER_ID, null)
        val email = prefs.getString(KEY_CUSTOMER_EMAIL, null)
        val firstName = prefs.getString(KEY_CUSTOMER_FIRST_NAME, null)
        val lastName = prefs.getString(KEY_CUSTOMER_LAST_NAME, null)

        return if (id != null && email != null && firstName != null && lastName != null) {
            CustomerData(id, email, firstName, lastName)
        } else {
            null
        }
    }

    override fun deleteCustomer(context: Context) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit().apply {
            remove(KEY_CUSTOMER_ID)
            remove(KEY_CUSTOMER_EMAIL)
            remove(KEY_CUSTOMER_FIRST_NAME)
            remove(KEY_CUSTOMER_LAST_NAME)
            apply()
        }
    }
}
