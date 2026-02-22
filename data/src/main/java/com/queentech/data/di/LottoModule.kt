package com.queentech.data.di

import com.queentech.data.usecase.login.GetUserUseCaseImpl
import com.queentech.data.usecase.login.SignUpUserUseCaseImpl
import com.queentech.data.usecase.login.UserRepositoryImpl
import com.queentech.data.usecase.lotto.GetExpectNumberUseCaseImpl
import com.queentech.data.usecase.lotto.GetLottoNumberUseCaseImpl
import com.queentech.domain.usecase.login.GetUserUseCase
import com.queentech.domain.usecase.login.SignUpUserUseCase
import com.queentech.domain.usecase.login.UserRepository
import com.queentech.domain.usecase.lotto.GetExpectNumberUseCase
import com.queentech.domain.usecase.lotto.GetLottoNumberUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class LottoModule {

    @Binds
    @Singleton
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository

    @Binds
    abstract fun bindGetExpectNumberUseCase(uc: GetExpectNumberUseCaseImpl): GetExpectNumberUseCase

    @Binds
    abstract fun bindSignUpUserUseCase(uc: SignUpUserUseCaseImpl): SignUpUserUseCase

    @Binds
    abstract fun bindGetUserUseCase(uc: GetUserUseCaseImpl): GetUserUseCase

    @Binds
    abstract fun bindGetLottoNumberUseCase(uc: GetLottoNumberUseCaseImpl): GetLottoNumberUseCase
}