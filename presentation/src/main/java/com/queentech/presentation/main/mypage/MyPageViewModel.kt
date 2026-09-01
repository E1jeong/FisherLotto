package com.queentech.presentation.main.mypage

import android.util.Log
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.queentech.domain.model.billing.SubscriptionProduct
import com.queentech.domain.model.billing.SubscriptionStatus
import com.queentech.domain.model.login.User
import com.queentech.domain.usecase.billing.BillingRepository
import com.queentech.domain.usecase.login.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val billingRepository: BillingRepository,
) : ViewModel(), ContainerHost<MyPageState, MyPageSideEffect> {

    override val container: Container<MyPageState, MyPageSideEffect> = container(
        initialState = MyPageState(),
        buildSettings = {
            this.exceptionHandler = CoroutineExceptionHandler { _, throwable ->
                intent {
                    reduce { state.copy(isLoading = false, isBillingLoading = false) }
                    postSideEffect(MyPageSideEffect.Toast("정보를 처리하지 못했습니다."))
                    Log.e(TAG, "MyPage operation failed")
                }
            }
        },
        onCreate = {
            loadUser()
            loadSubscriptionStatus()
            loadSubscriptionProducts()
        },
    )

    companion object {
        const val TAG = "MyPageViewModel"
    }

    // ── User ──

    private fun loadUser() = intent {
        userRepository.loadCachedUser()
        userRepository.currentUser.collect { user ->
            reduce { state.copy(user = user) }
        }
    }

    // ── Billing ──

    private fun loadSubscriptionStatus() = intent {
        billingRepository.subscriptionStatus.collect { status ->
            reduce { state.copy(subscriptionStatus = status) }
        }
    }

    fun refreshSubscriptionStatus() {
        viewModelScope.launch {
            billingRepository.refreshSubscriptionStatus()
                .onFailure { Log.e(TAG, "Subscription status refresh failed", it) }
        }
    }

    private fun loadSubscriptionProducts() = intent {
        reduce { state.copy(isBillingLoading = true) }
        billingRepository.querySubscriptionProducts()
            .onSuccess { products ->
                reduce { state.copy(subscriptionProducts = products, isBillingLoading = false) }
            }
            .onFailure {
                Log.e(TAG, "Subscription product loading failed")
                reduce { state.copy(isBillingLoading = false) }
            }
    }

    fun onSubscribeClick(productId: String) = intent {
        if (state.user?.email.isNullOrBlank()) {
            postSideEffect(MyPageSideEffect.NavigateToLogin)
            return@intent
        }
        postSideEffect(MyPageSideEffect.LaunchBillingFlow(productId))
    }

    fun launchBillingFlow(activityContext: Any, productId: String) = intent {
        reduce { state.copy(isBillingLoading = true) }
        billingRepository.launchSubscriptionFlow(activityContext, productId)
            .onFailure {
                postSideEffect(MyPageSideEffect.Toast("구독 처리 중 오류가 발생했습니다."))
            }
        reduce { state.copy(isBillingLoading = false) }
    }

    fun onDeleteAccountClick() = intent {
        reduce { state.copy(showDeleteAccountDialog = true) }
    }

    fun dismissDeleteAccountDialog() = intent {
        reduce { state.copy(showDeleteAccountDialog = false) }
    }

    fun onDeleteAccountConfirm() = intent {
        reduce { state.copy(isDeleting = true) }
        userRepository.deleteAccount()
            .onSuccess {
                reduce { state.copy(user = null, isDeleting = false, showDeleteAccountDialog = false) }
                postSideEffect(MyPageSideEffect.NavigateToLogin)
            }
            .onFailure {
                reduce { state.copy(isDeleting = false, showDeleteAccountDialog = false) }
                postSideEffect(MyPageSideEffect.Toast("회원탈퇴 중 오류가 발생했습니다."))
            }
    }
}

@Immutable
data class MyPageState(
    val user: User? = null,
    val isLoading: Boolean = false,
    val subscriptionStatus: SubscriptionStatus = SubscriptionStatus(
        isActive = false, productId = null, expiryTimeMillis = null, autoRenewing = false
    ),
    val subscriptionProducts: List<SubscriptionProduct> = emptyList(),
    val isBillingLoading: Boolean = false,
    val showDeleteAccountDialog: Boolean = false,
    val isDeleting: Boolean = false,
)

sealed interface MyPageSideEffect {
    data class Toast(val message: String) : MyPageSideEffect
    data object NavigateToLogin : MyPageSideEffect
    data class LaunchBillingFlow(val productId: String) : MyPageSideEffect
}
