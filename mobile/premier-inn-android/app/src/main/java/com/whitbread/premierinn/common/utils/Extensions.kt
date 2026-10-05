@file:JvmName("AppExtensions")

package com.whitbread.premierinn.common.utils

import android.app.Activity
import android.content.Context
import android.util.TypedValue
import android.view.View
import androidx.annotation.IdRes
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.whitbread.premierinn.R
import com.whitbread.premierinn.additionalinformation.entity.AnswerType
import com.whitbread.premierinn.additionalinformation.entity.EmployeeQuestionsModel
import com.whitbread.premierinn.api.request.booking.BookingAddress
import com.whitbread.premierinn.api.response.availability.HotelInfo
import com.whitbread.premierinn.api.response.availability.UpsellItem
import com.whitbread.premierinn.api.response.booking.BookingPrice
import com.whitbread.premierinn.api.response.customer.ContactDetail
import com.whitbread.premierinn.businessbooker.data.common.persistence.BusinessPersistenceManager
import com.whitbread.premierinn.businessbooker.domain.company.ManagementInformationQuestion
import com.whitbread.premierinn.common.AsyncResult
import com.whitbread.premierinn.common.BookingFlowInput
import com.whitbread.premierinn.common.CheckInCheckOutStringProvider
import com.whitbread.premierinn.common.PaymentProvider
import com.whitbread.premierinn.common.StringResourceProvider
import com.whitbread.premierinn.common.format.DateFormat
import com.whitbread.premierinn.common.format.toLocalDate
import com.whitbread.premierinn.common.mapper.toParcelableExtrasItemDomain
import com.whitbread.premierinn.common.service.LogService
import com.whitbread.premierinn.data.common.ADDRESS_TYPE_BUSINESS
import com.whitbread.premierinn.data.common.BRAND_PI
import com.whitbread.premierinn.data.common.BRAND_PI_GERMANY
import com.whitbread.premierinn.data.common.EMPTY_STRING
import com.whitbread.premierinn.data.common.persistence.SimplePersistenceManager
import com.whitbread.premierinn.data.common.toAllCheckInCheckoutTimesInfoDomain
import com.whitbread.premierinn.data.common.toPromoContentDomain
import com.whitbread.premierinn.data.remote.AllCheckInTimesInfo
import com.whitbread.premierinn.data.remote.ApiThrowable
import com.whitbread.premierinn.data.remote.PromoContent
import com.whitbread.premierinn.domain.ciol.usecase.GERMANY_ISO_CODE
import com.whitbread.premierinn.domain.common.ANSWER_TYPE_DROPDOWN
import com.whitbread.premierinn.domain.common.Address
import com.whitbread.premierinn.domain.common.AllCheckInCheckoutTimesInfoDomain
import com.whitbread.premierinn.domain.common.BUSINESS
import com.whitbread.premierinn.domain.common.EMPTY_STRING_DOMAIN
import com.whitbread.premierinn.domain.common.EXTRAS_LIST
import com.whitbread.premierinn.domain.common.GBP
import com.whitbread.premierinn.domain.common.PromoContentDomain
import com.whitbread.premierinn.domain.common.RoomCriteria
import com.whitbread.premierinn.domain.common.hoteldetails.entity.AncillaryCloseOutItem
import com.whitbread.premierinn.domain.common.hoteldetails.entity.ContactDetailsDomain
import com.whitbread.premierinn.domain.countries.entity.CountryDomain
import com.whitbread.premierinn.domain.customer.entity.BookingPreferences
import com.whitbread.premierinn.domain.customer.entity.Contact
import com.whitbread.premierinn.domain.customer.entity.Customer
import com.whitbread.premierinn.domain.customer.entity.FullName
import com.whitbread.premierinn.domain.customer.entity.PaymentCard
import com.whitbread.premierinn.domain.graphql.guestDetails.entity.PaymentMethodsWithDonationAndBookingConfirmationGQLDomain
import com.whitbread.premierinn.domain.graphql.hdp.entity.DataPackagesDomain
import com.whitbread.premierinn.domain.graphql.hdp.entity.MealDomain
import com.whitbread.premierinn.domain.graphql.hdp.entity.RateClassificationsDomain
import com.whitbread.premierinn.domain.graphql.requestBodyModels.Booker
import com.whitbread.premierinn.domain.graphql.requestBodyModels.BookerAddress
import com.whitbread.premierinn.domain.graphql.requestBodyModels.CategoryLabelsRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.CreateReservationGuestRequestBody
import com.whitbread.premierinn.domain.graphql.requestBodyModels.StayingGuestDetails
import com.whitbread.premierinn.domain.graphql.requestBodyModels.StayingGuests
import com.whitbread.premierinn.domain.hotel.entity.Hotel
import com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository
import com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository.Key
import com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository.Key.CHARGEABLE_PHONE_DESC
import com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository.Key.CUSTOMER_SERVICE_NUMBER
import com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository.Key.NON_CHARGEABLE_PHONE_DESC
import com.whitbread.premierinn.domain.resource.usecase.GetStringResource
import com.whitbread.premierinn.guestdetails.view.GuestDetailsFormDataInput
import com.whitbread.premierinn.hoteldetails.uimodel.CallUsUiModel
import com.whitbread.premierinn.paymentdetails.PaymentDetailsInput
import com.whitbread.premierinn.reviewbooking.ReviewBookingInput
import com.whitbread.premierinn.summary.SummaryInput
import com.whitbread.premierinn.summary.models.ParcelableExtrasItem
import com.whitbread.premierinn.summary.models.ParcelableMenuAndAllergyInfo
import com.whitbread.premierinn.summary.toListOfManagementInformationQuestion
import com.whitbread.premierinn.utils.openUrlWithFallback
import io.reactivex.disposables.CompositeDisposable
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import java.util.UUID
import java.util.concurrent.CompletableFuture


