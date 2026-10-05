package com.whitbread.premierinn.api.response.availability;

import androidx.annotation.Nullable;

import com.whitbread.premierinn.api.response.AcceptedCreditCardTest;
import com.whitbread.premierinn.api.response.InstanceFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class HotelInfoFactory extends InstanceFactory {

    private static final float LAT = 51.502460f;
    private static final float LON = -0.123291f;
    private static final String LONDON_HAMPSTEAD = "London Hampstead";
    private static final String DESCRIPTION = "There\u0027s more to Hampstead than its world-famous heath...";
    private static final String DIRECTIONS = "Exit M1 onto the North Circular A406 eastbound ...";
    private static final String PARKING = "Limited chargeable parking is available at £14 per night.";
    private static final List<HotelImage> HOTEL_IMAGES;
    private static final List<Facility> HOTEL_FACILITIES;
    private static final List<Facility> PARKING_FACILITIES;

    static {
        HOTEL_IMAGES = Arrays.asList(new AutoValue_HotelImage("/content/dam/pi/websites/hotelimages/gb/en/L/LONHMP/LONHMP 2.jpg",
                Arrays.asList("exterior")));

        HOTEL_FACILITIES = Arrays.asList(new AutoValue_Facility(Facility.Codes.RESTAURANT, "Restaurant", "Restaurant"));

        PARKING_FACILITIES = Arrays.asList(new AutoValue_Facility(Facility.Codes.FREE_PARKING, "Free Parking", "Free Parking"));
    }

    public static HotelInfo create() {
        return new AutoValue_HotelInfo("LONHAM",
                LONDON_HAMPSTEAD,
                HOTEL_FACILITIES,
                TripAdvisorFactory.create(),
                HOTEL_IMAGES,
                Coordinates.create(LAT, LON),
                new AutoValue_MessagingFlag("", 14),
                new AutoValue_AvailabilityAddress("215 Haverstock Hill", "Hampstead",
                        "London", "UK", "NW3 4RB"),
                DESCRIPTION, DIRECTIONS, PARKING,
                new AutoValue_ContactDetails("0871 527 8662", "0333 321 1265"),
                Collections.singletonList(AcceptedCreditCardTest.create()),
                null,
                null,
                false,
                ""
        );
    }

    public static HotelInfo createNoFacilities() {
        return new AutoValue_HotelInfo("LONHAM",
                LONDON_HAMPSTEAD,
                new ArrayList<>(),
                TripAdvisorFactory.create(),
                HOTEL_IMAGES,
                Coordinates.create(LAT, LON),
                new AutoValue_MessagingFlag("", 14),
                new AutoValue_AvailabilityAddress("215 Haverstock Hill", "Hampstead",
                        "London", "UK", "NW3 4RB"),
                DESCRIPTION, DIRECTIONS, PARKING,
                new AutoValue_ContactDetails("0871 527 8662", "0333 321 1265"),
                Collections.singletonList(AcceptedCreditCardTest.create()),
                null,
                null,
                false,
                ""
        );
    }

    public static HotelInfo createWith(@Nullable TripAdvisor tripAdvisor) {
        return new AutoValue_HotelInfo("LONHAM",
                LONDON_HAMPSTEAD,
                new ArrayList<>(),
                tripAdvisor,
                HOTEL_IMAGES,
                Coordinates.create(LAT, LON),
                null,
                new AutoValue_AvailabilityAddress("215 Haverstock Hill", "Hampstead",
                        "London", "UK", "NW3 4RB"),
                DESCRIPTION, DIRECTIONS, PARKING,
                new AutoValue_ContactDetails("0871 527 8662", "0333 321 1265"),
                Collections.singletonList(AcceptedCreditCardTest.create()),
                null,
                null,
                false,
                ""
        );
    }

    public static HotelInfo createWithBrand(@Nullable String hotelBrand) {
        return new AutoValue_HotelInfo("LONHAM",
                LONDON_HAMPSTEAD,
                new ArrayList<>(),
                null,
                HOTEL_IMAGES,
                Coordinates.create(LAT, LON),
                null,
                new AutoValue_AvailabilityAddress("215 Haverstock Hill", "Hampstead",
                        "London", "UK", "NW3 4RB"),
                DESCRIPTION, DIRECTIONS, PARKING,
                new AutoValue_ContactDetails("0871 527 8662", "0333 321 1265"),
                Collections.singletonList(AcceptedCreditCardTest.create()),
                null,
                null,
                false,
                hotelBrand
        );
    }

    public static HotelInfo createWithoutDescriptionDirectionsParking() {
        return new AutoValue_HotelInfo("LONHAM",
                LONDON_HAMPSTEAD,
                PARKING_FACILITIES,
                TripAdvisorFactory.create(),
                HOTEL_IMAGES,
                Coordinates.create(LAT, LON),
                new AutoValue_MessagingFlag("", 14),
                new AutoValue_AvailabilityAddress("215 Haverstock Hill", "Hampstead",
                        "London", "UK", "NW3 4RB"),
                null, null, null,
                new AutoValue_ContactDetails("0871 527 8662", "0333 321 1265"),
                Collections.singletonList(AcceptedCreditCardTest.create()),
                null,
                null,
                false,
                ""
        );
    }

    public static HotelInfo createWithAuthenticationRequired() {
        return new AutoValue_HotelInfo("LONHAM",
                LONDON_HAMPSTEAD,
                PARKING_FACILITIES,
                TripAdvisorFactory.create(),
                HOTEL_IMAGES,
                Coordinates.create(LAT, LON),
                new AutoValue_MessagingFlag("", 14),
                new AutoValue_AvailabilityAddress("215 Haverstock Hill",
                        "Hampstead", "London", "UK", "NW3 4RB"),
                null, null, null,
                new AutoValue_ContactDetails("0871 527 8662", "0333 321 1265"),
                Collections.singletonList(AcceptedCreditCardTest.create()),
                null,
                null,
                true,
                ""
        );
    }

    public static HotelInfo createFromJson() {
        return create(HotelInfo.class, "apiTest/hotel-info-London-Hampstead.json");
    }

    public static HotelInfo createGermanHotel() {
        return create(HotelInfo.class, "apiTest/hotel-info-Frankfurt.json");
    }

    public static HotelInfo createFromJsonNoImages() {
        return create(HotelInfo.class, "apiTest/hotel-info-London-Hampstead_no_images.json");
    }

    public static HotelInfo createFromJsonNoParking() {
        return create(HotelInfo.class, "apiTest/hotel-info-no_parking.json");
    }
}