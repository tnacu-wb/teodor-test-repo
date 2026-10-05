package com.whitbread.premierinn.bookingdetails

import com.whitbread.premierinn.common.forms.action.Action
import com.whitbread.premierinn.common.managebooking.ManageBookingInput
import org.threeten.bp.LocalDate

/**
 *
 */

class CheckInOnlineAction : Action

class ReadyToLeaveAction : Action

class AddToCalendarAction(val beginTime: LocalDate, val endTime: LocalDate, val title: String, val description: String, val location: String) : Action

class HotelDetailsAction(val hotelCode: String) : Action

class MapDirectionsAction(val input: String) : Action

class RoomKeyInstructionsAction : Action

class ParkingAction(val parkingDescription: String) : Action

class SendInvoiceAction : Action
