package com.example.buyva.utils.sharedpreference

import android.content.Context
import com.example.buyva.data.model.CustomerData
import com.example.buyva.utils.constants.CART_ID

interface SharedPreference {
    fun saveToSharedPreference(context: Context, key: String, value: String)
    fun getFromSharedPreference(context: Context, key: String): String?
    fun deleteSharedPreference(context: Context, key: String)

    fun saveCustomer(context: Context, id: String, email: String, firstName: String, lastName: String)
    fun getCustomer(context: Context): CustomerData?
    fun deleteCustomer(context: Context)


    fun saveCartId( cartId: String)
    fun getCartId(): String?
    fun saveToSharedPreferenceInGeneral( key: String, value: String)
    fun getFromSharedPreferenceInGeneral( key: String): String?
    fun saveLongToSharedPreferenceInGeneral(key: String, value: Double)
    fun getLongFromSharedPreferenceInGeneral(key: String): Double








}
