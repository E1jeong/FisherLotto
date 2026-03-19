package com.queentech.data.database.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.queentech.data.database.room.entity.ScanHistoryEntity

@Dao
interface ScanHistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: ScanHistoryEntity)

    @Query("SELECT * FROM scan_history ORDER BY scannedAt DESC")
    suspend fun getAll(): List<ScanHistoryEntity>

    @Query("DELETE FROM scan_history WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM scan_history WHERE scannedAt < :cutoffMillis")
    suspend fun deleteOlderThan(cutoffMillis: Long)

    @Query("SELECT EXISTS(SELECT 1 FROM scan_history WHERE drawNo = :drawNo AND games = :games)")
    suspend fun existsByDrawNoAndGames(drawNo: Int, games: String): Boolean

    @Query("DELETE FROM scan_history")
    suspend fun deleteAll()
}
