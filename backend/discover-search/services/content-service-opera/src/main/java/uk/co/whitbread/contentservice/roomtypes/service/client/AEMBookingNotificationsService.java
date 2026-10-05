package uk.co.whitbread.contentservice.roomtypes.service.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.co.whitbread.contentservice.roomtypes.client.feign.AEMBookingNotificationsFeignClient;

import uk.co.whitbread.contentservice.roomtypes.config.properties.FeignProperties;
import uk.co.whitbread.contentservice.roomtypes.exception.NoBookingNotificationsDataFoundException;
import uk.co.whitbread.contentservice.roomtypes.model.aem.AEMBookingNotifications;
import uk.co.whitbread.contentservice.roomtypes.model.aem.AEMBookingNotificationsItems;
import uk.co.whitbread.contentservice.roomtypes.model.aem.AEMBookingNotificationsResponse;
import uk.co.whitbread.contentservice.roomtypes.model.aem.BookingNotificationsContentFragment;
import uk.co.whitbread.contentservice.roomtypes.model.aem.BookingNotificationsRoot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class AEMBookingNotificationsService {
    private static final String ERROR_MESSAGE = "AEM booking notifications not found.";
    private final AEMBookingNotificationsFeignClient aemBookingNotificationsFeignClient;
    private final FeignProperties feignProperties;

    public List<AEMBookingNotifications> getBookingNotifications ( String country, String language, String brand, String rate) {
        List<AEMBookingNotifications> aemBookingNotifications = new ArrayList<>();
        try {

                AEMBookingNotificationsResponse bookingNotificationsJson = aemBookingNotificationsFeignClient
                        .getBookingNotifications(country, language, feignProperties.getAem().getBookingNotificationsResource(), brand, rate);
                final Map<String, BookingNotificationsContentFragment> contentFragments = Optional.ofNullable(bookingNotificationsJson.getItems())
                        .map(AEMBookingNotificationsItems::getRoot)
                        .map(BookingNotificationsRoot::getItems)
                        .orElseThrow(() -> new NoBookingNotificationsDataFoundException(ERROR_MESSAGE));

                if (!contentFragments.isEmpty())
                    contentFragments.values().forEach(contentFragment ->
                        aemBookingNotifications.add(contentFragment.getElements()));

        } catch (Exception exception) {
            log.error("Failed to get booking notifications no for " +
                    "language={}, brand={} , rate={} ", language, brand, rate, exception);

            throw new NoBookingNotificationsDataFoundException(ERROR_MESSAGE);
        }

        return aemBookingNotifications;
    }

}
