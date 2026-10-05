package com.whitbread.premierinn.domain.payment

import com.whitbread.premierinn.domain.common.PriceDomain

data class AmendReservationDomainDetails(val sessionId: String?,
                                  val confirmationNumber: String?,
                                  val checkInOnline: Boolean?,
                                  val totalCost: PriceDomain?,
                                  val cityTax: PriceDomain?,
                                  val vatRate: Float?,
                                  val carData: CarDataDetails?,
                                  val prepaymentSuccess: Boolean,
                                  val payOnArrivalSuccess: Boolean,
                                  val prepaymentText: String?,
                                  val pendingAmendId: String?)

data class CarDataDetails(val title: String?,
                          val carParkOperator: String?,
                          val hotelCode: String?,
                          val stayLength: Int?)