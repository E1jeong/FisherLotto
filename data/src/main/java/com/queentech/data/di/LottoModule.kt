package com.queentech.data.di

import com.queentech.data.usecase.GetLottoNumberUseCaseImpl
import com.queentech.domain.usecase.GetLottoNumberUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class LottoModule {

    @Binds
    abstract fun bindGetLottoNumberUseCase(uc: GetLottoNumberUseCaseImpl): GetLottoNumberUseCase
}