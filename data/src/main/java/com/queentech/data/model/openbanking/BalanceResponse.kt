package com.queentech.data.model.openbanking

import com.google.gson.annotations.SerializedName

data class BalanceResponse(
    @SerializedName("api_tran_id") val apiTranId: String?,
    @SerializedName("rsp_code") val rspCode: String?,
    @SerializedName("rsp_message") val rspMessage: String?,
    @SerializedName("bank_name") val bankName: String?,
    @SerializedName("fintech_use_num") val fintechUseNum: String?,
    @SerializedName("account_num_masked") val accountNumMasked: String?,
    @SerializedName("balance_amt") val balanceAmt: String?,
    @SerializedName("available_amt") val availableAmt: String?,
    @SerializedName("account_type") val accountType: String?,
    @SerializedName("product_name") val productName: String?,
)
