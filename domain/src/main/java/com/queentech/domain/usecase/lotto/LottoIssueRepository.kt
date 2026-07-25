package com.queentech.domain.usecase.lotto

interface LottoIssueRepository {
    suspend fun saveIssue(numbers: List<String>, weekStartMillis: Long)
    suspend fun getThisWeekNumbers(weekStartMillis: Long): List<String>
    suspend fun getLastWeekNumbers(lastWeekStartMillis: Long): List<String>
    suspend fun isThisWeekIssued(weekStartMillis: Long): Boolean
    suspend fun cleanupOldData(cutoffWeekStartMillis: Long)

    // 해당 주차 발급 기록 삭제 (구독 전환으로 서버가 번호를 재발급했을 때 사용)
    suspend fun deleteWeek(weekStartMillis: Long)
}