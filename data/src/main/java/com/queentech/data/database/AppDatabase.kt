package com.queentech.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.queentech.data.database.room.dao.LottoIssueDao
import com.queentech.data.database.room.entity.LottoIssueEntity

@Database(
    entities = [LottoIssueEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun lottoIssueDao(): LottoIssueDao
}