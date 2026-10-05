package com.whitbread.premierinn.mybookings

import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import com.contentsquare.android.Contentsquare
import com.jakewharton.rxbinding3.view.clicks
import com.jakewharton.rxrelay2.PublishRelay
import com.whitbread.premierinn.R
import com.whitbread.premierinn.common.forms.action.Action
import com.whitbread.premierinn.common.utils.bind
import com.whitbread.premierinn.common.view.BaseListViewHolder
import com.whitbread.premierinn.common.view.CallToActionButtonLayout
import com.whitbread.premierinn.common.view.InfoMessageBoxView
import com.whitbread.premierinn.common.view.ListItem
import com.whitbread.premierinn.data.common.EMPTY_STRING
import com.whitbread.premierinn.domain.common.QrKioskHotelsDomain
import io.reactivex.Observable
import org.threeten.bp.LocalDate

/**
 * Todo group this classes in separate files that make sense.
 */
const val BASKET_STATUS_PRE_CHECKED_IN = "PRE_CHECKED_IN"
const val BASKET_STATUS_PRE_CHECKED_OUT = "PRE_CHECKED_OUT"
const val BOOKING_STATUS_FUTURE = "FUTURE"
const val BOOKING_STATUS_CANCELLED = "CANCELLED"
const val BOOKING_STATUS_PAST = "PAST"

data class BookingUiModel(
    val id: String,
    val leadGuestSurname: String,
    val leadGuestFullName: String,
    val hotelCode: String,
    val hotelName: String,
    val dates: String,
    val arrivalDate: LocalDate,
    val departureDate: LocalDate,
    val isPrepaid: Boolean,
    val isBusinessBooking: Boolean,
    val bookingStatus: String,
    val isCheckInOnlineFlagEnabled: Boolean,
    val isCheckInOnlineAvailable: Boolean,
    val basketStatus: String?,
    val qrKioskHotels: List<QrKioskHotelsDomain>,
    val isCancelled: Boolean,
    val hotelCountry: String?,
    val isThirdPartyBooking: Boolean
) : ListItem {

    override fun id(): String = id
    override fun type(): Int = R.layout.item_my_bookings
}

class GDPRUiModel : ListItem {
    override fun id() = "GDPRUiModel"
    override fun type() = R.layout.item_gdpr

    override fun equals(other: Any?): Boolean {
        if (javaClass != other?.javaClass) return false
        val other: GDPRUiModel = other as GDPRUiModel
        if (type() != other.type()) return false
        return true
    }

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
}

data class HeaderUiModel(val header: String) : ListItem {
    override fun id() = "HeaderUiModel"
    override fun type() = R.layout.item_header_my_bookings_list
}

sealed class OnClickItemAction(val pos: Int) : Action {
    data class BookingItem(val position: Int, val bookingId: String = "", val isBusinessBooking: Boolean = false, val surname: String = "",
                           val arrivalDate: LocalDate = LocalDate.now()) : OnClickItemAction(position)
    data class PlanYourTrip(val position: Int, val hotelId: String = "",  val bookingId: String = "", val isBusinessBooking: Boolean = false,
                            val surname: String = "", val arrivalDate: LocalDate = LocalDate.now()) : OnClickItemAction(position)
    data class CheckIn(val position: Int, val bookingReference: String = "", val surname: String = "", val isBusinessBooking: Boolean = false,
                       val arrivalDate: LocalDate = LocalDate.now(), val hotelCode: String = "") : OnClickItemAction(position)
    data class RoomKeyInstructions(
        val position: Int,
        val hotelImage: String = EMPTY_STRING,
        val bookingId: String = EMPTY_STRING,
        val hotelId: String = EMPTY_STRING,
        val hotelCountry: String? = EMPTY_STRING
    ) : OnClickItemAction(position)

    data class QRCode(val position: Int, val bookingId: String = "") : OnClickItemAction(position)
}

class BookingUiModelViewHolder(val view: View, clicksRelay: PublishRelay<OnClickItemAction>) : BaseListViewHolder<ListItem>(view) {

    val leadGuestFullName by view.bind<TextView>(R.id.my_bookings_lead_guest_name)
    val hotelName by view.bind<TextView>(R.id.my_bookings_hotel_name)
    val dates by view.bind<TextView>(R.id.my_bookings_dates)
    val planYourTrip by view.bind<InfoMessageBoxView>(R.id.my_bookings_plan_your_trip)
    val bookingDetails by view.bind<InfoMessageBoxView>(R.id.my_bookings_details)
    val roomKeyInstructions by view.bind<InfoMessageBoxView>(R.id.room_key_instructions)
    val roomKeyInstructionsSeparator by view.bind<View>(R.id.room_key_instructions_separator)
    val qrCode by view.bind<InfoMessageBoxView>(R.id.qr_code)
    val qrCodeSeparator by view.bind<View>(R.id.qr_code_separator)
    val businessTrip by view.bind<TextView>(R.id.my_bookings_business_trip)
    private val bookingStatus by view.bind<TextView>(R.id.booking_status)
    private val checkInButton by view.bind<CallToActionButtonLayout>(R.id.booking_details_ciol_button)
    private val thirdPartyBookingInfoBannerLayout by view.bind<View>(R.id.third_party_booking_info_banner_layout)
    private val thirdPartyBookingInfoBanner by view.bind<InfoMessageBoxView>(R.id.third_party_booking_info_banner)

