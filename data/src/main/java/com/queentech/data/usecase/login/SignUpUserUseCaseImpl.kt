package com.queentech.data.usecase.login

import com.queentech.data.model.common.toDomainModel
import com.queentech.data.model.login.SignUpUserRequestBody
import com.queentech.data.model.service.UserService
import com.queentech.domain.model.common.CommonResponse
import com.queentech.domain.usecase.login.SignUpUserUseCase
import javax.inject.Inject

class SignUpUserUseCaseImpl @Inject constructor(
    private val userService: UserService
) : SignUpUserUseCase {
    override suspend fun invoke(
        name: String,
        email: String,
        birth: String,
        phone: String,
        verificationToken: String,
    ): Result<CommonResponse> = kotlin.runCatching {

        val requestBody = SignUpUserRequestBody(
            name = name,
            email = email,
            birth = birth,
            phone = phone,
            verificationToken = verificationToken,
        )

        userService.signUpUser(requestBody).toDomainModel()
    }
}
