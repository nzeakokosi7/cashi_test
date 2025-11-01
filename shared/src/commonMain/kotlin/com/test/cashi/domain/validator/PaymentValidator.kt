package com.test.cashi.domain.validator

import com.test.cashi.domain.model.Currency
import com.test.cashi.domain.model.ValidationResult

/**
 * Validates payment transaction data
 * This class is designed to be easily testable with BDD scenarios
 */
class PaymentValidator {

    /**
     * Email regex pattern for basic email validation
     */
    private val emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()

    /**
     * Validates recipient email format
     */
    fun validateEmail(email: String): ValidationResult {
        return when {
            email.isBlank() -> ValidationResult.Invalid("Email cannot be empty")
            !email.matches(emailPattern) -> ValidationResult.Invalid("Invalid email format")
            else -> ValidationResult.Valid
        }
    }

    /**
     * Validates payment amount
     */
    fun validateAmount(amount: Double): ValidationResult {
        return when {
            amount <= 0 -> ValidationResult.Invalid("Amount must be greater than 0")
            amount > 1_000_000 -> ValidationResult.Invalid("Amount exceeds maximum limit")
            else -> ValidationResult.Valid
        }
    }

    /**
     * Validates currency is supported
     */
    fun validateCurrency(currency: Currency?): ValidationResult {
        return if (currency == null) {
            ValidationResult.Invalid("Currency is required")
        } else {
            ValidationResult.Valid
        }
    }

    /**
     * Validates the entire payment request
     * Returns the first validation error encountered, or Valid if all checks pass
     */
    fun validatePayment(
        recipientEmail: String,
        amount: Double,
        currency: Currency?
    ): ValidationResult {
        // Check email first
        val emailValidation = validateEmail(recipientEmail)
        if (!emailValidation.isValid) return emailValidation

        // Check amount
        val amountValidation = validateAmount(amount)
        if (!amountValidation.isValid) return amountValidation

        // Check currency
        val currencyValidation = validateCurrency(currency)
        if (!currencyValidation.isValid) return currencyValidation

        return ValidationResult.Valid
    }

    /**
     * Validates and returns all errors (for displaying multiple validation errors)
     */
    fun validatePaymentWithAllErrors(
        recipientEmail: String,
        amount: Double,
        currency: Currency?
    ): List<String> {
        val errors = mutableListOf<String>()

        val emailValidation = validateEmail(recipientEmail)
        if (!emailValidation.isValid) {
            emailValidation.errorMessage()?.let { errors.add(it) }
        }

        val amountValidation = validateAmount(amount)
        if (!amountValidation.isValid) {
            amountValidation.errorMessage()?.let { errors.add(it) }
        }

        val currencyValidation = validateCurrency(currency)
        if (!currencyValidation.isValid) {
            currencyValidation.errorMessage()?.let { errors.add(it) }
        }

        return errors
    }
}