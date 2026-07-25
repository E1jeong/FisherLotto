package com.queentech.data.model.billing

import com.google.gson.annotations.SerializedName

data class ReceiptResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String?,
    @SerializedName("expiryTimeMillis") val expiryTimeMillis: Long?,
    // 서버가 이번주 예상번호를 10개에서 30개로 실제 교체했으면 true. 필드가 없는 응답은 null.
    @SerializedName("reissued") val reissued: Boolean?,
)
