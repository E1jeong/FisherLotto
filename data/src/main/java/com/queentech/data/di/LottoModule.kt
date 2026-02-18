package com.queentech.data.di

import com.queentech.data.usecase.login.GetUserUseCaseImpl
import com.queentech.data.usecase.login.SignUpUserUseCaseImpl
import com.queentech.data.usecase.lotto.GetLottoNumberUseCaseImpl
import com.queentech.domain.usecase.login.GetUserUseCase
import com.queentech.domain.usecase.login.SignUpUserUseCase
import com.queentech.domain.usecase.lotto.GetLottoNumberUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class LottoModule {

    @Binds
    abstract fun bindSignUpUserUseCase(uc: SignUpUserUseCaseImpl): SignUpUserUseCase

    @Binds
    abstract fun bindGetUserUseCase(uc: GetUserUseCaseImpl): GetUserUseCase

    @Binds
    abstract fun bindGetLottoNumberUseCase(uc: GetLottoNumberUseCaseImpl): GetLottoNumberUseCase
}