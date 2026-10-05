package com.whitbread.premierinn.common.utils

import com.whitbread.premierinn.common.AddressFormDataOutput
import com.whitbread.premierinn.common.Validator
import com.whitbread.premierinn.common.format.DateFormat
import com.whitbread.premierinn.domain.countries.entity.CountryDomain
import com.whitbread.premierinn.paymentdetails.DateFieldInfo
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.functions.Function
import java.text.ParseException
import java.util.*

const val DATE_FORMAT_SEPARATOR = "/"
const val DATE_FORMAT_POSITION = 2
const val DATE_DIGIT_LENGTH = 4
const val CVV_LENGTH_AMEX = 4
const val CVV_LENGTH_NON_AMEX = 3

fun textChangedAndFocusToTextAndError(
        action: Function<String, Boolean>): ObservableTransformer<Pair<CharSequence, Boolean>, Pair<String, Boolean>> {
    return ObservableTransformer { upstream: Observable<Pair<CharSequence, Boolean>> ->
        upstream.map { (first, second) -> first.toString().trim() to second }
                .map { (first, second) -> first to (!action.apply(first) && !second) }
    }
}

fun checkDate(action: Function<String, Boolean>): ObservableTransformer<DateFieldInfo, DateFieldInfo> {
    return ObservableTransformer { upstream: Observable<DateFieldInfo> ->
        upstream
                .map { dateInfo: DateFieldInfo ->
                    DateFieldInfo.create(dateInfo.date(), dateInfo.focus(),
                            cursorPositionAfterRemovingSeparator(dateInfo.date(), dateInfo.cursorPosition()))
                }
                .map { dateInfo: DateFieldInfo -> DateFieldInfo.create(unFormatDate(dateInfo.date()), dateInfo.focus(), dateInfo.cursorPosition()) }
                .distinctUntilChanged { dateInfo: DateFieldInfo? -> dateInfo }
                .flatMap { dateInfo: DateFieldInfo ->
                    Observable.just(dateInfo)
                            .map { dateInfoInner: DateFieldInfo -> Pair<CharSequence, Boolean>(dateInfoInner.date(), dateInfoInner.focus()) }
                            .compose(textChangedAndFocusToTextAndError(action))
                            .map { (first, second) -> DateFieldInfo.create(first, second, dateInfo.cursorPosition()) }
                }
    }
}

private fun cursorPositionAfterRemovingSeparator(date: String, cursorPositionBeforeRemovingSeparator: Int): Int {
    return if (date.contains(DATE_FORMAT_SEPARATOR) && date.indexOf(DATE_FORMAT_SEPARATOR) < cursorPositionBeforeRemovingSeparator) {
        cursorPositionBeforeRemovingSeparator - DATE_FORMAT_SEPARATOR.length
    } else cursorPositionBeforeRemovingSeparator
}

fun cursorPositionAfterAddingSeparator(date: String, cursorPositionBeforeAddingSeparator: Int): Int {
    return if (date.contains(DATE_FORMAT_SEPARATOR) && date.indexOf(DATE_FORMAT_SEPARATOR) <= cursorPositionBeforeAddingSeparator) {
        cursorPositionBeforeAddingSeparator + DATE_FORMAT_SEPARATOR.length
    } else cursorPositionBeforeAddingSeparator
}

fun formatDate(newDate: String, oldDate: String): String {
    if (oldDate.contains(DATE_FORMAT_SEPARATOR)) {
        return oldDate
    }
    val stringBuilder = StringBuilder(newDate)
    if (newDate.length == DATE_FORMAT_POSITION && newDate.length >= oldDate.length
            || newDate.length > DATE_FORMAT_POSITION) {
        stringBuilder.insert(DATE_FORMAT_POSITION, DATE_FORMAT_SEPARATOR)
    }
    return stringBuilder.toString()
}

private fun unFormatDate(newDate: String): String {
    return newDate.replace(DATE_FORMAT_SEPARATOR, "")
}

/**
 * Credit cards are valid up to AND INCLUDING the month shown on the card; hence we check that today's
 * date is before {expiry date + 1 month}
 */
fun isExpiryDateValid(expiryDateText: String): Boolean {
    return if (expiryDateText.length != DATE_DIGIT_LENGTH) {
        false
    } else try {
        val expiryDate = DateFormat.MONTH_YEAR_DIGITS.parse(expiryDateText) ?: throw Exception("Expiry date is null")
        val expiryDatePlusOneMonth = Calendar.getInstance()
        expiryDatePlusOneMonth.time = expiryDate
        expiryDatePlusOneMonth.add(Calendar.MONTH, 1)
        Calendar.getInstance().time.before(expiryDatePlusOneMonth.time)
    } catch (e: ParseException) {
        false
    }
}

fun isStartDateValid(startDateText: String): Boolean {
    return if (startDateText.length != DATE_DIGIT_LENGTH) {
        false
    } else try {
        val startDate = DateFormat.MONTH_YEAR_DIGITS.parse(startDateText) ?: throw Exception("Start date is null")
        startDate.before(Calendar.getInstance().time)
    } catch (e: ParseException) {
        false
    }
}

fun isCvvValid(cvvLength: Int, cardType: String) : Boolean {
    return cardType == CardTypeEnum.AM.name && cvvLength == CVV_LENGTH_AMEX || cvvLength == CVV_LENGTH_NON_AMEX
}

fun addressDataFormProcessing(): ObservableTransformer<AddressFormDataOutput, AddressFormDataOutput> {
    return ObservableTransformer { upstream: Observable<AddressFormDataOutput> ->
        upstream.map { formDataOutput: AddressFormDataOutput ->
            if (formNeedsTextValidation(formDataOutput.form())) {
                return@map AddressFormDataOutput.create(formDataOutput.text().trim(), formDataOutput.focus(), formDataOutput.form())
            }
            formDataOutput
        }.map { formDataOutput: AddressFormDataOutput ->
            var addressFormDataOutput: AddressFormDataOutput = formDataOutput
            if (formNeedsTextValidation(formDataOutput.form())) {
                addressFormDataOutput = AddressFormDataOutput.create(formDataOutput.text(),
                        !Validator.isNotEmpty(formDataOutput.text()) && !formDataOutput.focus(),
                        formDataOutput.form())
            }
            addressFormDataOutput
        }
    }
}

private fun formNeedsTextValidation(form: AddressFormDataOutput.Form): Boolean {
    return AddressFormDataOutput.Form.POSTCODE == form || AddressFormDataOutput.Form.ADDRESS_LINE_1 == form || AddressFormDataOutput.Form.COMPANY == form
}