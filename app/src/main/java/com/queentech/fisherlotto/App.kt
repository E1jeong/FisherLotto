package com.queentech.fisherlotto

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import com.kakao.sdk.common.KakaoSdk
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {

    companion object {
        const val CHANNEL_ID = "fisherlotto_default"
    }

    override fun onCreate() {
        super.onCreate()

        KakaoSdk.init(this, "e5fa846df764dbac35336e403aca8222")
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