    init {
        checkInButton.clicks().map { OnClickItemAction.CheckIn(adapterPosition) }.subscribe(clicksRelay)
        planYourTrip.clicks().map { OnClickItemAction.PlanYourTrip(adapterPosition) }.subscribe(clicksRelay)
        bookingDetails.clicks().map { OnClickItemAction.BookingItem(adapterPosition) }.subscribe(clicksRelay)
        roomKeyInstructions.clicks().map { OnClickItemAction.RoomKeyInstructions(adapterPosition) }.subscribe(clicksRelay)
        qrCode.clicks().map { OnClickItemAction.QRCode(adapterPosition) }.subscribe(clicksRelay)
    }

    override fun bind(item: ListItem) {
        item as BookingUiModel
        val resources = view.resources

        leadGuestFullName.text = item.leadGuestFullName
        hotelName.text = item.hotelName

        businessTrip.isVisible = item.isBusinessBooking
        bookingStatus.isVisible = !item.isBusinessBooking

        if (item.isCheckInOnlineFlagEnabled) {
            checkInButton.isVisible = item.isCheckInOnlineAvailable
            with(item.bookingStatus) {
                // Business requirement, we need to not show booking status for business booking
                bookingStatus.isVisible = this.isNotEmpty() && !item.isBusinessBooking

                val isBaskedStatusPreCheckIn =
                    item.basketStatus != null && item.basketStatus == BASKET_STATUS_PRE_CHECKED_IN
                val shouldShowCheckInLabel =
                    this == BOOKING_STATUS_FUTURE && isBaskedStatusPreCheckIn && !item.isBusinessBooking

                val shouldShowRoomKey =
                    this == BOOKING_STATUS_FUTURE && isBaskedStatusPreCheckIn

                roomKeyInstructionsSeparator.isVisible = shouldShowRoomKey
                roomKeyInstructions.isVisible = shouldShowRoomKey

                if (shouldShowCheckInLabel) {
                    val (statusText, statusDrawable, statusColor) = setGreenLabel(resources, R.string.checked_in)
                    displayBookingStatusLabel(statusText, statusDrawable, statusColor)
                } else {
                    // Normal scenario when ciol is enabled but not checked in
                    if (!item.isBusinessBooking) {
                        val (statusText, statusDrawable, statusColor) =
                            setLabelsBasedOnBookingOrBasketStatus(item, resources, item.basketStatus)
                        displayBookingStatusLabel(statusText, statusDrawable, statusColor)
                    }
                }
            }
        } else {
            // This logic is when the ciol flag is disabled
            checkInButton.isVisible = false
            with(item.bookingStatus) {
                if (!item.isBusinessBooking) {
                    val (statusText, statusDrawable, statusColor) =
                        setLabelsBasedOnBookingOrBasketStatus(item, resources, item.basketStatus)
                    displayBookingStatusLabel(statusText, statusDrawable, statusColor)
                }
            }
            roomKeyInstructions.isVisible = false
        }

        if (item.qrKioskHotels.any { item.hotelCode == it.hotelCode }
            && (LocalDate.now() >= item.arrivalDate.minusDays(2))
            && !(item.isCancelled || BOOKING_STATUS_PAST == item.bookingStatus)) {
            qrCode.visibility = View.VISIBLE
            qrCodeSeparator.visibility = View.VISIBLE
        } else {
            qrCode.visibility = View.GONE
            qrCodeSeparator.visibility = View.GONE
        }

        dates.text = item.dates

        // Show/hide the third party booking info banner based on isThirdPartyBooking
        //TODO: Logic will be handled in the https://whitbreadis.atlassian.net/browse/CTECH-5272 ticket hence passing true for now
        if (item.isThirdPartyBooking) {
            thirdPartyBookingInfoBannerLayout.visibility = View.VISIBLE
            val infoTextRes = if (item.bookingStatus in listOf(BASKET_STATUS_PRE_CHECKED_IN, BOOKING_STATUS_CANCELLED, BOOKING_STATUS_PAST)) {
                R.string.third_party_booking_past_cancelled_checkedIn_label
            } else {
                R.string.third_party_booking_upcoming_label
            }

            thirdPartyBookingInfoBanner.setHtmlText(view.context.getString(infoTextRes))
            thirdPartyBookingInfoBanner.setIconResource(R.drawable.ic_info)
            thirdPartyBookingInfoBanner.setIconColorFilter(R.color.information_blue)
        } else {
            thirdPartyBookingInfoBannerLayout.visibility = View.GONE
        }

        Contentsquare.mask(leadGuestFullName)
    }

    private fun displayBookingStatusLabel(
        statusText: String,
        statusDrawable: Drawable?,
        statusColor: Int
    ) {
        if (statusText.isEmpty()) {
            bookingStatus.isVisible = false
        } else {
            bookingStatus.apply {
                isVisible = true
                text = statusText
                background = statusDrawable
                setTextColor(statusColor)
            }
        }
    }

