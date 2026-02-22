package com.queentech.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.queentech.data.database.dao.LottoIssueDao
import com.queentech.data.database.entity.LottoIssueEntity

@Database(
    entities = [LottoIssueEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun lottoIssueDao(): LottoIssueDao
}