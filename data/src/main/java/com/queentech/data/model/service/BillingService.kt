package com.queentech.data.model.service

import com.queentech.data.model.billing.ReceiptRequest
import com.queentech.data.model.billing.ReceiptResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface BillingService {
    @POST("api/billing/receipt")
    suspend fun sendReceipt(
        @Body request: ReceiptRequest,
    ): ReceiptResponse
}
