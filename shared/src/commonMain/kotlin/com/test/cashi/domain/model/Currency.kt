package com.test.cashi.domain.model

import kotlinx.serialization.Serializable

/**
 * Supported currencies for payment transactions
 */
@Serializable
enum class Currency(val code: String, val symbol: String) {
    USD("USD", "$"),
    EUR("EUR", "€");

    companion object {
        fun fromCode(code: String): Currency? {
            return entries.find { it.code.equals(code, ignoreCase = true) }
        }

        val supportedCurrencies: List<Currency> = entries
    }
}