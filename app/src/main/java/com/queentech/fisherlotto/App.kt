package com.queentech.fisherlotto

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {

    companion object {
        const val CHANNEL_ID = "fisherlotto_default"
    }

    override fun onCreate() {
        super.onCreate()

        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "FisherLotto 알림",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "FisherLotto 서비스 알림"
        }
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }
}
