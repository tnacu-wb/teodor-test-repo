package com.whitbread.premierinn.hoteldetails.event

import com.whitbread.premierinn.common.UiEvent
import com.whitbread.premierinn.hoteldetails.BathroomSelectionInput

object AccessibilityRoomTypeInfoClickEvent : UiEvent

object SelectRateClickEvent : UiEvent

data class EditGuestClickEvent(val position: Int) : UiEvent

data class CheckAvailabilityClickEvent(val position: Int) : UiEvent

data class EditDatesClickEvent(val position: Int) : UiEvent

data class CoronavirusDismissClickEvent(val position: Int) : UiEvent

data class HotelsNearbyClickEvent(val position: Int) : UiEvent

data class ImportantInfoClickEvent(val position: Int) : UiEvent

object FindOutMoreClickEvent: UiEvent

data class MainGalleryItemClickEvent(val position: Int) : UiEvent

data class MakePhoneCallEvent(val phoneNumber: String) : UiEvent

data class MapViewClickEvent(val position: Int) : UiEvent

data class OpenWebLinkEvent(val webLink: String) : UiEvent

data class ReadMoreClickEvent(val position: Int) : UiEvent

data class SendEmailEvent(val emailAddress: String) : UiEvent

data class RateClickEvent(val rateType: String,
                          val rateClassification: String,
                          val pmsRoomType: String,
                          val roomClass: String,
                          val isAlternativeRoom: Boolean,
                          val formattedBaseRate: String?,
                          val promotionCode: String?,
                          val promotionTag: String?) : UiEvent

object RoomTypesClickEvent : UiEvent

class FacilitiesClickEvent : UiEvent

class ParkingClickEvent : UiEvent

class AccesibilityClickEvent : UiEvent

data class DiscountCodeClickEvent(val position: Int) : UiEvent

data class StartSummaryOrRoomSelectionEvent(val bathroomSelectionInput: BathroomSelectionInput,
                                            val selectOrBook: Boolean,
                                            val isAccessibleFlow: Boolean) : UiEvent
