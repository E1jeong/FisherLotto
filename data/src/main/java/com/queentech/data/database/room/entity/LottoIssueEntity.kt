package com.queentech.data.database.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lotto_issue")
data class LottoIssueEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val numbers: String,       // "1,7,15,23,38,42" 형태 그대로 저장
    val issuedAt: Long,        // System.currentTimeMillis() 발급 시점
    val weekStartMillis: Long  // 해당 주차의 일요일 00:00 timestamp
)