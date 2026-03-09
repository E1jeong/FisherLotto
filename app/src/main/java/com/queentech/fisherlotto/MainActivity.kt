package com.queentech.fisherlotto

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import com.queentech.domain.usecase.billing.BillingRepository
import com.queentech.fisherlotto.navigation.NavigationHost
import com.queentech.presentation.theme.FisherLottoTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
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
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            billingRepository.refreshSubscriptionStatus()
        }
    }
}
