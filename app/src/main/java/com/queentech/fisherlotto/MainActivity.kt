package com.queentech.fisherlotto

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.queentech.domain.usecase.billing.BillingRepository
import com.queentech.fisherlotto.navigation.NavigationHost
import com.queentech.fisherlotto.worker.SubscriptionExpiryWorker
import com.queentech.presentation.theme.FisherLottoTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var billingRepository: BillingRepository

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            FisherLottoTheme {
                NavigationHost()
            }
        }

        lifecycleScope.launch {
            billingRepository.subscriptionStatus.collect { status ->
                status.expiryTimeMillis?.let { scheduleExpiryNotification(it) }
            }
        }
    }

    private fun scheduleExpiryNotification(expiryTimeMillis: Long) {
        val delay = expiryTimeMillis - System.currentTimeMillis() - TimeUnit.DAYS.toMillis(3)
        if (delay <= 0) return
        val request = OneTimeWorkRequestBuilder<SubscriptionExpiryWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .build()
        WorkManager.getInstance(this)
            .enqueueUniqueWork(SubscriptionExpiryWorker.WORK_NAME, ExistingWorkPolicy.REPLACE, request)
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            billingRepository.refreshSubscriptionStatus()
        }
    }
}
