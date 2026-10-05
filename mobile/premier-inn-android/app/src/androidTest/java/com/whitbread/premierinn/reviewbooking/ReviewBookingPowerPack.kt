package com.whitbread.premierinn.reviewbooking

import android.content.SharedPreferences
import com.whitbread.premierinn.amend.toParcelable
import com.whitbread.premierinn.api.request.booking.BookingAddress
import com.whitbread.premierinn.api.request.booking.Breakfast
import com.whitbread.premierinn.api.response.AcceptedCreditCard
import com.whitbread.premierinn.api.response.CardInfo
import com.whitbread.premierinn.api.response.availability.AvailabilityAddress
import com.whitbread.premierinn.api.response.availability.BookingRule
import com.whitbread.premierinn.api.response.availability.File
import com.whitbread.premierinn.api.response.availability.UpsellItem
import com.whitbread.premierinn.bookingdetails.UpsellItemSummary
import com.whitbread.premierinn.common.BookingFlowInput
import com.whitbread.premierinn.common.PaymentTimingChoice
import com.whitbread.premierinn.common.mapper.toBookingPrice
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManagerImpl.Constants.KEY_PASSWORD
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManagerImpl.Constants.KEY_USERNAME
import com.whitbread.premierinn.domain.common.PriceDomain
import com.whitbread.premierinn.domain.common.RoomType
import com.whitbread.premierinn.guestdetails.view.GuestDetailsFormDataInput
import com.whitbread.premierinn.hoteldetails.DailyRateInput
import com.whitbread.premierinn.hoteldetails.toPriceParcelable
import com.whitbread.premierinn.paymentdetails.PaymentDetailsInput
import com.whitbread.premierinn.common.RoomBooking
import com.whitbread.premierinn.hoteldetails.SelectedHotel
import com.whitbread.premierinn.hoteldetails.SelectedRate
import com.whitbread.premierinn.summary.SummaryInput
import io.appflate.restmock.RESTMockServer
import io.appflate.restmock.utils.RequestMatchers
import org.threeten.bp.LocalDate

class ReviewBookingPowerPack(val preferences: SharedPreferences) {

    companion object {
        private const val BUSINESS_CARD_NUMBER = "3089500100045005041"
        private const val STANDARD_CARD_NUMBER = "4444333322221111"
        private const val BUSINESS_STORED_CARD_NUMBER = "***************5041"
        private const val STANDARD_STORED_CARD_NUMBER = "************1111"
        private const val MASTERCARD_CARD_TYPE = "AC"
        private const val BUSINESS_CARD_TYPE = "AT"
    }

    private val jan17_2018_0000: LocalDate = LocalDate.of(2018, 1, 17)
    private val jan18_2018_0000: LocalDate = LocalDate.of(2018, 1, 18)
    private val jan19_2018_0000: LocalDate = LocalDate.of(2018, 1, 19)

    private val dailyRates3Nights = listOf(DailyRateInput(jan17_2018_0000,  PriceDomain.createWithGBPCurrency(160f).toPriceParcelable()),
        DailyRateInput(jan18_2018_0000, PriceDomain.createWithGBPCurrency(115f).toPriceParcelable()),
        DailyRateInput(jan19_2018_0000, PriceDomain.createWithGBPCurrency(81f).toPriceParcelable()))

    private val doubleRoom_singleAdult_3nights = RoomBooking(
            type = RoomType.DOUBLE.name,
            lettingCode = "DBS",
            adults = 1,
            children = 0,
            cot = false,
            dailyRates = dailyRates3Nights,
            roomNumber = 1,
            cityTax = PriceDomain.createWithGBPCurrency(0f).toParcelable(),
            infants = 0,
            baseRateAmount = 0f)

    private val premierInnBreakfast = UpsellItem.builder().code("11").legend("Premier Inn Breakfast")
            .price(PriceDomain.createWithGBPCurrency(0f).toBookingPrice())
            .description("Some description")
            .freeBreakfastOption(true)
            .freeBreakfastTrigger(true)
            .freeBreakfastCode("15")
            .foodUpsell(true)
            .availableForChildren(true)
            .files(listOf(File.create("/content/dam/global/restaurants/Global/Global Breakfast.pdf", "Breakfast menu")))
            .build()

    private val ruleCancellationNotAllowed = BookingRule.builder().policy(BookingRule.Policy.NOT_ALLOWED)
            .type("CANCELLATION").days(0).build()

