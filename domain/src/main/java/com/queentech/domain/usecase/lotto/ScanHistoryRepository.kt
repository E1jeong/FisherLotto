package com.queentech.domain.usecase.lotto

import com.queentech.domain.model.lotto.ScanHistory

interface ScanHistoryRepository {
    suspend fun save(drawNo: Int, games: List<List<Int>>, matchCount: Int)
    suspend fun getAll(): List<ScanHistory>
    suspend fun deleteById(id: Long)
    suspend fun deleteOlderThan(cutoffMillis: Long)
    suspend fun exists(drawNo: Int, games: List<List<Int>>): Boolean
}
