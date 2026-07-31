package com.queentech.data.model.lotto

import com.google.gson.annotations.SerializedName
import com.queentech.domain.model.lotto.GetExpectNumber

data class GetExpectNumberResponse(
    @SerializedName("count") val count: Int,
    @SerializedName("lotto") val lotto: List<String>
)

fun GetExpectNumberResponse.toDomainModel(): GetExpectNumber = GetExpectNumber(
    count = count,
    lotto = lotto,
)
