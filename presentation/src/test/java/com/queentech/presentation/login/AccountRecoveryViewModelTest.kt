package com.queentech.presentation.login

import com.queentech.domain.model.billing.SubscriptionStatus
import com.queentech.domain.model.common.CommonResponse
import com.queentech.domain.model.login.EmailVerificationPurpose
import com.queentech.domain.model.login.User
import com.queentech.domain.usecase.billing.BillingRepository
import com.queentech.domain.usecase.fcm.FcmRepository
import com.queentech.domain.usecase.login.UserRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.orbitmvi.orbit.test.Item
import org.orbitmvi.orbit.test.test

@OptIn(ExperimentalCoroutinesApi::class)
class AccountRecoveryViewModelTest {
    @get:org.junit.Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val userRepository: UserRepository = mockk(relaxed = true)
    private val fcmRepository: FcmRepository = mockk(relaxed = true)
    private val billingRepository: BillingRepository = mockk(relaxed = true)

    @Test
    fun `recovery verifies recovery purpose then restores account with phone`() = runTest {
        coEvery { userRepository.sendVerificationCode(any(), any()) } returns Result.success(CommonResponse("8200"))
        coEvery { userRepository.verifyEmailCode(any(), any(), any()) } returns Result.success(CommonResponse("8200", verificationToken = "recovery-proof"))
        coEvery { userRepository.recoverAccount(any(), any(), any()) } returns Result.success(user())
        coEvery { fcmRepository.getFreshToken() } returns null
        coEvery { billingRepository.refreshSubscriptionStatus() } returns Result.success(mockk<SubscriptionStatus>())
        val viewModel = AccountRecoveryViewModel(userRepository, fcmRepository, billingRepository)

        viewModel.test(this) {
            expectInitialState()
            viewModel.onSendVerificationCode("user@example.com")
            while (true) {
                val item = awaitItem()
                if (item is Item.StateItem && item.value.verificationStep == EmailVerificationStep.CODE_SENT) break
            }
            viewModel.onVerifyEmailCode("user@example.com", "123456")
            while (true) {
                val item = awaitItem()
                if (item is Item.StateItem && item.value.verificationStep == EmailVerificationStep.VERIFIED) break
            }
            viewModel.onRecoverClick("user@example.com", "01011112222")
            while (true) {
                val item = awaitItem()
                if (item is Item.SideEffectItem && item.value == AccountRecoverySideEffect.NavigateToHome) break
            }
            cancelAndIgnoreRemainingItems()
        }

        coVerify { userRepository.sendVerificationCode("user@example.com", EmailVerificationPurpose.RECOVERY) }
        coVerify { userRepository.verifyEmailCode("user@example.com", "123456", EmailVerificationPurpose.RECOVERY) }
        coVerify { userRepository.recoverAccount("user@example.com", "01011112222", "recovery-proof") }
        coVerify { billingRepository.refreshSubscriptionStatus() }
    }

    @Test
    fun `invalid phone does not call account recovery`() = runTest {
        coEvery { userRepository.sendVerificationCode(any(), any()) } returns Result.success(CommonResponse("8200"))
        coEvery { userRepository.verifyEmailCode(any(), any(), any()) } returns Result.success(CommonResponse("8200", verificationToken = "recovery-proof"))
        val viewModel = AccountRecoveryViewModel(userRepository, fcmRepository, billingRepository)

        viewModel.test(this) {
            expectInitialState()
            viewModel.onSendVerificationCode("user@example.com")
            while (true) {
                val item = awaitItem()
                if (item is Item.StateItem && item.value.verificationStep == EmailVerificationStep.CODE_SENT) break
            }
            viewModel.onVerifyEmailCode("user@example.com", "123456")
            while (true) {
                val item = awaitItem()
                if (item is Item.StateItem && item.value.verificationStep == EmailVerificationStep.VERIFIED) break
            }
            viewModel.onRecoverClick("user@example.com", "010")
            while (true) {
                val item = awaitItem()
                if (item is Item.SideEffectItem && item.value == AccountRecoverySideEffect.Toast("전화번호를 확인해주세요.")) break
            }
            cancelAndIgnoreRemainingItems()
        }

        coVerify(exactly = 0) { userRepository.recoverAccount(any(), any(), any()) }
    }

    private fun user() = User(
        name = "홍길동", email = "user@example.com", birth = "19900101", phone = "01011112222",
    )
}
