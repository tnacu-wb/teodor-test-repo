package com.whitbread.premierinn.domain.common

data class AllCheckInCheckoutTimesInfoDomain(val ukCheckInTimes: HotelCheckInCheckoutInfoDomain,
                                             val germanyCheckInTimes: HotelCheckInCheckoutInfoDomain)

data class HotelCheckInCheckoutInfoDomain(val bookingDetailsCheckInInfo: String,
                                          val bookingDetailsCheckOutInfo: String,
                                          val summaryOrPaymentBreakdownCheckInInfo: String,
                                          val summaryOrPaymentBreakdownCheckOutInfo: String)