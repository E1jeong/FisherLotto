package com.queentech.data.model.common

import com.google.gson.annotations.SerializedName
import com.queentech.domain.model.common.CommonResponse

data class CommonResponseDto(
    @SerializedName("status") val status: String,
    @SerializedName("verificationToken") val verificationToken: String? = null,
)

fun CommonResponseDto.toDomainModel(): CommonResponse = CommonResponse(
    status = status,
    verificationToken = verificationToken,
)
