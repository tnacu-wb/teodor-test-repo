package com.whitbread.premierinn.ciol.mapper

import com.whitbread.premierinn.ciol.uimodel.HotelPreferenceUiModel
import com.whitbread.premierinn.domain.booking.entity.HotelPreferenceDomain

fun HotelPreferenceDomain.mapToHotelPreferenceUiModel() = HotelPreferenceUiModel(
    code = code,
    preferenceGroup = preferenceGroup,
    label = label
)
