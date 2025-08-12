package com.queentech.presentation.main.camera

import android.net.Uri

data class LottoQrResult(
    val drawNo: Int,
    val games: List<List<Int>>,
) {
    companion object {
        fun parse(raw: String): LottoQrResult? {
            val uri = runCatching { Uri.parse(raw) }.getOrNull() ?: return null
            val value = uri.getQueryParameter("v") ?: return null
            if (value.length < 4) return null

            val drawNo = value.substring(0, 4).toIntOrNull() ?: return null
            val numbersPart = value.substring(4)
            val games = numbersPart.chunked(12).mapNotNull { chunk ->
                val numbers = chunk.chunked(2)
                    .mapNotNull { it.toIntOrNull() }
                    .filter { it != 0 }
                numbers.ifEmpty { null }
            }
            return LottoQrResult(drawNo, games)
        }
    }
}