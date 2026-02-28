package com.queentech.domain.usecase.openbanking

import kotlinx.coroutines.flow.Flow

interface GetSavedTokenUseCase {
    operator fun invoke(): Flow<Pair<String?, String?>> // <AccessToken, UserSeqNo>
}