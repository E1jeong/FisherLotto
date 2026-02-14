package com.queentech.data.model.openbanking

import com.google.gson.annotations.SerializedName

data class TransferRequest(
    @SerializedName("fintech_use_num") val fintechUseNum: String,
    @SerializedName("tran_amt") val tranAmt: String,
    @SerializedName("req_client_name") val reqClientName: String,
    @SerializedName("req_client_num") val reqClientNum: String,
    @SerializedName("recv_client_name") val recvClientName: String,
    @SerializedName("recv_client_account_num") val recvClientAccountNum: String,
)
