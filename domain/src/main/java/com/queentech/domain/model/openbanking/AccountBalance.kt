package com.queentech.domain.model.openbanking

data class AccountBalance(
    val bankName: String,
    val accountNumMasked: String,
    val balanceAmt: Long,
    val availableAmt: Long,
    val productName: String,
)
