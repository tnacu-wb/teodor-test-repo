package uk.co.whitbread;

import static org.mockito.ArgumentMatchers.any;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.wiremock.spring.ConfigureWireMock;
import org.wiremock.spring.EnableWireMock;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.context.WebApplicationContext;
import uk.co.whitbread.domain.ports.secondary.CacheSearchOutPort;
import uk.co.whitbread.domain.ports.secondary.OnSaleFlagOutPort;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelStatusDto;
import uk.co.whitbread.shared.auth.account.Account;
import uk.co.whitbread.shared.auth.security.model.CustomJwtAuthenticationToken;

@SpringBootTest(classes = HotelEntityServiceApplication.class)
@EnableWireMock(@ConfigureWireMock(port = 0, filesUnderClasspath = "stubs"))
public abstract class ContractVerifierBaseTest {

  @Autowired
  private WebApplicationContext webApplicationContext;

  @Autowired
  private OnSaleFlagOutPort onSaleFlagOutPortImpl;

  private static final String DATE_PATTERN = "yyyy-MM-dd";

  @BeforeEach
  public void setUp() {
    var hotels = List.of("HEAPTI","LONKIN","LONTLW","LONOLD");
    List<HotelStatusDto> availableHotels = new ArrayList<>();
    for (String hotel : hotels) {
      var newHotel = new HotelStatusDto();
      newHotel.setHotelId(hotel);
      newHotel.setOnSale(true);
      newHotel.setPmsSource("OPERA");
      availableHotels.add(newHotel);
    }
    var cacheSearchOutPort = Mockito.mock(CacheSearchOutPort.class);
    Mockito.when(cacheSearchOutPort.getOnsaleFlagFromCache(any())).thenReturn(availableHotels);

    org.springframework.test.util.ReflectionTestUtils.setField(
        onSaleFlagOutPortImpl,
        "cacheSearchOutPort",
        cacheSearchOutPort
    );

    //TODO:: [DNRQ-47272] see if its worth adding the filters to the web context as in the below sample
    // ### OPTION 1 ### - need to mock jwk request and manage jwt decoding
    //    Collection<Filter> filterCollection = webApplicationContext.getBeansOfType(Filter.class).values();
    //    Filter[] filters = filterCollection.toArray(new Filter[filterCollection.size()]);
    //    RestAssuredMockMvc.webAppContextSetup(webApplicationContext, new MockMvcConfigurer() {
    //      @Override
    //      public RequestPostProcessor beforeMockMvcCreated(ConfigurableMockMvcBuilder<?> builder,
    //          WebApplicationContext context) {
    //        builder.addFilters(filters);
    //        return MockMvcConfigurer.super.beforeMockMvcCreated(builder, context);
    //      }
    //    });
    // ### OPTION 2 ### - hardcoded principal for all test runs
    RestAssuredMockMvc.webAppContextSetup(webApplicationContext);
    RestAssuredMockMvc.authentication = mockMvcRequestSpecification -> {
      var authentication = new CustomJwtAuthenticationToken(Mockito.mock(Jwt.class), Account.builder()
          .bartId("187")
          .operaCompanyId("7765828")
          .companyId("COMP_b9c604fe-f5b7-49fc-bb24-60fb93583687")
          .accessLevel("SELF")
          .email("bselfbooker@mailinator.com")
          .customerId("79")
          .employeeId("EMPL_6fe7ac40-01ed-4393-8772-393c13b064ac")
          .build());
      authentication.setAuthenticated(true);
      mockMvcRequestSpecification.auth()
          .authentication(authentication);
    };
  }

  public static String getCurrentDatePlusDays(long plusDays) {
    return LocalDate.now().plusDays(plusDays).format(DateTimeFormatter.ofPattern(DATE_PATTERN));
  }

}