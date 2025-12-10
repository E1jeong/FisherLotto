package com.queentech.data.model.service

import com.queentech.data.model.payments.CreateOrderRequest
import com.queentech.data.model.payments.CreateOrderResponse
import com.queentech.data.model.payments.PaymentResultResponse
import com.queentech.data.model.payments.ReadyPaymentRequest
import com.queentech.data.model.payments.ReadyPaymentResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface PaymentsService {
    /**
     * 주문 생성
     * POST /api/orders
     */
    @POST("api/orders")
    suspend fun createOrder(
        @Body request: CreateOrderRequest,
    ): CreateOrderResponse

    /**
     * 결제창 호출용 payUrl 생성
     * POST /api/payments/ready
     */
    @POST("api/payments/ready")
    suspend fun readyPayment(
        @Body request: ReadyPaymentRequest,
    ): ReadyPaymentResponse

    /**
     * 결제 결과 조회
     * GET /api/payments/{orderId}
     */
    @GET("api/payments/{orderId}")
    suspend fun getPaymentResult(
        @Path("orderId") orderId: String,
    ): PaymentResultResponse
}