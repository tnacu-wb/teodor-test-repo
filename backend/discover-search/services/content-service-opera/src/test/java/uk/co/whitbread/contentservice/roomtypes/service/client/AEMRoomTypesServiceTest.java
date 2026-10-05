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
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.contentservice.roomtypes.client.feign.AEMFeignClient;
import uk.co.whitbread.contentservice.roomtypes.config.properties.FeignClientProperties;
import uk.co.whitbread.contentservice.roomtypes.config.properties.FeignProperties;
import uk.co.whitbread.contentservice.roomtypes.exception.NoDataFoundException;
import uk.co.whitbread.contentservice.roomtypes.model.aem.AEMRoomType;
import uk.co.whitbread.contentservice.roomtypes.model.aem.AEMRoomTypesResponse;

@ExtendWith(MockitoExtension.class)
public class AEMRoomTypesServiceTest {

    @InjectMocks
    private AEMRoomTypesService target;

    @Mock
    private AEMFeignClient aemFeignClient;

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
        final Path path = Paths.get(getClass().getResource("/mocks/aem_room_types.json").toURI());
        final byte[] bytes = Files.readAllBytes(path);
        when(feignProperties.getAem())
                .thenReturn(aemFeignClientProperties);
        when(aemFeignClientProperties.getResource())
                .thenReturn("/content-service/room-types");

        AEMRoomTypesResponse aemLocationResponse = objectMapper.readValue(bytes, AEMRoomTypesResponse.class);
        when(aemFeignClient.getRoomTypes("gb", "en", "pi", "/content-service/room-types"))
                .thenReturn(aemLocationResponse);

        List<String> facilities = new ArrayList<>();
        facilities.add("Comfort:A kingsize Hypnos bed with a cosy duvet and choice of pillows, plus a handy desk and chair");
        facilities.add("Convenience:Tea & coffee making facilities and a power shower – plus a bath in most rooms");

        final List<AEMRoomType> result = target.getRoomTypes("gb", "en", "pi");
        assertThat(result.get(0).getRoomTypeCode().getValue()).isEqualTo("SB");
        assertThat(result.get(0).getRoomCategory().getValue()).isEqualTo("Standard");
        assertThat(result.get(0).getRoomLabel().getValue()).isEqualTo("Standard Room");
        assertThat(result.get(0).getRoomDescription().getValue()).isEqualTo("Enjoy everything that’s included in a Standard room, plus a few little extras to enhance your stay.");
        assertThat(result.get(0).getRoomInfoLabel().getValue()).isEqualTo("Room Information");
        assertThat(result.get(0).getRoomInfo().getValue()).isEqualTo("This hotel is being refurbished, we're sorry for any inconvenience.");
        assertThat(result.get(0).getRoomImage().getValue()).isNull();
        assertThat(result.get(0).getGridImage().getValue()).isNull();
        assertThat(result.get(0).getFacilities().getValue()).isEqualTo(facilities);

    }

    @Test
    public void getAemRoomTypesNoDataFound() throws URISyntaxException, IOException {
        assertThrows(NoDataFoundException.class, () -> target.getRoomTypes("gb", "en", "pi"));
    }
}