package com.whitbread.premierinn.domain.graphql.forgotpassword.usecase

import com.whitbread.premierinn.domain.graphql.forgotpassword.entity.ForgotPasswordDomain
import com.whitbread.premierinn.domain.graphql.forgotpassword.repository.ForgotPasswordRepository
import io.reactivex.Single
import javax.inject.Inject

class ForgotPasswordUseCase @Inject constructor(
    private val forgotPasswordRepository: ForgotPasswordRepository
) {
    fun execute(
        username: String,
        innBusiness: Boolean,
        language: String
    ): Single<ForgotPasswordDomain> = forgotPasswordRepository.forgotPassword(username, innBusiness, language)
}
