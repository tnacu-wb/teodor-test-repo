package com.whitbread.premierinn.data.location.repository

import com.google.gson.JsonParseException
import com.whitbread.premierinn.data.common.AppPackageDetails
import com.whitbread.premierinn.data.remote.PlacesApi
import com.whitbread.premierinn.domain.location.repository.LocationRepository
import com.whitbread.premierinn.domain.search.entity.Location
import io.reactivex.Single
import javax.inject.Inject

class LocationRepositoryImpl @Inject constructor(val placesApi: PlacesApi,
                             val apiKey: String,
                             val details: AppPackageDetails) : LocationRepository {
    override fun get(placeId: String): Single<Location> {
        return placesApi
            .location(
                placeId = placeId,
                apiKey = apiKey,
                packageName = details.name,
                packageSignature = details.sig
            )
            .map {
                if (it.location != null) {
                    it.location.toDomain()
                } else {
                    throw JsonParseException(" No result found - api key issue or Invalid Request ")
                }
            }
    }

    fun PlacesApi.Location.toDomain(): Location {
        return Location(this.lat, this.lon)
    }
}