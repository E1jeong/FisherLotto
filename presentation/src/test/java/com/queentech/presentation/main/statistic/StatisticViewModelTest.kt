package com.queentech.presentation.main.statistic

import com.queentech.domain.model.lotto.GetLottoNumber
import com.queentech.domain.model.lotto.GetLottoStats
import com.queentech.domain.usecase.lotto.GetLottoNumberUseCase
import com.queentech.domain.usecase.lotto.GetLottoStatsUseCase
import com.queentech.presentation.login.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.orbitmvi.orbit.test.Item
import org.orbitmvi.orbit.test.test

@OptIn(ExperimentalCoroutinesApi::class)
class StatisticViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getLottoNumberUseCase: GetLottoNumberUseCase = mockk()
    private val getLottoStatsUseCase: GetLottoStatsUseCase = mockk()

    @Test
    fun `발급 통계 재시도는 동행복권 목록을 유지한다`() = runTest {
        var isRetry = false
        coEvery { getLottoNumberUseCase(any()) } answers {
            val requestedRound = firstArg<Int>()
            Result.success(createLotto(if (requestedRound == 0) 5 else requestedRound))
        }
        coEvery { getLottoStatsUseCase(any()) } answers {
            Result.success(createStats(firstArg(), grade1 = if (isRetry) 99 else 1))
        }

        val viewModel = StatisticViewModel(getLottoNumberUseCase, getLottoStatsUseCase)

        viewModel.test(this) {
            expectInitialState()
            runOnCreate()

            var initialLottoList = emptyList<GetLottoNumber>()
            while (initialLottoList.isEmpty()) {
                val item = awaitItem()
                if (item is Item.StateItem && !item.value.isLoading) {
                    initialLottoList = item.value.lottoList
                }
            }

            isRetry = true
            viewModel.retryIssuedStats()

            var retriedState: StatisticState? = null
            while (retriedState == null) {
                val item = awaitItem()
                if (
                    item is Item.StateItem &&
                    !item.value.isLoading &&
                    item.value.issuedStatsList.all { it.grade1 == 99 }
                ) {
                    retriedState = item.value
                }
            }

            assertEquals(initialLottoList, retriedState.lottoList)
            cancelAndIgnoreRemainingItems()
        }
    }

    private fun createLotto(round: Int) = GetLottoNumber(
        firstCount = "1",
        firstMoney = "1",
        secondCount = "1",
        secondMoney = "1",
        thirdCount = "1",
        thirdMoney = "1",
        fourthCount = "1",
        fourthMoney = "1",
        fifthCount = "1",
        fifthMoney = "1",
        bonus = "7",
        num1 = "1",
        num2 = "2",
        num3 = "3",
        num4 = "4",
        num5 = "5",
        num6 = "6",
        pdate = "2026-09-05",
        round = round.toString(),
    )

    private fun createStats(round: Int, grade1: Int) = GetLottoStats(
        round = round.toString(),
        pdate = "2026-09-05",
        grade1 = grade1,
        grade2 = 0,
        grade3 = 0,
        grade4 = 0,
        grade5 = 0,
        combiCount = 10,
    )
}
