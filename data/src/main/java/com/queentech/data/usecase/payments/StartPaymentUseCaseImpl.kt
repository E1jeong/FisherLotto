package com.queentech.data.usecase.payments

import com.queentech.data.model.payments.CreateOrderRequest
import com.queentech.data.model.payments.ReadyPaymentRequest
import com.queentech.data.model.service.PaymentsService
import com.queentech.domain.model.payments.ReadyPaymentResult
import com.queentech.domain.usecase.payments.StartPaymentUseCase
import javax.inject.Inject

class StartPaymentUseCaseImpl @Inject constructor(
    private val paymentsService: PaymentsService,
) : StartPaymentUseCase {

    override suspend fun invoke(
        amount: Long,
        goodName: String,
        orderName: String,
    ): ReadyPaymentResult {
        // 1) 주문 생성
        val orderResponse = paymentsService.createOrder(
            CreateOrderRequest(
                amount = amount,
                goodName = goodName,
                orderName = orderName,
            )
        )

        // 2) ready 호출
        val readyResponse = paymentsService.readyPayment(
            ReadyPaymentRequest(
                orderId = orderResponse.orderId,
                amount = orderResponse.amount,
            )
        )

        // 3) 도메인 모델로 변환
        return ReadyPaymentResult(
            orderId = readyResponse.orderId,
            amount = readyResponse.amount,
            payUrl = readyResponse.payUrl,
        )
    }
}