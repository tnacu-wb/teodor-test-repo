package uk.co.whitbread.contentservice.roomtypes.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.contentservice.roomtypes.model.BookingNotifications;
import uk.co.whitbread.contentservice.roomtypes.model.BookingNotificationsResponse;
import uk.co.whitbread.contentservice.roomtypes.model.BrandCode;
import uk.co.whitbread.contentservice.roomtypes.model.CountryCode;
import uk.co.whitbread.contentservice.roomtypes.model.LanguageCode;
import uk.co.whitbread.contentservice.roomtypes.model.RateCode;
import uk.co.whitbread.contentservice.roomtypes.service.BookingNotificationsService;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingNotificationsControllerTest {

    @InjectMocks
    private BookingNotificationsController controller;

    @Mock
    private BookingNotificationsService bookingNotificationsService;

    @Test
    public void getBookingNotificationsSuccessResponse() {
        BookingNotifications bookingNotifications1 = BookingNotifications.builder()
                .bookingInfoMessage("You can amend or cancel your booking any time up to 1pm on the day you’re due to arrive")
                .build();

        BookingNotifications bookingNotifications2 = BookingNotifications.builder()
                .bookingInfoMessage("You can check-in after 12pm")
                .build();

        BookingNotificationsResponse bookingNotificationsResponse = new BookingNotificationsResponse();
        List<BookingNotifications> bookingNotificationsList = new ArrayList<>();
        bookingNotificationsList.add(bookingNotifications1);
        bookingNotificationsList.add(bookingNotifications2);
        bookingNotificationsResponse.setBookingNotifications(bookingNotificationsList);
        when(bookingNotificationsService.getBookingNotifications("gb","en", "pi", "a"))
        .thenReturn(bookingNotificationsResponse);

        final BookingNotificationsResponse bookingNotifications = controller.getBookingNotifications(CountryCode.gb, LanguageCode.en, BrandCode.pi, RateCode.a);

        assertThat(bookingNotifications.getBookingNotifications().get(0).getBookingInfoMessage()).isEqualTo("You can amend or cancel your booking any time up to 1pm on the day you’re due to arrive");
        assertThat(bookingNotifications.getBookingNotifications().get(1).getBookingInfoMessage()).isEqualTo("You can check-in after 12pm");
    }


    @Test
    public void getNotAvailableBookingNotifications() {

        BookingNotificationsResponse bookingNotificationsResponse = new BookingNotificationsResponse();
        bookingNotificationsResponse.setBookingNotifications(new ArrayList<>());
        when(bookingNotificationsService.getBookingNotifications("gb","en", "pi", "a"))
                .thenReturn(bookingNotificationsResponse);

        final BookingNotificationsResponse bookingNotifications = controller.getBookingNotifications(CountryCode.gb, LanguageCode.en, BrandCode.pi, RateCode.a);
        assertThat(bookingNotifications.getBookingNotifications()).isEmpty();

    }
}
