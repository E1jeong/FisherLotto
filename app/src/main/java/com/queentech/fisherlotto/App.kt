package com.queentech.fisherlotto

import android.app.Application
import com.kakao.sdk.common.KakaoSdk
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {
    override fun onCreate() {
        super.onCreate()

        KakaoSdk.init(this, "e5fa846df764dbac35336e403aca8222")
    }
}