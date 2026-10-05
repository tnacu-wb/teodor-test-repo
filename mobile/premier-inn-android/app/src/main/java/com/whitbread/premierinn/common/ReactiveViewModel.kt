package com.whitbread.premierinn.common

import io.reactivex.Observable

/**
 *
 */
interface ReactiveViewModel<E, S> {
    fun bind(viewEvents: Observable<out E>)
    fun viewStates(): Observable<S>
}