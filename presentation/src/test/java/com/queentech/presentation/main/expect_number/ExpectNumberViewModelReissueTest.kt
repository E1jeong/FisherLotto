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

/**
 * 구독 결제로 서버가 이번주 예상번호를 재발급했을 때(reissuePending = true),
 * 예상번호 화면이 로컬 기록을 지우고 발급 버튼을 다시 활성화하는지 검증한다.
 */
class ExpectNumberViewModelReissueTest {

    private val userRepository: UserRepository = mockk(relaxed = true)
    private val getExpectNumberUseCase: GetExpectNumberUseCase = mockk(relaxed = true)
    private val getLottoNumberUseCase: GetLottoNumberUseCase = mockk(relaxed = true)
    private val lottoIssueRepository: LottoIssueRepository = mockk(relaxed = true)
    private val billingRepository: BillingRepository = mockk(relaxed = true)

    // Room 대역. deleteWeek 호출이 실제로 조회 결과에 반영되도록 상태를 들고 있는다.
    private var storedThisWeek = listOf("1,2,3,4,5,6")

    private val reissueToast =
        ExpectNumberSideEffect.Toast("구독 혜택이 적용되었습니다. 다시 발급해 주세요")

    private fun viewModel(): ExpectNumberViewModel {
        every { userRepository.currentUser } returns MutableStateFlow(null)
        every { billingRepository.subscriptionStatus } returns emptyFlow()
        coEvery { lottoIssueRepository.getThisWeekNumbers(any()) } answers { storedThisWeek }
        coEvery { lottoIssueRepository.getLastWeekNumbers(any()) } returns emptyList()
        coEvery { lottoIssueRepository.deleteWeek(any()) } answers { storedThisWeek = emptyList() }

        return ExpectNumberViewModel(
            userRepository = userRepository,
            getExpectNumberUseCase = getExpectNumberUseCase,
            getLottoNumberUseCase = getLottoNumberUseCase,
            lottoIssueRepository = lottoIssueRepository,
            billingRepository = billingRepository,
        )
    }

    @Test
    fun `pending 재발급이 있으면 이번주 기록을 지우고 버튼을 다시 활성화한다`() = runTest {
        every { billingRepository.reissuePending } returns flowOf(true)
        val vm = viewModel()

        vm.test(this) {
            expectInitialState()
            runOnCreate()

            // 상태 방출 순서는 intent 병렬 실행에 따라 달라지므로, 토스트가 나올 때까지 훑는다.
            var toastSeen = false
            while (!toastSeen) {
                when (val item = awaitItem()) {
                    is Item.SideEffectItem -> {
                        assertEquals(reissueToast, item.value)
                        toastSeen = true
                    }
                    is Item.StateItem -> Unit
                }
            }
            cancelAndIgnoreRemainingItems()
        }

        coVerify(exactly = 1) { lottoIssueRepository.deleteWeek(any()) }
        coVerify(exactly = 1) { billingRepository.clearReissuePending() }
        // 로컬 기록 조회와 삭제가 어떤 순서로 끼어들어도 최종 상태는 "미발급"으로 수렴해야 한다.
        assertFalse(vm.container.stateFlow.value.isThisWeekIssued)
        assertTrue(vm.container.stateFlow.value.thisWeekNumbers.isEmpty())
    }

    @Test
    fun `pending이 없으면 기록을 지우지 않는다`() = runTest {
        every { billingRepository.reissuePending } returns flowOf(false)

        viewModel().test(this) {
            expectInitialState()
            runOnCreate()

            // 저장된 번호를 그대로 읽어 발급 상태로 남는지 확인한다.
            var issuedStateSeen = false
            while (!issuedStateSeen) {
                val item = awaitItem()
                if (item is Item.StateItem && item.value.isThisWeekIssued) issuedStateSeen = true
            }
            cancelAndIgnoreRemainingItems()
        }

        coVerify(exactly = 0) { lottoIssueRepository.deleteWeek(any()) }
        coVerify(exactly = 0) { billingRepository.clearReissuePending() }
    }
}
