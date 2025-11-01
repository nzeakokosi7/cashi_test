package com.test.cashi.di

import com.test.cashi.data.api.PaymentApiClient
import com.test.cashi.domain.usecase.ObserveTransactionsUseCase
import com.test.cashi.domain.usecase.SubmitPaymentUseCase
import com.test.cashi.domain.validator.PaymentValidator
import com.test.cashi.ui.TransactionViewModel
import org.koin.compose.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

/**
 * Common Koin dependency injection module (cross-platform)
 *
 * Contains dependencies that work on all platforms:
 * - Domain layer (validators, use cases) - business logic
 * - Data layer (API clients) - network access
 * - Presentation layer (ViewModels) - UI logic
 *
 * Platform-specific dependencies (like PaymentRepository with expect/actual)
 * are provided in platform-specific modules (see androidModule, iosModule)
 */
val commonModule = module {
    // Domain layer - Validators
    singleOf(::PaymentValidator)

    // Domain layer - Use Cases (get PaymentRepository from platform modules)
    factoryOf(::SubmitPaymentUseCase)
    factoryOf(::ObserveTransactionsUseCase)

    // Data layer - API Client (use default baseUrl)
    single { PaymentApiClient() }

    // Presentation layer - Compose Multiplatform ViewModel
    viewModelOf(::TransactionViewModel)
}