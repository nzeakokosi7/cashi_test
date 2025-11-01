package com.test.cashi.di

import com.test.cashi.data.api.PaymentApiClient
import com.test.cashi.data.repository.PaymentRepository
import com.test.cashi.domain.usecase.ObserveTransactionsUseCase
import com.test.cashi.domain.usecase.SubmitPaymentUseCase
import com.test.cashi.domain.validator.PaymentValidator
import com.test.cashi.ui.TransactionViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * Koin dependency injection module
 *
 * Clean Architecture layers:
 * 1. Domain layer (validators, use cases) - business logic
 * 2. Data layer (repositories, API clients) - data access
 * 3. Presentation layer (ViewModels) - UI logic
 *
 * This makes testing easier:
 * - Use cases can be tested independently
 * - Easy to mock for BDD tests
 * - Clear dependency flow: ViewModel -> UseCase -> Repository
 */
val appModule = module {
    // Domain layer - Validators
    single { PaymentValidator() }

    // Domain layer - Use Cases
    factory { SubmitPaymentUseCase(get(), get()) }
    factory { ObserveTransactionsUseCase(get()) }

    // Data layer
    single { PaymentApiClient() }
    single { PaymentRepository() }

    // Presentation layer
    viewModel { TransactionViewModel(get(), get()) }
}