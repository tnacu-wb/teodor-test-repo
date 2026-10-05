package uk.co.whitbread.contentservice.roomtypes.service.client;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.contentservice.roomtypes.client.feign.AEMFeignClientRates;
import uk.co.whitbread.contentservice.roomtypes.config.properties.FeignClientProperties;
import uk.co.whitbread.contentservice.roomtypes.config.properties.FeignProperties;
import uk.co.whitbread.contentservice.roomtypes.exception.NoRatesDataFoundException;
import uk.co.whitbread.contentservice.roomtypes.model.aem.AEMRateClassifications;
import uk.co.whitbread.contentservice.roomtypes.model.aem.AEMRateClassificationsResponse;

@ExtendWith(MockitoExtension.class)
public class AEMRateClassificationsServiceTest {

    @InjectMocks
    private AEMRateClassificationsService target;

    @Mock
    private AEMFeignClientRates aemFeignClientRates;

    @Mock
    private FeignProperties feignProperties;

    @Mock
    private FeignClientProperties aemFeignClientProperties;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
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
    public void getAemRoomTypesSuccessResponse() throws URISyntaxException, IOException {
        final Path path = Paths.get(getClass().getResource("/mocks/aem_rate_classifications.json").toURI());
        final byte[] bytes = Files.readAllBytes(path);
        when(feignProperties.getAem())
                .thenReturn(aemFeignClientProperties);
        when(aemFeignClientProperties.getRatesresource())
                .thenReturn("/content-service/rate-classifications");

        AEMRateClassificationsResponse aemRateClassificationsResponse = objectMapper.readValue(bytes, AEMRateClassificationsResponse.class);
        when(aemFeignClientRates.getRateClassifications("gb", "en", "pi", "/content-service/rate-classifications"))
                .thenReturn(aemRateClassificationsResponse);

        final List<AEMRateClassifications> result = target.getRateClassifications("en", "pi", "");
        assertThat(result.get(1).getRateClassification().getValue()).isEqualTo("Q");
        assertThat(result.get(1).getRateOrder().getValue()).isEqualTo("0");
        assertThat(result.get(1).getRateName().getValue()).isEqualTo("Semi-Flex");
        assertThat(result.get(1).getRateDescription().getValue()).isEqualTo("Pay now. Change arrival date. Cancel up to 3 days before arrival.");
        assertThat(result.get(1).getRateLongDescription().getValue()).isEqualTo("In general, if you repay the price of what you bought within the delay period you won’t pay any interest. That’s because these periods are usually interest-free. If you use buy now pay later carefully you could delay paying for something for several months, or even a year, and not pay a penny in interest. Many of the big firms won’t charge you any interest if you clear your balance before your delay period is up – even if you only pay the day before.\n\nAlternatively, some offers allow you to spread the cost over a longer period but interest may be charged at a high rate, for example 39.9% APR.");
        assertThat(result.get(1).getRateNotes().getValue()).isEqualTo("Lorem ipsum dolor sit amet");

    }

    @Test
    public void getAemRoomTypesNoDataFound() {
        when(feignProperties.getAem())
                .thenReturn(aemFeignClientProperties);

        when(aemFeignClientProperties.getRatesresource())
                .thenReturn("/content-service/rate-classifications");

        final AEMRateClassificationsResponse emptyResponse = AEMRateClassificationsResponse.builder().build();
        when(aemFeignClientRates.getRateClassifications("gb", "en", "TEST", "/content-service/rate-classifications"))
                .thenReturn(emptyResponse);

        when(aemFeignClientRates.getRateClassifications("gb", "en", "pid", "/content-service/rate-classifications"))
                .thenReturn(emptyResponse);

        assertThrows(NoRatesDataFoundException.class, () -> target.getRateClassifications("en", "pid", "TEST"));
    }

}
