package com.queentech.data.model.service

import com.queentech.data.model.billing.ReceiptRequest
import com.queentech.data.model.billing.ReceiptResponse
import com.queentech.data.model.billing.SubscriptionQueryRequest
import com.queentech.data.model.billing.SubscriptionQueryResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface BillingService {
    @POST("api/billing/receipt")
    suspend fun sendReceipt(
        @Body request: ReceiptRequest,
    ): ReceiptResponse

    @POST("api/billing/subscription")
    suspend fun querySubscription(
        @Body request: SubscriptionQueryRequest,
    ): SubscriptionQueryResponse
}
