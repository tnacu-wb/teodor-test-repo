package com.whitbread.premierinn.data.graphql.mapper

import com.whitbread.premierinn.data.remote.graphql.contracts.ForgotPasswordGraphQLContract
import com.whitbread.premierinn.domain.graphql.forgotpassword.entity.ForgotPasswordDomain

fun ForgotPasswordGraphQLContract.ForgotPasswordData.mapToForgotPasswordDomain(): ForgotPasswordDomain =
    ForgotPasswordDomain(this.data?.forgotPassword?.success ?: false)
