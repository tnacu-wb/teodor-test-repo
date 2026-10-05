package com.whitbread.premierinn.common.view.gallery

interface Gallery<T> {

    fun images(images: List<T>)
    fun currentItem(): Int
    fun setCurrentItem(position: Int)
}