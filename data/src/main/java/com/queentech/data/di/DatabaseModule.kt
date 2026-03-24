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

    private val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE `scan_history` ADD COLUMN `bestRank` INTEGER NOT NULL DEFAULT 0")
            db.execSQL("UPDATE `scan_history` SET `bestRank` = 0")
            db.execSQL(
                """CREATE TABLE IF NOT EXISTS `scan_history_new` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `drawNo` INTEGER NOT NULL,
                    `games` TEXT NOT NULL,
                    `bestRank` INTEGER NOT NULL DEFAULT 0,
                    `scannedAt` INTEGER NOT NULL
                )""".trimIndent()
            )
            db.execSQL(
                """INSERT INTO `scan_history_new` (`id`, `drawNo`, `games`, `bestRank`, `scannedAt`)
                   SELECT `id`, `drawNo`, `games`, `bestRank`, `scannedAt` FROM `scan_history`"""
            )
            db.execSQL("DROP TABLE `scan_history`")
            db.execSQL("ALTER TABLE `scan_history_new` RENAME TO `scan_history`")
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
        .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
        .build()

    @Provides
    @Singleton
    fun provideLottoIssueDao(db: AppDatabase): LottoIssueDao = db.lottoIssueDao()

    @Provides
    @Singleton
    fun provideScanHistoryDao(db: AppDatabase): ScanHistoryDao = db.scanHistoryDao()
}