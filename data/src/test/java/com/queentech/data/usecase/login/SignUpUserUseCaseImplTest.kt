package com.queentech.data.usecase.login

import com.queentech.data.model.login.SignUpUserRequestBody
import com.queentech.data.model.service.LottoService
import com.queentech.domain.model.common.CommonResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SignUpUserUseCaseImplTest {

    private val lottoService: LottoService = mockk()
    private val useCase = SignUpUserUseCaseImpl(lottoService)

    @Test
    fun `invoke returns success when sign up succeeds`() = runTest {
        // Arrange
        coEvery { lottoService.signUpUser(any()) } returns CommonResponse(status = "200")

        // Act
        val result = useCase.invoke(
            name = "홍길동",
            email = "hong@test.com",
            birth = "1990-01-01",
            phone = "01011112222",
        )

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(200, result.getOrThrow().statusInt)
        coVerify {
            lottoService.signUpUser(
                SignUpUserRequestBody(
                    name = "홍길동",
                    email = "hong@test.com",
                    birth = "1990-01-01",
                    phone = "01011112222",
                )
            )
        }
    }

    @Test
    fun `invoke returns failure when service throws`() = runTest {
        // Arrange
        coEvery { lottoService.signUpUser(any()) } throws RuntimeException("Connection refused")

        // Act
        val result = useCase.invoke(
            name = "홍길동",
            email = "hong@test.com",
            birth = "1990-01-01",
            phone = "01011112222",
        )

        // Assert
        assertTrue(result.isFailure)
        assertEquals("Connection refused", result.exceptionOrNull()?.message)
    }
}
