package com.queentech.data.usecase.lotto

import com.queentech.data.model.login.GetUserRequestBody
import com.queentech.data.model.service.LottoService
import com.queentech.domain.model.lotto.GetExpectNumber
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GetExpectNumberUseCaseImplTest {

    private val lottoService: LottoService = mockk()
    private val useCase = GetExpectNumberUseCaseImpl(lottoService)

    @Test
    fun `invoke returns success with expected numbers`() = runTest {
        // Arrange
        val expected = GetExpectNumber(count = 5, lotto = listOf("1,2,3,4,5,6"))
        coEvery { lottoService.getExpectNumber(any()) } returns expected

        // Act
        val result = useCase.invoke(email = "test@test.com", phone = "01012345678")

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(5, result.getOrThrow().count)
        coVerify {
            lottoService.getExpectNumber(
                GetUserRequestBody(email = "test@test.com", phone = "01012345678")
            )
        }
    }

    @Test
    fun `invoke returns failure when service throws`() = runTest {
        // Arrange
        coEvery { lottoService.getExpectNumber(any()) } throws RuntimeException("Server error")

        // Act
        val result = useCase.invoke(email = "test@test.com", phone = "01012345678")

        // Assert
        assertTrue(result.isFailure)
        assertEquals("Server error", result.exceptionOrNull()?.message)
    }
}
