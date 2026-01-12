package com.queentech.domain.usecase.lotto

interface GetLatestDrawNumberUseCase {

    suspend operator fun invoke(): Result<Int>
}