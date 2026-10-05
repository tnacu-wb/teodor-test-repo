package com.whitbread.premierinn.data.graphql

import com.whitbread.premierinn.domain.graphql.requestBodyModels.CreateReservationGuestRegCardRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.HotelPackagesRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.PaymentMethodsRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.UpdatePreStayInfoRequestBody
import org.json.JSONArray
import org.json.JSONObject

fun HotelPackagesRequestBody.constructVariables(jsonObject: JSONObject): JSONObject {
    return jsonObject.apply {
        put("packagesCriteria", JSONObject().apply {
            put("hotelId", this@constructVariables.hotelId)
            put("startDate", this@constructVariables.startDate)
            put("endDate", this@constructVariables.endDate)
            put("adultsNumber", this@constructVariables.adultsNumber)
            put("childrenNumber", this@constructVariables.childrenNumber)
            put("nightsNumber", this@constructVariables.nightsNumber)
            put("language", this@constructVariables.language)
            put("country", this@constructVariables.country)
            put("bookingFlowId", this@constructVariables.bookingFlowId)
            put("showMealInclusiveRate", this@constructVariables.showMealInclusiveRate)
            put("basketReferenceId", this@constructVariables.basketReferenceId)
            put("channel", this@constructVariables.channel)
        })
    }
}

fun PaymentMethodsRequestBody.constructVariables(jsonObject: JSONObject): JSONObject {
    return jsonObject.apply {
        put("paymentMethodsCriteria", JSONObject().apply {
            put("country", country)
            put("language", language)
            put("basketReference", basketReference)
            put("clientChannel", clientChannel)
            put("userType", userType)
            put("userType", userType)
            flowType?.let {
                put("flowType", it.name)
            }
        })
    }
}

fun UpdatePreStayInfoRequestBody.constructVariables(jsonObject: JSONObject): JSONObject {
    return jsonObject.apply {
        put("createReservationGuestCriteria", JSONObject().apply {
            put("basketReference", basketReference)
            put("hotelId", hotelId)
            put("reasonForStay", reasonForStay)
            put("booker", JSONObject().apply {
                put("title", title)
                put("firstName", firstName)
                put("lastName", lastName)
                put("emailAddress", emailAddress)
                put("mobile", mobile)
                put("address",
                    JSONObject().apply {
                        put("addressLine1", addressLine1)
                        put("addressLine2", addressLine2)
                        put("addressLine3", addressLine3)
                        put("addressLine4", addressLine4)
                        put("postalCode", postalCode)
                        put("countryCode", countryCode)
                    })
            })
            put("stayingGuests", JSONArray().apply {
                stayingGuests.forEach { guest ->
                    put(JSONObject().apply {
                        put("sameAsBooker", guest.sameAsBooker)
                        put("stayingGuestDetails", JSONObject().apply {
                            put("title", guest.stayingGuestDetails.title)
                            put("firstName", guest.stayingGuestDetails.firstName)
                            put("lastName", guest.stayingGuestDetails.lastName)
                            put("additionalDetails", JSONObject().apply {
                                put("dob", guest.stayingGuestDetails.additionalDetails?.dob)
                                put("passportNumber", guest.stayingGuestDetails.additionalDetails?.passportNumber)
                                put("nationality", guest.stayingGuestDetails.additionalDetails?.nationality)
                            })
                        })
                        guest.accompanyingGuestDetails?.let { accompanyingGuestDetails ->
                            put("accompanyingGuestDetails", JSONObject().apply {
                                put("firstName", accompanyingGuestDetails.firstName)
                                put("lastName", accompanyingGuestDetails.lastName)
                                put("title", accompanyingGuestDetails.title)
                                put("additionalDetails", JSONObject().apply {
                                    put("dob", accompanyingGuestDetails.additionalDetails?.dob)
                                    put("passportNumber", accompanyingGuestDetails.additionalDetails?.passportNumber)
                                    put("nationality", accompanyingGuestDetails.additionalDetails?.nationality)
                                })
                            })
                        }
                    })
                }
            })
        })
    }
}

fun CreateReservationGuestRegCardRequestBody.constructVariables(jsonObject: JSONObject): JSONObject {
    return jsonObject.apply {
        put("createReservationGuestCriteria", JSONObject().apply {
            put("basketReference", basketReference)
            put("hotelId", hotelId)
            put("reasonForStay", reasonForStay)
            put("preCheckIn", preCheckIn)
            put("booker", JSONObject().apply {
                put("title", title)
                acceptFutureMailing?.let {
                    put("acceptFutureMailing", acceptFutureMailing)
                }
                put("emailAddress", emailAddress)
                put("firstName", firstName)
                put("lastName", lastName)
                put("mobile", mobile)
                put("language", language)
                put("address", JSONObject().apply {
                    addressType?.let {
                        put("addressType", addressType)
                    }
                    put("addressLine1", addressLine1)
                    put("addressLine2", addressLine2)
                    put("addressLine3", addressLine3)
                    put("addressLine4", addressLine4)
                    put("postalCode", postalCode)
                    countryCode?.let {
                        put("countryCode", countryCode)
                    }
                })
            })
            put("stayingGuests", JSONArray().apply {
                stayingGuests.forEach { guest ->
                    put(JSONObject().apply {
                        put("sameAsBooker", guest.sameAsBooker)
                        put("reservationId", guest.reservationId)
                        put("isAccompanyingGuest", guest.isAccompanyingGuest)
                        put("stayingGuestDetails", JSONObject().apply {
                            put("firstName", guest.firstName)
                            put("lastName", guest.lastName)
                            if (!guest.isAccompanyingGuest) {
                                put("title", guest.title)
                                put("profileId", guest.profileId)
                                put("address", JSONObject().apply {
                                    stayingGuests.firstOrNull { !it.isAccompanyingGuest }?.address?.let { address ->
                                        put("postalCode", address.postalCode)
                                        put("addressLine1", address.addressLine1)
                                        put("addressLine2", address.addressLine2)
                                        put("addressLine3", address.addressLine3)
                                        put("addressType", address.addressType)
                                        put("cityName", address.cityName)
                                        put("countryCode", address.countryCode)
                                    }
                                })
                            }
                            put("additionalDetails", JSONObject().apply {
                                put("dob", guest.additionalDetails.dob)
                                put("passportNumber", guest.additionalDetails.passportNumber)
                                put("nationality", guest.additionalDetails.nationality)
                            })
                        })
                    })
                }
            })
        })
    }
}