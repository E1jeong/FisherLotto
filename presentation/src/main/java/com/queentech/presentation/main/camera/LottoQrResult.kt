package com.queentech.presentation.main.camera

import android.net.Uri

data class LottoQrResult(
    val drawNo: Int,
    val games: List<List<Int>>,
) {
    companion object {
        private val GAME_SEGMENT = Regex("[a-zA-Z](\\d{12})")
        private val LOTTO_NUMBERS = 1..45

        fun parse(raw: String): LottoQrResult? {
            val uri = runCatching { Uri.parse(raw) }.getOrNull() ?: return null
            val value = uri.getQueryParameter("v") ?: return null
            if (value.length < 4) return null

            // 1. 회차
            val drawNo = value.substring(0, 4).toIntOrNull() ?: return null

            // 2. 게임별 12자리 추출
            val body = value.substring(4)
            val chunks = if (body.any { it.isLetter() }) {
                // 'q'(자동)·'m'(수동) 같은 구분 문자 바로 뒤 12자리만 게임으로 읽는다.
                // 마지막 게임 뒤에 붙는 발권 일련번호 등 꼬리 숫자는 게임이 아니다.
                GAME_SEGMENT.findAll(body).map { it.groupValues[1] }.toList()
            } else {
                // 구분 문자가 없는 값은 12자리씩 자르고, 남는 메타데이터는 무시
                body.filter { it.isDigit() }.chunked(12).filter { it.length == 12 }
            }

            // 3. 2자리씩 -> Int 리스트로 변환
            val games = chunks.mapNotNull { chunk ->
                val numbers = chunk
                    .chunked(2)               // "021825303444" -> ["02","18",...]
                    .mapNotNull { it.toIntOrNull() }
                    .filter { it != 0 }       // 00은 미사용 자리라 버림

                numbers.takeIf { it.isNotEmpty() && it.all { number -> number in LOTTO_NUMBERS } }
            }

            if (games.isEmpty()) return null

            return LottoQrResult(drawNo, games)
        }
    }
}
