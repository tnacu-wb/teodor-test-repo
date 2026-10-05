package com.whitbread.premierinn.amend.amendreview.uimodel

import com.whitbread.premierinn.amend.amendreview.adapter.Diffable

data class ReviewChangesTitleItem(val title : String? = null) : Diffable {

    override val identifier: String
        get() = this.javaClass.name

    fun mapToReviewChangesHeader(title: String) : ReviewChangesTitleItem {
        return ReviewChangesTitleItem(title =  title)
    }
}