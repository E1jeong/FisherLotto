package com.queentech.data.model.service

import com.queentech.data.model.openbanking.AccountListResponse
import com.queentech.data.model.openbanking.AuthorizeResponse
import com.queentech.data.model.openbanking.BalanceResponse
import com.queentech.data.model.openbanking.TokenRequest
import com.queentech.data.model.openbanking.TokenResponse
import com.queentech.data.model.openbanking.TransferRequest
import com.queentech.data.model.openbanking.TransferResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface OpenBankingService {

    /**
     * OAuth 인증 URL 생성
     * GET /api/openbanking/auth/authorize
     */
    @GET("api/openbanking/auth/authorize")
    suspend fun getAuthorizeUrl(): AuthorizeResponse

    /**
     * 토큰 발급/갱신
     * POST /api/openbanking/auth/token
     */
    @POST("api/openbanking/auth/token")
    suspend fun getToken(
        @Body request: TokenRequest,
    ): TokenResponse

    /**
     * 사용자 등록 계좌 목록 조회
     * GET /api/openbanking/accounts?user_seq_no=xxx
     */
    @GET("api/openbanking/accounts")
    suspend fun getAccounts(
        @Header("Authorization") authorization: String,
        @Query("user_seq_no") userSeqNo: String,
    ): AccountListResponse

    /**
     * 계좌 잔액 조회
     * GET /api/openbanking/balance?fintech_use_num=xxx
     */
    @GET("api/openbanking/balance")
    suspend fun getBalance(
        @Header("Authorization") authorization: String,
        @Query("fintech_use_num") fintechUseNum: String,
    ): BalanceResponse

    /**
     * 출금이체 (송금)
     * POST /api/openbanking/transfer
     */
    @POST("api/openbanking/transfer")
    suspend fun transfer(
        @Header("Authorization") authorization: String,
        @Body request: TransferRequest,
    ): TransferResponse
}
