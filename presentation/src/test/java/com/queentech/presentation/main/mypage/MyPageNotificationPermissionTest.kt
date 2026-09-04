package com.queentech.presentation.main.mypage

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MyPageNotificationPermissionTest {

    @Test
    fun `Android 13 이상에서 알림이 꺼져 있고 안내 전이면 표시한다`() {
        assertTrue(
            shouldShowNotificationPermissionPrompt(
                sdkInt = 33,
                notificationsEnabled = false,
                promptShown = false,
            )
        )
    }

    @Test
    fun `안내 상태를 불러오기 전에는 표시하지 않는다`() {
        assertFalse(
            shouldShowNotificationPermissionPrompt(
                sdkInt = 33,
                notificationsEnabled = false,
                promptShown = null,
            )
        )
    }

    @Test
    fun `이미 안내했거나 알림이 켜져 있으면 표시하지 않는다`() {
        assertFalse(
            shouldShowNotificationPermissionPrompt(
                sdkInt = 33,
                notificationsEnabled = false,
                promptShown = true,
            )
        )
        assertFalse(
            shouldShowNotificationPermissionPrompt(
                sdkInt = 33,
                notificationsEnabled = true,
                promptShown = false,
            )
        )
    }

    @Test
    fun `Android 12 이하에서는 시스템 권한 안내를 표시하지 않는다`() {
        assertFalse(
            shouldShowNotificationPermissionPrompt(
                sdkInt = 32,
                notificationsEnabled = false,
                promptShown = false,
            )
        )
    }
}
