package com.whitbread.premierinn.common.view

/**
 * Generic Type for RecyclerView ListAdapter purposes
 */
interface ListItem {
    fun id(): String
    fun type(): Int
    override fun equals(other: Any?): Boolean
}