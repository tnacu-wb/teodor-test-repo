package com.whitbread.premierinn.common

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

class AutoCompositeDisposable(val lifecycle: Lifecycle, val doOnPause: Boolean = false) : LifecycleObserver {

    val compositeDisposable: CompositeDisposable

    init {
        lifecycle.addObserver(this)
        compositeDisposable = CompositeDisposable()
    }

    fun add(disposable: Disposable) {
        compositeDisposable.add(disposable)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        if (doOnPause) {
            compositeDisposable.clear()
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        if (!doOnPause) {
            compositeDisposable.clear()
        }
        lifecycle.removeObserver(this)
    }
}

fun Disposable.addTo(d: AutoCompositeDisposable) {
    d.add(this)
}