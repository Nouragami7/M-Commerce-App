package com.example.buyva.utils.sharedpreference.currency

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.example.buyva.utils.constants.CURRENCY_RATE
import com.example.buyva.utils.constants.CURRENCY_UNIT
import com.example.buyva.utils.sharedpreference.SharedPreferenceImpl


object CurrencyManager {

    private val _currencyRate = mutableStateOf(1.0)
    val currencyRate: State<Double> = _currencyRate

    private val _currencyUnit = mutableStateOf("EGP")
    val currencyUnit: State<String> = _currencyUnit

    init {
        loadFromPreferences()
    }

    fun convertPrice(egpPrice: Double): String {
        val converted = egpPrice * _currencyRate.value
        return String.format("%.2f %s", converted, _currencyUnit.value)
    }


    fun loadFromPreferences() {
        _currencyRate.value =
            SharedPreferenceImpl.getLongFromSharedPreferenceInGeneral(CURRENCY_RATE) ?: 1.0

        _currencyUnit.value =
            SharedPreferenceImpl.getFromSharedPreferenceInGeneral(CURRENCY_UNIT) ?: "EGP"
    }

    fun updateCurrency(rate: Double, unit: String) {
        _currencyRate.value = rate
        _currencyUnit.value = unit

        SharedPreferenceImpl.saveLongToSharedPreferenceInGeneral(CURRENCY_RATE, rate)
        SharedPreferenceImpl.saveToSharedPreferenceInGeneral(CURRENCY_UNIT, unit)
    }
}
