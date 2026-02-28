package com.queentech.data.usecase.openbanking

import com.queentech.data.database.datastore.OpenBankingLocalDataSource
import com.queentech.domain.usecase.openbanking.GetSavedTokenUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetSavedTokenUseCaseImpl @Inject constructor(
    private val localDataSource: OpenBankingLocalDataSource
) : GetSavedTokenUseCase {

    override fun invoke(): Flow<Pair<String?, String?>> {
        // 두 개의 Flow를 합쳐서 Pair로 반환합니다.
        return combine(
            localDataSource.accessTokenFlow,
            localDataSource.userSeqNoFlow
        ) { token, userSeqNo ->
            Pair(token, userSeqNo)
        }
    }
}