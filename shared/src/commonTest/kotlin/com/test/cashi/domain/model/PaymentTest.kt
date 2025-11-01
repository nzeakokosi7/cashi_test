package com.test.cashi.domain.model

import io.kotest.matchers.shouldBe
import kotlin.test.Test
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * Unit tests for Payment data class
 */
class PaymentTest {

    @Test
    fun `formattedAmount should format USD correctly`() {
        val payment = Payment(
            recipientEmail = "user@example.com",
            amount = 100.50,
            currency = Currency.USD
        )
        payment.formattedAmount() shouldBe "$100.50"
    }

    @Test
    fun `formattedAmount should format EUR correctly`() {
        val payment = Payment(
            recipientEmail = "user@example.com",
            amount = 250.75,
            currency = Currency.EUR
        )
        payment.formattedAmount() shouldBe "€250.75"
    }

    @Test
    fun `formattedAmount should format whole numbers with decimal places`() {
        val payment = Payment(
            recipientEmail = "user@example.com",
            amount = 100.0,
            currency = Currency.USD
        )
        payment.formattedAmount() shouldBe "$100.00"
    }

    @Test
    fun `formattedAmount should round to two decimal places`() {
        val payment = Payment(
            recipientEmail = "user@example.com",
            amount = 99.999,
            currency = Currency.USD
        )
        payment.formattedAmount() shouldBe "$100.00"
    }

    @OptIn(ExperimentalTime::class)
    @Test
    fun `Payment should have default timestamp`() {
        val before = Clock.System.now().toEpochMilliseconds()
        val payment = Payment(
            recipientEmail = "user@example.com",
            amount = 100.0,
            currency = Currency.USD
        )
        val after = Clock.System.now().toEpochMilliseconds()

        // Timestamp should be between before and after
        (payment.timestamp >= before) shouldBe true
        (payment.timestamp <= after) shouldBe true
    }

    @Test
    fun `Payment should have default PENDING status`() {
        val payment = Payment(
            recipientEmail = "user@example.com",
            amount = 100.0,
            currency = Currency.USD
        )
        payment.status shouldBe TransactionStatus.PENDING
    }

    @Test
    fun `Payment should have empty id by default`() {
        val payment = Payment(
            recipientEmail = "user@example.com",
            amount = 100.0,
            currency = Currency.USD
        )
        payment.id shouldBe ""
    }
}