inline fun AsyncResult.Error.logUnExpectedErrorAndRun(service: LogService, block: () -> Unit) {
    if (error !is ApiThrowable.Network) {
        service.logException(error)
        block()
    }
}

fun <T : View> Activity.bind(@IdRes idRes: Int): Lazy<T> {
    return unsafeLazy { findViewById<T>(idRes) }
}

fun <T : View> View.bind(@IdRes idRes: Int): Lazy<T> {
    return unsafeLazy { findViewById<T>(idRes) }
}

fun Context.dpToPx(dp: Float): Int {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, this.resources.displayMetrics)
        .toInt()
}

private fun <T> unsafeLazy(initializer: () -> T) = lazy(LazyThreadSafetyMode.NONE, initializer)


fun View.showFromTop() = this.animate().translationY(0f).start()

fun View.hideOnTop() = this.animate().translationY(-this.height.toFloat()).start()

fun SnapHelper.getSnapPosition(recyclerView: RecyclerView): Int {
    val layoutManager = recyclerView.layoutManager ?: return RecyclerView.NO_POSITION
    val snapView = findSnapView(layoutManager) ?: return RecyclerView.NO_POSITION
    return layoutManager.getPosition(snapView)
}

fun <T : Any> Activity.argument(key: String) = lazy {
    intent.extras?.get(key) as? T ?: error("Intent Argument $key is missing")
}

fun mapToHotelContactDetailsDomain(hotelInfo: HotelInfo): Hotel.ContactDetails? {
    return hotelInfo.contactDetails()
        ?.takeIf { !it.phone().isNullOrEmpty() && !it.hotelNationalPhone().isNullOrEmpty() }
        ?.let {
            Hotel.ContactDetails(
                chargeablePhone = it.phone()!!,
                freePhone = it.hotelNationalPhone()!!
            )
        }
}

fun mapToHotelContactDetailsDomainOpera(contactDetails: ContactDetailsDomain?): Hotel.ContactDetails? {
    contactDetails?.let {
        return Hotel.ContactDetails(
            chargeablePhone = it.phone,
            freePhone = it.nationalPhoneNumber
        )
    } ?: return null
}

