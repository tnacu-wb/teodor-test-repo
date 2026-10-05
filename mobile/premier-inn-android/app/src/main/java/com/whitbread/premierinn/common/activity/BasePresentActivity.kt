package com.whitbread.premierinn.common.activity

import androidx.viewbinding.ViewBinding
import com.whitbread.premierinn.common.mvp.Presenter
import com.whitbread.premierinn.common.mvp.PresenterView


abstract class BasePresenterActivity<
        V : PresenterView,
        VB : ViewBinding,
        P : Presenter<V>> : BaseActivity<VB>() {

    /** Prefer DI: inject presenter (Hilt/Koin/etc.). If you don't use DI, override createPresenter(). */
    protected lateinit var presenter: P

    /** If you don't inject, override this and return a presenter. Default throws to avoid silent nulls. */
    protected open fun createPresenter(): P =
        throw IllegalStateException("Provide presenter via DI or override createPresenter()")

    /** The actual view instance passed to presenter (can be `this` if Activity implements V). */
    protected abstract fun provideView(): V

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)

        // Create/resolve presenter if not injected
        if (!::presenter.isInitialized) {
            presenter = createPresenter()
        }
        presenter.attachView(provideView())
        onPresenterAttached(presenter)
        setContentView(binding.root)
    }

    override fun onDestroy() {
        presenter.detachView()
        super.onDestroy()
        if (isFinishing) {
            presenter.destroy()
        }
    }

    /** Optional hook */
    protected open fun onPresenterAttached(presenter: P) {}
}


