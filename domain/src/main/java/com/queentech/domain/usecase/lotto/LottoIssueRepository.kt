package com.queentech.domain.usecase.lotto

interface LottoIssueRepository {
    suspend fun saveIssue(numbers: List<String>, weekStartMillis: Long)
    suspend fun getThisWeekNumbers(weekStartMillis: Long): List<String>
    suspend fun getLastWeekNumbers(lastWeekStartMillis: Long): List<String>
    suspend fun isThisWeekIssued(weekStartMillis: Long): Boolean
    suspend fun cleanupOldData(cutoffWeekStartMillis: Long)
}