fun buildCallUiModel(
    order: Int = 0,
    chargeable: Boolean,
    contacts: Hotel.ContactDetails?,
    stringResource: GetStringResource,
    stringProvider: StringResourceProvider
): CallUsUiModel {

    val callInfo = contacts.numberToCall(chargeable, stringResource(CUSTOMER_SERVICE_NUMBER))

    fun hotelOrCustomerServiceLabel(): String {
        return when (callInfo.callDestination) {
            CallDestination.HOTEL -> stringProvider.getString(R.string.call_hotel)
            CallDestination.CALL_CENTRE -> stringProvider.getString(R.string.call_us)
        }
    }

    return CallUsUiModel.builder()
        .label(hotelOrCustomerServiceLabel())
        .telNumber(callInfo.number)
        .telCostInfo(
            if (callInfo.isChargeable) stringResource(CHARGEABLE_PHONE_DESC)
                .replace("\\n", "\n\n")
            else stringResource(NON_CHARGEABLE_PHONE_DESC)
        )
        .order(order).build()
}

fun getCheckInCheckoutTimes(hotelBrand: String,
                            allCheckInCheckoutTimesInfo: AllCheckInCheckoutTimesInfoDomain?,
                            checkInCheckOutStringProvider: CheckInCheckOutStringProvider,
                            isBreakdown: Boolean = false): androidx.core.util.Pair<String, String> {
    val checkInInfo: String
    val checkOutInfo: String
    when (hotelBrand) {
        BRAND_PI -> {
            checkInInfo = if (isBreakdown) {
                allCheckInCheckoutTimesInfo?.ukCheckInTimes?.summaryOrPaymentBreakdownCheckInInfo
                        ?: checkInCheckOutStringProvider.getUkSummaryCheckInInfo()
            } else {
                allCheckInCheckoutTimesInfo?.ukCheckInTimes?.bookingDetailsCheckInInfo
                        ?: checkInCheckOutStringProvider.getUkBookingDetailsCheckInInfo()
            }
            checkOutInfo = if (isBreakdown) {
                allCheckInCheckoutTimesInfo?.ukCheckInTimes?.summaryOrPaymentBreakdownCheckOutInfo
                        ?: checkInCheckOutStringProvider.getUkSummaryCheckOutInfo()
            } else {
                allCheckInCheckoutTimesInfo?.ukCheckInTimes?.bookingDetailsCheckOutInfo
                        ?: checkInCheckOutStringProvider.getUkBookingDetailsCheckOutInfo()
            }
        }

        BRAND_PI_GERMANY -> {
            checkInInfo = if (isBreakdown) {
                allCheckInCheckoutTimesInfo?.germanyCheckInTimes?.summaryOrPaymentBreakdownCheckInInfo
                        ?: checkInCheckOutStringProvider.getGermanySummaryCheckInInfo()
            } else {
                allCheckInCheckoutTimesInfo?.germanyCheckInTimes?.bookingDetailsCheckInInfo
                        ?: checkInCheckOutStringProvider.getGermanyBookingDetailsCheckInInfo()
            }
            checkOutInfo = if (isBreakdown) {
                allCheckInCheckoutTimesInfo?.germanyCheckInTimes?.summaryOrPaymentBreakdownCheckOutInfo
                        ?: checkInCheckOutStringProvider.getGermanySummaryCheckOutInfo()
            } else {
                allCheckInCheckoutTimesInfo?.germanyCheckInTimes?.bookingDetailsCheckOutInfo
                        ?: checkInCheckOutStringProvider.getGermanyBookingDetailsCheckOutInfo()
            }
        }

        else -> {
            checkInInfo = if (isBreakdown) {
                allCheckInCheckoutTimesInfo?.ukCheckInTimes?.summaryOrPaymentBreakdownCheckInInfo
                        ?: checkInCheckOutStringProvider.getUkSummaryCheckInInfo()
            } else {
                allCheckInCheckoutTimesInfo?.ukCheckInTimes?.bookingDetailsCheckInInfo
                        ?: checkInCheckOutStringProvider.getUkBookingDetailsCheckInInfo()
            }
            checkOutInfo = if (isBreakdown) {
                allCheckInCheckoutTimesInfo?.ukCheckInTimes?.summaryOrPaymentBreakdownCheckOutInfo
                        ?: checkInCheckOutStringProvider.getUkSummaryCheckOutInfo()
            } else {
                allCheckInCheckoutTimesInfo?.ukCheckInTimes?.bookingDetailsCheckOutInfo
                        ?: checkInCheckOutStringProvider.getUkBookingDetailsCheckOutInfo()
            }
        }
    }
    return androidx.core.util.Pair(checkInInfo, checkOutInfo)
}

