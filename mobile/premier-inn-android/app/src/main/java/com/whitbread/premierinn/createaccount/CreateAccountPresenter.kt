package com.whitbread.premierinn.createaccount

import com.whitbread.premierinn.common.analytics.AnalyticsConstants
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.ScreenState
import com.whitbread.premierinn.common.analytics.TrackingAnalytics
import com.whitbread.premierinn.common.forms.FormInputErrorMessageProvider
import com.whitbread.premierinn.common.forms.FormUiModel
import com.whitbread.premierinn.common.forms.Input
import com.whitbread.premierinn.common.forms.action.Action
import com.whitbread.premierinn.common.forms.action.ClickAction
import com.whitbread.premierinn.common.forms.action.ResetInputErrorAction
import com.whitbread.premierinn.common.forms.action.SubmitFormAction
import com.whitbread.premierinn.common.forms.observabletransformer.CountryChangedActionTransformer
import com.whitbread.premierinn.common.forms.observabletransformer.HomeWorkToggleActionTransformer
import com.whitbread.premierinn.common.forms.observabletransformer.PostcodeAddressTransformer
import com.whitbread.premierinn.common.forms.observabletransformer.ResetInputErrorActionTransformer
import com.whitbread.premierinn.common.forms.observabletransformer.SubmitFormActionTransformer
import com.whitbread.premierinn.common.forms.result.SubmitFormResult
import com.whitbread.premierinn.common.forms.scan.FormSubmissionScanBifunction
import com.whitbread.premierinn.common.mvp.Presenter
import com.whitbread.premierinn.common.mvp.PresenterView
import com.whitbread.premierinn.common.service.LogService
import com.whitbread.premierinn.createaccount.action.CountryChangedAction
import com.whitbread.premierinn.createaccount.action.HomeWorkToggleAction
import com.whitbread.premierinn.createaccount.observabletransformer.ClickActionTransformer
import com.whitbread.premierinn.createaccount.observabletransformer.CreateAccountSubmitApiFormTransformer
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider
import com.whitbread.premierinn.domain.authentication.usecase.AuthenticateCustomer
import com.whitbread.premierinn.domain.countries.GetCountries
import com.whitbread.premierinn.domain.countries.entity.CountryDomain
import com.whitbread.premierinn.domain.customer.usecase.CreateCustomer
import com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository
import com.whitbread.premierinn.domain.resource.usecase.GetStringResource
import com.whitbread.premierinn.postcodefinder.ParcelableAddress
import dagger.hilt.android.scopes.ActivityRetainedScoped
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

@ActivityRetainedScoped
class CreateAccountPresenter @Inject constructor(
    private val getCountries: GetCountries,
    private val compositeDisposable: CompositeDisposable,
    private val messageProvider: CreateAccountMessageProvider,
    private val createCustomer: CreateCustomer,
    private val authenticateCustomer: AuthenticateCustomer,
    private val trackingAnalytics: TrackingAnalytics,
    private val getStringResource: GetStringResource,
    private val crashlyticsLogger: LogService,
    private val deviceLocaleProvider: DeviceLocaleProvider,
    private val formInputErrorMessageProvider: FormInputErrorMessageProvider
) : Presenter<CreateAccountPresenter.View>() {

    override fun onAttachView(view: View) {
        trackingAnalytics.track(ScreenState.CREATE_ACCOUNT, AnalyticsConstants.Type.MY_PREMIER_INN)
        compositeDisposable.add(view.onSetImmediately()
                .subscribe { view.populateTitlesList(messageProvider.titlesList()) })

        if (isViewAttached) {
            getCountries.fetchCountriesFromSharedPref()?.let {
                view.populateCountriesList(it)
            }
        }

        compositeDisposable.add(view.onFindAddressClick()
                .subscribe { view.startPostcodeFinderActivity() })
        compositeDisposable.add(view.onPostcodeFindingSuccess()
                .compose(PostcodeAddressTransformer())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { uiModel: FormUiModel? -> view.update(uiModel) })

        val submitApiFormTransformer: ObservableTransformer<List<Input>, SubmitFormResult> =
                CreateAccountSubmitApiFormTransformer(createCustomer, authenticateCustomer,
                        messageProvider, trackingAnalytics, crashlyticsLogger, deviceLocaleProvider)
        val sharedActionTransformer = ObservableTransformer { events: Observable<Action?> ->
            events.publish { shared: Observable<Action?> ->
                Observable.merge(
                        shared.ofType(SubmitFormAction::class.java)
                                .compose(SubmitFormActionTransformer(submitApiFormTransformer)),
                        shared.ofType(ResetInputErrorAction::class.java)
                                .compose(ResetInputErrorActionTransformer()),
                        shared.ofType(CountryChangedAction::class.java)
                                .compose(CountryChangedActionTransformer()))
                        .mergeWith(shared.ofType(ClickAction::class.java)
                                .compose(ClickActionTransformer()))
                        .mergeWith(shared.ofType(HomeWorkToggleAction::class.java)
                                .compose(HomeWorkToggleActionTransformer()))
                        .mergeWith(shared.ofType(ClickAction::class.java)
                                .compose(ClickActionTransformer()))
            }
        }
        val scanFormResults = view.getActions()
                .compose(sharedActionTransformer)
                .scan(FormUiModel.idle(), FormSubmissionScanBifunction(formInputErrorMessageProvider))
        compositeDisposable.add(scanFormResults
                .distinctUntilChanged()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ uiModel: FormUiModel? -> view.update(uiModel) }) { e: Throwable? ->
                    view.showGenericError()
                    e?.let { crashlyticsLogger.logException(it, it.message) }

                })
        view.showDataUsageContent(getStringResource.invoke(ContentManagedResourceRepository.Key.GDPR_MY_DETAILS_USAGE))
    }

    override fun onDetachView() {
        compositeDisposable.clear()
        super.onDetachView()
    }

    interface View : PresenterView {
        fun populateCountriesList(countries: List<CountryDomain>)
        fun populateTitlesList(titlesList: MutableList<String>)
        fun startPostcodeFinderActivity()
        fun update(uiModel: FormUiModel?)
        fun showGenericError()
        fun onSetImmediately(): Observable<Any>
        fun onPostcodeFindingSuccess(): Observable<ParcelableAddress>
        fun onFindAddressClick(): Observable<Unit>
        fun getActions(): Observable<Action>
        fun showDataUsageContent(content: String?)
    }

}