package com.whitbread.premierinn.data.graphql

import com.whitbread.premierinn.data.common.FileDataProvider
import com.whitbread.premierinn.data.common.onGraphQLError
import com.whitbread.premierinn.data.remote.graphql.WBGraphQLServicesApi
import com.whitbread.premierinn.data.graphql.mapper.mapToPromotionsInformationDomain
import com.whitbread.premierinn.domain.graphql.promotions.entity.PromotionsInformationDomain
import com.whitbread.premierinn.domain.graphql.promotions.repository.GraphQLPromotionsInformationRepository
import io.reactivex.Single
import org.json.JSONObject
import javax.inject.Inject

class GraphQLPromotionsInformationRepositoryImpl @Inject constructor(
    private val wbGraphQLServicesApi: WBGraphQLServicesApi,
    private val fileDataProvider: FileDataProvider
) : GraphQLPromotionsInformationRepository {
    override fun getPromotionsInformation(
        country: String,
        language: String,
        channel: String,
        brand: String,
        stayStartDate: String,
        stayEndDate: String,
        basketReference: String?,
        promotionCode: String?,
        isPromoBox: Boolean?
    ): Single<PromotionsInformationDomain> {
        val query =
            fileDataProvider.loadFileFromAssetGQL("graphql/PromotionsInformationQueryGQL.txt")
        val variables = constructVariablesPromotionsInformation(
            JSONObject(),
            country,
            language,
            channel,
            brand,
            stayStartDate,
            stayEndDate,
            basketReference,
            promotionCode,
            isPromoBox
        )
        val jsonObject = JSONObject().apply {
            put("query", query)
            put("variables", variables)
        }
        return wbGraphQLServicesApi.getPromotionsInformationGraphQL(
            query = jsonObject.toString()
        )
            .onGraphQLError()
            .map { response ->
                response.mapToPromotionsInformationDomain()
            }
            .doOnError { throwable ->
                println("GraphQl not able to connect: " + throwable.localizedMessage)
            }
    }

    private fun constructVariablesPromotionsInformation(
        jsonObject: JSONObject,
        country: String,
        language: String,
        channel: String,
        brand: String,
        stayStartDate: String,
        stayEndDate: String,
        basketReference: String?,
        promotionCode: String?,
        isPromoBox: Boolean?
    ): JSONObject {
        return jsonObject.apply {
            put("promotionsInformationCriteria", JSONObject().apply {
                put("country", country)
                put("language", language)
                put("channel", channel)
                put("brand", brand)
                put("stayStartDate", stayStartDate)
                put("stayEndDate", stayEndDate)
                basketReference?.let { put("basketReference", it) }
                promotionCode?.let { put("promotionCode", it) }
                isPromoBox?.let { put("isPromoBox", it) }
            })
        }
    }
}