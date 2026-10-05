package com.whitbread.premierinn.data.common

import com.auth0.android.authentication.AuthenticationException
import com.whitbread.premierinn.data.remote.*
import com.whitbread.premierinn.domain.authentication.AuthenticationError
import com.whitbread.premierinn.domain.authentication.UnAuthorizedCustomerError
import com.whitbread.premierinn.domain.common.*
import com.whitbread.premierinn.domain.countries.entity.CountryDomain
import com.whitbread.premierinn.domain.customer.EmailAlreadyExistsError
import com.whitbread.premierinn.domain.customer.entity.BookingPreferences
import com.whitbread.premierinn.domain.customer.entity.Customer
import com.whitbread.premierinn.domain.customer.entity.PaymentCard
import com.whitbread.premierinn.domain.graphql.GraphQL4xxError
import com.whitbread.premierinn.domain.graphql.GraphQLGenericError
import com.whitbread.premierinn.domain.graphql.GraphQLServerError
import io.reactivex.Completable
import io.reactivex.Single
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter

fun LocalDate.APIformatted(): String {
    val format = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    return format.format(this)
}


fun String.toLocalDate(): LocalDate {
    val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    return LocalDate.parse(this, dateTimeFormatter)
}

fun List<ApiCommon.UpsellItem>?.toDomain(departureDate: LocalDate): List<Upsell> {
    return this?.asIterable()?.map { it.toDomain(departureDate) }.orEmpty()
}

fun toUpsellCategoryDomain(category: String?): Upsell.Category {
    return if (category == "F") {
        Upsell.Category.BREAKFAST
    } else Upsell.Category.OTHER
}

fun ApiCommon.UpsellItem.toDomain(departureDate: LocalDate): Upsell {
    return Upsell(postingDate = this.postingDate ?: departureDate, quantity = this.quantity,
            code = this.code, roomId = this.roomId,
            unitCost = PriceDomain(amount = this.unitCost.amount, currency = this.unitCost.currency),
            category = toUpsellCategoryDomain(this.category),
            legend = this.legend.orEmpty())
}

fun ApiThrowable.Http?.withMessage(msg: String): Boolean {
    return this?.apiErrorBody?.details?.any { message -> message.contains(msg, true) }
            ?: false
}

fun ApiThrowable.Http?.withCode(msErrorCode: Int): Boolean {
    return this?.apiErrorBody?.code == msErrorCode
}

inline fun <reified T> throwApiExceptionIfNullable(value: T?): T {
    if (value == null) {
        throw ApiDataException("API ${T::class} should not be null")
    } else {
        return value
    }
}

fun <T> Single<T>.onErrorThrowWhenUserSessionExpired(): Single<T> {
    return this.onErrorResumeNext {
        if (isErrorTreatedAsUnAuthorizedCustomerError(it)) {
            Single.error(UnAuthorizedCustomerError)
        } else Single.error(it)
    }
}

fun <T> Single<T>.onGraphQLError(): Single<T> {
    return this.onErrorResumeNext{
        if (isGraphQlError(it)) {
            val graphQLError = it as GraphQlThrowable.GraphQLError
            val code = graphQLError.graphqlErrorBody.code
            when(code) {
                400 -> {
                    Single.error(GraphQL4xxError)
                }
                401 -> {
                    Single.error(UnAuthorizedCustomerError)
                }
                500 -> {
                    Single.error(GraphQLServerError)

                }
                else -> {
                    Single.error(GraphQLGenericError)

                }
            }
        } else Single.error(it)
    }
}

fun Completable.onErrorThrowWhenUserSessionExpired(): Completable {
    return this.onErrorResumeNext {
        if (isErrorTreatedAsUnAuthorizedCustomerError(it)) {
            Completable.error(UnAuthorizedCustomerError)
        } else Completable.error(it)
    }
}

fun Completable.onErrorThrowWhenUserAccountExists(): Completable {
    return this.onErrorResumeNext {
        if (isEmailAlreadyExistsError(it)) {
            Completable.error(EmailAlreadyExistsError)
        } else Completable.error(it)
    }
}

private fun isEmailAlreadyExistsError(e: Throwable): Boolean {
    return e is ApiThrowable.Http && e.apiErrorBody?.code == 7007
}

private fun isErrorTreatedAsUnAuthorizedCustomerError(e: Throwable): Boolean {
    return e is ApiThrowable.Http && (e.httpCode == 400 || e.httpCode == 401 || e.httpCode == 404)
}

private fun isGraphQlError(e: Throwable): Boolean {
    return e is GraphQlThrowable.GraphQLError
}

fun AuthenticationException.toDomainError(): AuthenticationError {
    return when {
        this.isNetworkError -> AuthenticationError.ofType(AuthenticationError.Type.NETWORK)
        this.isInvalidCredentials || this.isInvalidCredentials2() -> AuthenticationError.ofType(AuthenticationError.Type.INVALID_CREDENTIALS)
        "too_many_attempts" == this.code -> AuthenticationError.ofType(AuthenticationError.Type.TOO_MANY_ATTEMPTS)
        else -> AuthenticationError.unexpected("Auth0 Unhandled: ${this.code} - ${this.description}")
    }
}

fun AuthenticationException.isInvalidCredentials2(): Boolean {
    return "invalid_user_password" == code || "invalid_grant" == code && "Incorrect email/password." == description
}

