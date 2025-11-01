package com.test.cashi.data.api

import com.test.cashi.BuildKonfig
import com.test.cashi.domain.model.PaymentRequest
import com.test.cashi.domain.model.PaymentResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

/**
 * API client for payment operations
 *
 * Uses environment-based configuration for base URL:
 * - Development: http://10.0.2.2:8080 (Android emulator localhost)
 * - Can be overridden via API_BASE_URL environment variable
 *
 * Designed for testability:
 * - Can be easily mocked for BDD tests
 * - Configurable base URL for different environments
 * - Structured error handling
 */
class PaymentApiClient(
    private val baseUrl: String = BuildKonfig.API_BASE_URL
) {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }

        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.INFO
        }

        install(HttpTimeout) {
            requestTimeoutMillis = 15000
            connectTimeoutMillis = 15000
        }

        defaultRequest {
            contentType(ContentType.Application.Json)
        }
    }

    /**
     * Submit a payment to the server
     * @param request Payment request data
     * @return Result with PaymentResponse or error
     */
    suspend fun submitPayment(request: PaymentRequest): Result<PaymentResponse> {
        return try {
            val response = client.post("$baseUrl/payments") {
                setBody(request)
            }

            if (response.status.isSuccess()) {
                Result.success(response.body<PaymentResponse>())
            } else {
                Result.failure(
                    Exception("Payment failed: ${response.status.description}")
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Close the HTTP client
     */
    fun close() {
        client.close()
    }
}