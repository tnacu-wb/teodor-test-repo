package uk.co.whitbread.contentservice.roomtypes.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.contentservice.roomtypes.model.BookingNotifications;
import uk.co.whitbread.contentservice.roomtypes.model.BookingNotificationsResponse;
import uk.co.whitbread.contentservice.roomtypes.model.aem.AEMBookingNotifications;
import uk.co.whitbread.contentservice.roomtypes.model.aem.BookingInfoMessage;
import uk.co.whitbread.contentservice.roomtypes.service.client.AEMBookingNotificationsService;
import uk.co.whitbread.contentservice.roomtypes.util.AEMResponseConverter;

@ExtendWith(MockitoExtension.class)
public class BookingNotificationsServiceTest {

    @Mock
    private AEMBookingNotificationsService aemBookingNotificationsService;

    @Mock
    private AEMResponseConverter aemResponseConverter;

    @InjectMocks
    private BookingNotificationsService service;

    private final static String MESSAGE_1 = "You can amend or cancel your booking any time up to 1pm on the day you’re due to arrive.";
    private final static String MESSAGE_2 = "You can check-in after 12pm";

    @Test
    public void getBookingNotificationsSuccess() {

        List<AEMBookingNotifications> aemBookingNotifications = new ArrayList<>();
        aemBookingNotifications.add(createAEMBookingNotifications(MESSAGE_1));
        when(aemBookingNotificationsService.getBookingNotifications("gb", "en", "pi", "a"))
                .thenReturn(aemBookingNotifications);

        List<BookingNotifications> bookingNotificationsList = new ArrayList<>();
        bookingNotificationsList.add(createBookingNotifications(MESSAGE_1));
        when(aemResponseConverter.convertAEMBookingNotificationsResponse(aemBookingNotifications))
                .thenReturn(bookingNotificationsList);

        BookingNotificationsResponse response = new BookingNotificationsResponse();
        response.setBookingNotifications(bookingNotificationsList);

        final BookingNotificationsResponse bookingNotificationsResponse = service.getBookingNotifications("gb", "en", "pi", "a");
        assertThat(bookingNotificationsResponse.getBookingNotifications().size()).isEqualTo(1);
        assertThat(bookingNotificationsResponse.getBookingNotifications().get(0).getBookingInfoMessage()).isEqualTo(MESSAGE_1);

    }

    @Test
    public void getMultipleBookingNotificationsSuccess() {

        List<AEMBookingNotifications> aemBookingNotifications = new ArrayList<>();
        aemBookingNotifications.add(createAEMBookingNotifications(MESSAGE_1));
        aemBookingNotifications.add(createAEMBookingNotifications(MESSAGE_2));
        when(aemBookingNotificationsService.getBookingNotifications("gb", "en", "pi", "a"))
                .thenReturn(aemBookingNotifications);

        List<BookingNotifications> bookingNotificationsList = new ArrayList<>();
        bookingNotificationsList.add(createBookingNotifications(MESSAGE_1));
        bookingNotificationsList.add(createBookingNotifications(MESSAGE_2));
        when(aemResponseConverter.convertAEMBookingNotificationsResponse(aemBookingNotifications))
                .thenReturn(bookingNotificationsList);

        BookingNotificationsResponse response = new BookingNotificationsResponse();
        response.setBookingNotifications(bookingNotificationsList);

        final BookingNotificationsResponse bookingNotificationsResponse = service.getBookingNotifications("gb", "en", "pi", "a");
        assertThat(bookingNotificationsResponse.getBookingNotifications().size()).isEqualTo(2);
        assertThat(bookingNotificationsResponse.getBookingNotifications().get(0).getBookingInfoMessage()).isEqualTo(MESSAGE_1);
        assertThat(bookingNotificationsResponse.getBookingNotifications().get(1).getBookingInfoMessage()).isEqualTo(MESSAGE_2);

    }

    @Test
    public void getNoBookingNotificationsSuccess() {
        List<AEMBookingNotifications> aemBookingNotifications = new ArrayList<>();
        when(aemBookingNotificationsService.getBookingNotifications("gb", "en", "pi", "a"))
                .thenReturn(aemBookingNotifications);

        List<BookingNotifications> bookingNotificationsList = new ArrayList<>();
        when(aemResponseConverter.convertAEMBookingNotificationsResponse(aemBookingNotifications))
                .thenReturn(bookingNotificationsList);

        BookingNotificationsResponse response = new BookingNotificationsResponse();
        response.setBookingNotifications(bookingNotificationsList);

        final BookingNotificationsResponse bookingNotificationsResponse = service.getBookingNotifications("gb", "en", "pi", "a");
        assertThat(bookingNotificationsResponse.getBookingNotifications().size()).isZero();

    }

    private AEMBookingNotifications createAEMBookingNotifications(String message) {
        return AEMBookingNotifications.builder()
                .bookingInfoMessage(BookingInfoMessage.builder().value(message).build())
                .build();
    }

    private BookingNotifications createBookingNotifications(String message) {
        return BookingNotifications.builder()
                .bookingInfoMessage(message)
                .build();
    }
}