package com.example.buyva.features.profile.currency.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.buyva.data.model.CurrencyResponse
import com.example.buyva.data.model.uistate.ResponseState
import com.example.buyva.data.repository.currency.CurrencyRepo
import com.example.buyva.utils.constants.CURRENCY_RATE
import com.example.buyva.utils.constants.CURRENCY_UNIT
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CurrencyViewModel @Inject constructor(private val repository: CurrencyRepo) : ViewModel() {

    private val _currencyResponse = MutableStateFlow("EGP")
    val currencyResponse = _currencyResponse.asStateFlow()

    private val _currencyResponseRate = MutableStateFlow(1.0576)
    val currencyResponseRate = _currencyResponseRate.asStateFlow()

    private val _currencyRates = MutableStateFlow<Map<String, Double>>(emptyMap())
    val currencyRates = _currencyRates.asStateFlow()


    init {
        viewModelScope.launch {
            getCurrencyUnit()
            fetchCurrencyRates()
        }
    }

    suspend fun getCurrencyUnit() {
        val currencyUnit = repository.readCurrencyUnit(CURRENCY_UNIT)
        _currencyResponse.value = currencyUnit
    }

    suspend fun getCurrencyRate(): Double {
        return repository.readCurrencyRate(CURRENCY_RATE)
    }

    fun setCurrencyUnit(unit: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.writeCurrencyUnit(CURRENCY_UNIT, unit)
            _currencyResponse.value = unit
        }
    }

    fun setCurrencyRate(value: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.writeCurrencyRate(CURRENCY_RATE, value)
            _currencyResponseRate.value = value
        }
    }

    fun fetchCurrencyRates(base: String = "USD") {
        viewModelScope.launch {
            repository.getCurrencyRate(base).collect { result ->
                when (result) {
                    is ResponseState.Success<*> -> {
                        val data = result.data as? CurrencyResponse
                        data?.let {
                            _currencyRates.value = it.data.mapValues { entry ->
                                entry.value.value
                            }
                        }
                    }

                    is ResponseState.Failure -> {
                      //  Log.e("CurrencyViewModel", "Failed: ${result.message}")
                    }

                    else -> Unit
                }
            }
        }


    }

}
