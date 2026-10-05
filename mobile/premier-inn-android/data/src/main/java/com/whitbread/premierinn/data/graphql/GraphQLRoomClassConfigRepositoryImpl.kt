package com.whitbread.premierinn.data.graphql

import com.whitbread.premierinn.data.common.FileDataProvider
import com.whitbread.premierinn.data.remote.graphql.WBGraphQLServicesApi
import com.whitbread.premierinn.domain.graphql.requestBodyModels.RoomClassConfigRequestBody
import com.whitbread.premierinn.domain.graphql.roomClassConfig.entity.RoomClassConfig
import com.whitbread.premierinn.domain.graphql.roomClassConfig.repository.GraphQLRoomClassConfigRepository
import io.reactivex.Observable
import org.json.JSONObject
import javax.inject.Inject

class GraphQLRoomClassConfigRepositoryImpl @Inject constructor(
    private val wbGraphQLServicesApi: WBGraphQLServicesApi,
    private val fileDataProvider: FileDataProvider,
    private val jsonObject: JSONObject,
    private val jsonObjectForVariablesRoomClassConfig: JSONObject
) : GraphQLRoomClassConfigRepository {


    override fun roomClassConfigObservable(input: RoomClassConfigRequestBody): Observable<List<RoomClassConfig>> {
        val query = fileDataProvider.loadFileFromAssetGQL("graphql/RoomClassConfigQueryGQL.txt")
        val variables = jsonObjectForVariablesRoomClassConfig.apply {
            put("channel", input.channel)
            put("brand", input.brand)
            put("country", input.country)
            put("language", input.language)
        }
        jsonObject.put("query", query)
        jsonObject.put("variables", variables)

        return wbGraphQLServicesApi.roomClassConfig(jsonObject.toString())
            .map { response ->
                response.data?.roomClassConfig?.roomClassConfigItems?.mapNotNull { item ->
                    if (item.code != null && item.order != null) {
                        RoomClassConfig(
                            code = item.code,
                            order = item.order
                        )
                    } else null
                } ?: emptyList<RoomClassConfig>()
            }
            .toObservable()
    }
}