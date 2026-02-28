package com.queentech.data.usecase.lotto

import com.queentech.data.database.room.dao.LottoIssueDao
import com.queentech.data.database.room.entity.LottoIssueEntity
import com.queentech.domain.usecase.lotto.LottoIssueRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LottoIssueRepositoryImpl @Inject constructor(
    private val dao: LottoIssueDao
) : LottoIssueRepository {
    override suspend fun saveIssue(numbers: List<String>, weekStartMillis: Long) {
        val now = System.currentTimeMillis()
        val entities = numbers.map { numberSet ->
            LottoIssueEntity(
                numbers = numberSet,
                issuedAt = now,
                weekStartMillis = weekStartMillis
            )
        }
        dao.insertAll(entities)
    }

    override suspend fun getThisWeekNumbers(weekStartMillis: Long): List<String> {
        return dao.getByWeek(weekStartMillis).map { it.numbers }
    }

    override suspend fun getLastWeekNumbers(lastWeekStartMillis: Long): List<String> {
        return dao.getByWeek(lastWeekStartMillis).map { it.numbers }
    }

    override suspend fun isThisWeekIssued(weekStartMillis: Long): Boolean {
        return dao.countByWeek(weekStartMillis) > 0
    }

    override suspend fun cleanupOldData(cutoffWeekStartMillis: Long) {
        dao.deleteOlderThan(cutoffWeekStartMillis)
    }
}