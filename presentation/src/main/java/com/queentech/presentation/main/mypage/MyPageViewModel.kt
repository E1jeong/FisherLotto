package com.queentech.presentation.main.mypage

import android.util.Log
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import com.queentech.domain.model.login.User
import com.queentech.domain.model.openbanking.Account
import com.queentech.domain.model.openbanking.AccountBalance
import com.queentech.domain.usecase.login.UserRepository
import com.queentech.domain.usecase.openbanking.GetAccountsUseCase
import com.queentech.domain.usecase.openbanking.GetAuthorizeUrlUseCase
import com.queentech.domain.usecase.openbanking.GetBalanceUseCase
import com.queentech.domain.usecase.openbanking.GetSavedTokenUseCase
import com.queentech.domain.usecase.openbanking.GetTokenUseCase
import com.queentech.domain.usecase.openbanking.TransferUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.collectLatest
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
    private val getAuthorizeUrlUseCase: GetAuthorizeUrlUseCase,
    private val getTokenUseCase: GetTokenUseCase,
    private val getAccountsUseCase: GetAccountsUseCase,
    private val getBalanceUseCase: GetBalanceUseCase,
    private val transferUseCase: TransferUseCase,
    private val getSavedTokenUseCase: GetSavedTokenUseCase,
) : ViewModel(), ContainerHost<MyPageState, MyPageSideEffect> {

    override val container: Container<MyPageState, MyPageSideEffect> = container(
        initialState = MyPageState(),
        buildSettings = {
            this.exceptionHandler = CoroutineExceptionHandler { _, throwable ->
                intent {
                    reduce { state.copy(isLoading = false) }
                    postSideEffect(MyPageSideEffect.Toast(throwable.message ?: "오류가 발생했습니다."))
                    Log.e(TAG, "error handler: ${throwable.message}", throwable)
                }
            }
        },
        onCreate = {
            loadUser()
            checkSavedOpenBankingToken()
        },
    )

    companion object {
        const val TAG = "MyPageViewModel"
    }

    // ── User ──

    private fun loadUser() = intent {
        userRepository.loadCachedUser()
        val user = userRepository.currentUser.value
        if (user != null) {
            reduce { state.copy(user = user) }
        }
    }

    private fun checkSavedOpenBankingToken() = intent {
        getSavedTokenUseCase().collectLatest { (token, seqNo) ->
            if (!token.isNullOrBlank() && !seqNo.isNullOrBlank()) {
                // 토큰이 존재하면 State를 업데이트하고 은행 연결 상태를 true로 변경
                reduce {
                    state.copy(
                        accessToken = token,
                        userSeqNo = seqNo,
                        isBankConnected = true
                    )
                }
                // State가 업데이트 되었으니 즉시 계좌 목록을 불러옵니다!
                loadAccounts()
            }
        }
    }

    fun onLogoutClick() = intent {
        userRepository.logout()
        reduce { state.copy(user = null) }
        postSideEffect(MyPageSideEffect.NavigateToLogin)
    }

    // ── OpenBanking: 인증 ──

    fun onConnectBankClick() = intent {
        reduce { state.copy(isLoading = true) }
        val result = getAuthorizeUrlUseCase()
        reduce { state.copy(isLoading = false) }
        postSideEffect(MyPageSideEffect.OpenBankAuth(result.authorizeUrl))
    }

    fun onAuthCodeReceived(code: String) = intent {
        reduce { state.copy(isLoading = true) }
        val token = getTokenUseCase(code = code)
        reduce {
            state.copy(
                isLoading = false,
                accessToken = token.accessToken,
                userSeqNo = token.userSeqNo,
                refreshToken = token.refreshToken,
                isBankConnected = true,
            )
        }
        postSideEffect(MyPageSideEffect.Toast("계좌 연결이 완료되었습니다."))
        loadAccounts()
    }

    // ── OpenBanking: 계좌 목록 ──

    private fun loadAccounts() = intent {
        val token = state.accessToken ?: return@intent
        val userSeqNo = state.userSeqNo ?: return@intent

        reduce { state.copy(isLoading = true) }
        val accounts = getAccountsUseCase(accessToken = token, userSeqNo = userSeqNo)
        reduce { state.copy(isLoading = false, accounts = accounts) }
    }

    // ── OpenBanking: 잔액 조회 ──

    fun onCheckBalanceClick(fintechUseNum: String) = intent {
        val token = state.accessToken ?: return@intent

        reduce { state.copy(isLoading = true) }
        val balance = getBalanceUseCase(accessToken = token, fintechUseNum = fintechUseNum)
        reduce { state.copy(isLoading = false, selectedBalance = balance) }
    }

    // ── OpenBanking: 송금 ──

    fun onTransferClick(
        fintechUseNum: String,
        amount: Long,
        reqClientName: String,
        reqClientNum: String,
        recvClientName: String,
        recvClientAccountNum: String,
    ) = intent {
        val token = state.accessToken ?: return@intent

        reduce { state.copy(isLoading = true) }
        val result = transferUseCase(
            accessToken = token,
            fintechUseNum = fintechUseNum,
            tranAmt = amount,
            reqClientName = reqClientName,
            reqClientNum = reqClientNum,
            recvClientName = recvClientName,
            recvClientAccountNum = recvClientAccountNum,
        )
        reduce { state.copy(isLoading = false) }

        if (result.rspCode == "A0000") {
            postSideEffect(MyPageSideEffect.Toast("송금 완료: ${result.recvClientName}님에게 ${result.tranAmt}원"))
            loadAccounts() // 잔액 갱신
        } else {
            postSideEffect(MyPageSideEffect.Toast("송금 실패: ${result.rspMessage}"))
        }
    }
}

@Immutable
data class MyPageState(
    val user: User? = null,
    val isLoading: Boolean = false,

    // OpenBanking
    val accessToken: String? = null,
    val refreshToken: String? = null,
    val userSeqNo: String? = null,
    val isBankConnected: Boolean = false,
    val accounts: List<Account> = emptyList(),
    val selectedBalance: AccountBalance? = null,
)

sealed interface MyPageSideEffect {
    data class Toast(val message: String) : MyPageSideEffect
    data object NavigateToLogin : MyPageSideEffect
    data class OpenBankAuth(val url: String) : MyPageSideEffect
}
