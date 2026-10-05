package com.whitbread.premierinn.landing.ui_compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.whitbread.premierinn.R
import com.whitbread.premierinn.domain.graphql.common.entity.NotificationDomain
import com.whitbread.premierinn.domain.graphql.common.entity.NotificationType

@Composable
fun HomeNotification(
    notification: NotificationDomain,
    onNotificationLinkClick: (link: String, shouldOpenInApp: Boolean) -> Unit,
    onCloseClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(colorResource(id = backgroundColorResId(notification.type)))
            .border(1.dp, colorResource(id = backgroundBorderColorResId(notification.type)), RoundedCornerShape(4.dp))
            .padding(16.dp),
        horizontalArrangement = Arrangement.Start
    ) {

        Image(
            modifier = Modifier
                .align(Alignment.Top)
                .padding(top = 2.dp, end = 8.dp),
            painter = painterResource(id = iconResId(notification.type)),
            colorFilter = iconColorFilter(notification.type),
            contentDescription = "notification icon"
        )

        Column(modifier = Modifier.weight(1f)) {
            NotificationContent(notification, onNotificationLinkClick)
        }

        if (notification.dismissible) {
            Image(
                modifier = Modifier
                    .align(Alignment.Top)
                    .clickable { onCloseClick() },
                painter = painterResource(id = R.drawable.ic_close_dark),
                colorFilter = ColorFilter.tint(color = colorResource(id = R.color.grey_dark)),
                contentDescription = "notification close icon"
            )
        }
    }
}

@Composable
fun NotificationContent(notification: NotificationDomain, onNotificationLinkClick: (link: String, shouldOpenInApp: Boolean) -> Unit) {
    Text(
        text = notification.title,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        fontFamily = FontFamily(Font(R.font.proxima_nova_semibold)),
        color = colorResource(id = R.color.grey_dark),
        fontSize = 14.sp
    )

    Text(
        text = notification.message,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        fontFamily = FontFamily(Font(R.font.proxima_nova_medium)),
        color = colorResource(id = R.color.grey_dark),
        fontSize = 14.sp
    )

    if (notification.linkLabel.isNotBlank() && notification.linkPath.isNotBlank()) {
        ClickableText(
            text = buildAnnotatedString { append(notification.linkLabel) },
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = TextStyle(
                fontFamily = FontFamily(Font(R.font.proxima_nova_medium)),
                color = colorResource(id = R.color.premier_inn_purple_dark),
                fontSize = 14.sp,
                textDecoration = TextDecoration.Underline
            )
        ) {
            onNotificationLinkClick(notification.linkPath, notification.openLinkInApp)
        }
    }
}

@Composable
private fun backgroundColorResId(notificationType: NotificationType): Int = when (notificationType) {
    is NotificationType.Info -> R.color.information_blue_tint
    is NotificationType.Error -> R.color.error_notification
    is NotificationType.Alert -> R.color.alert_orange_tint
}

@Composable
private fun backgroundBorderColorResId(notificationType: NotificationType): Int = when (notificationType) {
    is NotificationType.Info -> R.color.information_blue_40_percent
    is NotificationType.Error -> R.color.new_error_red_40_percent
    is NotificationType.Alert -> R.color.alert_orange_40_percent
}

@Composable
private fun iconResId(notificationType: NotificationType): Int = when (notificationType) {
    is NotificationType.Info, NotificationType.Error -> R.drawable.ic_notifications_info
    is NotificationType.Alert -> R.drawable.notifications_alert
}

@Composable
private fun iconColorFilter(notificationType: NotificationType): ColorFilter? = when (notificationType) {
    is NotificationType.Info -> ColorFilter.tint(color = colorResource(id = R.color.information_blue))
    is NotificationType.Error -> ColorFilter.tint(color = colorResource(id = R.color.new_error_red))
    is NotificationType.Alert -> null
}

@Preview
@Composable
fun PreviewInfoHomeNotification() {
    HomeNotification(
        notification =
        NotificationDomain(
            NotificationType.Info,
            "Notification title",
            "Notification message",
            "Optional link",
            "www.google.com",
            true,
            true
        ),
        onNotificationLinkClick = { _: String, _: Boolean -> },
        onCloseClick = {}
    )
}

@Preview
@Composable
fun PreviewErrorHomeNotification() {
    HomeNotification(
        notification =
        NotificationDomain(
            NotificationType.Error,
            "Notification title",
            "Notification message",
            "Optional link",
            "www.google.com",
            true,
            true
        ),
        onNotificationLinkClick = { _: String, _: Boolean -> },
        onCloseClick = {}
    )
}

@Preview
@Composable
fun PreviewAlertHomeNotification() {
    HomeNotification(
        notification =
        NotificationDomain(
            NotificationType.Alert,
            "Notification title",
            "Notification message",
            "Optional link",
            "www.google.com",
            true,
            true
        ),
        onNotificationLinkClick = { _: String, _: Boolean -> },
        onCloseClick = {}
    )
}
