package com.queentech.data.di

import com.queentech.data.usecase.openbanking.GetAccountsUseCaseImpl
import com.queentech.data.usecase.openbanking.GetAuthorizeUrlUseCaseImpl
import com.queentech.data.usecase.openbanking.GetBalanceUseCaseImpl
import com.queentech.data.usecase.openbanking.GetTokenUseCaseImpl
import com.queentech.data.usecase.openbanking.TransferUseCaseImpl
import com.queentech.domain.usecase.openbanking.GetAccountsUseCase
import com.queentech.domain.usecase.openbanking.GetAuthorizeUrlUseCase
import com.queentech.domain.usecase.openbanking.GetBalanceUseCase
import com.queentech.domain.usecase.openbanking.GetTokenUseCase
import com.queentech.domain.usecase.openbanking.TransferUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class OpenBankingModule {

    @Binds
    @Singleton
    abstract fun bindGetAuthorizeUrlUseCase(
        impl: GetAuthorizeUrlUseCaseImpl,
    ): GetAuthorizeUrlUseCase

    @Binds
    @Singleton
    abstract fun bindGetTokenUseCase(
        impl: GetTokenUseCaseImpl,
    ): GetTokenUseCase

    @Binds
    @Singleton
    abstract fun bindGetAccountsUseCase(
        impl: GetAccountsUseCaseImpl,
    ): GetAccountsUseCase

    @Binds
    @Singleton
    abstract fun bindGetBalanceUseCase(
        impl: GetBalanceUseCaseImpl,
    ): GetBalanceUseCase

    @Binds
    @Singleton
    abstract fun bindTransferUseCase(
        impl: TransferUseCaseImpl,
    ): TransferUseCase
}
