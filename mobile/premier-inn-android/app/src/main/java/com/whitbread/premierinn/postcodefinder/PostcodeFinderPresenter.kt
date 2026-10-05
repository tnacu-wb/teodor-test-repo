package com.whitbread.premierinn.postcodefinder

import com.whitbread.premierinn.common.mvp.Presenter
import com.whitbread.premierinn.common.mvp.PresenterView
import com.whitbread.premierinn.domain.common.Address
import com.whitbread.premierinn.domain.common.AddressShort
import com.whitbread.premierinn.domain.graphql.guestDetails.usecase.GraphQLGuestDetailsUseCase
import com.whitbread.premierinn.domain.graphql.requestBodyModels.PartialAddressRequestBody
import com.whitbread.premierinn.postcodefinder.PostcodeState.Action.SHOW_DEFAULT_SCREEN
import com.whitbread.premierinn.postcodefinder.PostcodeState.Action.SHOW_LOADING
import com.whitbread.premierinn.postcodefinder.PostcodeState.Action.SHOW_NO_RESULT
import com.whitbread.premierinn.postcodefinder.PostcodeState.Action.SHOW_RESULT
import dagger.hilt.android.scopes.ActivityRetainedScoped
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@ActivityRetainedScoped
class PostcodeFinderPresenter @Inject constructor(
    private val graphQLGuestDetailsUseCase: GraphQLGuestDetailsUseCase,
    private val compositeDisposable: CompositeDisposable,
) : Presenter<PostcodeFinderPresenter.View>() {

    private lateinit var postcode: String
    private var postcodeAddresses: List<AddressShort>? = null
    private var postcodeInput: String? = null

    fun initParams(postcode: String) {
        postcodeInput = postcode
    }

    override fun onAttachView(view: View) {
        if (postcodeAddresses == null) {
            view.showDefaultScreen()
            if (postcodeInput != null && postcodeInput!!.isNotEmpty()) {
                view.displayPostcode(postcodeInput!!)
                postcodeInput = null
            }
        } else {
            view.showResults(postcodeAddresses!!)
        }
        compositeDisposable.add(view.onPostCodeEntered()
            .map { s -> s.replace("\\s+".toRegex(), "") }
            .switchMap { postCode ->
                if (postCode.length >= MIN_CHARACTERS_TO_START_SEARCH) {

                    graphQLGuestDetailsUseCase.getPartialAddress(
                        PartialAddressRequestBody(postCode)
                    )
                        .subscribeOn(Schedulers.io())
                        .map { postcodeAddressSearchResult ->
                            PostcodeState.create(
                                SHOW_RESULT,
                                postcodeAddressSearchResult.partialAddress
                            )
                        }
                        .observeOn(AndroidSchedulers.mainThread())
                        .onErrorReturn { PostcodeState.create(SHOW_NO_RESULT) }
                        .toObservable()
                        .startWith(PostcodeState.create(SHOW_LOADING))

                } else {
                    Observable.just(PostcodeState.create(SHOW_DEFAULT_SCREEN))
                }
            }
            .subscribe({ postcodeState ->
                if (isViewAttached) {
                    when (postcodeState.action()) {
                        SHOW_DEFAULT_SCREEN -> view.showDefaultScreen()
                        SHOW_LOADING -> view.showLoading()
                        SHOW_RESULT -> {
                            postcodeAddresses = postcodeState.addresses()
                            if (postcodeAddresses != null && postcodeAddresses!!.isNotEmpty()) {
                                view.showResults(postcodeAddresses!!)
                            } else {
                                view.showNoResults()
                            }
                        }

                        SHOW_NO_RESULT -> view.showNoResults()
                        else -> view.showNoResults()
                    }
                }
            }, {
                view.showNoResults()
            })
        )
        compositeDisposable.add(view.onAddressClick()
            .switchMapSingle { addressShort ->
                graphQLGuestDetailsUseCase.getFormattedAddress(addressShort.id)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe {
                        if (isViewAttached) {
                            view.showSelectedAddressLoading()
                        }
                    }
                    .doOnError {
                        if (isViewAttached) {
                            view.showSelectedAddressLoadingFailed()
                        }
                    }
                    .onErrorResumeNext(Single.never())
            }
            .subscribe({ address ->
                if (isViewAttached) {
                    view.selectAddress(address.formattedAddress)
                }
            }, {
                view.showSelectedAddressLoadingFailed()
            })
        )
        compositeDisposable.add(view.onManualAddressClicked()
            .subscribe { view.showManualAddressInput() }
        )

    }

    public override fun onDetachView() {
        if (compositeDisposable.size() > 0) {
            compositeDisposable.clear()
        }
    }

    interface View : PresenterView {
        fun showResults(postcodeAddresses: List<AddressShort?>)
        fun showNoResults()
        fun showDefaultScreen()
        fun showLoading()
        fun displayPostcode(text: String)
        fun selectAddress(postcodeAddress: Address)
        fun showManualAddressInput()
        fun showSelectedAddressLoading()
        fun showSelectedAddressLoadingFailed()
        fun onAddressClick(): Observable<AddressShort?>
        fun onPostCodeEntered(): Observable<String>
        fun onManualAddressClicked(): Observable<Unit>
    }

    companion object {
        private const val MIN_CHARACTERS_TO_START_SEARCH = 5
    }
}
