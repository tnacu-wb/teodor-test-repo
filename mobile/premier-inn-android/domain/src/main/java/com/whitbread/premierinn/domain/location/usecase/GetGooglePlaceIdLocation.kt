package com.whitbread.premierinn.domain.location.usecase

import com.whitbread.premierinn.domain.location.repository.LocationRepository
import com.whitbread.premierinn.domain.search.entity.Location
import io.reactivex.Single
import javax.inject.Inject

class GetGooglePlaceIdLocation @Inject constructor(val repository: LocationRepository) {
    fun execute(placeId: String): Single<Location> {
        return repository.get(placeId)
    }
}