package com.queentech.data.di

import android.content.Context
import androidx.room.Room
import com.queentech.data.database.AppDatabase
import com.queentech.data.database.room.dao.LottoIssueDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "fisher_lotto_db"
    ).build()

    @Provides
    @Singleton
    fun provideLottoIssueDao(db: AppDatabase): LottoIssueDao = db.lottoIssueDao()
}