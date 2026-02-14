package com.queentech.domain.model.openbanking

data class TransferResult(
    val apiTranId: String,
    val rspCode: String,
    val rspMessage: String,
    val dpsBankName: String,
    val dpsAccountNumMasked: String,
    val tranAmt: Long,
    val recvClientName: String,
)
