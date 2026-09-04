package com.queentech.presentation.main.mypage

import com.queentech.domain.model.billing.SubscriptionStatus
import com.queentech.domain.usecase.billing.BillingRepository
import com.queentech.domain.usecase.fcm.FcmRepository
import com.queentech.domain.usecase.login.UserRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.orbitmvi.orbit.test.Item
import org.orbitmvi.orbit.test.test

@OptIn(ExperimentalCoroutinesApi::class)
class MyPageViewModelTest {

    private val userRepository: UserRepository = mockk(relaxed = true)
    private val billingRepository: BillingRepository = mockk(relaxed = true)
    private val fcmRepository: FcmRepository = mockk(relaxed = true)
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `마이페이지 활성화 시 구독 상태를 갱신한다`() = runTest {
        every { userRepository.currentUser } returns MutableStateFlow(null)
        every { billingRepository.subscriptionStatus } returns emptyFlow()
        coEvery { billingRepository.querySubscriptionProducts() } returns Result.success(emptyList())
        coEvery { billingRepository.refreshSubscriptionStatus() } returns Result.success(
            SubscriptionStatus(false, null, null, false)
        )
        MyPageViewModel(userRepository, billingRepository, fcmRepository).refreshSubscriptionStatus()
        advanceUntilIdle()

        coVerify { billingRepository.refreshSubscriptionStatus() }
    }

    @Test
    fun `알림 권한 안내 선택 시 표시 이력을 저장한다`() = runTest {
        every { userRepository.currentUser } returns MutableStateFlow(null)
        every { billingRepository.subscriptionStatus } returns emptyFlow()
        every { billingRepository.expectedNumberResetEvents } returns emptyFlow()
        coEvery { billingRepository.querySubscriptionProducts() } returns Result.success(emptyList())
        coEvery { fcmRepository.hasShownNotificationPermissionPrompt() } returns false
        val viewModel = MyPageViewModel(userRepository, billingRepository, fcmRepository)

        viewModel.test(this) {
            expectInitialState()
            runOnCreate()

            while (true) {
                val item = awaitItem()
                if (item is Item.StateItem && item.value.notificationPermissionPromptShown == false) {
                    break
                }
            }

            viewModel.markNotificationPermissionPromptShown()

            while (true) {
                val item = awaitItem()
                if (item is Item.StateItem && item.value.notificationPermissionPromptShown == true) {
                    break
                }
            }
            cancelAndIgnoreRemainingItems()
        }

        coVerify(exactly = 1) { fcmRepository.markNotificationPermissionPromptShown() }
    }
}
