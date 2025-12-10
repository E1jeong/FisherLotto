package com.queentech.presentation.main.mypage

import android.util.Log
import androidx.lifecycle.ViewModel
import com.queentech.domain.model.payments.PaymentResult
import com.queentech.domain.usecase.payments.GetPaymentResultUseCase
import com.queentech.domain.usecase.payments.StartPaymentUseCase
import com.queentech.presentation.login.LoginViewModel.Companion.TAG
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val startPaymentUseCase: StartPaymentUseCase,
    private val getPaymentResultUseCase: GetPaymentResultUseCase,
) : ViewModel(), ContainerHost<MyPageState, MyPageSideEffect> {

    override val container: Container<MyPageState, MyPageSideEffect> = container(
        initialState = MyPageState(),
        buildSettings = {
            this.exceptionHandler = CoroutineExceptionHandler { _, throwable ->
                intent {
                    postSideEffect(MyPageSideEffect.Toast(throwable.message.toString()))
                    Log.e(TAG, "error handler: ${throwable.message}", throwable)
                }
            }
        },
        onCreate = {},
    )

    /**
     * 테스트용 결제 플로우
     * - 1) 주문 생성 + ready 호출 (startPaymentUseCase)
     * - 2) 바로 결제 결과 조회 (getPaymentResultUseCase)
     * 실제 WebView 붙이기 전까지는 이렇게 한 번에 왕복해서 확인
     */
    fun onTestPaymentButtonClick() = intent {
        // 1. 로딩 시작
        reduce {
            state.copy(
                isLoading = true,
                errorMessage = null,
                result = null,
            )
        }

        // 2. 결제 시작 (order + ready)
        //    여기 금액/상품명/주문자명은 테스트 값으로 고정
        val ready = startPaymentUseCase(
            amount = 1000L,
            goodName = "테스트 상품",
            orderName = "원정",
        )

        // 3. 결제 결과 조회 (지금은 mock이라 바로 성공 응답)
        val result = getPaymentResultUseCase(ready.orderId)

        // 4. 상태 업데이트
        reduce {
            state.copy(
                isLoading = false,
                currentOrderId = ready.orderId,
                payUrl = ready.payUrl,
                result = result,
                errorMessage = null,
            )
        }

        // 5. 토스트로 간단히 결과 알려주기
        postSideEffect(
            MyPageSideEffect.Toast(
                "결제 상태: ${result.status} / 금액: ${result.amount}"
            )
        )
    }
}

data class MyPageState(
    val isLoading: Boolean = false,
    val currentOrderId: String? = null,
    val payUrl: String? = null,
    val result: PaymentResult? = null,
    val errorMessage: String? = null,
)

sealed interface MyPageSideEffect {
    data class Toast(val message: String) : MyPageSideEffect
}