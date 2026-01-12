package com.queentech.data.di

import com.queentech.data.usecase.lotto.GetLatestDrawNumberUseCaseImpl
import com.queentech.data.usecase.lotto.GetLottoNumberUseCaseImpl
import com.queentech.domain.usecase.lotto.GetLatestDrawNumberUseCase
import com.queentech.domain.usecase.lotto.GetLottoNumberUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class LottoModule {

    @Binds
    abstract fun bindGetLottoNumberUseCase(uc: GetLottoNumberUseCaseImpl): GetLottoNumberUseCase

    @Binds
    abstract fun bindGetLatestDrawNumberUseCase(uc: GetLatestDrawNumberUseCaseImpl): GetLatestDrawNumberUseCase
}