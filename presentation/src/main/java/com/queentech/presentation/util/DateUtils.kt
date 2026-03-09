package com.queentech.presentation.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

object DateUtils {
    private const val KOREA_ZONE_ID = "Asia/Seoul"

    private fun getKoreaCalendar(): Calendar {
        return Calendar.getInstance(TimeZone.getTimeZone(KOREA_ZONE_ID))
    }

    /**
     * 이번 주 일요일 00:00:00 KST의 timestamp를 반환
     */
    fun getCurrentWeekStartMillis(): Long {
        val cal = getKoreaCalendar()
        val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
        val diff = dayOfWeek - Calendar.SUNDAY
        cal.add(Calendar.DAY_OF_YEAR, -diff)
        
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

    /**
     * 주어진 시작 시간(일요일)부터 토요일까지의 범위를 "yy.MM.dd ~ MM.dd" 형식으로 반환
     * 연도가 바뀌는 경우 "yy.MM.dd ~ yy.MM.dd" 형식으로 반환합니다.
     */
    fun getWeekRangeString(startMillis: Long): String {
        val sdfShortYear = SimpleDateFormat("yy.MM.dd", Locale.KOREA).apply {
            timeZone = TimeZone.getTimeZone(KOREA_ZONE_ID)
        }
        val sdfMonthDay = SimpleDateFormat("MM.dd", Locale.KOREA).apply {
            timeZone = TimeZone.getTimeZone(KOREA_ZONE_ID)
        }
        
        val startCal = getKoreaCalendar().apply { timeInMillis = startMillis }
        val endCal = getKoreaCalendar().apply {
            timeInMillis = startMillis
            add(Calendar.DAY_OF_YEAR, 6)
        }

        val startStr = sdfShortYear.format(startCal.time)
        val endStr = if (startCal.get(Calendar.YEAR) == endCal.get(Calendar.YEAR)) {
            // 같은 연도인 경우 월.일만 표시
            sdfMonthDay.format(endCal.time)
        } else {
            // 연도가 바뀌는 경우 연도 뒤 2자리까지 표시
            sdfShortYear.format(endCal.time)
        }

        return "$startStr ~ $endStr"
    }

    /**
     * 토요일 20:30 ~ 23:59 (KST) 인지 확인
     */
    fun isSaturdayDeadline(): Boolean {
        val cal = getKoreaCalendar()
        if (cal.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY) return false
        val hour = cal.get(Calendar.HOUR_OF_DAY)
        val minute = cal.get(Calendar.MINUTE)
        return (hour > 20 || (hour == 20 && minute >= 30))
    }
}