fun retrieveCheckInCheckoutInfoFromFirebase(compositeDisposable: CompositeDisposable,
                                            logService: LogService,
                                            contentRepository: ContentManagedResourceRepository): AllCheckInCheckoutTimesInfoDomain {
    val future = CompletableFuture<AllCheckInCheckoutTimesInfoDomain>()
    compositeDisposable.add(
            contentRepository.getStringSingle(ContentManagedResourceRepository.Key.ALL_CHECK_IN_CHECK_OUT_TIMES.value)
                    .map { jsonString ->
                        val type = object : TypeToken<AllCheckInTimesInfo>() {}.type
                        Gson().fromJson<AllCheckInTimesInfo>(jsonString, type)
                    }
                    .toObservable()
                    .firstOrError()
                    .subscribe({
                        future.complete(it.toAllCheckInCheckoutTimesInfoDomain())
                    }, {
                        logService.logException(it, "Retrieval of check in/ check out from firebase failed")
                    }))

    return future.get()
}

fun Hotel.ContactDetails?.numberToCall(wantChargeable: Boolean, fallbackNumber: String): CallInfo {
    return this?.let {
        if (wantChargeable) {
            CallInfo(it.chargeablePhone, true, CallDestination.HOTEL)
        } else {
            CallInfo(it.freePhone, false, CallDestination.HOTEL)
        }
    } ?: CallInfo(fallbackNumber, false, CallDestination.CALL_CENTRE)
}

data class CallInfo(
    val number: String,
    val isChargeable: Boolean,
    val callDestination: CallDestination
)

enum class CallDestination {
    HOTEL, CALL_CENTRE
}

fun Customer.toCustomerResponse(): Customer {
    fun toBookingPreference(): BookingPreferences {
        return BookingPreferences(
            mealPreference = this.bookingPreferences.mealPreference,
            roomCriteriaPreference = RoomCriteria(
                numberOfAdults = this.bookingPreferences.roomCriteriaPreference.numberOfAdults,
                numberOfChildren = this.bookingPreferences.roomCriteriaPreference.numberOfChildren,
                roomType = this.bookingPreferences.roomCriteriaPreference.roomType,
                roomNumber = 1,
                hotelBrand = this.bookingPreferences.roomCriteriaPreference.hotelBrand,
                includeCot = this.bookingPreferences.roomCriteriaPreference.includeCot
            )
        )
    }

    fun toPaymentCard(): PaymentCard? {
        return if (this.paymentCard != null) {
            val paymentCard = this.paymentCard!!
            PaymentCard(
                number = paymentCard.number,
                cardType = paymentCard.cardType,
                cardID = paymentCard.cardID,
                holdersFullName = paymentCard.holdersFullName,
                expiryDate = paymentCard.expiryDate
            )
        } else null
    }


    return Customer(
        customerAccountID =this.customerAccountID,
        sessionId = this.sessionId,
        guestHistoryNumber = this.guestHistoryNumber,
        fullName = this.fullName,
        contact = Contact(this.contact.email, this.contact.mobile, this.contact.telephone),
        address = Address(
            this.address.line1,
            this.address.line2,
            this.address.line3,
            this.address.line4,
            this.address.line5,
            this.address.postCode,
            this.address.companyName,
            this.address.countryCode
        ),
        carRegistration = this.carRegistration,
        companyId = this.companyId,
        nationality = this.nationality,
        passport = if (this.passport != null) com.whitbread.premierinn.domain.common.Passport()
            .copy(this.passport!!.number, this.passport!!.placeOfIssue) else null,
        bookingPreferences = toBookingPreference(),
        paymentCard = toPaymentCard(),
        business = this.business,
        businessUse = this.businessUse,
        company = this.company,
        guestHistoryCreation = this.guestHistoryCreation,
        totalStays = this.totalStays,
        operaCompanyID = this.operaCompanyID
    )
}

