package com.queentech.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.queentech.data.database.room.dao.LottoIssueDao
import com.queentech.data.database.room.dao.ScanHistoryDao
import com.queentech.data.database.room.entity.LottoIssueEntity
import com.queentech.data.database.room.entity.ScanHistoryEntity

@Database(
    entities = [LottoIssueEntity::class, ScanHistoryEntity::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun lottoIssueDao(): LottoIssueDao
    abstract fun scanHistoryDao(): ScanHistoryDao
}