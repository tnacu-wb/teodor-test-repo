package com.whitbread.premierinn.api.response.booking

import android.os.Parcelable
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import com.whitbread.premierinn.api.response.availability.BookingRule
import com.whitbread.premierinn.api.response.availability.UpsellItem
import com.whitbread.premierinn.data.remote.RateContentItem
import com.whitbread.premierinn.domain.resource.repository.ContentManagedResourceRepository
import io.reactivex.Observable
import kotlinx.parcelize.Parcelize

@Deprecated(message = "Use com.whitbread.premierinn.data.remote.BookingRatePlan")
@Parcelize
data class BookingRatePlan(
        @SerializedName("classification") val classification: String,
        @SerializedName("name") var rateName: String?,
        @SerializedName("description") var description: String?,
        @SerializedName("rooms") val rooms: List<BookingRoom>,
        @SerializedName("totalCost") val totalCost: BookingPrice,
        @SerializedName("cityTax") val cityTax: BookingPrice?,
        @SerializedName("code") val rateCode: String,
        @SerializedName("prepaymentRequired") val prepaymentRequired: Boolean,
        @SerializedName("guaranteeRequired") val guaranteeRequired: Boolean,
        @SerializedName("cardFeeApplies") val cardFeeApplies: Boolean,
        @SerializedName("upsellItems") val upsellItems: List<UpsellItem>,
        @SerializedName("bookingRules") val bookingRules: List<BookingRule>
) : Parcelable {

    companion object {
        const val UNKNOWN_RATE = "UNKNOWN"
        const val RATE_NULLNAME_BUT_WITH_CLASSIFICATION = "RATENAME"
    }

    val totalAdults: Int
        get() {
            return Observable.fromIterable(this.rooms)
                    .map { it.adults }
                    .reduce { adultCount1, adultCount2 -> adultCount1 + adultCount2 }
                    .onErrorReturn { 0 }
                    .blockingGet(0)
        }

    val rateType: String?
        get() {
            return if (this.classification.isBlank() && this.rateName != null) {
                UNKNOWN_RATE
            } else if (this.rateName == null && this.classification.isNotBlank()) {
                RATE_NULLNAME_BUT_WITH_CLASSIFICATION
            } else this.rateName
        }

    fun calculateToShowMSOrFirebaseRateName(contentRepository: ContentManagedResourceRepository) : String {
        val firebaseRateName = getRateName(contentRepository)

        if (firebaseRateName == UNKNOWN_RATE) {
            if (this.rateName != null) {
                this.rateName?.let { rateNameFromMS ->
                    return if (rateNameFromMS.isNotEmpty()) {
                        rateNameFromMS
                    } else {
                        firebaseRateName
                    }
                }
            } else {
                return firebaseRateName
            }
        } else {
            return firebaseRateName
        }
        return firebaseRateName
    }

    fun calculateToShowMSOrFirebaseRateDescription(contentRepository: ContentManagedResourceRepository) : String {
        val firebaseRateDescription = getRateDescription(contentRepository)

        if (firebaseRateDescription == UNKNOWN_RATE) {
            if (this.description != null) {
                this.description?.let { rateDescriptionFromMS ->
                    return if (rateDescriptionFromMS.isNotEmpty()) {
                        rateDescriptionFromMS
                    } else {
                        firebaseRateDescription
                    }
                }
            } else {
                return firebaseRateDescription
            }
        } else {
            return firebaseRateDescription
        }
        return firebaseRateDescription
    }

    fun getRateName(contentRepository: ContentManagedResourceRepository): String {
        val fallbackRateName = contentRepository.getStringSingle(ContentManagedResourceRepository.Key.RATE_CONTENT.value)
                .map { jsonString ->
                    if (jsonString.isNotEmpty()) {
                        val listType = object : TypeToken<List<RateContentItem>>() {}.type
                        Gson().fromJson<List<RateContentItem>>(jsonString, listType)
                    } else emptyList()
                }
                .toObservable()
                .flatMapIterable { it }
                .filter { it.classification == classification }
                .map { it.name }
                .blockingFirst(UNKNOWN_RATE)


        return if (fallbackRateName.isNullOrBlank() ||
                   fallbackRateName == UNKNOWN_RATE) {
             UNKNOWN_RATE
        } else {
            this.rateName = fallbackRateName
            fallbackRateName
        }
    }

    fun getRateDescription(contentRepository: ContentManagedResourceRepository): String {
        val fallbackRateDescription = contentRepository.getStringSingle(ContentManagedResourceRepository.Key.RATE_CONTENT.value)
                .map { jsonString ->
                    if (jsonString.isNotEmpty()) {
                        val listType = object : TypeToken<List<RateContentItem>>() {}.type
                        Gson().fromJson<List<RateContentItem>>(jsonString, listType)
                    } else emptyList()
                }
                .toObservable()
                .flatMapIterable { it }
                .filter { it.classification == classification }
                .map { it.description }
                .blockingFirst(UNKNOWN_RATE)

        return if (fallbackRateDescription.isNullOrBlank() ||
            fallbackRateDescription == UNKNOWN_RATE) {
                UNKNOWN_RATE
        } else {
            this.description = fallbackRateDescription
            fallbackRateDescription
        }
    }
}