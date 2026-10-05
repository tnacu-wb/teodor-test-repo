package com.whitbread.premierinn.data.remote.graphql.contracts

import com.google.gson.annotations.SerializedName
import com.whitbread.premierinn.data.remote.graphql.GraphQLBase

interface RoomKeyInstructionsGraphQLContract {
    data class CategoryLabelsData(
        @SerializedName("data") val data: Data,
        @SerializedName("errors") val errors: List<GraphQLBase.BaseError>?
    )

    data class Data(
        @SerializedName("categoryLabels") val categoryLabels: CategoryLabels
    )

    data class CategoryLabels(
        @SerializedName("labels") val labels: String
    )

    data class LabelsContent(
        @SerializedName("mybookings.post-checkin.notification.item1.title") val title: String,
        @SerializedName("mybookings.post-checkin.notification.item1.description") val description: String? = null,
        @SerializedName("mybookings.post-checkin.notification.item1.description.PID") val descriptionPid: String? = null
    )
}
