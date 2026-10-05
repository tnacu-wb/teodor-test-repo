package com.whitbread.premierinn.domain.graphql.requestBodyModels

data class CategoryLabelsRequestBody(
    val country: String,
    val language: String,
    val category: String,
    val labels: List<String>
) {
    companion object {
        const val CATEGORY = "main"
        const val LABEL_TITLE = "mybookings.post-checkin.notification.item1.title"
        const val LABEL_DESCRIPTION = "mybookings.post-checkin.notification.item1.description"
        const val LABEL_DE_DESCRIPTION = "mybookings.post-checkin.notification.item1.description.PID"
    }
}