fun Customer.toApiNewsletterPreferenceBody(marketingOptIn: Boolean,
                                           deviceLanguage: String): AccountApiContract.NewsletterPreferenceEditBody {
    return AccountApiContract.NewsletterPreferenceEditBody(
        brandCodes = listOf(BRAND_CODE).joinToString(),
        optIn = marketingOptIn,
        contactChannelValue = this.contact.email,
        language = deviceLanguage,
        country = this.address.countryCode ?: COUNTRY_CODE_UK,
        doubleOptIn = deviceLanguage == LANGUAGE_DEUTSCH_DOMAIN && this.address.countryCode == CountryDomain.GERMANY_CODE
                || deviceLanguage == LANGUAGE_ENGLISH_DOMAIN && this.address.countryCode == CountryDomain.GERMANY_CODE,
        customer = AccountApiContract.NewsLetterCustomerEditBody(
            this.address.countryCode ?: COUNTRY_CODE_UK, deviceLanguage)
    )
}

fun Customer.toApiCustomerPersonalDetailsBody(): AccountApiContract.CustomerPersonalDetailsBody {
    return AccountApiContract.CustomerPersonalDetailsBody(
        contactDetails = AccountApiContract.CustomerContactDetails(
            title = this.fullName.title,
            firstName = this.fullName.firstName,
            lastName = this.fullName.lastName,
            email = this.contact.email,
            telephone = this.contact.telephone,
            mobile = this.contact.mobile,
            carRegistration = this.carRegistration,
            nationality = this.nationality,
            passport = this.passport?.toApiPassport(),
            address = this.address.toApiAddress()),
            bookingPreference = this.toApiCustomerBookingPreferences())
}

fun Customer.toApiCustomerContactDetailsBody(): AccountApiContract.CustomerContactDetails {
    return AccountApiContract.CustomerContactDetails(
        title = this.fullName.title,
        firstName = this.fullName.firstName,
        lastName = this.fullName.lastName,
        email = this.contact.email,
        telephone = this.contact.telephone,
        mobile = this.contact.mobile,
        carRegistration = this.carRegistration,
        nationality = this.nationality,
        passport = this.passport?.toApiPassport(),
        address = this.address.toApiAddress())
}

fun Customer.toApiCustomerPaymentPreference(): AccountApiContract.PaymentPreference {
    return AccountApiContract.PaymentPreference(
            ApiCommon.PaymentCard(
                    cardType = this.paymentCard?.cardType,
                    cardNumber = this.paymentCard?.number,
                    expiryDate = this.paymentCard?.expiryDate,
                    cardHolderName = this.paymentCard?.holdersFullName,
                    billingAddress = this.address.toApiAddress()
            )
    )
}

fun Customer.toApiCustomerBookingPreferences(): AccountApiContract.BookingPreference {
    return AccountApiContract.BookingPreference(
            roomRequirements = this.bookingPreferences.roomCriteriaPreference.toApiRoomRequirements(),
            mealPreference = this.bookingPreferences.mealPreference
    )
}

fun RoomCriteria.toApiRoomRequirements(): AccountApiContract.RoomRequirements {
    return AccountApiContract.RoomRequirements(
        type = this.roomType.code,
        adults = this.numberOfAdults,
        children = this.numberOfChildren,
        cotRequired = this.includeCot,
        hotelBrand = this.hotelBrand)
}

fun PaymentCard.toApiCustomerPaymentPreferenceBody(address: Address?, customer: Customer): AccountApiContract.CustomerPaymentPreferencesBody {
    return AccountApiContract.CustomerPaymentPreferencesBody(
            paymentPreference = AccountApiContract.PaymentPreference(
                    paymentCard = if (this != PaymentCard.EMPTY) ApiCommon.PaymentCard(
                            cardHolderName = this.holdersFullName,
                            cardNumber = this.number,
                            cardType = this.cardType,
                            expiryDate = this.expiryDate,
                            billingAddress = address.toApiAddress()
                    ) else ApiCommon.PaymentCard(null, null, null, null,
                        null, null, null)
            ),
        contactDetails = customer.toApiCustomerContactDetailsBody(),
        bookingPreference = customer.toApiCustomerBookingPreferences()
    )
}

fun BookingPreferences.toApiCustomerBookingPreferenceBody(customer: Customer): AccountApiContract.CustomerBookingPreferencesBody {
    return AccountApiContract.CustomerBookingPreferencesBody(
            bookingPreference = AccountApiContract.BookingPreference(
                    mealPreference = this.mealPreference,
                    roomRequirements = roomCriteriaPreference?.let {
                        AccountApiContract.RoomRequirements(
                                adults = it.numberOfAdults,
                                children = it.numberOfChildren,
                                cotRequired = it.includeCot,
                                hotelBrand = HOTEL_BRAND,
                                type = it.roomType.toRoomString()!!)
                    }
            ),
        contactDetails = customer.toApiCustomerContactDetailsBody()
    )
}

fun Passport.toApiPassport(): ApiCommon.Passport {
    return ApiCommon.Passport(
            number = this.number,
            countryOfIssue = this.placeOfIssue
    )
}

fun Address?.toApiAddress(): ApiCommon.Address {
    return ApiCommon.Address(
            line1 = this?.line1 ?: EMPTY_STRING,
            line2 = this?.line2,
            line3 = this?.line3,
            line4 = this?.line4,
            line5 = this?.line5,
            type = if (this != null) {
                if (!this.isWorkAddress()) ADDRESS_TYPE_HOME else ADDRESS_TYPE_BUSINESS
            } else EMPTY_STRING,
            countryCode = this?.countryCode,
            postcode = this?.postCode,
            companyName = this?.companyName)
}

fun ErrorLogger.logAllExceptNetwork(tag: String, e: AuthenticationError) {
    if (e.errorType != AuthenticationError.Type.NETWORK) {
        logWarning(throwable = e, tag = tag, message = EMPTY_STRING)
    }
}