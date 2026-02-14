package com.queentech.data.model.openbanking

import com.google.gson.annotations.SerializedName

data class AccountListResponse(
    @SerializedName("api_tran_id") val apiTranId: String,
    @SerializedName("rsp_code") val rspCode: String,
    @SerializedName("rsp_message") val rspMessage: String,
    @SerializedName("user_seq_no") val userSeqNo: String,
    @SerializedName("res_cnt") val resCnt: Int,
    @SerializedName("account_list") val accountList: List<AccountItem>,
)

data class AccountItem(
    @SerializedName("fintech_use_num") val fintechUseNum: String,
    @SerializedName("account_alias") val accountAlias: String,
    @SerializedName("bank_name") val bankName: String,
    @SerializedName("account_num_masked") val accountNumMasked: String,
    @SerializedName("account_holder_name") val accountHolderName: String,
    @SerializedName("account_type") val accountType: String,
    @SerializedName("inquiry_agree_yn") val inquiryAgreeYn: String,
    @SerializedName("transfer_agree_yn") val transferAgreeYn: String,
)