    private val ruleAmendmentNotAllowed = BookingRule.builder().policy(BookingRule.Policy.NOT_ALLOWED)
            .type("AMENDMENT").days(0).build()

    private val ruleCancellationAllowed = BookingRule.builder().policy(BookingRule.Policy.STANDARD)
            .type("CANCELLATION").days(0).build()

    private val ruleAmendmentAllowed = BookingRule.builder().policy(BookingRule.Policy.STANDARD)
            .type("AMENDMENT").days(0).build()

    private val saverBookingRules = listOf(ruleCancellationNotAllowed, ruleAmendmentNotAllowed)
    private val flexBookingRules = listOf(ruleCancellationAllowed, ruleAmendmentAllowed)

    private val acceptedCardmasterCard = AcceptedCreditCard.builder().creditCardCode("AC").feeCurrency("").feeAmount("")
            .name("Mastercard Credit").listOrder(3).paymentOnly(false)
            .schemeLogo("/content/dam/global/booking/Mastercard.jpg").build()

    private val bookerDetails1 = GuestDetailsFormDataInput.create("Mr", "Miguel", "Santos",
            "email@email.com", "07488822228282")

    private val bookerDetails2 = GuestDetailsFormDataInput.create("Mr", "Mark", "O'Meara",
            "mark@email.com", "07490677777")

    private val bookingAddress = BookingAddress.create("UK", "UK", "32 Street", "Big Street", "London", "E146FY")

    private val cardInfoMastercard = CardInfo.builder()
            .cardFeeApplies(false)
            .cardType(MASTERCARD_CARD_TYPE)
            .cardLegend("Mastercard")
            .issueNumberRequired(false)
            .startDateRequired(false).build()

    fun createInput(storedCard: Boolean = false,
                    rateType: String,
                    cardType: CardType,
                    selectedUpsellType: UpsellItemSummary.UpsellItemType? = null,
                    payNowLaterChoice: PayNowLaterChoice = PayNowLaterChoice.PAY_NOW_LATER_NOT_CHOSEN,
                    loggedIn: Boolean = false,
                    bookingResponse: BookingOutcome = BookingOutcome.FAILURE,
                    cnpInfoEntered: Boolean = false,
                    amendmentAllowed: Boolean = false,
                    cancellationAllowed: Boolean = false,
                    multipleRooms: Boolean = false): ReviewBookingInput {

        preferences.edit().clear().apply()

        if (loggedIn) {
            preferences.edit().putString(KEY_USERNAME, "user").apply()
            preferences.edit().putString(KEY_PASSWORD, "pass").apply()
        }

        val selectedUpsellList = if (selectedUpsellType?.equals(MealType.PI_BREAKFAST) == true) listOf(premierInnBreakfast) else listOf()

        val cancellationRule = if (cancellationAllowed) ruleCancellationAllowed else ruleCancellationNotAllowed
        val amendmentRule = if (amendmentAllowed) ruleAmendmentAllowed else ruleAmendmentNotAllowed

        val chosenRate = SelectedRate.builder()
                .rateName(rateType)
                .description("rate description")
                .rateType(rateType)
                .code("RT315")
                .prepaymentRequired(rateType != "Flex")
                .guaranteeRequired(true)
                .cardFeeApplies(false)
                .bookingRules(listOf(cancellationRule, amendmentRule))
                .build()

        val hotelAddress = AvailabilityAddress.builder()
                .addressLine1("line1")
                .addressLine2("line2")
                .addressLine3("line3")
                .postcode("ec1n2td")
                .country("UK").build()

        val hotel = SelectedHotel.builder()
                .isHub(false)
                .code("hotelCode")
                .name("hotelName")
                .imageReference("imageRef").build()

        val summaryInput = SummaryInput.builder()
                .bookingId("bookingId")
                .hotel(hotel)
                .prepaymentAllowed(true)
                .rate(chosenRate)
                .upsellItems(selectedUpsellList)
                .totalNights(3)
                .isAlternativeRoom(false)
                .cityTaxForLeisure(false)
                .cityTaxForBusiness(false)
                .roomBookings(createRoomBookings(multipleRooms)).build()

        val selectedUpsell = if (selectedUpsellList.isEmpty()) null else selectedUpsellList[0]
        val bookingFlowInput = BookingFlowInput.create(summaryInput,
            Breakfast.createBreakfasts(premierInnBreakfast, summaryInput.roomBookings()!!), selectedUpsell, emptyList(), 100f, emptyList())

        val paymentDetailsInput = PaymentDetailsInput.builder()
                .isBookerStaying(true)
                .isBusinessTrip(false)
                .isTaxExempt(false)
                .marketingOptIn(true)
                .address(bookingAddress)
                .bookerDetails(bookerDetails1)
                .guestDetailsList(createGuestDetailsList(multipleRooms))
                .bookingFlowInput(bookingFlowInput).build()

        val cardNumber = if (cardType == CardType.BUSINESS) {
            if (storedCard) BUSINESS_STORED_CARD_NUMBER else BUSINESS_CARD_NUMBER
        } else {
            if (storedCard) STANDARD_STORED_CARD_NUMBER else STANDARD_CARD_NUMBER
        }

        val cardTypeCode = if (cardType == CardType.BUSINESS) BUSINESS_CARD_TYPE else MASTERCARD_CARD_TYPE

        var paymentTimingChoice: PaymentTimingChoice? = null
        if (payNowLaterChoice == PayNowLaterChoice.PAY_NOW) {
            paymentTimingChoice = PaymentTimingChoice.PAY_NOW
        } else if (payNowLaterChoice == PayNowLaterChoice.PAY_LATER) {
            paymentTimingChoice = PaymentTimingChoice.PAY_LATER
        }
        val reviewBookingInputBuilder = ReviewBookingInput.builder()
                .paymentDetailsInput(paymentDetailsInput)
                .cardInfo(cardInfoMastercard)
                .cardUrl("")
                .cardNumber(cardNumber)
                .nameOnCard("Miguel Santos")
                .expiryDate("0329")
                .cardHolderAddress(bookingAddress)
                .userSelectedPaymentChoice(paymentTimingChoice)
                .cardType(cardTypeCode)

        if (cnpInfoEntered) {
            reviewBookingInputBuilder
                    .cnpPurchaseOrderNumber("purchase_order_number")
                    .cnpCustomerReferenceNumber("customer_reference")
        }

        mockBookingResponse(bookingResponse)

        return reviewBookingInputBuilder.build()
    }

