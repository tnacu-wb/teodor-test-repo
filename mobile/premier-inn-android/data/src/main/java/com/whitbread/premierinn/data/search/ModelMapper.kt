package com.whitbread.premierinn.data.search


import com.whitbread.premierinn.data.common.BRAND_HUB
import com.whitbread.premierinn.data.common.BRAND_PI
import com.whitbread.premierinn.data.common.BRAND_PI_GERMANY
import com.whitbread.premierinn.data.common.BRAND_ZIP
import com.whitbread.premierinn.data.remote.SearchTopDestinationItem
import com.whitbread.premierinn.data.remote.SnowDropApiContract
import com.whitbread.premierinn.data.search.entity.SearchEntity
import com.whitbread.premierinn.domain.search.entity.Location
import com.whitbread.premierinn.domain.search.entity.SearchSuggetionItem
import com.whitbread.premierinn.domain.search.entity.SearchSuggetionItem.Type.GOOGLE_PLACE
import com.whitbread.premierinn.domain.search.entity.SearchSuggetionItem.Type.HUB_HOTEL
import com.whitbread.premierinn.domain.search.entity.SearchSuggetionItem.Type.LOCATION
import com.whitbread.premierinn.domain.search.entity.SearchSuggetionItem.Type.PI_GERMAN_HOTEL
import com.whitbread.premierinn.domain.search.entity.SearchSuggetionItem.Type.PI_HOTEL
import com.whitbread.premierinn.domain.search.entity.SearchSuggetionItem.Type.UNKNOWN_HOTEL
import com.whitbread.premierinn.domain.search.entity.SearchSuggetionItem.Type.ZIP_HOTEL

fun SnowDropApiContract.Property.toDomain(): SearchSuggetionItem {
    return SearchSuggetionItem(
            name = name,
            location = Location(location.coordinates[1], location.coordinates[0]),
            code = hotelCode,
            type = toPropertyType(brand)
    )
}

fun SnowDropApiContract.ManagedPlace.toDomain(): SearchSuggetionItem? {
    return location?.let {
        SearchSuggetionItem(name = name,
                location = Location(location.coordinates[1], location.coordinates[0]),
                type = LOCATION)
    }
}

fun SnowDropApiContract.GooglePlace.toDomain(): SearchSuggetionItem {
    return SearchSuggetionItem(name = name, code = placeId, type = GOOGLE_PLACE)
}

fun SearchTopDestinationItem.toDomain(): SearchSuggetionItem {
    return SearchSuggetionItem(name = name, location = Location(lat.toDouble(), long.toDouble()), type = LOCATION)
}

fun toPropertyType(brand: String?): SearchSuggetionItem.Type {
    return when (brand) {
        BRAND_PI -> PI_HOTEL
        BRAND_HUB -> HUB_HOTEL
        BRAND_ZIP -> ZIP_HOTEL
        BRAND_PI_GERMANY -> PI_GERMAN_HOTEL
        else -> UNKNOWN_HOTEL
    }
}

fun SearchEntity.toDomain(): SearchSuggetionItem {
    return SearchSuggetionItem(name = searchTerm, code = hotelId,
            location = if (lat != 0.0 && lon != 0.0) Location(lat, lon) else null,
            type = toType())
}

fun toSearchEntity(item: SearchSuggetionItem, dateTime: Long): SearchEntity {
    return SearchEntity(
            searchTerm = item.name,
            lat = if (item.location == null) 0.0 else item.location!!.latitude,
            lon = if (item.location == null) 0.0 else item.location!!.longitude,
            date = dateTime,
            hotelId = item.code,
            hotelBrand = toHotelBrand(item.type)
    )
}

fun SearchEntity.toType(): SearchSuggetionItem.Type {
    return if (!hotelBrand.isNullOrEmpty()) {
        toPropertyType(hotelBrand)
    } else if (!hotelId.isNullOrEmpty() && (lat == 0.0 && lon == 0.0)) {
        GOOGLE_PLACE
    } else {
        LOCATION
    }
}

fun toHotelBrand(type: SearchSuggetionItem.Type?): String? {
    return when (type) {
        HUB_HOTEL -> BRAND_HUB
        PI_HOTEL -> BRAND_PI
        ZIP_HOTEL -> BRAND_ZIP
        PI_GERMAN_HOTEL -> BRAND_PI_GERMANY
        else -> null
    }
}