fun ContactDetail.toCustomer(): Customer {
    return Customer(
        fullName = FullName(
            title = title(),
            firstName = firstName(),
            lastName = lastName()
        ),
        contact = Contact(
            email = email(),
            mobile = mobile(),
            telephone = telephone()
        ),
        address = Address(
            line1 = address().line1(),
            line2 = address().line2(),
            line3 = address().line3(),
            line4 = address().line4(),
            line5 = address().line5(),
            postCode = address().postCode(),
            companyName = address().companyName(),
            countryCode = address().countryCode()
        ),
        nationality = nationality(),
        passport = if (passport() != null) com.whitbread.premierinn.domain.common.Passport(
            number = passport()!!.number()!!,
            placeOfIssue = passport()!!.countryOfIssue()
        ) else null,
        bookingPreferences = BookingPreferences.EMPTY,
        carRegistration = carRegistration()
    )
}

fun <T> Iterable<T>.sumByFloat(selector: (T) -> Float): Float {
    var sum = 0F
    for (element in this) {
        sum += selector(element)
    }
    return sum
}

fun Pair<List<UpsellItem?>?,List<ParcelableExtrasItem?>?>.toAnalyticsDataString(): String {
    val upsellLegendAll = this.first?.mapNotNull { it?.legend() }?.distinct()?.joinToString(", ") ?: ""
    val extrasLegendAll = this.second?.mapNotNull { it?.name }?.distinct()?.joinToString(", ") ?: ""

    return when {
        upsellLegendAll.isEmpty() -> extrasLegendAll
        extrasLegendAll.isEmpty() -> upsellLegendAll
        else -> "$upsellLegendAll, $extrasLegendAll"
    }
}

fun List<ParcelableExtrasItem>.calculateTotalPrices(num: Int): Map<String, Double> {
    return this.filter { it.id in EXTRAS_LIST && it.price != null }
        .groupBy { it.id }
        .mapValues { (_, items) -> items.sumOf { it.price!! * num } }
}

fun String?.getDescriptionLabel(): String {
    return if (this == Hotel.Brand.PID.name || this == CountryDomain.GERMANY_ISO_CODE) {
        CategoryLabelsRequestBody.LABEL_DE_DESCRIPTION
    } else {
        CategoryLabelsRequestBody.LABEL_DESCRIPTION
    }
}

fun String.isGerman(): Boolean = this == GERMANY_ISO_CODE

fun retrieveAppIncentivePromoContentFirebase(getStringResource: GetStringResource): PromoContentDomain {
    return Gson().fromJson(
        getStringResource.invoke(Key.APP_PROMO_CONTENT),
        PromoContent::class.java
    ).toPromoContentDomain()
}

fun retrieveFreeBreakfastContentFirebase(getStringResource: GetStringResource): PromoContentDomain {
    return Gson().fromJson(
        getStringResource.invoke(Key.FREE_BREAKFAST_PROMO_CONTENT),
        PromoContent::class.java
    ).toPromoContentDomain()
}

fun openIncentiveTermsAndConditions(activity: Activity, url: String, promoCode: String) {
    val originalUri = url.toUri()
    val newUri = originalUri.buildUpon().appendQueryParameter("CID", "ATW_$promoCode").build()
    openUrlWithFallback(activity, newUri.toString())
}

fun BusinessPersistenceManager?.getUpsellItemsAllowed(): List<String>? {
    return this?.getCompany()
        ?.requestedCompany
        ?.bookingAllowances
        ?.upsellItemsAllowed
}

