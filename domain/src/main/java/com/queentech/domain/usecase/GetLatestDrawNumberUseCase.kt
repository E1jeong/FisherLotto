package com.queentech.domain.usecase

interface GetLatestDrawNumberUseCase {

    suspend operator fun invoke(): Result<Int>
}