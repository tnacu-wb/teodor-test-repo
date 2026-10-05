package com.whitbread.premierinn.api.response.availability;


import com.whitbread.premierinn.domain.hotel.entity.Hotel;

import org.junit.Test;

import static com.whitbread.premierinn.api.response.availability.HotelInfo.HotelRating.ALL_AVAILABLE;
import static com.whitbread.premierinn.api.response.availability.HotelInfo.HotelRating.AVAILABLE_NO_IMAGE;
import static com.whitbread.premierinn.api.response.availability.HotelInfo.HotelRating.AVAILABLE_NO_NUMBER;
import static com.whitbread.premierinn.api.response.availability.HotelInfo.HotelRating.UNAVAILABLE;
import static junit.framework.Assert.assertNotSame;
import static junit.framework.Assert.assertSame;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class HotelInfoTest {

    @Test
    public void hotelIsHub() {
        HotelInfo hotelInfo = HotelInfoFactory.createWithBrand("hub");

        assertSame(Hotel.Brand.HUB, hotelInfo.hotelBrand());
    }

    @Test
    public void hotelIsNotHub() {
        HotelInfo hotelInfo = HotelInfoFactory.createWithBrand("zip");

        assertNotSame(Hotel.Brand.HUB, hotelInfo.hotelBrand());
    }

    @Test
    public void hotelRatingIsUnavailable() throws Exception {
        HotelInfo hotelInfoWithNullTripAdvisor = HotelInfoFactory.createWith((TripAdvisor) null);
        HotelInfo hotelInfoWithEmptyTripAdvisor = HotelInfoFactory.createWith(TripAdvisorFactory.createEmpty());

        assertThat(hotelInfoWithNullTripAdvisor.getRatingState(), is(UNAVAILABLE));
        assertThat(hotelInfoWithEmptyTripAdvisor.getRatingState(), is(UNAVAILABLE));
    }

    @Test
    public void hotelRatingIsAvailableInFull() throws Exception {
        HotelInfo hotelInfoWithTripAdvisor = HotelInfoFactory.createWith(TripAdvisorFactory.create());
        assertThat(hotelInfoWithTripAdvisor.getRatingState(), is(ALL_AVAILABLE));
    }

    @Test
    public void hotelRatingIsAvailableImageOnly() throws Exception {
        HotelInfo hotelInfoWithTripAdvisor = HotelInfoFactory.createWith(TripAdvisorFactory.createImageOnly());
        assertThat(hotelInfoWithTripAdvisor.getRatingState(), is(AVAILABLE_NO_NUMBER));
    }

    @Test
    public void hotelRatingIsAvailableNumberOnly() throws Exception {
        HotelInfo hotelInfoWithTripAdvisor = HotelInfoFactory.createWith(TripAdvisorFactory.createNumberOnly());
        assertThat(hotelInfoWithTripAdvisor.getRatingState(), is(AVAILABLE_NO_IMAGE));
    }
}
