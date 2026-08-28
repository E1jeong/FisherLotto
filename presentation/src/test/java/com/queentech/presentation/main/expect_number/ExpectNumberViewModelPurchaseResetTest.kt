package com.queentech.presentation.main.expect_number

import com.queentech.domain.usecase.billing.BillingRepository
import com.queentech.domain.usecase.login.UserRepository
import com.queentech.domain.usecase.lotto.GetExpectNumberUseCase
import com.queentech.domain.usecase.lotto.GetLottoNumberUseCase
import com.queentech.domain.usecase.lotto.LottoIssueRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.orbitmvi.orbit.test.Item
import org.orbitmvi.orbit.test.test

class ExpectNumberViewModelPurchaseResetTest {

    private val userRepository: UserRepository = mockk(relaxed = true)
    private val getExpectNumberUseCase: GetExpectNumberUseCase = mockk(relaxed = true)
    private val getLottoNumberUseCase: GetLottoNumberUseCase = mockk(relaxed = true)
    private val lottoIssueRepository: LottoIssueRepository = mockk(relaxed = true)
    private val billingRepository: BillingRepository = mockk(relaxed = true)

    private val resetToast =
        ExpectNumberSideEffect.Toast("구독 혜택이 적용되었습니다. 다시 발급해 주세요")

    private fun viewModel(storedThisWeek: List<String>): ExpectNumberViewModel {
        every { userRepository.currentUser } returns MutableStateFlow(null)
        every { billingRepository.subscriptionStatus } returns emptyFlow()
        coEvery { lottoIssueRepository.getThisWeekNumbers(any()) } returns storedThisWeek
        coEvery { lottoIssueRepository.getLastWeekNumbers(any()) } returns emptyList()

        return ExpectNumberViewModel(
            userRepository = userRepository,
            getExpectNumberUseCase = getExpectNumberUseCase,
            getLottoNumberUseCase = getLottoNumberUseCase,
            lottoIssueRepository = lottoIssueRepository,
            billingRepository = billingRepository,
        )
    }

    @Test
    fun `신규 결제 캐시 초기화 이벤트가 오면 미발급 상태와 안내를 표시한다`() = runTest {
        every { billingRepository.expectedNumberResetEvents } returns flowOf(Unit)
        val vm = viewModel(storedThisWeek = emptyList())

        vm.test(this) {
            expectInitialState()
            runOnCreate()

            var toastSeen = false
            while (!toastSeen) {
                when (val item = awaitItem()) {
                    is Item.SideEffectItem -> {
                        assertEquals(resetToast, item.value)
                        toastSeen = true
                    }
                    is Item.StateItem -> Unit
                }
            }
            cancelAndIgnoreRemainingItems()
        }

        coVerify(exactly = 0) { lottoIssueRepository.deleteWeek(any()) }
        assertFalse(vm.container.stateFlow.value.isThisWeekIssued)
        assertTrue(vm.container.stateFlow.value.thisWeekNumbers.isEmpty())
    }

    @Test
    fun `초기화 이벤트가 없으면 저장된 번호를 유지한다`() = runTest {
        every { billingRepository.expectedNumberResetEvents } returns emptyFlow()

        viewModel(storedThisWeek = listOf("1,2,3,4,5,6")).test(this) {
            expectInitialState()
            runOnCreate()

            var issuedStateSeen = false
            while (!issuedStateSeen) {
                val item = awaitItem()
                if (item is Item.StateItem && item.value.isThisWeekIssued) issuedStateSeen = true
            }
            cancelAndIgnoreRemainingItems()
        }

        coVerify(exactly = 0) { lottoIssueRepository.deleteWeek(any()) }
    }
}
