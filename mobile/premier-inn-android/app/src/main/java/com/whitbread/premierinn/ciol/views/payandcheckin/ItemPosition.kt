package com.whitbread.premierinn.ciol.views.payandcheckin

enum class ItemPosition {
    TOP,
    MIDDLE,
    BOTTOM
}

fun getItemPosition(itemIndex: Int, totalItems: Int) = when (itemIndex) {
    0 -> ItemPosition.TOP
    totalItems - 1 -> ItemPosition.BOTTOM
    else -> ItemPosition.MIDDLE
}
