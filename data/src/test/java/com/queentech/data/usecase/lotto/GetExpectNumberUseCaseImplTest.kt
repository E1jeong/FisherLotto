package com.queentech.data.usecase.lotto

import com.queentech.data.model.login.GetUserRequestBody
import com.queentech.data.model.service.SubLottoService
import com.queentech.domain.model.lotto.GetExpectNumber
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GetExpectNumberUseCaseImplTest {

    private val subLottoService: SubLottoService = mockk()
    private val useCase = GetExpectNumberUseCaseImpl(subLottoService)

    @Test
    fun `invoke returns success with expected numbers`() = runTest {
        // Arrange
        val expected = GetExpectNumber(count = 5, lotto = listOf("1,2,3,4,5,6"))
        coEvery { subLottoService.getExpectNumber(any()) } returns expected

        // Act
        val result = useCase.invoke(email = "test@test.com", phone = "01012345678")

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(5, result.getOrThrow().count)
        coVerify {
            subLottoService.getExpectNumber(
                GetUserRequestBody(email = "test@test.com", phone = "01012345678")
            )
        }
    }

    @Test
    fun `invoke returns failure when service throws`() = runTest {
        // Arrange
        coEvery { subLottoService.getExpectNumber(any()) } throws RuntimeException("Server error")

        // Act
        val result = useCase.invoke(email = "test@test.com", phone = "01012345678")

        // Assert
        assertTrue(result.isFailure)
        assertEquals("Server error", result.exceptionOrNull()?.message)
    }
}
