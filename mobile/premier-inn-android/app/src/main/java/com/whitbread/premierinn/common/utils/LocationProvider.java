package com.whitbread.premierinn.common.utils;

import android.location.Location;

import com.google.android.gms.location.LocationRequest;
import com.patloew.rxlocation.RxLocation;
import com.whitbread.premierinn.common.mapper.LocationMapper;

import javax.inject.Inject;

import io.reactivex.Observable;

public final class LocationProvider {

    private final RxLocation rxLocation;

    @Inject
    public LocationProvider(RxLocation location) {
        this.rxLocation = location;
    }

    @Deprecated
    @SuppressWarnings("MissingPermission")
    public Observable<Location> locationUpdateObservable() {
        LocationRequest request = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        return rxLocation.settings()
                .checkAndHandleResolution(request)
                .flatMapObservable(permissionGranted -> permissionGranted
                        ? rxLocation.location().updates(request).take(1)
                        : Observable.error(new Exception("No location permission allowed")));
    }

    @SuppressWarnings("MissingPermission")
    public Observable<com.whitbread.premierinn.domain.search.entity.Location> locationUpdateObservable2() {
        LocationRequest request = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        return rxLocation.settings()
                .checkAndHandleResolution(request)
                .flatMapObservable(permissionGranted -> permissionGranted
                        ? rxLocation.location().updates(request).take(1).map(new LocationMapper())
                        : Observable.error(new Exception("No location permission allowed")));
    }
}
