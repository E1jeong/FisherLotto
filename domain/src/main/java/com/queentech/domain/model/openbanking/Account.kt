package com.queentech.domain.model.openbanking

data class Account(
    val fintechUseNum: String,
    val accountAlias: String,
    val bankName: String,
    val accountNumMasked: String,
    val accountHolderName: String,
    val transferAgreeYn: String,
)
