package com.queentech.data.model.lotto

import com.google.gson.annotations.SerializedName
import com.queentech.domain.model.lotto.GetExpectNumber

data class GetExpectNumberResponse(
    @SerializedName("status") val status: String? = null,
    @SerializedName("count") val count: Int? = null,
    @SerializedName("lotto") val lotto: List<String>? = null,
)

fun GetExpectNumberResponse.toDomainModel(): GetExpectNumber = GetExpectNumber(
    count = count ?: 0,
    lotto = lotto ?: emptyList(),
)

