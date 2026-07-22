package com.queentech.data.usecase.lotto

import com.queentech.data.model.lotto.GetLottoNumberResponse
import com.queentech.data.model.service.SubLottoService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GetLottoNumberUseCaseImplTest {

    private val subLottoService: SubLottoService = mockk()
    private val useCase = GetLottoNumberUseCaseImpl(subLottoService)

    private fun createResponse(round: Int = 1210) = GetLottoNumberResponse(
        firstCount = "10", firstMoney = "2000000000",
        secondCount = "50", secondMoney = "60000000",
        thirdCount = "2000", thirdMoney = "1500000",
        fourthCount = "100000", fourthMoney = "50000",
        fifthCount = "2000000", fifthMoney = "5000",
        bonus = "7",
        num1 = "3", num2 = "12", num3 = "18",
        num4 = "27", num5 = "33", num6 = "45",
        pdate = "2026-02-07", round = round,
    )

    @Test
    fun `invoke returns success with mapped domain model`() = runTest {
        // Arrange
        coEvery { subLottoService.getNumber(round = 1210) } returns createResponse()

        // Act
        val result = useCase.invoke(1210)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(1210, result.getOrThrow().roundInt)
        assertEquals(3, result.getOrThrow().num1Int)
        coVerify { subLottoService.getNumber(round = 1210) }
    }

    @Test
    fun `invoke returns failure when service throws exception`() = runTest {
        // Arrange
        coEvery { subLottoService.getNumber(round = any()) } throws RuntimeException("Network error")

        // Act
        val result = useCase.invoke(9999)

        // Assert
        assertTrue(result.isFailure)
        assertEquals("Network error", result.exceptionOrNull()?.message)
    }

    @Test
    fun `invoke passes correct round parameter to service`() = runTest {
        // Arrange
        coEvery { subLottoService.getNumber(round = 500) } returns createResponse(round = 500)

        // Act
        val result = useCase.invoke(500)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(500, result.getOrThrow().roundInt)
        coVerify(exactly = 1) { subLottoService.getNumber(round = 500) }
    }
}
