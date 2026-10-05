package com.whitbread.premierinn.data.graphql.mapper

import com.whitbread.premierinn.data.common.EMPTY_STRING
import com.whitbread.premierinn.data.remote.graphql.contracts.HomePageAppsContentGraphQLContract
import com.whitbread.premierinn.domain.graphql.common.entity.AppsContentCardDomain
import com.whitbread.premierinn.domain.graphql.common.entity.HomePageAppsContentDomain
import com.whitbread.premierinn.domain.graphql.common.entity.NotificationDomain
import com.whitbread.premierinn.domain.graphql.common.entity.NotificationType

private const val NOTIFICATION_TYPE_INFO = "info"
private const val NOTIFICATION_TYPE_ERROR = "error"
private const val NOTIFICATION_TYPE_ALERT = "alert"
fun HomePageAppsContentGraphQLContract.HomePageAppsContentData.toHomePageAppsContentDomain(): HomePageAppsContentDomain {
    this.data?.homePageAppsContent?.let {
        return HomePageAppsContentDomain(
            destinationCards = it.destinationCards?.toAppsContentCardDomain() ?: emptyList(),
            contentCards = it.contentCards?.toAppsContentCardDomain() ?: emptyList(),
            promoCards = it.promoCards?.toAppsContentCardDomain() ?: emptyList(),
            heading = it.heading,
            logo = it.logo,
            notification = it.notification?.toNotificationDomain()
        )

    } ?: return HomePageAppsContentDomain.createEmptyDomain()
}

fun List<HomePageAppsContentGraphQLContract.AppsContentCard>.toAppsContentCardDomain(): List<AppsContentCardDomain> {
    val domainList = mutableListOf<AppsContentCardDomain>()
    this.forEach {
        domainList.add(
            AppsContentCardDomain(
                imagePath = it.imagePath,
                imageTag = it.imageTag ?: EMPTY_STRING,
                linkPath = it.linkPath,
                openLinkInApp = it.openLinkInApp,
                order = it.order,
                subtitle = it.subtitle,
                title = it.title,
                trackingId = it.trackingId,
                latitude = it.latitude ?: EMPTY_STRING,
                longitude = it.longitude ?: EMPTY_STRING
            )
        )
    }
    return domainList
}


fun HomePageAppsContentGraphQLContract.Notification.toNotificationDomain(): NotificationDomain {
    val typeDomain = when(type) {
        NOTIFICATION_TYPE_INFO -> NotificationType.Info
        NOTIFICATION_TYPE_ERROR -> NotificationType.Error
        NOTIFICATION_TYPE_ALERT -> NotificationType.Alert
        else -> NotificationType.Info
    }
    return NotificationDomain(
        type = typeDomain,
        title = title,
        message = message,
        linkLabel = linkLabel,
        linkPath = linkPath,
        openLinkInApp = openLinkInApp,
        dismissible = dismissible
    )
}
