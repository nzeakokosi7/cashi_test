package com.test.cashi.domain.usecase

import com.test.cashi.data.repository.PaymentRepository
import com.test.cashi.domain.model.Currency
import com.test.cashi.domain.model.Payment
import com.test.cashi.domain.model.PaymentRequest
import com.test.cashi.domain.model.PaymentResponse
import com.test.cashi.domain.model.TransactionStatus
import com.test.cashi.domain.validator.PaymentValidator
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for SubmitPaymentUseCase
 * Uses MockK to mock the repository - no Firebase needed!
 */
class SubmitPaymentUseCaseTest {
    private lateinit var repository: PaymentRepository
    private lateinit var validator: PaymentValidator
    private lateinit var useCase: SubmitPaymentUseCase

    @Before
    fun setup() {
        repository = mockk()
        validator = PaymentValidator() // Real validator, no need to mock
        useCase = SubmitPaymentUseCase(repository, validator)
    }

    // ============ Success Cases ============

    @Test
    fun `invoke should return success when payment is valid and submission succeeds`() = runTest {
        // Given
        val email = "user@example.com"
        val amount = 100.0
        val currency = Currency.USD

        val expectedPayment = Payment(
            id = "payment123",
            recipientEmail = email,
            amount = amount,
            currency = currency,
            status = TransactionStatus.COMPLETED
        )
        val expectedResponse = PaymentResponse(
            success = true,
            payment = expectedPayment
        )

        coEvery {
            repository.submitPayment(any())
        } returns Result.success(expectedResponse)

        // When
        val result = useCase(email, amount, currency)

        // Then
        result.isSuccess shouldBe true
        result.getOrNull()?.success shouldBe true
        result.getOrNull()?.payment shouldBe expectedPayment

        // Verify repository was called with correct request
        coVerify(exactly = 1) {
            repository.submitPayment(
                match { request ->
                    request.recipientEmail == email &&
                    request.amount == amount &&
                    request.currency == currency
                }
            )
        }
    }

    @Test
    fun `invoke should trim email whitespace before submission`() = runTest {
        // Given
        val email = "  user@example.com  "
        val amount = 100.0
        val currency = Currency.USD

        coEvery {
            repository.submitPayment(any())
        } returns Result.success(PaymentResponse(success = true))

        // When
        useCase(email, amount, currency)

        // Then - verify trimmed email was sent
        coVerify {
            repository.submitPayment(
                match { it.recipientEmail == "user@example.com" }
            )
        }
    }

    // ============ Validation Error Cases ============

    @Test
    fun `invoke should return validation error for invalid email`() = runTest {
        // Given
        val invalidEmail = "not-an-email"
        val amount = 100.0
        val currency = Currency.USD

        // When
        val result = useCase(invalidEmail, amount, currency)

        // Then
        result.isFailure shouldBe true
        result.exceptionOrNull().shouldBeInstanceOf<SubmitPaymentError.ValidationError>()
        (result.exceptionOrNull() as SubmitPaymentError.ValidationError)
            .message shouldBe "Invalid email format"

        // Verify repository was NOT called
        coVerify(exactly = 0) { repository.submitPayment(any()) }
    }

    @Test
    fun `invoke should return validation error for empty email`() = runTest {
        // Given
        val email = ""
        val amount = 100.0
        val currency = Currency.USD

        // When
        val result = useCase(email, amount, currency)

        // Then
        result.isFailure shouldBe true
        result.exceptionOrNull().shouldBeInstanceOf<SubmitPaymentError.ValidationError>()
        (result.exceptionOrNull() as SubmitPaymentError.ValidationError)
            .message shouldBe "Email cannot be empty"

        coVerify(exactly = 0) { repository.submitPayment(any()) }
    }

    @Test
    fun `invoke should return validation error for zero amount`() = runTest {
        // Given
        val email = "user@example.com"
        val amount = 0.0
        val currency = Currency.USD

        // When
        val result = useCase(email, amount, currency)

        // Then
        result.isFailure shouldBe true
        result.exceptionOrNull().shouldBeInstanceOf<SubmitPaymentError.ValidationError>()
        (result.exceptionOrNull() as SubmitPaymentError.ValidationError)
            .message shouldBe "Amount must be greater than 0"

        coVerify(exactly = 0) { repository.submitPayment(any()) }
    }

    @Test
    fun `invoke should return validation error for negative amount`() = runTest {
        // Given
        val email = "user@example.com"
        val amount = -50.0
        val currency = Currency.USD

        // When
        val result = useCase(email, amount, currency)

        // Then
        result.isFailure shouldBe true
        result.exceptionOrNull().shouldBeInstanceOf<SubmitPaymentError.ValidationError>()

        coVerify(exactly = 0) { repository.submitPayment(any()) }
    }

    @Test
    fun `invoke should return validation error for amount exceeding maximum`() = runTest {
        // Given
        val email = "user@example.com"
        val amount = 2_000_000.0
        val currency = Currency.USD

        // When
        val result = useCase(email, amount, currency)

        // Then
        result.isFailure shouldBe true
        result.exceptionOrNull().shouldBeInstanceOf<SubmitPaymentError.ValidationError>()
        (result.exceptionOrNull() as SubmitPaymentError.ValidationError)
            .message shouldBe "Amount exceeds maximum limit"

        coVerify(exactly = 0) { repository.submitPayment(any()) }
    }

    // ============ Network Error Cases ============

    @Test
    fun `invoke should return network error when repository fails`() = runTest {
        // Given
        val email = "user@example.com"
        val amount = 100.0
        val currency = Currency.USD

        coEvery {
            repository.submitPayment(any())
        } returns Result.failure(Exception("Connection timeout"))

        // When
        val result = useCase(email, amount, currency)

        // Then
        result.isFailure shouldBe true
        result.exceptionOrNull().shouldBeInstanceOf<SubmitPaymentError.NetworkError>()
        (result.exceptionOrNull() as SubmitPaymentError.NetworkError)
            .message shouldBe "Connection timeout"
    }

    // ============ getValidationErrors Tests ============

    @Test
    fun `getValidationErrors should return empty list for valid payment`() {
        // Given
        val email = "user@example.com"
        val amount = 100.0
        val currency = Currency.USD

        // When
        val errors = useCase.getValidationErrors(email, amount, currency)

        // Then
        errors shouldBe emptyList()
    }

    @Test
    fun `getValidationErrors should return all errors for invalid payment`() {
        // Given
        val email = "invalid-email"
        val amount = 0.0
        val currency: Currency? = null

        // When
        val errors = useCase.getValidationErrors(email, amount, currency!!)

        // Then
        errors.size shouldBe 3
        errors shouldBe listOf(
            "Invalid email format",
            "Amount must be greater than 0",
            "Currency is required"
        )
    }
}