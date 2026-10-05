package com.whitbread.premierinn.common.usecase

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.whitbread.premierinn.data.common.toQrKioskHotelsDomain
import com.whitbread.premierinn.data.remote.QrKioskHotels
import com.whitbread.premierinn.domain.common.QrKioskHotelsDomain
import com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository
import com.whitbread.premierinn.domain.resource.usecase.GetStringResource
import javax.inject.Inject

class GetQrKioskHotelUseCase @Inject constructor(private val getStringResource: GetStringResource) {

    operator fun invoke(): List<QrKioskHotelsDomain> {
        val type = object : TypeToken<List<QrKioskHotels>>() {}.type
       return Gson().fromJson<List<QrKioskHotels>>(
            getStringResource.invoke(
                ContentManagedResourceRepository.Key.QR_KIOSK_HOTELS
            ), type)
            .toList()
            .map { it.toQrKioskHotelsDomain() }
    }
}
