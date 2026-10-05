package com.whitbread.premierinn.changepassword

import com.whitbread.premierinn.common.analytics.AnalyticsConstants
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.ERROR_MESSAGE
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Key.LABEL_KEY
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Value.PASSWORD_UPDATED
import com.whitbread.premierinn.common.analytics.AnalyticsConstants.Value.WRONG_PASSWORD_MATCH
import com.whitbread.premierinn.common.analytics.TrackingAnalytics
import com.whitbread.premierinn.common.analytics.analyticsDataOf
import com.whitbread.premierinn.common.forms.FormInputErrorMessageProvider
import com.whitbread.premierinn.common.forms.FormUiModel
import com.whitbread.premierinn.common.forms.Input
import com.whitbread.premierinn.common.forms.action.Action
import com.whitbread.premierinn.common.forms.action.ResetInputErrorAction
import com.whitbread.premierinn.common.forms.action.SubmitFormAction
import com.whitbread.premierinn.common.forms.observabletransformer.ResetInputErrorActionTransformer
import com.whitbread.premierinn.common.forms.observabletransformer.SubmitFormActionTransformer
import com.whitbread.premierinn.common.forms.result.FormResult
import com.whitbread.premierinn.common.forms.result.ResetInputErrorResult
import com.whitbread.premierinn.common.forms.result.Result
import com.whitbread.premierinn.common.forms.result.SubmitFormResult
import com.whitbread.premierinn.common.mvp.Presenter
import com.whitbread.premierinn.common.mvp.PresenterView
import com.whitbread.premierinn.common.service.LogService
import com.whitbread.premierinn.domain.customer.usecase.UpdateCustomerPassword
import dagger.hilt.android.scopes.ActivityRetainedScoped
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

@ActivityRetainedScoped
class ChangePasswordPresenter @Inject constructor(
    private val updateCustomerPassword: UpdateCustomerPassword,
    private val compositeDisposable: CompositeDisposable,
    private val trackingAnalytics: TrackingAnalytics,
    private val crashlyticsLogger: LogService,
    private val formInputErrorMessageProvider: FormInputErrorMessageProvider
):Presenter<ChangePasswordPresenter.View>() {

    override fun onAttachView(view: View) {
        trackingAnalytics.track(AnalyticsConstants.ScreenState.CHANGE_PASSWORD, AnalyticsConstants.Type.MY_PREMIER_INN)
        val submitApiFormTransformer: ObservableTransformer<List<Input>, SubmitFormResult> = ChangePasswordApiFormTransformer(
                updateCustomerPassword, crashlyticsLogger)
        val sharedActionTransformer = ObservableTransformer { events: Observable<Action?> ->
            events.publish { shared: Observable<Action?> ->
                Observable.merge(
                        shared.ofType(SubmitFormAction::class.java)
                                .compose(SubmitFormActionTransformer(submitApiFormTransformer)),
                        shared.ofType(ResetInputErrorAction::class.java)
                                .compose(ResetInputErrorActionTransformer()))
            }
        }
        val scanFormResults = view.actions
                .compose(sharedActionTransformer)
                .scan(FormUiModel.idle(), { oldState: FormUiModel, result: Result? ->
                    if (result is SubmitFormResult) {
                        when (result.state()) {
                            SubmitFormResult.State.IN_FLIGHT -> return@scan FormUiModel.inProgress()
                            SubmitFormResult.State.VALIDATION_FAILED -> {
                                val data = analyticsDataOf(ERROR_MESSAGE to WRONG_PASSWORD_MATCH)
                                trackingAnalytics.track(AnalyticsConstants.ScreenState.PASSWORD_CHANGED, data)
                                return@scan FormUiModel.updateForm(result.inputs(), formInputErrorMessageProvider)
                            }
                            SubmitFormResult.State.SUCCESS -> {
                                val data = analyticsDataOf(LABEL_KEY to PASSWORD_UPDATED)
                                trackingAnalytics.track(AnalyticsConstants.ScreenState.MY_ACCOUNT, data)
                                return@scan FormUiModel.success()
                            }
                            SubmitFormResult.State.FAILED -> return@scan FormUiModel.error(result.errorMessage())
                            else -> {}
                        }
                    }
                    if (result is ResetInputErrorResult && oldState.state() == FormUiModel.State.FORM_UPDATE) {
                        return@scan FormUiModel.resetInputError(result.inputId(), oldState.inputStates())
                    }
                    if (result is FormResult) {
                        return@scan FormUiModel.builder()
                                .state(FormUiModel.State.FORM_UPDATE)
                                .inputStates(result.inputs()).build()
                    }
                    FormUiModel.idle()
                })
        compositeDisposable.add(scanFormResults
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    view.update(it)
                }, {
                    view.showGenericError()
                    crashlyticsLogger.logException(it)
                }))
    }

    override fun onDetachView() {
        compositeDisposable.clear()
        super.onDetachView()
    }

    interface View : PresenterView {
        fun update(uiModel: FormUiModel?)
        fun showGenericError()
        val actions: Observable<Action?>
    }

}