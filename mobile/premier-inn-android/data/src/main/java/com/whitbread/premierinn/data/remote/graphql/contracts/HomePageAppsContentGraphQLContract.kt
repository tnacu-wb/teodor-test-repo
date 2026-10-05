package com.whitbread.premierinn.data.remote.graphql.contracts

import com.google.gson.annotations.SerializedName
import com.whitbread.premierinn.data.remote.graphql.GraphQLBase

interface HomePageAppsContentGraphQLContract {
    data class HomePageAppsContentData(
        val data: Data?,
        val errors: List<GraphQLBase.BaseError>?
    )

    data class Data(
        @SerializedName("homepageAppsContent") val homePageAppsContent: HomePageAppsContent
    )

    data class HomePageAppsContent(
        val destinationCards: List<AppsContentCard>?,
        val contentCards: List<AppsContentCard>?,
        val promoCards: List<AppsContentCard>?,
        val heading: String,
        val logo: String,
        val notification: Notification?
    )

    data class AppsContentCard(
        val imagePath: String,
        val imageTag: String?,
        val linkPath: String,
        val openLinkInApp: Boolean,
        val order: Int,
        val subtitle: String,
        val title: String,
        val trackingId: String,
        val latitude: String?,
        val longitude: String?
    )

    data class Notification(
        val type: String,
        val title: String,
        val message: String,
        val linkLabel: String,
        val linkPath: String,
        val openLinkInApp: Boolean,
        val dismissible: Boolean
    )
}