    private fun String.setLabelsBasedOnBookingOrBasketStatus(
        item: BookingUiModel,
        resources: Resources,
        basketStatus: String?
    ) = when (this) {
        BOOKING_STATUS_FUTURE -> {
            if (item.isCancelled) { // since the booking status is future for cancelled booking as locally we havent updated this
                setCancelReservationLabel(resources)
            } else {
                setGreenLabel(resources, R.string.upcoming_reservation_tag)
            }
        }

        BOOKING_STATUS_PAST -> {
            setPastLabel(resources)

        }

        BOOKING_STATUS_CANCELLED -> {
            setCancelReservationLabel(resources)
        }

        else -> {
            // This is for imported booking. Imported booking dont have bookingstatus
            // We can only import cancelled and upcoming booking
            if (this.isEmpty()) {
                if (item.isCancelled) {
                    setCancelReservationLabel(resources)
                } else {
                    setLabelForImportBookingBasedOnBasketStatus(item, basketStatus, resources)
                }
            } else {
                setLabelForImportBookingBasedOnBasketStatus(item, basketStatus, resources)
            }
        }
    }

    private fun setLabelForImportBookingBasedOnBasketStatus(
        item: BookingUiModel,
        basketStatus: String?,
        resources: Resources
    ) = if (item.basketStatus != null) {
        bookingStatus.isVisible = true
        when (basketStatus) {
            "COMPLETED" ->
                if (item.departureDate.isBefore(LocalDate.now())) {
                    setPastLabel(resources)
                } else {
                    setGreenLabel(resources, R.string.upcoming_reservation_tag)
                }

            "CANCELLED" -> setCancelReservationLabel(resources)

            else -> Triple(
                EMPTY_STRING,
                null,
                0
            )
        }
    } else {
        Triple(
            EMPTY_STRING,
            null,
            0
        )
    }

    private fun setCancelReservationLabel(resources: Resources) = Triple(
        resources.getString(R.string.cancelled_reservation_tag),
        ContextCompat.getDrawable(
            view.context,
            R.drawable.shape_light_red_stroke
        ),
        resources.getColor(R.color.new_error_red)
    )

    private fun setGreenLabel(resources: Resources, resourceId: Int) = Triple(
        resources.getString(resourceId),
        ContextCompat.getDrawable(
            view.context,
            R.drawable.shape_light_green_stroke
        ),
        resources.getColor(R.color.success_green)
    )

    private fun setPastLabel(resources: Resources) = Triple(
        resources.getString(R.string.past_reservation_tag),
        ContextCompat.getDrawable(
            view.context,
            R.drawable.shape_light_grey_3_stroke
        ),
        resources.getColor(R.color.dark_grey)
    )
}

class HeaderUiModelViewHolder(val view: View) : BaseListViewHolder<ListItem>(view) {
    val headerText by view.bind<TextView>(R.id.my_bookings_header_text)

    override fun bind(item: ListItem) {
        item as HeaderUiModel
        headerText.text = item.header
    }
}

class GDPRUiModelViewHolder(val view: View) : BaseListViewHolder<ListItem>(view) {
    override fun bind(item: ListItem) {
    }
}

class BookingListAdapter(callback: DiffUtil.ItemCallback<ListItem>? = null, val clicksRelay: PublishRelay<OnClickItemAction>) : BaseListAdapter(callback) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseListViewHolder<ListItem> {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return when (viewType) {
            R.layout.item_my_bookings -> BookingUiModelViewHolder(view, clicksRelay)
            R.layout.item_gdpr -> GDPRUiModelViewHolder(view)
            R.layout.item_header_my_bookings_list -> HeaderUiModelViewHolder(view)
            else -> throw IllegalArgumentException("Not supported type $viewType")
        }
    }

    fun listItemClicks(): Observable<OnClickItemAction> {
        return clicksRelay.map {
            val item = getItem(it.pos) as BookingUiModel
            when (it) {
                is OnClickItemAction.CheckIn -> it.copy(
                    bookingReference = item.id,
                    surname = item.leadGuestSurname,
                    arrivalDate = item.arrivalDate,
                    hotelCode = item.hotelCode,
                    isBusinessBooking = item.isBusinessBooking
                )

                is OnClickItemAction.BookingItem -> it.copy(
                    bookingId = item.id,
                    surname = item.leadGuestSurname,
                    arrivalDate = item.arrivalDate,
                    isBusinessBooking = item.isBusinessBooking
                )

                is OnClickItemAction.PlanYourTrip -> it.copy(
                    hotelId = item.hotelCode,
                    bookingId = item.id,
                    surname = item.leadGuestSurname,
                    arrivalDate = item.arrivalDate,
                    isBusinessBooking = item.isBusinessBooking
                )

                is OnClickItemAction.RoomKeyInstructions -> it.copy(
                    hotelImage = item.hotelName,
                    bookingId = item.id,
                    hotelId = item.hotelCode,
                    hotelCountry = item.hotelCountry
                )
                is OnClickItemAction.QRCode -> it.copy(bookingId = item.id)
            }
        }
    }
}
