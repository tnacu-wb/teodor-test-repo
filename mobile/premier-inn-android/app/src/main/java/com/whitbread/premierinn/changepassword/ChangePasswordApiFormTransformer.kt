package com.whitbread.premierinn.changepassword

import com.whitbread.premierinn.R
import com.whitbread.premierinn.common.AsyncResult
import com.whitbread.premierinn.common.forms.Input
import com.whitbread.premierinn.common.forms.result.SubmitFormResult
import com.whitbread.premierinn.common.mapToAsyncResult
import com.whitbread.premierinn.common.service.LogService
import com.whitbread.premierinn.domain.customer.usecase.UpdateCustomerPassword
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer

class ChangePasswordApiFormTransformer(private val updateCustomerPassword: UpdateCustomerPassword,
                                       private val crashlyticsLogger: LogService) : ObservableTransformer<List<Input>, SubmitFormResult> {
    override fun apply(upstream: Observable<List<Input>>): ObservableSource<SubmitFormResult> {
        return upstream
                .flatMap { inputs: List<Input> ->
                    var newPassword = ""
                    var confirmPassword = ""
                    for (input in inputs) {
                        when (input.id()) {
                            R.id.change_password_new_password_input -> newPassword = input.value().toString()
                            R.id.change_password_confirm_password_input -> confirmPassword = input.value().toString()
                            else -> {
                            }
                        }
                    }
                    updateCustomerPassword(UpdateCustomerPassword.Params(newPassword, confirmPassword)).mapToAsyncResult()
                }
                .map { asyncResult: AsyncResult<*> ->
                    when (asyncResult) {
                        is AsyncResult.Success -> return@map SubmitFormResult.serverSuccess()
                        is AsyncResult.Error -> {
                            crashlyticsLogger.logException(asyncResult.error, asyncResult.error.message)
                            return@map SubmitFormResult.serverError()
                        }
                        is AsyncResult.Loading -> return@map SubmitFormResult.inFlight()
                        else -> return@map SubmitFormResult.inFlight()
                    }
                }
    }
}