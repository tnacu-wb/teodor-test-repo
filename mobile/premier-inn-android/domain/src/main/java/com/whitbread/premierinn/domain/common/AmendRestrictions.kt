package com.whitbread.premierinn.domain.common

data class AmendRestrictions(val nights: Boolean,
                             val rooms: Boolean,
                             val guestNames: Boolean,
                             val upsell: Boolean,
                             val restricted: Boolean) {

    companion object {
        fun createWithDefaults(
                nights : Boolean = false,
                rooms : Boolean = false,
                guestNames : Boolean = false,
                upsell : Boolean = false,
                restricted : Boolean = false): AmendRestrictions {
            return AmendRestrictions(
                    nights = nights,
                    rooms = rooms,
                    guestNames = guestNames,
                    upsell = upsell,
                    restricted = restricted
            )
        }
    }
}