package com.whitbread.premierinn.domain.graphql.forgotpassword.repository

import com.whitbread.premierinn.domain.graphql.forgotpassword.entity.ForgotPasswordDomain
import io.reactivex.Single

interface ForgotPasswordRepository {
    fun forgotPassword(
        username: String,
        innBusiness: Boolean,
        language: String
    ): Single<ForgotPasswordDomain>
}
