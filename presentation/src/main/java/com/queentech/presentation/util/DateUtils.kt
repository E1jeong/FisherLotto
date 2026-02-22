package com.queentech.presentation.util

import java.util.Calendar
import java.util.TimeZone

object DateUtils {
    private fun getKoreaCalendar(): Calendar {
        return Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"))
    }

    /**
     * 이번 주 일요일 00:00:00 KST의 timestamp를 반환
     *
     * 예: 현재가 2026-02-25 (수요일) → 2026-02-22 (일요일) 00:00 반환
     * 예: 현재가 2026-02-22 (일요일) → 2026-02-22 (일요일) 00:00 반환
     */
    fun getCurrentWeekStartMillis(): Long {
        val cal = getKoreaCalendar()
        // 이번 주 일요일로 이동 (Calendar.SUNDAY = 1)
        val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK) // 일=1, 월=2, ..., 토=7
        // 현재 요일에서 일요일까지의 차이
        val diff = dayOfWeek - Calendar.SUNDAY // 일요일이면 0, 월요일이면 1, ... 토요일이면 6
        cal.add(Calendar.DAY_OF_YEAR, -diff)
        // 시간을 00:00:00.000으로 초기화
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    /**
     * 저번 주 일요일 00:00:00 KST의 timestamp를 반환
     */
    fun getLastWeekStartMillis(): Long {
        val cal = getKoreaCalendar()
        cal.timeInMillis = getCurrentWeekStartMillis()
        cal.add(Calendar.WEEK_OF_YEAR, -1)
        return cal.timeInMillis
    }

    /**
     * 2주 전 일요일 00:00:00 KST (정리 기준선)
     */
    fun getCutoffWeekStartMillis(): Long {
        val cal = getKoreaCalendar()
        cal.timeInMillis = getCurrentWeekStartMillis()
        cal.add(Calendar.WEEK_OF_YEAR, -2)
        return cal.timeInMillis
    }
}