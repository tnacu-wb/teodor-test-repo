package com.whitbread.premierinn.domain.search.entity

/**
 *
 */
data class SearchSuggetionItem(val name: String,
                               val location: Location? = null,
                               val code: String? = null,
                               val type: Type) {

    enum class Type {
        //places
        LOCATION, //CurrentLocation or ManagedPlace or StaticSuggestions
        GOOGLE_PLACE,

        //hotels
        PI_HOTEL,
        PI_GERMAN_HOTEL,
        HUB_HOTEL,
        ZIP_HOTEL,
        UNKNOWN_HOTEL
    }
}