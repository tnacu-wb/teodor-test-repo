package uk.co.whitbread.content.infrastructure.rest.client.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class HotelInformationUtilsTest {

    @Test
    void testHaversineDistanceInKilometers__ShouldReturnOk() {
        Double latitudeRef = 40.7128;
        Double longitudeRef = -74.0060;
        Double hotelLatitude = 34.0522;
        Double hotelLongitude = -118.2437;

        double expectedDistanceInKm = 3935.74;

        Double actualDistanceInKm = HotelInformationUtils.haversineDistance(
                latitudeRef, longitudeRef, hotelLatitude, hotelLongitude, false);

        assertEquals(expectedDistanceInKm, actualDistanceInKm, 0.1,
                "Distance in kilometers is not calculated correctly.");
    }

    @Test
    void testHaversineDistanceInMiles__ShouldReturnOk() {
        Double latitudeRef = 40.7128;
        Double longitudeRef = -74.0060;
        Double hotelLatitude = 34.0522;
        Double hotelLongitude = -118.2437;

        double expectedDistanceInMiles = 2445.55;

        Double actualDistanceInMiles = HotelInformationUtils.haversineDistance(
                latitudeRef, longitudeRef, hotelLatitude, hotelLongitude, true);

        assertEquals(expectedDistanceInMiles, actualDistanceInMiles, 0.1,
                "Distance in miles is not calculated correctly.");
    }

    @Test
    void testHaversineDistanceSameLocation__ShouldReturnOk() {
        Double latitudeRef = 40.7128;
        Double longitudeRef = -74.0060;
        Double hotelLatitude = 40.7128;
        Double hotelLongitude = -74.0060;

        double expectedDistance = 0.0;

        Double actualDistance = HotelInformationUtils.haversineDistance(
                latitudeRef, longitudeRef, hotelLatitude, hotelLongitude, false);

        assertEquals(expectedDistance, actualDistance, 0.1,
                "Distance for the same location should be zero.");
    }
}