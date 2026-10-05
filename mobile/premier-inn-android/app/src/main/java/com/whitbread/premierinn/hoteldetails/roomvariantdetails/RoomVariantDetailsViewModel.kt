package com.whitbread.premierinn.hoteldetails.roomvariantdetails

import androidx.lifecycle.SavedStateHandle
import com.whitbread.premierinn.api.Urls
import com.whitbread.premierinn.common.Reducer
import com.whitbread.premierinn.common.RxViewModelStore
import com.whitbread.premierinn.common.mapToAsyncResult
import com.whitbread.premierinn.data.common.BRAND_HUB
import com.whitbread.premierinn.data.common.BRAND_PI
import com.whitbread.premierinn.data.common.BRAND_PI_GERMANY
import com.whitbread.premierinn.data.common.BRAND_ZIP
import com.whitbread.premierinn.data.common.devicelocal.DeviceLocaleProvider
import com.whitbread.premierinn.data.remote.graphql.contracts.HOTEL_DISCLAIMER_HUB_KEY
import com.whitbread.premierinn.data.remote.graphql.contracts.HOTEL_DISCLAIMER_PID_KEY
import com.whitbread.premierinn.data.remote.graphql.contracts.HOTEL_DISCLAIMER_PI_KEY
import com.whitbread.premierinn.data.remote.graphql.contracts.HOTEL_DISCLAIMER_ZIP_KEY
import com.whitbread.premierinn.domain.common.hoteldetails.usecase.GraphQLHotelDetailsUseCase
import com.whitbread.premierinn.domain.common.hoteldetails.usecase.GraphQLHotelDisclaimerUseCase
import com.whitbread.premierinn.domain.graphql.requestBodyModels.CategoryLabelsRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.CategoryLabelsRequestBody.Companion.CATEGORY
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class RoomVariantDetailsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    graphQLHotelDetailsUseCase: GraphQLHotelDetailsUseCase,
    graphQLHotelDisclaimerUseCase: GraphQLHotelDisclaimerUseCase,
    deviceLocaleProvider: DeviceLocaleProvider
): RxViewModelStore<RoomVariantDetailsState, Nothing>(RoomVariantDetailsState(deviceLocaleProvider)) {

    private val hotelCode: String  by lazy {
        savedStateHandle.get<String>("hotel_code_key") ?: ""
    }
    private val hotelBrand: String  by lazy {
        savedStateHandle.get<String>("hotel_brand_key") ?: ""
    }

    init {
        Observable.zip(
            graphQLHotelDetailsUseCase.fetchRoomVariantsDetails(
                deviceLocaleProvider.getDeviceLocale().country.lowercase(),
                hotelCode, deviceLocaleProvider.getDeviceLanguage()
            ).toObservable(),
            graphQLHotelDisclaimerUseCase.invoke(getHotelDisclaimerRequestBody(deviceLocaleProvider)).toObservable()
        ) { roomVariantDetails, hotelDisclaimer ->
            roomVariantDetails.map { item ->
                item.copy(
                    imageUrl = Urls.CONTENT_BASE_URL + item.imageUrl,
                    disclaimer = hotelDisclaimer.disclaimer
                )
            }
        }
            .mapToAsyncResult()
            .subscribeOn(Schedulers.io())
            .subscribe { result -> applyState(Reducer { it.copy(result = result) }) }
            .addDisposable()
    }

    private fun getHotelDisclaimerRequestBody(deviceLocaleProvider: DeviceLocaleProvider): CategoryLabelsRequestBody {
        val requestLabel = when(hotelBrand) {
            BRAND_PI -> HOTEL_DISCLAIMER_PI_KEY
            BRAND_PI_GERMANY -> HOTEL_DISCLAIMER_PID_KEY
            BRAND_HUB -> HOTEL_DISCLAIMER_HUB_KEY
            BRAND_ZIP -> HOTEL_DISCLAIMER_ZIP_KEY
            else -> HOTEL_DISCLAIMER_PI_KEY
        }

        return CategoryLabelsRequestBody(
            country = deviceLocaleProvider.getCountryIfRegion(deviceLocaleProvider.getDeviceLocale())
                .lowercase(),
            language = deviceLocaleProvider.getDeviceLanguage().lowercase(),
            category = CATEGORY,
            labels = listOf(requestLabel)
        )
    }
}