    private fun createRoomBookings(multipleRooms: Boolean): List<RoomBooking> {
        return if (multipleRooms) {
            listOf(doubleRoom_singleAdult_3nights, doubleRoom_singleAdult_3nights)
        } else {
            listOf(doubleRoom_singleAdult_3nights)
        }
    }

    private fun createGuestDetailsList(multipleRooms: Boolean): List<GuestDetailsFormDataInput> {
        return if (multipleRooms) {
            listOf(bookerDetails1, bookerDetails2)
        } else {
            listOf(bookerDetails1)
        }
    }

    private fun mockBookingResponse(bookingOutcome: BookingOutcome) {
        when (bookingOutcome) {
            BookingOutcome.SUCCESS -> RESTMockServer.whenPOST(RequestMatchers.pathContains("/booking/hotels/"))
                    .thenReturnFile(200, "apiTest/make-booking-noprepay-success.json")
            BookingOutcome.FAIL_THEN_SUCCEED -> {
                RESTMockServer.whenPOST(RequestMatchers.pathContains("/booking/hotels/"))
                        .thenReturnEmpty(500)
                        .thenReturnFile(200, "apiTest/make-booking-noprepay-success.json")
            }
            BookingOutcome.HOLD_EXPIRED -> {
                RESTMockServer.whenPOST(RequestMatchers.pathContains("/booking/hotels/"))
                        .thenReturnFile(400, "apiTest/hold-expired.json")
            }
            BookingOutcome.D3S -> {
                // TODO
            }
            BookingOutcome.FAILURE -> RESTMockServer.whenPOST(RequestMatchers.pathContains("/booking/hotels/"))
                    .thenReturnEmpty(500)
        }
    }

    enum class MealType {
        PI_BREAKFAST, MEAL_DEAL, NONE
    }

    enum class PayNowLaterChoice {
        PAY_NOW, PAY_LATER, PAY_NOW_LATER_NOT_CHOSEN
    }

    enum class CardType {
        MASTERCARD, BUSINESS
    }

    enum class BookingOutcome {
        SUCCESS, D3S, FAILURE, FAIL_THEN_SUCCEED, HOLD_EXPIRED
    }

}