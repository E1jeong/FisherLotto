package com.queentech.data.usecase.lotto

import com.queentech.data.database.room.dao.ScanHistoryDao
import com.queentech.data.database.room.entity.ScanHistoryEntity
import com.queentech.domain.model.lotto.ScanHistory
import com.queentech.domain.usecase.lotto.ScanHistoryRepository
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScanHistoryRepositoryImpl @Inject constructor(
    private val dao: ScanHistoryDao
) : ScanHistoryRepository {

    override suspend fun save(drawNo: Int, games: List<List<Int>>, matchCount: Int) {
        val gamesJson = Json.encodeToString(games)
        dao.insert(
            ScanHistoryEntity(
                drawNo = drawNo,
                games = gamesJson,
                matchCount = matchCount,
                scannedAt = System.currentTimeMillis()
            )
        )
    }

    override suspend fun getAll(): List<ScanHistory> {
        return dao.getAll().map { entity ->
            ScanHistory(
                id = entity.id,
                drawNo = entity.drawNo,
                games = Json.decodeFromString(entity.games),
                matchCount = entity.matchCount,
                scannedAt = entity.scannedAt,
            )
        }
    }

    override suspend fun deleteById(id: Long) {
        dao.deleteById(id)
    }

    override suspend fun deleteOlderThan(cutoffMillis: Long) {
        dao.deleteOlderThan(cutoffMillis)
    }

    override suspend fun exists(drawNo: Int, games: List<List<Int>>): Boolean {
        val gamesJson = Json.encodeToString(games)
        return dao.existsByDrawNoAndGames(drawNo, gamesJson)
    }
}
