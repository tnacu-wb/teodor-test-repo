package uk.co.whitbread.availabilitycacheservice.infrastructure.adapters.hotelprice;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.availabilitycacheservice.domain.ports.secondary.hotelprice.AemLocationPort;
import uk.co.whitbread.availabilitycacheservice.infrastructure.config.aem.AemFeignClientProperties;
import uk.co.whitbread.availabilitycacheservice.infrastructure.exceptions.HotelAvailabilitiesException;
import uk.co.whitbread.availabilitycacheservice.infrastructure.feign.AemFeignClient;
import uk.co.whitbread.availabilitycacheservice.infrastructure.model.aem.AemLocationResponse;
import uk.co.whitbread.availabilitycacheservice.infrastructure.model.aem.Location;

@ExtendWith(MockitoExtension.class)

class AemLocationServiceTest {

  @Mock
  private AemFeignClient aemFeignClient;

  @Mock
  private AemFeignClientProperties aemFeignClientProperties;

  private ObjectMapper objectMapper;

  private AemLocationPort aemLocationPort;

  @BeforeEach
  void setup() {
    aemLocationPort = new AemLocationsService(aemFeignClient, aemFeignClientProperties);

    final JavaTimeModule javaTimeModule = new JavaTimeModule();
    javaTimeModule.addDeserializer(LocalDateTime.class,
        new LocalDateTimeDeserializer(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
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
  void getAemLocationsWithNullItems() throws URISyntaxException, IOException {
    final Path path = Paths.get(getClass().getResource("/mocks/aem_null_values.json").toURI());
    final byte[] bytes = Files.readAllBytes(path);

    Mockito.when(aemFeignClientProperties.getResource())
        .thenReturn("/path");
    Mockito.when(aemFeignClientProperties.getCountry())
        .thenReturn("gb");
    Mockito.when(aemFeignClientProperties.getLanguage())
        .thenReturn("en");

    AemLocationResponse aemLocationResponse = objectMapper.readValue(bytes, AemLocationResponse.class);
    Mockito.when(aemFeignClient.getLocations("gb", "en", "/path"))
        .thenReturn(aemLocationResponse);

    HotelAvailabilitiesException exception = assertThrows(HotelAvailabilitiesException.class,
        () -> aemLocationPort.getAemLocations());
    assertEquals("AEM locations not found.", exception.getMessage());
  }

  @Test
  void getAemLocationsWithNullRoot() throws URISyntaxException, IOException {
    final Path path = Paths.get(getClass().getResource("/mocks/aem_root_as_null.json").toURI());
    final byte[] bytes = Files.readAllBytes(path);

    Mockito.when(aemFeignClientProperties.getResource())
        .thenReturn("/path");
    Mockito.when(aemFeignClientProperties.getCountry())
        .thenReturn("gb");
    Mockito.when(aemFeignClientProperties.getLanguage())
        .thenReturn("en");

    AemLocationResponse aemLocationResponse = objectMapper.readValue(bytes, AemLocationResponse.class);
    Mockito.when(aemFeignClient.getLocations("gb", "en", "/path"))
        .thenReturn(aemLocationResponse);

    HotelAvailabilitiesException exception = assertThrows(HotelAvailabilitiesException.class,
        () -> aemLocationPort.getAemLocations());
    assertEquals("AEM locations not found.", exception.getMessage());
  }

  @Test
  void getAemLocationsSuccess() throws URISyntaxException, IOException {
    final Path path = Paths.get(getClass().getResource("/mocks/aem_locations.json").toURI());
    final byte[] bytes = Files.readAllBytes(path);

    Mockito.when(aemFeignClientProperties.getResource())
        .thenReturn("/path");
    Mockito.when(aemFeignClientProperties.getCountry())
        .thenReturn("gb");
    Mockito.when(aemFeignClientProperties.getLanguage())
        .thenReturn("en");

    AemLocationResponse aemLocationResponse = objectMapper.readValue(bytes, AemLocationResponse.class);
    Mockito.when(aemFeignClient.getLocations("gb", "en", "/path"))
        .thenReturn(aemLocationResponse);

    final Collection<Location> location = aemLocationPort.getAemLocations();
    final List<Location> locationList = new ArrayList<>(location);
    Assertions.assertThat(location).hasSize(2);
    Assertions.assertThat(locationList.get(0).getName()).isEqualTo("London");
    Assertions.assertThat(locationList.get(0).getPlaceId()).isEqualTo("ChIJdd4hrwug2EcRmSrV3Vo6llI");
    Assertions.assertThat(locationList.get(1).getName()).isEqualTo("Bristol");
    Assertions.assertThat(locationList.get(1).getPlaceId()).isEqualTo("ChIJYdizgWaDcUgRH9eaSy6y5I4");
  }
}
