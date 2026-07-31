package com.queentech.presentation.main.home

import com.queentech.domain.model.lotto.GetLottoNumber
import com.queentech.domain.model.news.NewsArticle
import com.queentech.domain.usecase.lotto.GetLottoNumberUseCase
import com.queentech.domain.usecase.news.GetLotteryNewsUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test
import org.orbitmvi.orbit.test.Item
import org.orbitmvi.orbit.test.test

class HomeViewModelTest {

    private val getLottoNumberUseCase: GetLottoNumberUseCase = mockk()
    private val getLotteryNewsUseCase: GetLotteryNewsUseCase = mockk()

    @Test
    fun `당첨번호 응답을 기다리는 동안 뉴스 로딩을 막지 않는다`() = runTest {
        val lottoDeferred = CompletableDeferred<Result<GetLottoNumber>>()
        val lotto = lottoNumber()
        val news = listOf(
            NewsArticle(
                title = "로또 뉴스",
                link = "https://example.com",
                source = "테스트",
                publishedAtEpochMillis = 0L,
                summary = "요약",
            )
        )

        coEvery { getLottoNumberUseCase(round = 0) } coAnswers { lottoDeferred.await() }
        coEvery {
            getLotteryNewsUseCase(maxResults = 20, query = any(), forceRefresh = false)
        } returns Result.success(news)

        val viewModel = HomeViewModel(
            getLottoNumberUseCase = getLottoNumberUseCase,
            getLotteryNewsUseCase = getLotteryNewsUseCase,
        )

        viewModel.test(this) {
            expectInitialState()
            runOnCreate()

            var newsLoaded = false
            while (!newsLoaded) {
                val item = awaitItem()
                if (item is Item.StateItem && item.value.news == news) {
                    newsLoaded = true
                }
            }

            assertFalse(lottoDeferred.isCompleted)

            lottoDeferred.complete(Result.success(lotto))
            var lottoLoaded = false
            while (!lottoLoaded) {
                val item = awaitItem()
                if (item is Item.StateItem && item.value.getLottoNumberResponse == lotto) {
                    lottoLoaded = true
                }
            }

            cancelAndIgnoreRemainingItems()
        }

        assertEquals(news, viewModel.container.stateFlow.value.news)
        assertEquals(lotto, viewModel.container.stateFlow.value.getLottoNumberResponse)
    }

    private fun lottoNumber() = GetLottoNumber(
        firstCount = "10",
        firstMoney = "2000000000",
        secondCount = "50",
        secondMoney = "60000000",
        thirdCount = "2000",
        thirdMoney = "1500000",
        fourthCount = "100000",
        fourthMoney = "50000",
        fifthCount = "2000000",
        fifthMoney = "5000",
        bonus = "7",
        num1 = "3",
        num2 = "12",
        num3 = "18",
        num4 = "27",
        num5 = "33",
        num6 = "45",
        pdate = "2026-02-07",
        round = "1210",
    )
}
