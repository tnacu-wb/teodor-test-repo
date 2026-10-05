package com.whitbread.premierinn.data.graphql.mapper

import com.whitbread.premierinn.data.common.EMPTY_STRING
import com.whitbread.premierinn.data.remote.graphql.contracts.HotelPreferencesGraphQLContract
import com.whitbread.premierinn.domain.booking.entity.HotelPreferenceDomain

fun HotelPreferencesGraphQLContract.HotelPreference.mapToHotelPreferenceDomain(): HotelPreferenceDomain = HotelPreferenceDomain(
    code = code,
    preferenceGroup = preferenceGroup,
    label = label ?: EMPTY_STRING
)
