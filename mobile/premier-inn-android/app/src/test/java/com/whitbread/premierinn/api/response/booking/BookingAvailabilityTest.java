package com.whitbread.premierinn.api.response.booking;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Objects;

import static com.google.common.truth.Truth.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class BookingAvailabilityTest {

    @Test
    public void sleepParkFlyRateNotShown() {
        BookingAvailability bookingAvailability = BookingAvailabilityFactory.sleepParkFly();

        assertThat(bookingAvailability.filteredRoomRatePlans().size()).isEqualTo(2);
        assertThat(Objects.requireNonNull(bookingAvailability.ratePlans()).get(0).getRateName()).isEqualTo("Flex");
    }
}