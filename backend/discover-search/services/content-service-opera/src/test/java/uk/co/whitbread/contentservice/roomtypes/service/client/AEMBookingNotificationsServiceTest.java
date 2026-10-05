package uk.co.whitbread.contentservice.roomtypes.service.client;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.contentservice.roomtypes.client.feign.AEMBookingNotificationsFeignClient;
import uk.co.whitbread.contentservice.roomtypes.config.properties.FeignClientProperties;
import uk.co.whitbread.contentservice.roomtypes.config.properties.FeignProperties;
import uk.co.whitbread.contentservice.roomtypes.exception.NoBookingNotificationsDataFoundException;
import uk.co.whitbread.contentservice.roomtypes.model.BrandCode;
import uk.co.whitbread.contentservice.roomtypes.model.CountryCode;
import uk.co.whitbread.contentservice.roomtypes.model.LanguageCode;
import uk.co.whitbread.contentservice.roomtypes.model.aem.AEMBookingNotifications;
import uk.co.whitbread.contentservice.roomtypes.model.aem.AEMBookingNotificationsResponse;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AEMBookingNotificationsServiceTest {

    @InjectMocks
    private AEMBookingNotificationsService target;

    @Mock
    private AEMBookingNotificationsFeignClient aemBookingNotificationsFeignClient;

    @Mock
    private FeignProperties feignProperties;

    @Mock
    private FeignClientProperties aemFeignClientProperties;

    private ObjectMapper objectMapper;

    private static final String BOOKING_NOTIFICATIONS_COOKIES_POLICY = "/content-services/booking-info-messages";

    @BeforeEach
    public void setup() {
        when(feignProperties.getAem())
                .thenReturn(aemFeignClientProperties);

        when(aemFeignClientProperties.getBookingNotificationsResource())
                .thenReturn(BOOKING_NOTIFICATIONS_COOKIES_POLICY);

        final JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ISO_LOCAL_DATE));

        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, false);
        objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, false);
        objectMapper.registerModule(javaTimeModule);
        objectMapper.setSerializationInclusion(NON_NULL);
    }

    @Test
    public void getAemBookingNotificationsSuccessResponse() throws URISyntaxException, IOException {
        final Path path = Paths.get(getClass().getResource("/__files/aem_booking_notifications.json").toURI());
        final byte[] bytes = Files.readAllBytes(path);
        when(aemBookingNotificationsFeignClient.getBookingNotifications(CountryCode.gb.name(), LanguageCode.en.name(), BOOKING_NOTIFICATIONS_COOKIES_POLICY, BrandCode.pi.name(), "a"))
                .thenReturn(null);
        AEMBookingNotificationsResponse bookingNotificationsResponse = objectMapper.readValue(bytes, AEMBookingNotificationsResponse.class);
        when(aemBookingNotificationsFeignClient.getBookingNotifications(CountryCode.gb.name(), LanguageCode.en.name(), BOOKING_NOTIFICATIONS_COOKIES_POLICY, BrandCode.pi.name(), "a"))
                .thenReturn(bookingNotificationsResponse);

        final List<AEMBookingNotifications> result = target.getBookingNotifications(CountryCode.gb.name(), LanguageCode.en.name(), BrandCode.pi.name(), "a");
        assertThat(result.get(0).getBookingInfoMessage().getValue()).isEqualTo("You can amend or cancel your booking any time up to 1pm on the day you’re due to arrive.");
    }

    @Test
    public void getBookingNotificationsNoDataFound() {
        final AEMBookingNotificationsResponse emptyResponse = AEMBookingNotificationsResponse.builder().build();
        when(aemBookingNotificationsFeignClient.getBookingNotifications(CountryCode.gb.name(), LanguageCode.en.name(),BOOKING_NOTIFICATIONS_COOKIES_POLICY, BrandCode.pi.name(), "a"))
                .thenReturn(emptyResponse);
        assertThrows(NoBookingNotificationsDataFoundException.class,
            () -> target.getBookingNotifications(CountryCode.gb.name(), LanguageCode.en.name(), BrandCode.pi.name(),"a"));
    }

}
