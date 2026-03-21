package com.queentech.data.usecase.login

import com.queentech.data.model.login.GetUserRequestBody
import com.queentech.data.model.service.LottoService
import com.queentech.domain.model.common.CommonResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GetUserUseCaseImplTest {

    private val lottoService: LottoService = mockk()
    private val useCase = GetUserUseCaseImpl(lottoService)

    @Test
    fun `invoke returns success with common response`() = runTest {
        // Arrange
        coEvery { lottoService.getUser(any()) } returns CommonResponse(status = "200")

        // Act
        val result = useCase.invoke(email = "user@test.com", phone = "01098765432")

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(200, result.getOrThrow().statusInt)
        coVerify {
            lottoService.getUser(
                GetUserRequestBody(email = "user@test.com", phone = "01098765432")
            )
        }
    }

    @Test
    fun `invoke returns failure when service throws`() = runTest {
        // Arrange
        coEvery { lottoService.getUser(any()) } throws RuntimeException("Timeout")

        // Act
        val result = useCase.invoke(email = "user@test.com", phone = "01098765432")

        // Assert
        assertTrue(result.isFailure)
        assertEquals("Timeout", result.exceptionOrNull()?.message)
    }
}
