package com.whitbread.premierinn.common.utils;

import android.location.Location;

import java.util.Locale;


public final class LocationUtils {

    private static final float MILE_FACTOR = 1609.344f;
    public static final String GOOGLE_MAPS_URL = "https://www.google.com/maps/search/?api=1&query=%f,%f";

    private LocationUtils() {
        throw new AssertionError("no instances allowed");
    }

    public static Location create(double lat, double lon) {
        Location location = new Location("");
        location.setLatitude(lat);
        location.setLongitude(lon);
        return location;
    }

    public static String gMapsDirectionsUri(double lat, double lon) {
        return String.format(Locale.ENGLISH, GOOGLE_MAPS_URL, lat, lon);
    }

    public static float distanceInMiles(Location from, Location to) {
        return from.distanceTo(to) / MILE_FACTOR;
    }
}
