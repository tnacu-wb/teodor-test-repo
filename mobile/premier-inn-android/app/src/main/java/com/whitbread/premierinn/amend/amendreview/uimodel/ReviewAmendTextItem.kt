package com.whitbread.premierinn.amend.amendreview.uimodel

import com.whitbread.premierinn.amend.amendreview.adapter.Diffable

data class ReviewAmendTextItem(
    val title: String? = null,
    val description: String? = null,
    val priceChange: String? = null
) : Diffable {
    override val identifier: String
        get() = this.javaClass.name

    fun mapToReviewAmendTextItem(title: String? = null, description: String? = null, priceChange: String? = null) : ReviewAmendTextItem {
        return ReviewAmendTextItem(
            title = title,
            description = description,
            priceChange = priceChange
        )
    }
}