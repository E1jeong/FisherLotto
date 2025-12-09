package com.queentech.presentation.main.camera

import android.net.Uri
import android.util.Log

data class LottoQrResult(
    val drawNo: Int,
    val games: List<List<Int>>,
) {
    companion object {
        fun parse(raw: String): LottoQrResult? {
            val uri = runCatching { Uri.parse(raw) }.getOrNull() ?: return null
            val value = uri.getQueryParameter("v") ?: return null
            Log.d("LottoQr", "v=$value")
            if (value.length < 4) return null

            // 1. 회차
            val drawNo = value.substring(0, 4).toIntOrNull() ?: return null

            // 2. 나머지에서 숫자만 추출
            val digitsOnly = value.substring(4)
                .filter { it.isDigit() }   // 'm', 'q' 같은 문자 전부 제거
            Log.d("LottoQr", "digitsOnly=$digitsOnly")
            Log.d("LottoQr", "digitChunks=${digitsOnly.chunked(12)}")

            if (digitsOnly.length < 12) return null  // 최소 한 게임도 안될 때

            // 3. 12자리씩 잘라서 -> 2자리씩 -> Int 리스트로 변환
            val games = digitsOnly
                .chunked(12)                      // 한 게임(최대 6번호)
                .mapNotNull { chunk ->
                    if (chunk.length < 12) {
                        // 마지막에 남는 4자리 같은 메타데이터(예: 2233)는 무시
                        return@mapNotNull null
                    }

                    val numbers = chunk
                        .chunked(2)               // "021825303444" -> ["02","18",...]
                        .mapNotNull { it.toIntOrNull() }
                        .filter { it != 0 }       // 00은 미사용 자리라 버림

                    numbers.takeIf { it.isNotEmpty() }
                }

            if (games.isEmpty()) return null

            return LottoQrResult(drawNo, games)
        }
    }
}
