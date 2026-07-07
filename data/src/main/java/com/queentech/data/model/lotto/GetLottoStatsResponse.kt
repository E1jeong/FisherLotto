package com.queentech.data.model.lotto

import com.google.gson.annotations.SerializedName
import com.queentech.domain.model.lotto.GetLottoStats

data class GetLottoStatsResponse(
    @SerializedName("lottoRound") val round: Int,
    @SerializedName("pickDate") val pdate: String,
    @SerializedName("grade1") val grade1: Int,
    @SerializedName("grade2") val grade2: Int,
    @SerializedName("grade3") val grade3: Int,
    @SerializedName("grade4") val grade4: Int,
    @SerializedName("grade5") val grade5: Int,
    @SerializedName("combiCount") val combiCount: Int,
)

fun GetLottoStatsResponse.toDomainModel(): GetLottoStats = GetLottoStats(
    round = round.toString(),
    pdate = pdate,
    grade1 = grade1,
    grade2 = grade2,
    grade3 = grade3,
    grade4 = grade4,
    grade5 = grade5,
    combiCount = combiCount,
)
