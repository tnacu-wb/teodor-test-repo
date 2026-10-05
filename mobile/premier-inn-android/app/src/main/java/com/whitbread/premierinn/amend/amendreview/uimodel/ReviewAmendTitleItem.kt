package com.whitbread.premierinn.amend.amendreview.uimodel

import com.whitbread.premierinn.amend.amendreview.adapter.Diffable

data class ReviewAmendTitleItem(val title: String? = null, val total: String? = null) : Diffable {

    override val identifier: String
        get() = this.javaClass.name

    fun mapToReviewAmendTitle(title: String, previousTotal: String): ReviewAmendTitleItem {
        return ReviewAmendTitleItem(
                title = title,
                total = previousTotal
        )
    }
}