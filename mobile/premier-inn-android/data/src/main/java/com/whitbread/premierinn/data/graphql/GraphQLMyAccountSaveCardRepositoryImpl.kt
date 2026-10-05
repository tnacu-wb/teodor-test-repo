package com.whitbread.premierinn.data.graphql

import com.google.gson.Gson
import com.whitbread.premierinn.data.common.FileDataProvider
import com.whitbread.premierinn.data.common.onGraphQLError
import com.whitbread.premierinn.data.graphql.mapper.mapToSaveCardDomainGQL
import com.whitbread.premierinn.data.remote.graphql.AUTHORIZATION_BEARER
import com.whitbread.premierinn.data.remote.graphql.WBGraphQLServicesApi
import com.whitbread.premierinn.domain.graphql.myAccount.entity.SaveCardDomain
import com.whitbread.premierinn.domain.graphql.myAccount.repository.GraphQLMyAccountSaveCardRepository
import com.whitbread.premierinn.domain.graphql.requestBodyModels.SaveCardRequestBody
import io.reactivex.Single
import org.json.JSONObject
import javax.inject.Inject

class GraphQLMyAccountSaveCardRepositoryImpl @Inject constructor(
    private val wbGraphQLServicesApi: WBGraphQLServicesApi,
    private val fileDataProvider: FileDataProvider,
    private val jsonObject: JSONObject,
    private val jsonObjectForVariablesSaveCard: JSONObject
    ): GraphQLMyAccountSaveCardRepository {


    override fun saveCard(token: String, input: SaveCardRequestBody): Single<SaveCardDomain> {
        val mutation = fileDataProvider.loadFileFromAssetGQL("graphql/MyAccountSaveCardMutationGQL.txt")
        val constructVariablesJsonObj = input.constructVariables(jsonObjectForVariablesSaveCard)

        jsonObject.put("query", mutation)
        jsonObject.put("variables", constructVariablesJsonObj)

        return wbGraphQLServicesApi.saveCardGraphQL(
            bearerToken = "$AUTHORIZATION_BEARER $token",
            query = jsonObject.toString())
            .onGraphQLError()
            .map { response ->
                return@map response.mapToSaveCardDomainGQL()
            }.doOnError { throwable ->
                println("GraphQl not able to connect: "+throwable.localizedMessage)
            }
    }

    private fun SaveCardRequestBody.constructVariables(jsonObject: JSONObject) : JSONObject {
        return jsonObject.apply {
            put("initiateSaveCardRequest", JSONObject(Gson().toJson(this@constructVariables.initiateSaveCardRequest)))
        }
    }

}
