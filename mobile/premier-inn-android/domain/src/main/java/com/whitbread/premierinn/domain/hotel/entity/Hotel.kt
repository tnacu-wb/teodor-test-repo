package com.whitbread.premierinn.domain.hotel.entity

import com.whitbread.premierinn.domain.common.Address
import com.whitbread.premierinn.domain.common.EMPTY_STRING_DOMAIN
import com.whitbread.premierinn.domain.common.TripAdvisorRating
import com.whitbread.premierinn.domain.hotel.entity.Hotel.Brand.PI
import com.whitbread.premierinn.domain.search.entity.Location

data class Hotel(val code: String,
                 val name: String,
                 val address: Address,
                 val location: Location,
                 val brand: Brand = PI,
                 val cityTax: CityTax? = null,
                 val contactDetails: ContactDetails? = null,
                 val facilities: List<Facility2> = emptyList(),
                 val photoPath: String? = null,
                 val restaurantImages: List<RestaurantImage>? = null,
                 val flag: Flag? = null,
                 val rating: TripAdvisorRating? = null,
                 val roomVariantDetails: List<RoomVariantDetails>,
                 val acceptedCreditCards: List<AcceptedCreditCard>) {

    fun has(lookUpType: Facility2.Type): Facility2? {
        facilities.forEach {
            if (it.type == lookUpType) return it else return@forEach
        }
        return null
    }

    enum class RestaurantTags(val tags: String) {
        BAR("bar"),
        BEEFEATER("beefeater"),
        BREWERSFAYRE("brewers-fayre"),
        COFFEESHOP("coffee-shop"),
        FOOD("food"),
        ORANGECOW("orange-cow"),
        RESTAURANT("restaurant"),
        TGIFRIDAYS("tgi-fridays"),
        TABLETABLE("table-table"),
        THMYE("thyme"),
        WHITBREADINN("whitbread-inn"),
        BREAKFAST("breakfast"),
        FAMILY("family")
    }

    enum class Brand {
        PI, PID, HUB, ZIP, UNKNOWN
    }

    data class Flag(val label: String, val colorHex: String)
    data class ContactDetails(val freePhone: String, val chargeablePhone: String)

    data class RoomVariantDetails(val title: String,
                                  val type: RoomVariant,
                                  val description: String,
                                  val imageUrl: String,
                                  val features: List<Feature>,
                                  val additionalInfo: String?,
                                  val room: String,
                                  val disclaimer: String = EMPTY_STRING_DOMAIN) {

        data class Feature(val name: String, val details: String)
    }

    data class RestaurantImage(val fileReference: String)
}

data class CityTax(val url: String?, val isCityTaxHotel: Boolean, val isCityTaxBusinessHotel: Boolean)

data class Facility2(val type: Type, val description: String) {
    enum class Type {
        FREE_PARKING,
        CHARGEABLE_PARKING,
        CHARGEABLE_ONSITE_PARKING,
        CHARGEABLE_OFFSITE_PARKING,
        FREE_WIFI,
        ACCESSIBLE_ROOM,
        LIFT,
        FAMILY,
        AIR_CONDITIONING,
        RESTAURANT,
        LUGGAGE_STORAGE,
        SLEEP_PARK_FLY,
        MEETING_ROOM,
        IN_ROOM_APP,
        COSTA,
        UNKNOWN
    }
}

data class AcceptedCreditCard(val creditCardCode: String, val feeAmount: String)