fun SummaryInput.updateSummaryInput(dataPackagesDomain: DataPackagesDomain,
                                    ancillariesCloseOut: List<AncillaryCloseOutItem>,
                                    listOfUpsellAllowedForBB: List<String>?,
                                    basketReference: String): SummaryInput {
    val pairOfUpsellAndUpsellImages = dataPackagesDomain.toListOfUpsellItem(this, ancillariesCloseOut, listOfUpsellAllowedForBB)

    return this.toBuilder()
        .upsellItems(pairOfUpsellAndUpsellImages.first)
        .extrasItems(dataPackagesDomain.filterExtras(listOfUpsellAllowedForBB))
        .upsellsImages(pairOfUpsellAndUpsellImages.second)
        .cityTaxForLeisure(dataPackagesDomain.hotelHasCityTaxForLeisure)
        .cityTaxForBusiness(dataPackagesDomain.hotelHasCityTaxForBusiness)
        .basketReference(basketReference)
        .menus(dataPackagesDomain.getMenus())
        .allergyInfo(dataPackagesDomain.getAllergyInfo())
        .build()

}

fun DataPackagesDomain.toListOfUpsellItem(summaryInput: SummaryInput, ancillariesCloseOut: List<AncillaryCloseOutItem>,
                                          listOfUpsellAllowedForBB: List<String>?): Pair<List<UpsellItem>, List<String>> {

    val listOfUpsell = mutableListOf<UpsellItem>()
    val listOfUpsellImages = mutableListOf<String>()

    var filteredMeals = filterMealsBasedOnAncillariesCloseout(ancillariesCloseOut, this.packages.meals.toMutableList(),
        summaryInput.arrivalDateGQ().toLocalDate(),
        summaryInput.departureDateGQ().toLocalDate())


    if (summaryInput.isBusinessUser && listOfUpsellAllowedForBB != null) {
        filteredMeals = filteredMeals.filter {
                meal -> meal.bartId != null && listOfUpsellAllowedForBB.contains(meal.bartId) }.toMutableList()
    }
    filteredMeals.forEach { mealDomain ->
        mealDomain.imageSrc?.let {
            listOfUpsellImages.add(mealDomain.imageSrc!!)
        }
        listOfUpsell.add(UpsellItem.builder()
            .code(mealDomain.id)
            .description(mealDomain.description)
            .shortDescription(mealDomain.shortDescription)
            .availableForChildren(mealDomain.freeBreakfastOption ?: false)
            .freeBreakfastCode(mealDomain.freeBreakfastCode)
            .freeBreakfastOption(mealDomain.freeBreakfastOption ?: false)
            .freeBreakfastTrigger(mealDomain.freeBreakfastOption ?: false)
            .price(BookingPrice(mealDomain.price.toString().toFloat(), mealDomain.currency ?: GBP))
            .files(emptyList())
            .legend(mealDomain.name)
            .foodUpsell(true)
            .build())
    }
    return Pair(listOfUpsell, listOfUpsellImages)
}

fun DataPackagesDomain.filterExtras(listOfUpsellAllowedForBB: List<String>?):  List<ParcelableExtrasItem> {
    return this.packages.extrasItems?.toParcelableExtrasItemDomain(listOfUpsellAllowedForBB) ?: emptyList()
}

fun filterMealsBasedOnAncillariesCloseout(
    ancillaryCloseOutItems: List<AncillaryCloseOutItem>?, meals: MutableList<MealDomain>,
    arrivalDate: LocalDate, departureDate: LocalDate): List<MealDomain> {
    if (!ancillaryCloseOutItems.isNullOrEmpty()) {
        val filteredUpsellCodes = filterAncillaryCloseOutItems(ancillaryCloseOutItems, arrivalDate, departureDate)

        return meals.filter { meal -> meal.id !in filteredUpsellCodes }
    }
    return meals
}

fun isAncillariesCloseoutApplicable(ancillaryCloseOutItems: List<AncillaryCloseOutItem>, meals: List<MealDomain>,
                                    arrivalDate: LocalDate, departureDate: LocalDate): Boolean {
    val filteredUpsellCodes = filterAncillaryCloseOutItems(ancillaryCloseOutItems, arrivalDate, departureDate)

    return meals.all { meal -> meal.id in filteredUpsellCodes }
}

fun List<String>.isRestaurantClosed(meals: List<MealDomain>) : Boolean {
    return meals.all { meal -> meal.id in this }
}

