package com.queentech.data.di

import com.queentech.data.usecase.payments.GetPaymentResultUseCaseImpl
import com.queentech.data.usecase.payments.StartPaymentUseCaseImpl
import com.queentech.domain.usecase.payments.GetPaymentResultUseCase
import com.queentech.domain.usecase.payments.StartPaymentUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PaymentsModule {

    @Binds
    @Singleton
    abstract fun bindStartPaymentUseCase(
        impl: StartPaymentUseCaseImpl,
    ): StartPaymentUseCase

    @Binds
    @Singleton
    abstract fun bindGetPaymentResultUseCase(
        impl: GetPaymentResultUseCaseImpl,
    ): GetPaymentResultUseCase
}