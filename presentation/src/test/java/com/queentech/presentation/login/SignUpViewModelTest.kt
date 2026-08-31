package com.queentech.presentation.login

import com.queentech.domain.model.common.CommonResponse
import com.queentech.domain.model.login.User
import com.queentech.domain.model.login.SignUpException
import com.queentech.domain.model.login.SignUpResultStatus
import com.queentech.domain.model.login.EmailVerificationPurpose
import com.queentech.domain.usecase.login.UserRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.orbitmvi.orbit.test.Item
import org.orbitmvi.orbit.test.test

@OptIn(ExperimentalCoroutinesApi::class)
class SignUpViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val userRepository: UserRepository = mockk(relaxed = true)

    @Test
    fun `verified proof is passed only to sign up`() = runTest {
        coEvery { userRepository.sendVerificationCode(any(), any()) } returns
            Result.success(CommonResponse(status = "8200"))
        coEvery { userRepository.verifyEmailCode(any(), any(), any()) } returns
            Result.success(
                CommonResponse(
                    status = "8200",
                    verificationToken = "one-time-proof",
                )
            )
        coEvery { userRepository.signUp(any(), any(), any(), any(), any()) } returns
            Result.failure(IllegalStateException("stop after verification"))
        val viewModel = SignUpViewModel(userRepository)

        viewModel.test(this) {
            expectInitialState()
            viewModel.onSendVerificationCode("user@example.com")
            while (true) {
                val item = awaitItem()
                if (item is Item.StateItem && item.value.emailVerificationStep == EmailVerificationStep.CODE_SENT) break
            }
            viewModel.onVerifyEmailCode("user@example.com", "123456")
            while (true) {
                val item = awaitItem()
                if (item is Item.StateItem && item.value.emailVerificationStep == EmailVerificationStep.VERIFIED) break
            }
            viewModel.onSignUpSubmitClick(
                inputName = "홍길동",
                inputEmail = "user@example.com",
                inputBirth = "19900101",
                inputPhone = "01011112222",
            )
            var failureSeen = false
            while (!failureSeen) {
                val item = awaitItem()
                failureSeen = item is Item.SideEffectItem &&
                    item.value == SignUpSideEffect.Toast("stop after verification")
            }
            cancelAndIgnoreRemainingItems()
        }

        coVerify {
            userRepository.sendVerificationCode(
                "user@example.com",
                EmailVerificationPurpose.REGISTRATION,
            )
        }
        coVerify {
            userRepository.verifyEmailCode(
                "user@example.com",
                "123456",
                EmailVerificationPurpose.REGISTRATION,
            )
        }
        coVerify {
            userRepository.signUp(
                name = "홍길동",
                email = "user@example.com",
                birth = "19900101",
                phone = "01011112222",
                verificationToken = "one-time-proof",
            )
        }
    }

    @Test
    fun `changing email clears verification and prevents sign up`() = runTest {
        coEvery { userRepository.sendVerificationCode(any(), any()) } returns
            Result.success(CommonResponse(status = "8200"))
        coEvery { userRepository.verifyEmailCode(any(), any(), any()) } returns
            Result.success(
                CommonResponse(
                    status = "8200",
                    verificationToken = "one-time-proof",
                )
            )
        val viewModel = SignUpViewModel(userRepository)

        viewModel.test(this) {
            expectInitialState()
            viewModel.onSendVerificationCode("user@example.com")
            while (true) {
                val item = awaitItem()
                if (item is Item.StateItem && item.value.emailVerificationStep == EmailVerificationStep.CODE_SENT) break
            }
            viewModel.onVerifyEmailCode("user@example.com", "123456")
            while (true) {
                val item = awaitItem()
                if (item is Item.StateItem && item.value.emailVerificationStep == EmailVerificationStep.VERIFIED) break
            }
            viewModel.onEmailChanged("other@example.com")
            while (true) {
                val item = awaitItem()
                if (item is Item.StateItem && item.value.emailVerificationStep == EmailVerificationStep.IDLE) break
            }
            viewModel.onSignUpSubmitClick(
                inputName = "홍길동",
                inputEmail = "other@example.com",
                inputBirth = "19900101",
                inputPhone = "01011112222",
            )
            var warningSeen = false
            while (!warningSeen) {
                val item = awaitItem()
                warningSeen = item is Item.SideEffectItem &&
                    item.value == SignUpSideEffect.Toast("이메일 인증을 완료해주세요.")
            }
            cancelAndIgnoreRemainingItems()
        }

        coVerify(exactly = 0) { userRepository.signUp(any(), any(), any(), any(), any()) }
    }

    @Test
    fun `fifth invalid code shows attempts exceeded state`() = runTest {
        coEvery { userRepository.sendVerificationCode(any(), any()) } returns
            Result.success(CommonResponse(status = "8200"))
        coEvery { userRepository.verifyEmailCode(any(), any(), any()) } returns
            Result.success(CommonResponse(status = "8702"))
        val viewModel = SignUpViewModel(userRepository)

        viewModel.test(this) {
            expectInitialState()
            viewModel.onSendVerificationCode("user@example.com")
            while (true) {
                val item = awaitItem()
                if (item is Item.StateItem && item.value.emailVerificationStep == EmailVerificationStep.CODE_SENT) break
            }
            viewModel.onVerifyEmailCode("user@example.com", "123456")
            while (true) {
                val item = awaitItem()
                if (item is Item.StateItem && item.value.emailVerificationStep == EmailVerificationStep.EXPIRED) break
            }
            cancelAndIgnoreRemainingItems()
        }

        assertEquals(
            "인증번호 입력 횟수를 초과했습니다. 새 인증번호를 받아주세요.",
            viewModel.container.stateFlow.value.verificationError,
        )
    }

    @Test
    fun `expired registration proof clears verified state`() = runTest {
        coEvery { userRepository.sendVerificationCode(any(), any()) } returns
            Result.success(CommonResponse(status = "8200"))
        coEvery { userRepository.verifyEmailCode(any(), any(), any()) } returns
            Result.success(
                CommonResponse(
                    status = "8200",
                    verificationToken = "one-time-proof",
                )
            )
        coEvery { userRepository.signUp(any(), any(), any(), any(), any()) } returns
            Result.failure(
                SignUpException(
                    status = SignUpResultStatus.EMAIL_PROOF_INVALID,
                    message = "이메일 인증을 다시 진행해주세요.",
                )
            )
        val viewModel = SignUpViewModel(userRepository)

        viewModel.test(this) {
            expectInitialState()
            viewModel.onSendVerificationCode("user@example.com")
            while (true) {
                val item = awaitItem()
                if (item is Item.StateItem && item.value.emailVerificationStep == EmailVerificationStep.CODE_SENT) break
            }
            viewModel.onVerifyEmailCode("user@example.com", "123456")
            while (true) {
                val item = awaitItem()
                if (item is Item.StateItem && item.value.emailVerificationStep == EmailVerificationStep.VERIFIED) break
            }
            viewModel.onSignUpSubmitClick(
                inputName = "홍길동",
                inputEmail = "user@example.com",
                inputBirth = "19900101",
                inputPhone = "01011112222",
            )
            while (true) {
                val item = awaitItem()
                if (item is Item.StateItem && item.value.emailVerificationStep == EmailVerificationStep.IDLE) break
            }
            cancelAndIgnoreRemainingItems()
        }

        assertEquals(
            "이메일 인증이 만료되었습니다. 다시 인증해주세요.",
            viewModel.container.stateFlow.value.verificationError,
        )
    }

    @Test
    fun `rapid double submit is guarded and calls signUp once`() = runTest {
        coEvery { userRepository.sendVerificationCode(any(), any()) } returns
            Result.success(CommonResponse(status = "8200"))
        coEvery { userRepository.verifyEmailCode(any(), any(), any()) } returns
            Result.success(
                CommonResponse(
                    status = "8200",
                    verificationToken = "one-time-proof",
                )
            )
        coEvery { userRepository.signUp(any(), any(), any(), any(), any()) } returns
            Result.success(user())
        val viewModel = SignUpViewModel(userRepository)

        viewModel.test(this) {
            expectInitialState()
            viewModel.onSendVerificationCode("user@example.com")
            while (true) {
                val item = awaitItem()
                if (item is Item.StateItem && item.value.emailVerificationStep == EmailVerificationStep.CODE_SENT) break
            }
            viewModel.onVerifyEmailCode("user@example.com", "123456")
            while (true) {
                val item = awaitItem()
                if (item is Item.StateItem && item.value.emailVerificationStep == EmailVerificationStep.VERIFIED) break
            }
            // Rapid double submit
            viewModel.onSignUpSubmitClick(
                inputName = "홍길동",
                inputEmail = "user@example.com",
                inputBirth = "19900101",
                inputPhone = "01011112222",
            )
            viewModel.onSignUpSubmitClick(
                inputName = "홍길동",
                inputEmail = "user@example.com",
                inputBirth = "19900101",
                inputPhone = "01011112222",
            )
            while (true) {
                val item = awaitItem()
                if (item is Item.StateItem && item.value.isSignUpComplete) break
            }
            cancelAndIgnoreRemainingItems()
        }

        coVerify(exactly = 1) {
            userRepository.signUp(
                name = "홍길동",
                email = "user@example.com",
                birth = "19900101",
                phone = "01011112222",
                verificationToken = "one-time-proof",
            )
        }
    }

    @Test
    fun `resend verification code resets timer and handles failure safely`() = runTest {
        coEvery {
            userRepository.sendVerificationCode(
                "user@example.com",
                EmailVerificationPurpose.REGISTRATION,
            )
        } returns
            Result.success(CommonResponse(status = "8200"))
        val viewModel = SignUpViewModel(userRepository)

        viewModel.test(this) {
            expectInitialState()
            viewModel.onSendVerificationCode("user@example.com")
            while (true) {
                val item = awaitItem()
                if (item is Item.StateItem && item.value.emailVerificationStep == EmailVerificationStep.CODE_SENT) break
            }

            // Resend fails
            coEvery {
                userRepository.sendVerificationCode(
                    "user@example.com",
                    EmailVerificationPurpose.REGISTRATION,
                )
            } returns
                Result.failure(RuntimeException("Network error"))

            viewModel.onSendVerificationCode("user@example.com")
            while (true) {
                val item = awaitItem()
                if (item is Item.StateItem && item.value.emailVerificationStep == EmailVerificationStep.CODE_SENT && item.value.verificationError != null) break
            }
            cancelAndIgnoreRemainingItems()
        }

        assertEquals(
            "인증번호를 발송하지 못했습니다. 네트워크를 확인해주세요.",
            viewModel.container.stateFlow.value.verificationError,
        )
        assertEquals(0, viewModel.container.stateFlow.value.remainingSeconds)
    }

    private fun user() = User(
        name = "홍길동",
        email = "user@example.com",
        birth = "19900101",
        phone = "01011112222",
    )

}

@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(
    private val dispatcher: TestDispatcher = UnconfinedTestDispatcher(TestCoroutineScheduler()),
) : TestWatcher() {
    override fun starting(description: Description) {
        Dispatchers.setMain(dispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}
