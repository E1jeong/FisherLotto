package com.queentech.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.queentech.data.database.entity.LottoIssueEntity

@Dao
interface LottoIssueDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(issues: List<LottoIssueEntity>)

    // 특정 주차(weekStartMillis)에 해당하는 번호들 조회
    @Query("SELECT * FROM lotto_issue WHERE weekStartMillis = :weekStart ORDER BY id ASC")
    suspend fun getByWeek(weekStart: Long): List<LottoIssueEntity>

    // 이번주 발급 여부 확인
    @Query("SELECT COUNT(*) FROM lotto_issue WHERE weekStartMillis = :weekStart")
    suspend fun countByWeek(weekStart: Long): Int

    // 오래된 데이터 정리 (2주 이전 삭제)
    @Query("DELETE FROM lotto_issue WHERE weekStartMillis < :cutoffWeekStart")
    suspend fun deleteOlderThan(cutoffWeekStart: Long)

    // 모든 데이터 삭제
    @Query("DELETE FROM lotto_issue")
    suspend fun deleteAll()
}