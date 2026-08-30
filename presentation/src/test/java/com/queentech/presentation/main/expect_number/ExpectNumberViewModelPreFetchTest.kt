package com.queentech.presentation.main.expect_number

import com.queentech.domain.model.billing.SubscriptionStatus
import com.queentech.domain.model.login.User
import com.queentech.domain.model.lotto.GetExpectNumber
import com.queentech.domain.usecase.billing.BillingRepository
import com.queentech.domain.usecase.login.UserRepository
import com.queentech.domain.usecase.lotto.GetExpectNumberUseCase
import com.queentech.domain.usecase.lotto.GetLottoNumberUseCase
import com.queentech.domain.usecase.lotto.LottoIssueRepository
import com.queentech.presentation.login.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.orbitmvi.orbit.test.Item
import org.orbitmvi.orbit.test.test

@OptIn(ExperimentalCoroutinesApi::class)
class ExpectNumberViewModelPreFetchTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val userRepository: UserRepository = mockk(relaxed = true)
    private val getExpectNumberUseCase: GetExpectNumberUseCase = mockk(relaxed = true)
    private val getLottoNumberUseCase: GetLottoNumberUseCase = mockk(relaxed = true)
    private val lottoIssueRepository: LottoIssueRepository = mockk(relaxed = true)
    private val billingRepository: BillingRepository = mockk(relaxed = true)

    private val testUser = User(
        name = "테스터",
        email = "test@fisherlotto.com",
        birth = "19900101",
        phone = "01012345678",
        tier = User.TIER_FREE,
    )

    private fun createViewModel(): ExpectNumberViewModel {
        every { userRepository.currentUser } returns MutableStateFlow(testUser)
        every { billingRepository.subscriptionStatus } returns emptyFlow()
        every { billingRepository.expectedNumberResetEvents } returns emptyFlow()
        coEvery { lottoIssueRepository.getThisWeekNumbers(any()) } returns emptyList()
        coEvery { lottoIssueRepository.getLastWeekNumbers(any()) } returns emptyList()
        coEvery { lottoIssueRepository.isThisWeekIssued(any()) } returns false

        return ExpectNumberViewModel(
            userRepository = userRepository,
            getExpectNumberUseCase = getExpectNumberUseCase,
            getLottoNumberUseCase = getLottoNumberUseCase,
            lottoIssueRepository = lottoIssueRepository,
            billingRepository = billingRepository,
        )
    }

    @Test
    fun `서버에 번호가 없으면 광고를 띄우지 않고 안내 토스트를 표시한다`() = runTest {
        coEvery { getExpectNumberUseCase(any(), any()) } returns Result.success(
            GetExpectNumber(count = 0, lotto = emptyList())
        )

        val vm = createViewModel()

        vm.test(this) {
            expectInitialState()
            runOnCreate()

            vm.onExpectNumberClick()

            var toastSeen = false
            while (!toastSeen) {
                when (val item = awaitItem()) {
                    is Item.SideEffectItem -> {
                        assertEquals(
                            ExpectNumberSideEffect.Toast("이번 회차 추천 번호가 아직 준비되지 않았습니다."),
                            item.value
                        )
                        toastSeen = true
                    }
                    is Item.StateItem -> Unit
                }
            }
            cancelAndIgnoreRemainingItems()
        }

        coVerify(exactly = 0) { lottoIssueRepository.saveIssue(any(), any()) }
        assertFalse(vm.container.stateFlow.value.isThisWeekIssued)
    }

    @Test
    fun `서버에 번호가 있으면 무료 회원은 광고 요청을 보낸다`() = runTest {
        val mockNumbers = listOf("1,2,3,4,5,6", "7,8,9,10,11,12")
        coEvery { getExpectNumberUseCase(any(), any()) } returns Result.success(
            GetExpectNumber(count = 2, lotto = mockNumbers)
        )
        every { billingRepository.subscriptionStatus } returns flowOf(
            SubscriptionStatus(isActive = false, productId = null, expiryTimeMillis = null, autoRenewing = false)
        )

        val vm = createViewModel()

        vm.test(this) {
            expectInitialState()
            runOnCreate()

            vm.onExpectNumberClick()

            var adSeen = false
            while (!adSeen) {
                when (val item = awaitItem()) {
                    is Item.SideEffectItem -> {
                        assertEquals(ExpectNumberSideEffect.ShowRewardAd, item.value)
                        adSeen = true
                    }
                    is Item.StateItem -> Unit
                }
            }
            cancelAndIgnoreRemainingItems()
        }
    }

    @Test
    fun `광고 시청 완료 후 사전 조회된 번호가 정상 저장 및 반영된다`() = runTest {
        val mockNumbers = listOf("1,2,3,4,5,6", "7,8,9,10,11,12")
        coEvery { getExpectNumberUseCase(any(), any()) } returns Result.success(
            GetExpectNumber(count = 2, lotto = mockNumbers)
        )

        val vm = createViewModel()

        vm.test(this) {
            expectInitialState()
            runOnCreate()

            vm.onExpectNumberClick()

            var adSeen = false
            while (!adSeen) {
                when (val item = awaitItem()) {
                    is Item.SideEffectItem -> {
                        if (item.value == ExpectNumberSideEffect.ShowRewardAd) {
                            adSeen = true
                        }
                    }
                    is Item.StateItem -> Unit
                }
            }

            vm.onAdWatchedSuccessfully()

            var issuedSeen = false
            while (!issuedSeen) {
                when (val item = awaitItem()) {
                    is Item.StateItem -> {
                        if (item.value.isThisWeekIssued) {
                            assertEquals(mockNumbers, item.value.thisWeekNumbers)
                            issuedSeen = true
                        }
                    }
                    is Item.SideEffectItem -> Unit
                }
            }
            cancelAndIgnoreRemainingItems()
        }

        coVerify(exactly = 1) { lottoIssueRepository.saveIssue(mockNumbers, any()) }
        assertTrue(vm.container.stateFlow.value.isThisWeekIssued)
    }
}
