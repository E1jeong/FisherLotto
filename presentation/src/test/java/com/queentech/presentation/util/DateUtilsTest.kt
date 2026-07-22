package com.queentech.presentation.util

import java.util.Calendar
import java.util.TimeZone
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DateUtilsTest {

    @Test
    fun `issue window is closed from saturday 7 pm`() {
        assertTrue(DateUtils.isIssueWindowClosed(calendarOf(Calendar.SATURDAY, 19, 0)))
    }

    @Test
    fun `issue window remains closed on sunday morning`() {
        assertTrue(DateUtils.isIssueWindowClosed(calendarOf(Calendar.SUNDAY, 11, 59)))
    }

    @Test
    fun `issue window opens at sunday noon`() {
        assertFalse(DateUtils.isIssueWindowClosed(calendarOf(Calendar.SUNDAY, 12, 0)))
    }

    @Test
    fun `issue window is open before saturday 7 pm`() {
        assertFalse(DateUtils.isIssueWindowClosed(calendarOf(Calendar.SATURDAY, 18, 59)))
    }

    private fun calendarOf(dayOfWeek: Int, hour: Int, minute: Int): Calendar {
        return Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul")).apply {
            clear()
            set(2026, Calendar.JULY, 19 + ((dayOfWeek - Calendar.SUNDAY + 7) % 7), hour, minute)
        }
    }
}
