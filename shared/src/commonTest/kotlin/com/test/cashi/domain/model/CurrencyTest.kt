package com.test.cashi.domain.model

import io.kotest.matchers.shouldBe
import kotlin.test.Test

/**
 * Unit tests for Currency enum
 */
class CurrencyTest {

    @Test
    fun `Currency USD should have correct code and symbol`() {
        Currency.USD.code shouldBe "USD"
        Currency.USD.symbol shouldBe "$"
    }

    @Test
    fun `Currency EUR should have correct code and symbol`() {
        Currency.EUR.code shouldBe "EUR"
        Currency.EUR.symbol shouldBe "€"
    }

    @Test
    fun `fromCode should return USD for usd string`() {
        Currency.fromCode("USD") shouldBe Currency.USD
    }

    @Test
    fun `fromCode should return EUR for eur string`() {
        Currency.fromCode("EUR") shouldBe Currency.EUR
    }

    @Test
    fun `fromCode should be case insensitive`() {
        Currency.fromCode("usd") shouldBe Currency.USD
        Currency.fromCode("eur") shouldBe Currency.EUR
        Currency.fromCode("Usd") shouldBe Currency.USD
    }

    @Test
    fun `fromCode should return null for invalid currency code`() {
        Currency.fromCode("GBP") shouldBe null
        Currency.fromCode("INVALID") shouldBe null
        Currency.fromCode("") shouldBe null
    }

    @Test
    fun `supportedCurrencies should contain USD and EUR`() {
        val supported = Currency.supportedCurrencies
        supported.size shouldBe 2
        supported shouldBe listOf(Currency.USD, Currency.EUR)
    }
}