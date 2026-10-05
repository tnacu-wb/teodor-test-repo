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
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import uk.co.whitbread.contentservice.roomtypes.client.feign.AEMFeignClientCookiePolicies;
import uk.co.whitbread.contentservice.roomtypes.config.properties.FeignClientProperties;
import uk.co.whitbread.contentservice.roomtypes.config.properties.FeignProperties;
import uk.co.whitbread.contentservice.roomtypes.exception.NoCookiePoliciesDataFoundException;
import uk.co.whitbread.contentservice.roomtypes.model.BrandCode;
import uk.co.whitbread.contentservice.roomtypes.model.CountryCode;
import uk.co.whitbread.contentservice.roomtypes.model.LanguageCode;
import uk.co.whitbread.contentservice.roomtypes.model.SubBrandCode;
import uk.co.whitbread.contentservice.roomtypes.model.aem.AEMCookiePolicies;
import uk.co.whitbread.contentservice.roomtypes.model.aem.AEMCookiePoliciesResponse;

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
@MockitoSettings(strictness = Strictness.LENIENT)
public class AEMCookiePoliciesServiceTest {

    @InjectMocks
    private AEMCookiePoliciesService target;

    @Mock
    private AEMFeignClientCookiePolicies aemFeignClientCookiePolicies;

    @Mock
    private FeignProperties feignProperties;

    @Mock
    private FeignClientProperties aemFeignClientProperties;

    private ObjectMapper objectMapper;

    private static String CONTENT_SERVICE_COOKIES_POLICY = "/content-services/cookies-policies";

