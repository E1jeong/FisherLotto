package com.queentech.data.model.openbanking

import com.google.gson.annotations.SerializedName

data class TransferResponse(
    @SerializedName("api_tran_id") val apiTranId: String,
    @SerializedName("rsp_code") val rspCode: String,
    @SerializedName("rsp_message") val rspMessage: String,
    @SerializedName("dps_bank_name") val dpsBankName: String,
    @SerializedName("dps_account_num_masked") val dpsAccountNumMasked: String,
    @SerializedName("bank_tran_id") val bankTranId: String,
    @SerializedName("bank_tran_date") val bankTranDate: String,
    @SerializedName("bank_code_tran") val bankCodeTran: String,
    @SerializedName("bank_rsp_code") val bankRspCode: String,
    @SerializedName("bank_rsp_message") val bankRspMessage: String,
    @SerializedName("fintech_use_num") val fintechUseNum: String,
    @SerializedName("tran_amt") val tranAmt: String,
    @SerializedName("recv_client_name") val recvClientName: String,
    @SerializedName("wd_limit_remain_amt") val wdLimitRemainAmt: String,
)
