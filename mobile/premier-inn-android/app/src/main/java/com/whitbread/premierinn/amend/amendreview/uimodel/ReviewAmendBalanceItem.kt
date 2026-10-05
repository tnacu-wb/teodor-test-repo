package com.whitbread.premierinn.amend.amendreview.uimodel

import com.whitbread.premierinn.amend.amendreview.adapter.Diffable

data class ReviewAmendBalanceItem(
        val title: String? = null,
        val description: String? = null,
        val balance: String? = null) : Diffable {

    override val identifier: String
    get() = this.javaClass.name

    fun mapToReviewAmendBalanceItem(title: String, description: String, balance: String): ReviewAmendBalanceItem {
        return ReviewAmendBalanceItem(
                title = title,
                description = description,
                balance = balance
        )
    }
}