package com.test.cashi

import com.test.cashi.firebase.FirebaseAdmin
import com.test.cashi.routes.paymentRoutes
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json

fun main() {
    // Initialize Firebase Admin SDK before starting server
    try {
        FirebaseAdmin.initialize()
    } catch (e: Exception) {
        System.err.println("Failed to start server: Firebase initialization failed")
        return
    }

    embeddedServer(Netty, port = SERVER_PORT, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    // Install JSON content negotiation for serialization
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
    }

    routing {
        // Health check endpoint
        get("/") {
            call.respondText("Cashi Payment Server is running ✅")
        }

        // Payment API routes
        paymentRoutes()
    }
}