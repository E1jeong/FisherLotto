package com.queentech.data.usecase.lotto

import com.queentech.domain.usecase.lotto.GetLatestDrawNumberUseCase
import org.jsoup.Jsoup
import javax.inject.Inject

class GetLatestDrawNumberUseCaseImpl @Inject constructor() : GetLatestDrawNumberUseCase {
    override suspend fun invoke(): Result<Int> = kotlin.runCatching {
        // URL에서 HTML 문서를 가져옵니다.
        val document = Jsoup.connect("https://dhlottery.co.kr/common.do?method=main").get()
        // id가 lottoDrwNo인 요소를 찾고, 텍스트를 추출합니다.
        val drawNoText = document.getElementById("lottoDrwNo")?.text() ?: "0"
        // 텍스트를 정수형으로 변환합니다.
        drawNoText.toInt()
    }
}