fun filterAncillaryCloseOutItems(ancillaryCloseOutItems: List<AncillaryCloseOutItem>, arrivalDate: LocalDate, departureDate: LocalDate): List<String> {
    val df = DateTimeFormatter.ofPattern(DateFormat.SLASHED_DAY_MONTH_YEAR)
    return ancillaryCloseOutItems.filter { closeOut ->
        closeOut.startDate.isNotEmpty() && closeOut.endDate.isNotEmpty() &&
                !arrivalDate.isAfter(LocalDate.parse(closeOut.endDate, df)) &&
                !departureDate.isBefore(LocalDate.parse(closeOut.startDate, df))
    }.flatMap { it.upsellCodes.split(",") }
}

fun DataPackagesDomain.getAllergyInfo(): List<ParcelableMenuAndAllergyInfo> =
    this.packages.meals.map {
        ParcelableMenuAndAllergyInfo(
            it.name,
            it.allergyInfoSrc ?: EMPTY_STRING_DOMAIN
        )
    }

fun DataPackagesDomain.getMenus(): List<ParcelableMenuAndAllergyInfo> =
    this.restaurant.menus.map { ParcelableMenuAndAllergyInfo(it.name, it.menuSrc) }


fun SummaryInput.constructBookingFlowInput(basketReference: String): BookingFlowInput {
    return BookingFlowInput.create(
        this.toBuilder().basketReference(basketReference).build(),
        emptyList(), null, emptyList(),
        0f, null)
}

fun SummaryInput.constructReviewBookingInputWhenSkippingUpsellForBBUser(
                                        simplePersistenceManager: SimplePersistenceManager,
                                        basketReference: String,
                                        language: String): ReviewBookingInput {

    val address = simplePersistenceManager.getCustomer().address
    val customer = simplePersistenceManager.getCustomer()
    val bookerDetails = GuestDetailsFormDataInput.create(
        customer.fullName.title, customer.fullName.firstName, customer.fullName.lastName,
        customer.contact.email, customer.contact.mobile, language
    )
    val guestDetailsList = mutableListOf<GuestDetailsFormDataInput>()
    guestDetailsList.add(bookerDetails)

    val paymentDetailsInput = PaymentDetailsInput.builder()
        .bookingFlowInput(this.constructBookingFlowInput(basketReference))
        .address(
            BookingAddress.create(
                address.countryCode.takeUnless {address.countryCode.isNullOrEmpty()} ?: EMPTY_STRING,
                EMPTY_STRING, address.line1, address.line2,
                address.line3.takeUnless {address.line3.isNullOrEmpty()} ?: EMPTY_STRING,
                address.postCode.takeUnless {address.postCode.isNullOrEmpty()} ?: EMPTY_STRING
            )
        )
        .bookerDetails(bookerDetails)
        .guestDetailsList(guestDetailsList)
        .isBookerStaying(true)
        .isTaxExempt(false)
        .marketingOptIn(false)
        .isBusinessTrip(true)
        .accountPassword(null)
        .shouldCreateAccount(false)
        .paymentProvider(PaymentProvider.from(simplePersistenceManager.getPaymentProvider()))
        .build()

    val reviewBookingInput = ReviewBookingInput.builder()
        .paymentDetailsInput(paymentDetailsInput)
        .cardHolderAddress(
            BookingAddress.create(
                address.countryCode.takeUnless {address.countryCode.isNullOrEmpty()} ?: EMPTY_STRING,
                EMPTY_STRING, address.line1, address.line2,
                address.line3.takeUnless {address.line3.isNullOrEmpty()} ?: EMPTY_STRING,
                address.postCode.takeUnless {address.postCode.isNullOrEmpty()} ?: EMPTY_STRING
            )
        )
        .marketingOptIn(false)
        .guestHistoryNumber(customer.guestHistoryNumber)
        .uuidBasketReference(basketReference)
        .isBusinessUser(true)
        .isWifiAvailable(false)
        .build()

    return reviewBookingInput
}

