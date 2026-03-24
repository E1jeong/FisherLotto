package com.queentech.data.database.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scan_history")
data class ScanHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val drawNo: Int,
    val games: String,       // JSON 문자열 e.g. "[[1,2,3,4,5,6],[7,8,9,10,11,12]]"
    val bestRank: Int,       // 1~5 = 등수, 0 = 낙첨
    val scannedAt: Long,
)
