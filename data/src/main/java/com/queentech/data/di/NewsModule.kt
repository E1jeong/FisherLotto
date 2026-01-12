package com.queentech.data.di

import com.queentech.data.usecase.news.GetLotteryNewsUseCaseImpl
import com.queentech.domain.usecase.news.GetLotteryNewsUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NewsUseCaseModule {
    @Binds
    @Singleton
    abstract fun bindGetLotteryNewsUseCase(impl: GetLotteryNewsUseCaseImpl): GetLotteryNewsUseCase
}