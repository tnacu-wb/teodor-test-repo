package com.whitbread.premierinn.amend.amendreview.uimodel

import com.whitbread.premierinn.amend.amendreview.adapter.Diffable

data class ReviewAmendEciLcoInfoBoxItem(val message: String? = null) : Diffable {

    override val identifier: String
        get() = this.javaClass.name

    fun mapToReviewAmendEciLcoInfoMessage(message: String): ReviewAmendEciLcoInfoBoxItem {
        return ReviewAmendEciLcoInfoBoxItem(
            message = message
        )
    }
}