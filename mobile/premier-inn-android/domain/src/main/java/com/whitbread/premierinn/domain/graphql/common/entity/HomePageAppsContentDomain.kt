package com.whitbread.premierinn.domain.graphql.common.entity

import com.whitbread.premierinn.domain.common.EMPTY_STRING_DOMAIN

class HomePageAppsContentDomain(
    val destinationCards: List<AppsContentCardDomain>,
    val contentCards: List<AppsContentCardDomain>,
    val promoCards: List<AppsContentCardDomain>,
    val heading: String,
    val logo: String,
    val notification: NotificationDomain?
) {
    companion object {
        fun createEmptyDomain(): HomePageAppsContentDomain {
            return HomePageAppsContentDomain(
                destinationCards = emptyList(),
                contentCards = emptyList(),
                promoCards = emptyList(),
                heading = EMPTY_STRING_DOMAIN,
                logo = EMPTY_STRING_DOMAIN,
                notification = null
            )
        }
    }
}

data class AppsContentCardDomain(
    val imagePath: String,
    val imageTag: String,
    val linkPath: String,
    val openLinkInApp: Boolean,
    val order: Int,
    val subtitle: String,
    val title: String,
    val trackingId: String,
    val latitude: String,
    val longitude: String
)

data class NotificationDomain(
    val type: NotificationType,
    val title: String,
    val message: String,
    val linkLabel: String,
    val linkPath: String,
    val openLinkInApp: Boolean,
    val dismissible: Boolean
)

sealed class NotificationType {
    data object Info : NotificationType()
    data object Error : NotificationType()
    data object Alert : NotificationType()
}
