package com.test.cashi.domain.validator

import com.test.cashi.domain.model.Currency
import com.test.cashi.domain.model.ValidationResult
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlin.test.Test

/**
 * Unit tests for PaymentValidator
 * These tests verify validation logic without any external dependencies
 */
class PaymentValidatorTest {
    private val validator = PaymentValidator()

    // ============ Email Validation Tests ============

    @Test
    fun `validateEmail should return Valid for correct email format`() {
        val result = validator.validateEmail("user@example.com")
        result.shouldBeInstanceOf<ValidationResult.Valid>()
        result.isValid shouldBe true
    }

    @Test
    fun `validateEmail should return Invalid for empty email`() {
        val result = validator.validateEmail("")
        result.shouldBeInstanceOf<ValidationResult.Invalid>()
        result.errorMessage() shouldBe "Email cannot be empty"
    }

    @Test
    fun `validateEmail should return Invalid for blank email`() {
        val result = validator.validateEmail("   ")
        result.shouldBeInstanceOf<ValidationResult.Invalid>()
        result.errorMessage() shouldBe "Email cannot be empty"
    }

    @Test
    fun `validateEmail should return Invalid for email without @ symbol`() {
        val result = validator.validateEmail("userexample.com")
        result.shouldBeInstanceOf<ValidationResult.Invalid>()
        result.errorMessage() shouldBe "Invalid email format"
    }

    @Test
    fun `validateEmail should return Invalid for email without domain`() {
        val result = validator.validateEmail("user@")
        result.shouldBeInstanceOf<ValidationResult.Invalid>()
        result.errorMessage() shouldBe "Invalid email format"
    }

    @Test
    fun `validateEmail should return Invalid for email without top-level domain`() {
        val result = validator.validateEmail("user@example")
        result.shouldBeInstanceOf<ValidationResult.Invalid>()
        result.errorMessage() shouldBe "Invalid email format"
    }

    @Test
    fun `validateEmail should accept various valid email formats`() {
        val validEmails = listOf(
            "simple@example.com",
            "user.name@example.com",
            "user+tag@example.co.uk",
            "user_name@example-domain.com",
            "123@example.com"
        )

        validEmails.forEach { email ->
            val result = validator.validateEmail(email)
            result.isValid shouldBe true
        }
    }

    // ============ Amount Validation Tests ============

    @Test
    fun `validateAmount should return Valid for positive amounts`() {
        val result = validator.validateAmount(100.0)
        result.shouldBeInstanceOf<ValidationResult.Valid>()
        result.isValid shouldBe true
    }

    @Test
    fun `validateAmount should return Valid for small decimal amounts`() {
        val result = validator.validateAmount(0.01)
        result.shouldBeInstanceOf<ValidationResult.Valid>()
        result.isValid shouldBe true
    }

    @Test
    fun `validateAmount should return Invalid for zero amount`() {
        val result = validator.validateAmount(0.0)
        result.shouldBeInstanceOf<ValidationResult.Invalid>()
        result.errorMessage() shouldBe "Amount must be greater than 0"
    }

    @Test
    fun `validateAmount should return Invalid for negative amounts`() {
        val result = validator.validateAmount(-10.0)
        result.shouldBeInstanceOf<ValidationResult.Invalid>()
        result.errorMessage() shouldBe "Amount must be greater than 0"
    }

    @Test
    fun `validateAmount should return Invalid for amounts exceeding maximum`() {
        val result = validator.validateAmount(2_000_000.0)
        result.shouldBeInstanceOf<ValidationResult.Invalid>()
        result.errorMessage() shouldBe "Amount exceeds maximum limit"
    }

    @Test
    fun `validateAmount should return Valid for amount at maximum limit`() {
        val result = validator.validateAmount(1_000_000.0)
        result.shouldBeInstanceOf<ValidationResult.Valid>()
        result.isValid shouldBe true
    }

    // ============ Currency Validation Tests ============

    @Test
    fun `validateCurrency should return Valid for USD`() {
        val result = validator.validateCurrency(Currency.USD)
        result.shouldBeInstanceOf<ValidationResult.Valid>()
        result.isValid shouldBe true
    }

    @Test
    fun `validateCurrency should return Valid for EUR`() {
        val result = validator.validateCurrency(Currency.EUR)
        result.shouldBeInstanceOf<ValidationResult.Valid>()
        result.isValid shouldBe true
    }

    @Test
    fun `validateCurrency should return Invalid for null currency`() {
        val result = validator.validateCurrency(null)
        result.shouldBeInstanceOf<ValidationResult.Invalid>()
        result.errorMessage() shouldBe "Currency is required"
    }

    // ============ Complete Payment Validation Tests ============

    @Test
    fun `validatePayment should return Valid for valid payment details`() {
        val result = validator.validatePayment(
            recipientEmail = "user@example.com",
            amount = 100.0,
            currency = Currency.USD
        )
        result.shouldBeInstanceOf<ValidationResult.Valid>()
        result.isValid shouldBe true
    }

    @Test
    fun `validatePayment should return Invalid for invalid email`() {
        val result = validator.validatePayment(
            recipientEmail = "invalid-email",
            amount = 100.0,
            currency = Currency.USD
        )
        result.shouldBeInstanceOf<ValidationResult.Invalid>()
        result.errorMessage() shouldBe "Invalid email format"
    }

    @Test
    fun `validatePayment should return Invalid for invalid amount`() {
        val result = validator.validatePayment(
            recipientEmail = "user@example.com",
            amount = 0.0,
            currency = Currency.USD
        )
        result.shouldBeInstanceOf<ValidationResult.Invalid>()
        result.errorMessage() shouldBe "Amount must be greater than 0"
    }

    @Test
    fun `validatePayment should return Invalid for null currency`() {
        val result = validator.validatePayment(
            recipientEmail = "user@example.com",
            amount = 100.0,
            currency = null
        )
        result.shouldBeInstanceOf<ValidationResult.Invalid>()
        result.errorMessage() shouldBe "Currency is required"
    }

    @Test
    fun `validatePayment should return first error when multiple validations fail`() {
        val result = validator.validatePayment(
            recipientEmail = "invalid-email",
            amount = 0.0,
            currency = null
        )
        result.shouldBeInstanceOf<ValidationResult.Invalid>()
        // Should return the first error (email validation)
        result.errorMessage() shouldBe "Invalid email format"
    }

    // ============ All Errors Validation Tests ============

    @Test
    fun `validatePaymentWithAllErrors should return empty list for valid payment`() {
        val errors = validator.validatePaymentWithAllErrors(
            recipientEmail = "user@example.com",
            amount = 100.0,
            currency = Currency.USD
        )
        errors shouldBe emptyList()
    }

    @Test
    fun `validatePaymentWithAllErrors should return all errors for completely invalid payment`() {
        val errors = validator.validatePaymentWithAllErrors(
            recipientEmail = "invalid-email",
            amount = 0.0,
            currency = null
        )
        errors.size shouldBe 3
        errors shouldBe listOf(
            "Invalid email format",
            "Amount must be greater than 0",
            "Currency is required"
        )
    }

    @Test
    fun `validatePaymentWithAllErrors should return multiple errors for partially invalid payment`() {
        val errors = validator.validatePaymentWithAllErrors(
            recipientEmail = "user@example.com",
            amount = -10.0,
            currency = null
        )
        errors.size shouldBe 2
        errors shouldBe listOf(
            "Amount must be greater than 0",
            "Currency is required"
        )
    }
}