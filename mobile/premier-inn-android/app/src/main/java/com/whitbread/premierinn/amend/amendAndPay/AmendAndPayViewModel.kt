package com.whitbread.premierinn.amend.amendAndPay


import androidx.lifecycle.SavedStateHandle
import com.whitbread.premierinn.amend.amendAndPay.AmendAndPayActivity.Companion.AMEND_SUMMARY_DOMAIN
import com.whitbread.premierinn.amend.amendAndPay.AmendAndPayActivity.Companion.CURRENCY
import com.whitbread.premierinn.amend.amendguestsrooms.EXTRA_AMEND_INPUT
import com.whitbread.premierinn.common.AppConfiguration
import com.whitbread.premierinn.common.Reducer
import com.whitbread.premierinn.common.RxViewModelStore
import com.whitbread.premierinn.common.format.PriceFormat
import com.whitbread.premierinn.common.managebooking.ManageBookingInput
import com.whitbread.premierinn.common.mapToAsyncResult
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider
import com.whitbread.premierinn.domain.common.PAY_NOW
import com.whitbread.premierinn.domain.common.SUB_CHANNEL
import com.whitbread.premierinn.domain.graphql.amend.entity.AmendSummaryDomain
import com.whitbread.premierinn.domain.graphql.amend.usecase.GraphQLAmendUseCase
import com.whitbread.premierinn.domain.graphql.requestBodyModels.BookingChannelDetails
import com.whitbread.premierinn.domain.graphql.requestBodyModels.Channel
import com.whitbread.premierinn.domain.graphql.requestBodyModels.ConfirmAmendLogicRequestBody
import com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository
import com.whitbread.premierinn.domain.resource.usecase.GetStringResource
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class AmendAndPayViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    getStringResource: GetStringResource,
    val deviceLocaleProvider: DeviceLocaleProvider,
    val graphQLAmendUseCase: GraphQLAmendUseCase,
    val appConfiguration: AppConfiguration
    ) : RxViewModelStore<AmendAndPayState, AmendAndPayViewModel.AmendAndPayEvent>(AmendAndPayState()) {

    private val input: ManageBookingInput by lazy {
        requireNotNull(savedStateHandle.get<ManageBookingInput>(EXTRA_AMEND_INPUT))
    }
    private val amendSummaryDomain: AmendSummaryDomain by lazy {
        requireNotNull(savedStateHandle.get<AmendSummaryDomain>(AMEND_SUMMARY_DOMAIN))
    }
    private val currency: String by lazy {
        savedStateHandle.get<String>(CURRENCY) ?: ""
    }

    init {
        getStringResource.invoke(ContentManagedResourceRepository.Key.BOOKING_PRIVACY_FOOTER).let {
            result -> applyState(Reducer { it.copy(privacyPolicyText = result) })
        }

        applyState(Reducer {
            it.copy(totalCost =  PriceFormat.format(amendSummaryDomain.payOnArrival, currency, deviceLocaleProvider))
        })

    }

    fun confirmAmendLogicCheck(tempBookingRef: String, originalBookingRef: String, token: String) {
        val environment = appConfiguration.graphQLUrl.replace("api", "www")
        graphQLAmendUseCase.confirmAmendLogic(ConfirmAmendLogicRequestBody(
            bookingChannel = BookingChannelDetails(
                if (input.isBusinessBooking) Channel.BB.name else Channel.PI.name,
                SUB_CHANNEL,
                deviceLocaleProvider.getDeviceLanguage().lowercase()
            ),
            tempBookingRef = tempBookingRef,
            originalBookingRef = originalBookingRef,
            token = token,
            paymentOptionSelected = PAY_NOW,
            environment = environment))
            .mapToAsyncResult()
            .subscribeOn(Schedulers.io())
            .subscribe { result ->
                applyState(Reducer {it.copy(confirmAmendResponse = result)})
            }.addDisposable()


    }

    sealed class AmendAndPayEvent {
        object GenericErrorEvent : AmendAndPayEvent()
    }

    sealed class AmendAndPayState {
        data class InitialState(val isLoaded: Boolean) : AmendAndPayState()
    }
}