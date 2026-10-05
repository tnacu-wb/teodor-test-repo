package uk.co.whitbread.hotel.account.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.hotel.account.model.BookingStatus;
import uk.co.whitbread.hotel.account.model.StaysResponse;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static uk.co.whitbread.hotel.account.utils.BookingUtils.filterStaysByBookingStatus;
import static uk.co.whitbread.hotel.account.utils.BookingUtils.isPastBooking;

@ExtendWith(MockitoExtension.class)
class BookingUtilsTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
    }

    @Test
    void filterOutStaysByBookingStatus() throws Exception {

        StaysResponse response = getExpectedResponseUpcoming();
       
        assertThat(response.getStays().size(), is(7));

        filterStaysByBookingStatus(response, BookingStatus.FUTURE);
       
        // After filtering the stays
        assertThat(response.getStays().size(), is(7));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "2024-11-12T00:00:00+00:00",
        "2023-01-01T00:00:00+00:00",
        "2025-05-20T00:00:00+00:00"
    })
    void isPastBookingTest(String departureDate) {
        // Act
        boolean resp = isPastBooking(departureDate);

        // Assert
        boolean expected = LocalDateTime.parse(departureDate, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
            .isBefore(LocalDateTime.now());
        assertThat(resp, is(expected));
    }

    private StaysResponse getExpectedResponseUpcoming() throws IOException {
        return objectMapper.readValue(new File("src/test/resources/mapping/stays/StaysResponseFilteredBookings.json"),
                StaysResponse.class);
    }

}
