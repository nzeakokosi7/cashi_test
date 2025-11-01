package com.test.cashi.domain.model

/**
 * Represents the result of a validation operation
 * This sealed class makes it easy to test validation logic
 */
sealed class ValidationResult {
    /**
     * Validation passed successfully
     */
    data object Valid : ValidationResult()

    /**
     * Validation failed with a specific error message
     * @property message Description of the validation error
     */
    data class Invalid(val message: String) : ValidationResult()

    /**
     * Convenience property to check if validation passed
     */
    val isValid: Boolean
        get() = this is Valid

    /**
     * Convenience method to get error message if validation failed
     */
    fun errorMessage(): String? = (this as? Invalid)?.message
}