package com.whitbread.premierinn.amend.amendreview.adapter

interface Diffable {

    val identifier: String

    fun diff(other: Any): Any? = null

    operator fun plus(items: Collection<Diffable>): List<Diffable> {
        return listOf(this, *items.toTypedArray())
    }
}