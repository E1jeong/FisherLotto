package com.queentech.presentation.login

import com.queentech.domain.model.billing.SubscriptionStatus
import com.queentech.domain.model.login.User
import com.queentech.domain.usecase.billing.BillingRepository
import com.queentech.domain.usecase.fcm.FcmRepository
import com.queentech.domain.usecase.login.UserRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.orbitmvi.orbit.test.Item
import org.orbitmvi.orbit.test.test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val userFlow = MutableStateFlow<User?>(null)
    private val userRepository: UserRepository = mockk(relaxed = true) {
        every { currentUser } returns userFlow
    }
    private val fcmRepository: FcmRepository = mockk(relaxed = true)
    private val billingRepository: BillingRepository = mockk(relaxed = true)

    @Test
    fun `observing currentUser updates state and resets state when user becomes null`() = runTest {
        val viewModel = LoginViewModel(userRepository, fcmRepository, billingRepository)

        viewModel.test(this) {
            expectInitialState()
            runOnCreate()

            // When cached user is loaded
            userFlow.value = user()
            while (true) {
                val item = awaitItem()
                if (item is Item.StateItem && item.value.userEmail == "test@example.com") {
                    assertEquals("test@example.com", item.value.emailInput)
                    assertEquals("홍길동", item.value.userName)
                    break
                }
            }

            // When user logs out or withdraws (user becomes null)
            userFlow.value = null
            while (true) {
                val item = awaitItem()
                if (item is Item.StateItem && item.value.userEmail.isEmpty()) {
                    assertEquals("", item.value.emailInput)
                    assertEquals("", item.value.userName)
                    break
                }
            }

            cancelAndIgnoreRemainingItems()
        }
    }

    @Test
    fun `onEmailChanged is ignored when userEmail is already present`() = runTest {
        val viewModel = LoginViewModel(userRepository, fcmRepository, billingRepository)

        viewModel.test(this) {
            expectInitialState()
            runOnCreate()

            userFlow.value = user()
            while (true) {
                val item = awaitItem()
                if (item is Item.StateItem && item.value.userEmail == "test@example.com") break
            }

            // Attempt to change email when user already cached
            viewModel.onEmailChanged("new@example.com")
            cancelAndIgnoreRemainingItems()
        }

        assertEquals("test@example.com", viewModel.container.stateFlow.value.emailInput)
    }

    @Test
    fun `successful login navigates to home and refreshes billing`() = runTest {
        coEvery { userRepository.login(any(), any(), any(), any()) } returns Result.success(user())
        coEvery { fcmRepository.getFreshToken() } returns null
        coEvery { fcmRepository.getCachedToken() } returns null
        coEvery { fcmRepository.getCachedEmail() } returns null
        coEvery { billingRepository.refreshSubscriptionStatus() } returns Result.success(mockk<SubscriptionStatus>())

        val viewModel = LoginViewModel(userRepository, fcmRepository, billingRepository)

        viewModel.test(this) {
            expectInitialState()
            runOnCreate()

            viewModel.onEmailChanged("test@example.com")
            while (true) {
                val item = awaitItem()
                if (item is Item.StateItem && item.value.emailInput == "test@example.com") break
            }

            viewModel.onLoginClick()
            while (true) {
                val item = awaitItem()
                if (item is Item.SideEffectItem && item.value == LoginSideEffect.NavigateToHome) break
            }

            cancelAndIgnoreRemainingItems()
        }

        coVerify { userRepository.login("", "", "", "test@example.com") }
        coVerify { billingRepository.refreshSubscriptionStatus() }
    }

    private fun user() = User(
        name = "홍길동", email = "test@example.com", birth = "19900101", phone = "01012345678",
    )
}