    @BeforeEach
    public void setup() {
        when(feignProperties.getAem())
                .thenReturn(aemFeignClientProperties);

        when(aemFeignClientProperties.getCookiePoliciesResource())
                .thenReturn(CONTENT_SERVICE_COOKIES_POLICY);

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
    public void getAemCookiePoliciesSuccessResponse() throws URISyntaxException, IOException {
        final Path path = Paths.get(getClass().getResource("/mocks/aem_cookie_policies.json").toURI());
        final byte[] bytes = Files.readAllBytes(path);
        AEMCookiePoliciesResponse aemCookiePoliciesResponse = objectMapper.readValue(bytes, AEMCookiePoliciesResponse.class);
        when(aemFeignClientCookiePolicies.getCookiePolicies(CountryCode.gb.name(), LanguageCode.en.name(), BrandCode.pi.name(), CONTENT_SERVICE_COOKIES_POLICY))
                .thenReturn(aemCookiePoliciesResponse);

        final List<AEMCookiePolicies> result = target.getCookiePolicies(LanguageCode.en.name(), BrandCode.pi.name(), SubBrandCode.none.name());
        assertThat(result.get(0).getVersion().getValue()).isEqualTo("1");
        assertThat(result.get(0).getBrand().getValue()).isEqualTo(BrandCode.pi.name());
        assertThat(result.get(0).getIntroViewTitle().getValue()).isEqualTo("Cookie Settings Title");
        assertThat(result.get(0).getIntroViewDescription().getValue()).isEqualTo("<p><a href=\"https://sourcedcode.com\">introView</a>: Lorem ipsum dolor sit amet, consectetur adipiscing elit. Ut nec consequat neque. In pretium lacus a tortor facilisis mattis. Vivamus sed facilisis odio. Maecenas venenatis <b>pellentesque</b> velit, non convallis purus placerat id. Etiam sollicitudin sapien odio, vitae accumsan est venenatis nec. Sed quis aliquet nisl. Morbi interdum ullamcorper malesuada. Curabitur eu libero sed dui volutpat bibendum</p>\n");
        assertThat(result.get(0).getManageViewTitle().getValue()).isEqualTo("Manage View Testing Now");
        assertThat(result.get(0).getManageViewDescription().getValue()).isEqualTo("<p>introView: <b>Lorem</b> ipsum dolor sit amet, consectetur adipiscing elit. Ut nec consequat neque. In pretium</p>\n");
        assertThat(result.get(0).getManageViewAlwaysActiveText().getValue()).isEqualTo("Manage View Active Test");
        assertThat(result.get(0).getManageViewSaveSettingsButtonText().getValue()).isEqualTo("Manage Saving Button");
        assertThat(result.get(2).getCookieName().getValue()).isEqualTo("dtm.adobe.analytics");
        assertThat(result.get(2).getTitle().getValue()).isEqualTo("Analytics Cookies");
        assertThat(result.get(2).getDescription().getValue()).isEqualTo("<p>velit, non convallis purus placerat id. Etiam <b>sollicitudin</b> sapien odio, vitae accumsan est venenatis nec. Sed quis aliquet nisl. Morbi interdum <b>ullamcorper</b> malesuada. Curabitur eu libero sed dui volutpat <a href=\"https://premierinn.co.uk\">bibendum</a></p>\n");
        assertThat(result.get(2).getToggleLabel().getValue()).isEqualTo("Toggle Label");
    }

    @Test
    public void getAemCookiePoliciesSuccessResponse_defaultPIFallBack() throws URISyntaxException, IOException {
        final Path path = Paths.get(getClass().getResource("/mocks/aem_cookie_policies.json").toURI());
        final byte[] bytes = Files.readAllBytes(path);
        when(aemFeignClientCookiePolicies.getCookiePolicies(CountryCode.gb.name(), LanguageCode.en.name(), "random", CONTENT_SERVICE_COOKIES_POLICY))
                .thenReturn(null);
        AEMCookiePoliciesResponse aemCookiePoliciesResponse = objectMapper.readValue(bytes, AEMCookiePoliciesResponse.class);
        when(aemFeignClientCookiePolicies.getCookiePolicies(CountryCode.gb.name(), LanguageCode.en.name(), BrandCode.pi.name(), CONTENT_SERVICE_COOKIES_POLICY))
                .thenReturn(aemCookiePoliciesResponse);

        final List<AEMCookiePolicies> result = target.getCookiePolicies(LanguageCode.en.name(), "random", SubBrandCode.none.name());
        assertThat(result.get(0).getVersion().getValue()).isEqualTo("1");
        assertThat(result.get(0).getBrand().getValue()).isEqualTo(BrandCode.pi.name());
    }

    @Test
    public void getAemCookiePoliciesSuccessResponse_subBrand() throws URISyntaxException, IOException {
        final Path path = Paths.get(getClass().getResource("/mocks/aem_cookie_policies_restaurant_beefeater.json").toURI());
        final byte[] bytes = Files.readAllBytes(path);
        AEMCookiePoliciesResponse aemCookiePoliciesResponse = objectMapper.readValue(bytes, AEMCookiePoliciesResponse.class);
        when(aemFeignClientCookiePolicies.getCookiePolicies(CountryCode.gb.name(), LanguageCode.en.name(), BrandCode.restaurant.name(), SubBrandCode.beefeater.name(), CONTENT_SERVICE_COOKIES_POLICY))
                .thenReturn(aemCookiePoliciesResponse);

        final List<AEMCookiePolicies> result = target.getCookiePolicies(LanguageCode.en.name(), BrandCode.restaurant.name(), SubBrandCode.beefeater.name());
        assertThat(result.get(0).getVersion().getValue()).isEqualTo("1");
        assertThat(result.get(0).getBrand().getValue()).isEqualTo(BrandCode.restaurant.name());
        assertThat(result.get(0).getIntroViewTitle().getValue()).isEqualTo("Beefeater Cookie Settings Title");
        assertThat(result.get(0).getIntroViewDescription().getValue()).isEqualTo("<p>introView: Lorem ipsum dolor sit amet, consectetur adipiscing elit. Ut nec consequat neque. In pretium lacus a tortor facilisis mattis. Vivamus sed facilisis odio. Maecenas venenatis pellentesque velit, non convallis purus placerat id. Etiam sollicitudin sapien odio, vitae accumsan est venenatis nec. Sed quis aliquet nisl. Morbi interdum ullamcorper malesuada. Curabitur eu libero sed dui volutpat bibendum</p>\n");
        assertThat(result.get(0).getManageViewTitle().getValue()).isEqualTo("Beefeater Title");
        assertThat(result.get(0).getManageViewDescription().getValue()).isEqualTo("<p>lorem <b>ipsum </b><a href=\"https://google.com\">hello</a></p>\n");
        assertThat(result.get(0).getManageViewAlwaysActiveText().getValue()).isEqualTo("Beefeater Always Active");
        assertThat(result.get(0).getManageViewSaveSettingsButtonText().getValue()).isEqualTo("Beefeater Button Text");
        assertThat(result.get(1).getCookieName().getValue()).isEqualTo("beefeater.dtm.adobe.functional");
        assertThat(result.get(1).getTitle().getValue()).isEqualTo("Beefeater Functional Cookie");
        assertThat(result.get(1).getDescription().getValue()).isEqualTo("<p>Beefeater Lorem ipsum dolor sit amet, consectetur adipiscing elit. Ut nec consequat neque. In pretium lacus a tortor facilisis mattis. Vivamus sed <b>facilisis</b> odio. Maecenas venenatis pellentesque velit, non convallis purus placerat id. Etiam sollicitudin sapien odio, vitae accumsan est venenatis nec.</p>");
        assertThat(result.get(1).getToggleLabel().getValue()).isEqualTo("Beefeater Toggle Label");
    }

    @Test
    public void getAemCookiePoliciesSuccessResponse_subBrand_toBrandFallBack() throws URISyntaxException, IOException {
        final Path path = Paths.get(getClass().getResource("/mocks/aem_cookie_policies_restaurant.json").toURI());
        final byte[] bytes = Files.readAllBytes(path);
        AEMCookiePoliciesResponse aemCookiePoliciesResponse = objectMapper.readValue(bytes, AEMCookiePoliciesResponse.class);

        when(aemFeignClientCookiePolicies.getCookiePolicies(CountryCode.gb.name(), LanguageCode.en.name(), BrandCode.restaurant.name(), CONTENT_SERVICE_COOKIES_POLICY))
                .thenReturn(aemCookiePoliciesResponse);

        final List<AEMCookiePolicies> result = target.getCookiePolicies(LanguageCode.en.name(), BrandCode.restaurant.name(), SubBrandCode.beefeater.name());
        assertThat(result.get(0).getVersion().getValue()).isEqualTo("1");
        assertThat(result.get(0).getBrand().getValue()).isEqualTo(BrandCode.restaurant.name());
        assertThat(result.get(0).getIntroViewTitle().getValue()).isEqualTo("Restaurants Cookie Settings Title");
        assertThat(result.get(0).getIntroViewDescription().getValue()).isEqualTo("<p>Restaurants generic description</p>\n");
        assertThat(result.get(0).getManageViewTitle().getValue()).isEqualTo("Restaurants  Manage View");
        assertThat(result.get(0).getManageViewDescription().getValue()).isEqualTo("<p>restaurants lorem <b>ipsum </b><a href=\"https://google.com\">hello</a></p>\n");
        assertThat(result.get(0).getManageViewAlwaysActiveText().getValue()).isEqualTo("Restaurants  Active Text");
        assertThat(result.get(0).getManageViewSaveSettingsButtonText().getValue()).isEqualTo("Restaurants Settings Save");
        assertThat(result.get(1).getCookieName().getValue()).isEqualTo("restaurant.dtm.adobe.functional");
        assertThat(result.get(1).getTitle().getValue()).isEqualTo("Restaurants Functional Cookie");
        assertThat(result.get(1).getDescription().getValue()).isEqualTo("<p>Restaurant generic <b>facilisis</b> odio. Maecenas venenatis pellentesque velit, non convallis purus placerat id. Etiam sollicitudin sapien odio, vitae accumsan est venenatis nec.</p>");
        assertThat(result.get(1).getToggleLabel().getValue()).isEqualTo("Toggle Label");
    }

    @Test
    public void getCookiePoliciesNoDataFound() {
        final AEMCookiePoliciesResponse emptyResponse = AEMCookiePoliciesResponse.builder().build();
        when(aemFeignClientCookiePolicies.getCookiePolicies(CountryCode.gb.name(), LanguageCode.en.name(), BrandCode.pi.name(), CONTENT_SERVICE_COOKIES_POLICY))
                .thenReturn(emptyResponse);
        assertThrows(NoCookiePoliciesDataFoundException.class,
            () -> target.getCookiePolicies(LanguageCode.en.name(), BrandCode.pi.name(), SubBrandCode.none.name()));
    }

    @Test
    public void getCookiePoliciesNoDataFound_SubBrand() {
        final AEMCookiePoliciesResponse emptyResponse = AEMCookiePoliciesResponse.builder().build();
        assertThrows(NoCookiePoliciesDataFoundException.class,
            () -> target.getCookiePolicies(LanguageCode.en.name(), BrandCode.restaurant.name(), SubBrandCode.beefeater.name()));
    }
}
