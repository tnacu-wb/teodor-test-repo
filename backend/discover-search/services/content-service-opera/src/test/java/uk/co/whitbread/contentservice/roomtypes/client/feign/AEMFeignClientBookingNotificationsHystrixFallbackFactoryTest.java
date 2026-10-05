package uk.co.whitbread.contentservice.roomtypes.client.feign;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.whitbread.contentservice.roomtypes.model.aem.AEMBookingNotificationsResponse;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;

public class AEMFeignClientBookingNotificationsHystrixFallbackFactoryTest {
    private final AEMFeignClientBookingNotificationsHystrixFallbackFactory target =
            new AEMFeignClientBookingNotificationsHystrixFallbackFactory();

    private Throwable mockThrowable;

    @BeforeEach
    public void setup() {
        mockThrowable = mock(Throwable.class);
    }

    @Test
    public void shouldBeNullSafe() {
        final AEMBookingNotificationsFeignClient aemBookingNotificationsFeignClient = target.create(mockThrowable);

        final AEMBookingNotificationsResponse bookingNotificationsResponse = aemBookingNotificationsFeignClient.getBookingNotifications(null, null,
                null, null, null);

        assertThat(bookingNotificationsResponse.getItems(), Matchers.nullValue());
    }

    @Test
    public void shouldBeEmptyFieldSafe() {
        final AEMBookingNotificationsFeignClient aemBookingNotificationsFeignClient = target.create(mockThrowable);

        final AEMBookingNotificationsResponse bookingNotificationsResponse = aemBookingNotificationsFeignClient.getBookingNotifications("", "",
                "","", "");

        assertThat(bookingNotificationsResponse.getItems(), Matchers.nullValue());
    }

    @Test
    public void shouldReturnEmptyObject() {
        final AEMBookingNotificationsFeignClient aemBookingNotificationsFeignClient = target.create(mockThrowable);

        final AEMBookingNotificationsResponse bookingNotificationsResponse = aemBookingNotificationsFeignClient.getBookingNotifications("gb", "en",
                "resource", "someBrand", "a");

        assertThat(bookingNotificationsResponse.getItems(), Matchers.nullValue());
    }
}
