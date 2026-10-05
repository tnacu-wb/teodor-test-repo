package com.whitbread.premierinn.common.mapper;

import android.location.Location;

import io.reactivex.functions.Function;


/**
 *
 */
public class LocationMapper implements Function<Location, com.whitbread.premierinn.domain.search.entity.Location> {
    @Override public com.whitbread.premierinn.domain.search.entity.Location apply(Location location) {
        return new com.whitbread.premierinn.domain.search.entity.Location(location.getLatitude(), location.getLongitude());
    }
}
