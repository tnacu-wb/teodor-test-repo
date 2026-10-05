package com.whitbread.premierinn.common

/**
 *
 */
interface ReactiveView<S> {
    fun render(state: S)
}