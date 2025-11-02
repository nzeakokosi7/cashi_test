package com.test.cashi.routes

import com.test.cashi.domain.model.Payment
import com.test.cashi.domain.model.PaymentRequest
import com.test.cashi.domain.model.PaymentResponse
import com.test.cashi.domain.model.TransactionStatus
import com.test.cashi.domain.validator.PaymentValidator
import com.test.cashi.firebase.FirebaseAdmin
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

/**
 * Payment API routes
 *
 * POST /payments - Process a new payment
 *
 * This is designed for easy testing:
 * - Clear request/response models
 * - Validation separated from persistence
 * - Structured error handling
 * - Easy to test with JMeter (performance) and BDD (behavior)
 */
fun Route.paymentRoutes() {
    val validator = PaymentValidator()
    val paymentsCollection by lazy {
        FirebaseAdmin.getFirestore().collection("payments")
    }

    /**
     * POST /payments
     *
     * Request body: { "recipientEmail": String, "amount": Double, "currency": String }
     * Response: { "success": Boolean, "payment": Payment?, "error": String? }
     */
    post("/payments") {
        try {
            // Parse request
            val request = call.receive<PaymentRequest>()

            // Validate payment
            val validationResult = validator.validatePayment(
                recipientEmail = request.recipientEmail,
                amount = request.amount,
                currency = request.currency
            )

            if (!validationResult.isValid) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    PaymentResponse(
                        success = false,
                        error = validationResult.errorMessage()
                    )
                )
                return@post
            }

            // Create payment object
            val payment = Payment(
                recipientEmail = request.recipientEmail,
                amount = request.amount,
                currency = request.currency,
                timestamp = System.currentTimeMillis(),
                status = TransactionStatus.COMPLETED
            )

            // Save to Firestore
            val docRef = paymentsCollection.add(
                mapOf(
                    "recipientEmail" to payment.recipientEmail,
                    "amount" to payment.amount,
                    "currency" to payment.currency.code,
                    "timestamp" to payment.timestamp,
                    "status" to payment.status.name
                )
            ).get() // Blocking call for immediate response

            // Return success with the saved payment (including Firebase-generated ID)
            val savedPayment = payment.copy(id = docRef.id)

            call.respond(
                HttpStatusCode.Created,
                PaymentResponse(
                    success = true,
                    payment = savedPayment
                )
            )

        } catch (e: Exception) {
            call.respond(
                HttpStatusCode.InternalServerError,
                PaymentResponse(
                    success = false,
                    error = "Payment processing failed: ${e.message}"
                )
            )
        }
    }
}