package uk.co.whitbread.content.infrastructure.rest.client.utils;

import lombok.experimental.UtilityClass;


@UtilityClass
public class HotelInformationUtils {

  private static final double EARTH_RADIUS_KM = 6371d;
  private static final double KM_TO_MILES = 0.621371;

  public static Double haversineDistance(Double latitudeRef, Double longitudeRef,
                                          Double hotelLatitude, Double hotelLongitude, boolean useMiles) {
    Double latitudeRefRad = Math.toRadians(latitudeRef);
    Double longitudeRefRad = Math.toRadians(longitudeRef);
    Double hotelLatitudeRad = Math.toRadians(hotelLatitude);
    Double hotelLongitudeRad = Math.toRadians(hotelLongitude);

    Double deltaLatitude = hotelLatitudeRad - latitudeRefRad;
    Double deltaLongitude = hotelLongitudeRad - longitudeRefRad;

    Double a = Math.pow(Math.sin(deltaLatitude / 2), 2)
            + Math.cos(latitudeRefRad) * Math.cos(hotelLatitudeRad)
            * Math.pow(Math.sin(deltaLongitude / 2), 2);

    Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    Double distanceInKm = EARTH_RADIUS_KM * c;

    return useMiles ? distanceInKm * KM_TO_MILES : distanceInKm;
  }
}
