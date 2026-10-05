package com.whitbread.premierinn.common


interface AppConfiguration {

    val microServicesUrl: String


    val graphQLUrl: String


    val snowdropUrl: String

    val checkInOnlineUrl: String


    var environment: String

    fun setMicroServicesURL(value: String)

    fun setSnowdropServicesURL(value: String)

    fun setGraphQlURL(value: String)


    val isLive: Boolean


    val isPreLive: Boolean


    var isCacheEnabled: Boolean


    var isSSLPinningEnabled: Boolean


    var isEmployeeOfferEnabled: Boolean


    var isApolloEnabled: Boolean
}