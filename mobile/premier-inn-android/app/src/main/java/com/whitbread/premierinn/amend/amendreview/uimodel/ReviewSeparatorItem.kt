package com.whitbread.premierinn.amend.amendreview.uimodel

import com.whitbread.premierinn.amend.amendreview.adapter.Diffable

object ReviewSeparatorItem : Diffable {
    override val identifier: String
        get() = this.javaClass.name
}