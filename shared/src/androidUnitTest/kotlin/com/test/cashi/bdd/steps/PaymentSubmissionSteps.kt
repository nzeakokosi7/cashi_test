package com.test.cashi.bdd.steps

import com.test.cashi.data.repository.PaymentRepository
import com.test.cashi.domain.model.Currency
import com.test.cashi.domain.model.Payment
import com.test.cashi.domain.model.PaymentRequest
import com.test.cashi.domain.model.PaymentResponse
import com.test.cashi.domain.model.TransactionStatus
import com.test.cashi.domain.usecase.SubmitPaymentUseCase
import com.test.cashi.domain.validator.PaymentValidator
import io.cucumber.datatable.DataTable
import io.cucumber.java.en.And
import io.cucumber.java.en.Given
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking

/**
 * Cucumber step definitions for payment submission scenarios
 * These tests verify business logic without requiring Firebase
 */
class PaymentSubmissionSteps {
    private lateinit var repository: PaymentRepository
    private lateinit var validator: PaymentValidator
    private lateinit var useCase: SubmitPaymentUseCase

    private var recipientEmail: String = ""
    private var amount: Double = 0.0
    private var currency: Currency? = null
    private var result: Result<PaymentResponse>? = null
    private var networkAvailable = true

    @Given("a user enters valid payment details")
    fun userEntersValidPaymentDetails(dataTable: DataTable) {
        setupUseCase()
        val data = dataTable.asMaps()[0]
        recipientEmail = data["recipientEmail"]!!
        amount = data["amount"]!!.toDouble()
        currency = Currency.fromCode(data["currency"]!!)

        // Mock successful repository response
        coEvery {
            repository.submitPayment(any())
        } returns Result.success(
            PaymentResponse(
                success = true,
                payment = Payment(
                    id = "payment123",
                    recipientEmail = recipientEmail,
                    amount = amount,
                    currency = currency!!,
                    status = TransactionStatus.COMPLETED
                )
            )
        )
    }

    @Given("a user enters payment details with invalid email")
    fun userEntersInvalidEmail(dataTable: DataTable) {
        setupUseCase()
        val data = dataTable.asMaps()[0]
        recipientEmail = data["recipientEmail"] ?: ""
        amount = data["amount"]?.toDouble() ?: 0.0
        currency = Currency.fromCode(data["currency"] ?: "USD")
    }

    @Given("a user enters payment details with empty email")
    fun userEntersEmptyEmail(dataTable: DataTable) {
        setupUseCase()
        val data = dataTable.asMaps()[0]
        recipientEmail = data["recipientEmail"] ?: ""  // Handle empty value
        amount = data["amount"]!!.toDouble()
        currency = Currency.fromCode(data["currency"]!!)
    }

    @Given("a user enters payment details with zero amount")
    fun userEntersZeroAmount(dataTable: DataTable) {
        setupUseCase()
        val data = dataTable.asMaps()[0]
        recipientEmail = data["recipientEmail"]!!
        amount = data["amount"]!!.toDouble()
        currency = Currency.fromCode(data["currency"]!!)
    }

    @Given("a user enters payment details with negative amount")
    fun userEntersNegativeAmount(dataTable: DataTable) {
        setupUseCase()
        val data = dataTable.asMaps()[0]
        recipientEmail = data["recipientEmail"]!!
        amount = data["amount"]!!.toDouble()
        currency = Currency.fromCode(data["currency"]!!)
    }

    @Given("a user enters payment details with excessive amount")
    fun userEntersExcessiveAmount(dataTable: DataTable) {
        setupUseCase()
        val data = dataTable.asMaps()[0]
        recipientEmail = data["recipientEmail"]!!
        amount = data["amount"]!!.toDouble()
        currency = Currency.fromCode(data["currency"]!!)
    }

    @And("the network is unavailable")
    fun networkIsUnavailable() {
        networkAvailable = false
        // Mock network error
        coEvery {
            repository.submitPayment(any())
        } returns Result.failure(Exception("Network error"))
    }

    @When("they submit the payment")
    fun submitPayment() = runBlocking {
        result = useCase(recipientEmail, amount, currency!!)
    }

    @Then("the payment is processed successfully")
    fun paymentProcessedSuccessfully() {
        result!!.isSuccess shouldBe true
        result!!.getOrNull()?.success shouldBe true
    }

    @And("the payment is saved to Firestore")
    fun paymentSavedToFirestore() {
        // Verify repository was called (simulating Firestore save)
        coVerify(exactly = 1) {
            repository.submitPayment(
                match { request: PaymentRequest ->
                    request.recipientEmail == recipientEmail &&
                    request.amount == amount &&
                    request.currency == currency
                }
            )
        }
    }

    @Then("the payment submission fails")
    fun paymentSubmissionFails() {
        result!!.isFailure shouldBe true
    }

    @And("an error message {string} is shown")
    fun errorMessageShown(expectedMessage: String) {
        val errorMessage = result!!.exceptionOrNull()?.message ?: ""
        errorMessage shouldBe expectedMessage
    }

    private fun setupUseCase() {
        repository = mockk()
        validator = PaymentValidator()
        useCase = SubmitPaymentUseCase(repository, validator)
        networkAvailable = true
    }
}