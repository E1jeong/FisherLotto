package com.queentech.data.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.queentech.data.database.AppDatabase
import com.queentech.data.database.room.dao.LottoIssueDao
import com.queentech.data.database.room.dao.ScanHistoryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """CREATE TABLE IF NOT EXISTS `scan_history` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `drawNo` INTEGER NOT NULL,
                    `games` TEXT NOT NULL,
                    `matchCount` INTEGER NOT NULL,
                    `scannedAt` INTEGER NOT NULL
                )""".trimIndent()
            )
        }
    }

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "fisher_lotto_db"
    )
        .addMigrations(MIGRATION_1_2)
        .build()

    @Provides
    @Singleton
    fun provideLottoIssueDao(db: AppDatabase): LottoIssueDao = db.lottoIssueDao()

    @Provides
    @Singleton
    fun provideScanHistoryDao(db: AppDatabase): ScanHistoryDao = db.scanHistoryDao()
}