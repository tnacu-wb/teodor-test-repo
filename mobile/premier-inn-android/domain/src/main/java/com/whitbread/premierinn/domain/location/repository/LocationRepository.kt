package com.whitbread.premierinn.domain.location.repository

import com.whitbread.premierinn.domain.search.entity.Location
import io.reactivex.Single

/**
 *
 */
interface LocationRepository {
    fun get(placeId: String): Single<Location>
}