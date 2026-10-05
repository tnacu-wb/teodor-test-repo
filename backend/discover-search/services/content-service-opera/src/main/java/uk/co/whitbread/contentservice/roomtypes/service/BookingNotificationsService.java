package uk.co.whitbread.contentservice.roomtypes.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.co.whitbread.contentservice.roomtypes.exception.NoBookingNotificationsDataFoundException;
import uk.co.whitbread.contentservice.roomtypes.model.BookingNotificationsResponse;
import uk.co.whitbread.contentservice.roomtypes.model.aem.AEMBookingNotifications;
import uk.co.whitbread.contentservice.roomtypes.service.client.AEMBookingNotificationsService;
import uk.co.whitbread.contentservice.roomtypes.util.AEMResponseConverter;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingNotificationsService {
    private final AEMBookingNotificationsService aemBookingNotificationsService;
    private final AEMResponseConverter aemResponseConverter;

    public BookingNotificationsResponse getBookingNotifications(String country, String language, String brand, String rate) {
        BookingNotificationsResponse bookingNotificationsResponse = new BookingNotificationsResponse();
        try {
            List<AEMBookingNotifications> aemBookingNotifications = aemBookingNotificationsService.getBookingNotifications(country, language, brand, rate);
            bookingNotificationsResponse.setBookingNotifications(aemResponseConverter.convertAEMBookingNotificationsResponse(aemBookingNotifications));
        } catch (Exception e) {
            throw new NoBookingNotificationsDataFoundException("Error while requesting booking notifications");
        }
        return bookingNotificationsResponse;
    }
}