fun PaymentMethodsWithDonationAndBookingConfirmationGQLDomain.findPaypalClientTokenIfPresent(): String? {
    return this.paymentMethods?.find { it.clientToken != null }?.clientToken
}

fun BusinessPersistenceManager.getAdditionalInfoQuestions(): List<EmployeeQuestionsModel>? {
    var listOfManagementQuestion = emptyList<ManagementInformationQuestion>()

    this.getCompany()?.let { company ->
        company.requestedCompany.companyManagementDetails?.let {
            listOfManagementQuestion = it.toListOfManagementInformationQuestion()
        }
    }

    return listOfManagementQuestion.toListOfEmployeeQuestionsModel()
}

fun getListOfManageQuestions(businessPersistenceManager: BusinessPersistenceManager): List<ManagementInformationQuestion> {
    var listOfManagementQuestion = listOf<ManagementInformationQuestion>()
    businessPersistenceManager.getCompany()?.let { company ->
        company.requestedCompany.companyManagementDetails?.let {
            listOfManagementQuestion = it.toListOfManagementInformationQuestion()
        }
    }
    return listOfManagementQuestion
}

fun ManagementInformationQuestion.toEmployeeQuestionsModel(): EmployeeQuestionsModel {
    val isDropDown = this.managementInformationAnswer?.answerType.equals(ANSWER_TYPE_DROPDOWN)
    val listOfPreSetAnswers = if (isDropDown) { this.managementInformationAnswer?.answers ?: emptyList() } else emptyList()
    return EmployeeQuestionsModel(
        id= UUID.randomUUID().toString(),
        questionHeader = label ?: EMPTY_STRING_DOMAIN,
        isMandatory = mandatory ?: false,
        answerType = if (isDropDown) AnswerType.DROPDOWN else AnswerType.TEXT_FIELD,
        listOfPreSetAnswers = listOfPreSetAnswers,
        questionType = this.questionType
    )
}

fun List<ManagementInformationQuestion>.toListOfEmployeeQuestionsModel(): List<EmployeeQuestionsModel>? {
    return this.takeIf { it.isNotEmpty() }?.map { it.toEmployeeQuestionsModel() }
}

fun buildCreateReservationGuestRequestBody(
    bookerDetails: Booker,
    stayingGuests: List<StayingGuests>,
    basketReference: String,
    hotelCode: String
): CreateReservationGuestRequestBody {
    return CreateReservationGuestRequestBody(
        basketReference = basketReference,
        hotelId = hotelCode,
        reasonForStay = BUSINESS,
        booker = bookerDetails,
        stayingGuests = stayingGuests
    )
}

fun Customer.bookerDetails(address: Address, email: String): Booker {
    return Booker(
        title = this.fullName.title,
        firstName = this.fullName.firstName,
        lastName = this.fullName.lastName,
        emailAddress = email,
        mobile = this.contact.telephone ?: EMPTY_STRING_DOMAIN,
        acceptFutureMailing = false,
        address = BookerAddress(
            addressLine1 = address.line1,
            addressLine2 = address.line2 ?: EMPTY_STRING_DOMAIN,
            addressLine3 = address.line3 ?: EMPTY_STRING_DOMAIN,
            addressLine4 = address.line4 ?: EMPTY_STRING_DOMAIN,
            addressType = ADDRESS_TYPE_BUSINESS,
            countryCode = address.countryCode ?: EMPTY_STRING_DOMAIN,
            postalCode = address.postCode ?: EMPTY_STRING_DOMAIN
        )
    )
}

fun Customer.stayingGuests(): List<StayingGuests> {
    val stayingGuestDetails = StayingGuests(
        sameAsBooker = true,
        stayingGuestDetails = StayingGuestDetails(
            title = this.fullName.title,
            firstName = this.fullName.firstName,
            lastName = this.fullName.lastName
        ))

    return listOf(stayingGuestDetails)
}

fun getFirstRateTag(rateClassifications: List<RateClassificationsDomain>?): String? {
    return rateClassifications?.asSequence()
        ?.map { it.rateTags }
        ?.filter { it.isNotEmpty() }
        ?.flatMap { it.asSequence() }
        ?.firstOrNull()
}