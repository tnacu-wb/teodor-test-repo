package com.whitbread.premierinn.data.graphql

import android.util.Log
import com.whitbread.premierinn.data.common.FileDataProvider
import com.whitbread.premierinn.data.common.onGraphQLError
import com.whitbread.premierinn.data.graphql.mapper.mapToForgotPasswordDomain
import com.whitbread.premierinn.data.remote.graphql.WBGraphQLServicesApi
import com.whitbread.premierinn.domain.graphql.forgotpassword.entity.ForgotPasswordDomain
import com.whitbread.premierinn.domain.graphql.forgotpassword.repository.ForgotPasswordRepository
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject
import javax.inject.Inject

class ForgotPasswordRepositoryImpl @Inject constructor(
    private val wbGraphQLServicesApi: WBGraphQLServicesApi,
    private val fileDataProvider: FileDataProvider
): ForgotPasswordRepository {
    override fun forgotPassword(username: String, innBusiness: Boolean, language: String): Single<ForgotPasswordDomain> {
        val query = fileDataProvider.loadFileFromAssetGQL("graphql/ForgotPasswordMutationGQL.txt")
        val variables = JSONObject().apply {
            put("forgottenPasswordRequest", JSONObject().apply {
                put("username", username)
            })
            put("innBusiness", innBusiness)
            put("language", language)
        }

        val queryJson = JSONObject().apply {
            put("query", query)
            put("variables", variables)
        }

        return wbGraphQLServicesApi.forgotPassword(queryJson.toString())
            .subscribeOn(Schedulers.io())
            .onGraphQLError()
            .map { response ->
                response.mapToForgotPasswordDomain()
            }
            .doOnError { error -> Log.d("Forgot password graphql call failed: ", error.localizedMessage ?: "") }
    }
}
