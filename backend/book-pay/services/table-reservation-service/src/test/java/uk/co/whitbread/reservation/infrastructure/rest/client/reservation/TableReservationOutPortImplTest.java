package uk.co.whitbread.reservation.infrastructure.rest.client.reservation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.mockito.Answers;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.web.client.RestClient;
import uk.co.whitbread.reservation.domain.model.out.enquiry.EnquiryResponse;
import uk.co.whitbread.reservation.domain.model.out.enquiry.EventOrEquiryRequest;
import uk.co.whitbread.reservation.domain.model.out.events.EventsResponse;
import uk.co.whitbread.reservation.domain.model.out.events.Menus;
import uk.co.whitbread.reservation.domain.model.out.events.OrderMenu;
import uk.co.whitbread.reservation.domain.model.out.menu.MenuResp;
import uk.co.whitbread.reservation.domain.model.out.menu.MenuResponse;
import uk.co.whitbread.reservation.domain.model.out.occasion.Occasions;
import uk.co.whitbread.reservation.domain.model.out.occasion.OccasionsResponse;
import uk.co.whitbread.reservation.domain.model.out.outlets.Company;
import uk.co.whitbread.reservation.domain.model.out.outlets.Consent;
import uk.co.whitbread.reservation.domain.model.out.outlets.ConsentStatement;
import uk.co.whitbread.reservation.domain.model.out.outlets.OutletResponse;
import uk.co.whitbread.reservation.domain.model.out.outlets.Site;
import uk.co.whitbread.reservation.domain.model.out.slots.Dates;
import uk.co.whitbread.reservation.domain.model.out.slots.SessionResponse;
import uk.co.whitbread.reservation.domain.model.out.slots.SlotsResponse;
import uk.co.whitbread.reservation.domain.model.out.slots.Times;
import uk.co.whitbread.reservation.infrastructure.config.ZonalConfigurationProperties;
import uk.co.whitbread.reservation.infrastructure.rest.client.reservation.exception.ResponseParsingException;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TableReservationOutPortImplTest {

  private static final String BASE_URI = "https://api-staging.liveres.co.uk/events/v1";
  private static final String BASE_URI_PROD = "https://api.liveres.co.uk/events/v1";
  private static final String CHECK_URI = "/check";
  private static final String SLOTS_URI = "/slot";
  private static final String USERNAME = "9668dca3-aa8a-4144-a3ec-021d87ad9a38";
  private static final String PASSWORD = "%ioG^Tv2h#%mMc9Np@bV7z2cZo$g";
  private static OutletResponse outletResponse;

  private TableReservationOutPortImpl tableReservationPortImpl;

  @Mock
  private RestClient restClient;

  private RestClient.ResponseSpec getResponseSpec;
  private RestClient.ResponseSpec postResponseSpec;

  @BeforeAll
  static void mockResponseFromFile() {
    outletResponse = mockResponseOutlet();
  }

  private static OutletResponse mockResponseOutlet() {
    OutletResponse outletResponse = null;
    try {
      InputStream inputStream = TableReservationOutPortImplTest.class.getResourceAsStream(
          "/outletResponse.json");

      if (inputStream != null) {
        ObjectMapper objectMapper = new ObjectMapper();

        outletResponse = objectMapper.readValue(inputStream, OutletResponse.class);

        System.out.println(outletResponse);

        inputStream.close();
      } else {
        System.err.println("Unable to find the specified resource.");
      }
    } catch (IOException exception) {
      System.out.println(exception.getMessage());
    }
    return outletResponse;
  }

  @BeforeEach
  void setUp() {
    ZonalConfigurationProperties config = new ZonalConfigurationProperties();
    config.setBaseUri(BASE_URI_PROD);
    config.setBaseUri(BASE_URI);
    config.setCheckUri(CHECK_URI);
    config.setSlotsUri(SLOTS_URI);
    config.setUsername(USERNAME);
    config.setPassword(PASSWORD);

    tableReservationPortImpl = new TableReservationOutPortImpl(restClient, config);

    RestClient.RequestHeadersUriSpec<?> getUriSpec = mock(RestClient.RequestHeadersUriSpec.class);
    RestClient.RequestHeadersSpec<?> getHeadersSpec = mock(RestClient.RequestHeadersSpec.class);
    getResponseSpec = mock(RestClient.ResponseSpec.class);

    doReturn(getUriSpec).when(restClient).get();
    doReturn(getHeadersSpec).when(getUriSpec).uri(anyString());
    when(getHeadersSpec.retrieve()).thenReturn(getResponseSpec);

    RestClient.RequestBodyUriSpec postUriSpec =
        mock(RestClient.RequestBodyUriSpec.class, Answers.RETURNS_SELF);
    postResponseSpec = mock(RestClient.ResponseSpec.class);

    doReturn(postUriSpec).when(restClient).post();
    when(postUriSpec.retrieve()).thenReturn(postResponseSpec);
  }

  @Test
  void slots_WhenFromDinner_returnDinnerList() {
    SlotsResponse slotsResponse = new SlotsResponse();
    ArrayList<Dates> dates = new ArrayList<>();
    Dates dinner = new Dates();
    dinner.setDate("2024-03-29");
    List<Times> dinnerTimesList = new ArrayList<>();
    Times dinnerTimes = new Times();
    dinnerTimes.setTotalCapacity(20);
    dinnerTimes.setRemainingCapacity(10);
    dinnerTimes.setTime("20:30");
    dinnerTimesList.add(dinnerTimes);
    dinnerTimes.setAvailable(true);
    dinner.setTimes(dinnerTimesList);
    dates.add(dinner);
    slotsResponse.setDates(dates);

    when(getResponseSpec.body(SlotsResponse.class)).thenReturn(slotsResponse);

    SessionResponse response = tableReservationPortImpl.slots(null, null, null, null, null, null);
    assertTrue(response.getDates().getFirst().isDinnerAvailable());
  }

  @Test
  void slotsWhenTimeNull() {
    String from = "2023-08-19";
    String until = "2023-08-20";
    String adult = "4";
    String children = "2";
    String siteId = "123";

    SlotsResponse slotsResponse = new SlotsResponse();
    ArrayList<Dates> dates = new ArrayList<>();
    Dates lunch = new Dates();
    lunch.setDate("2024-03-29");
    List<Times> lunchTimesList = new ArrayList<>();
    Times lunchTimes = new Times();
    lunchTimes.setTotalCapacity(20);
    lunchTimes.setRemainingCapacity(10);
    lunchTimes.setTime("14:30");
    lunchTimesList.add(lunchTimes);
    lunchTimes.setAvailable(true);
    lunch.setTimes(lunchTimesList);
    dates.add(lunch);
    slotsResponse.setDates(dates);

    when(getResponseSpec.body(SlotsResponse.class)).thenReturn(slotsResponse);

    SessionResponse response = tableReservationPortImpl.slots(from, until, null, adult, children, siteId);
    assertNotNull(response);
    assertTrue(response.getDates().getFirst().isLunchAvailable());
  }

  @Test
  void slotsWhenException_throwException() {
    when(getResponseSpec.body(SlotsResponse.class)).thenThrow(ResponseParsingException.class);
    assertThrows(ResponseParsingException.class,
        () -> tableReservationPortImpl.slots("2023-08-19", "2023-08-20", null, "4", "2", "123"));
  }

  @Test
  void outletsForPreProd() {
    OutletResponse mockOutlet = mockOutletResponse();
    mockOutlet.getCompanies().getFirst().getConsent().getPrivacyStatement().setUrl("beefeater");
    mockOutlet.getCompanies().getFirst().getSites().getFirst().setName("Whitbread Demo");
    mockOutlet.getCompanies().getFirst().getSites().getFirst().setAztecSiteReference("id");

    when(getResponseSpec.body(OutletResponse.class)).thenReturn(mockOutlet);

    OutletResponse outlets = tableReservationPortImpl.outlets("Bengaluru", "id");
    assertNotNull(outlets);
    assertEquals("NAME1", outlets.getCompanies().get(0).getName());
    assertEquals("termsAndConditions", outlets.getCompanies().get(0).getTermsAndConditions());
    assertEquals("1", outlets.getCompanies().get(0).getId());
  }

  @Test
  void outletsForConflictSitesForParticularBrand() {
    OutletResponse mockOutlet = mockOutletForConflictResponse();
    when(getResponseSpec.body(OutletResponse.class)).thenReturn(mockOutlet);
    OutletResponse outlets = tableReservationPortImpl.outlets("Bengaluru", "id001");
    assertNotNull(outlets);
    assertEquals("NAME1", outlets.getCompanies().getFirst().getName());
    assertEquals("termsAndConditions", outlets.getCompanies().getFirst().getTermsAndConditions());
    assertEquals("1", outlets.getCompanies().getFirst().getId());
    assertEquals("id001", outlets.getCompanies().getFirst().getSites().getFirst().getAztecSiteReference());
  }

  @ParameterizedTest
  @CsvSource({
      "41018510, Brewers Fayre, 0ba882eb-60d2-4aea-9989-00c44ea92161",
      "41511055, Table Table, 8c34b30e-d017-444f-b257-0a1ef61314a2",
      "41010770, Whitbread Inns, 600844dd-463c-4ff9-be64-24e4ff6f4640",
      "40537840, Cookhouse + Pub, 8fbc413f-6eed-4fcf-ad0f-077aef541037"
  })
  void outletsForMappedBrands(String id, String expectedName, String expectedSiteId) {
    when(getResponseSpec.body(OutletResponse.class)).thenReturn(outletResponse);
    OutletResponse outlets = tableReservationPortImpl.outlets("", id);
    assertNotNull(outlets);
    assertEquals(expectedName, outlets.getCompanies().getFirst().getName());
    assertEquals("http://bookings.liveres.co.uk/tc.html",
        outlets.getCompanies().getFirst().getTermsAndConditions());
    assertEquals(expectedSiteId, outlets.getCompanies().getFirst().getSites().getFirst().getId());
  }

  @Test
  void outletsForBarBlockSteakhouse() {
    when(getResponseSpec.body(OutletResponse.class)).thenReturn(outletResponse);
    OutletResponse outlets = tableReservationPortImpl.outlets("", "40538460");
    assertNotNull(outlets);
    assertNotNull(outlets.getCompanies());
    assertEquals(1, outlets.getCompanies().size());
    assertNotNull(outlets.getCompanies().get(0).getSites());
    assertEquals(1, outlets.getCompanies().get(0).getSites().size());
    assertNotNull(outlets.getCompanies().get(0).getName());
    assertNotNull(outlets.getCompanies().get(0).getTermsAndConditions());
    assertNotNull(outlets.getCompanies().get(0).getSites().get(0).getId());
    assertNotNull(outlets.getCompanies().get(0).getSites().get(0).getAztecSiteReference());
    assertNotNull(outlets.getCompanies().get(0).getSites().get(0).getName());
    assertNotNull(outlets.getCompanies().get(0).getSites().get(0).getTimezone());
    assertNotNull(outlets.getCompanies().get(0).getSites().get(0).getFeatures());
    assertEquals("40538460", outlets.getCompanies().getFirst().getSites().getFirst().getAztecSiteReference());
    assertEquals("Bar + Block Steakhouse", outlets.getCompanies().get(0).getName());
    assertEquals("http://bookings.liveres.co.uk/tc.html",
        outlets.getCompanies().get(0).getTermsAndConditions());
    assertEquals("9b862210-a0cc-44a4-8063-1d74c18e554f",
        outlets.getCompanies().get(0).getSites().get(0).getId());
  }

  @Test
  void outletsForBeefeaterBarBlock() {
    when(getResponseSpec.body(OutletResponse.class)).thenReturn(outletResponse);
    OutletResponse outlets = tableReservationPortImpl.outlets("", "40537270");
    assertNotNull(outlets);
    assertEquals("Beefeater Bar + Block", outlets.getCompanies().get(0).getName());
    assertEquals("http://bookings.liveres.co.uk/tc.html",
        outlets.getCompanies().get(0).getTermsAndConditions());
    assertEquals("614b5c86-79f2-497c-b262-9507225c2349",
        outlets.getCompanies().get(0).getSites().get(0).getId());
  }

  @Test
  void outletsForBeefeater() {
    when(getResponseSpec.body(OutletResponse.class)).thenReturn(outletResponse);
    OutletResponse outlets = tableReservationPortImpl.outlets("", "40010500");
    assertNotNull(outlets);
    assertEquals("Beefeater", outlets.getCompanies().get(0).getName());
    assertEquals("http://bookings.liveres.co.uk/tc.html",
        outlets.getCompanies().get(0).getTermsAndConditions());
    assertEquals("faf6d4b9-7ae9-4c00-aab9-3b5c939079b0", outlets.getCompanies().get(0).getId());
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {"400105001"})
  void outletsForBeefeaterInvalidIds(String id) {
    when(getResponseSpec.body(OutletResponse.class)).thenReturn(outletResponse);
    OutletResponse outlets = tableReservationPortImpl.outlets("", id);
    assertNull(outlets.getCompanies());
  }

  @Test
  void outlets() {
    when(getResponseSpec.body(OutletResponse.class)).thenReturn(outletResponse);
    OutletResponse outlets = tableReservationPortImpl.outlets("Bengaluru", "id");
    assertNotNull(outlets);
  }

  @Test
  void events() {
    EventOrEquiryRequest eventRequest = requestBody();
    EventsResponse eventsResponse = mockEventsResponse();
    when(postResponseSpec.body(EventsResponse.class)).thenReturn(eventsResponse);
    EventsResponse response = tableReservationPortImpl.events(eventRequest);
    assertNotNull(response);
  }

  @Test
  void eventsWithInvalidFirstname() {
    EventOrEquiryRequest eventRequest = new EventOrEquiryRequest();
    eventRequest.setAdults(1);
    eventRequest.setTime("18:80");
    eventRequest.setDate("2023-08-20");
    eventRequest.setFirstname("X123");
    assertThrows(IllegalArgumentException.class, () -> tableReservationPortImpl.events(eventRequest));
  }

  @Test
  void eventsWithInvalidLastname() {
    EventOrEquiryRequest eventRequest = new EventOrEquiryRequest();
    eventRequest.setAdults(1);
    eventRequest.setTime("18:80");
    eventRequest.setDate("2023-08-20");
    eventRequest.setFirstname("Xyz");
    eventRequest.setLastname("Z123");
    assertThrows(IllegalArgumentException.class, () -> tableReservationPortImpl.events(eventRequest));
  }

  @Test
  void eventsWithInvalidPhoneNumber() {
    EventOrEquiryRequest eventRequest = new EventOrEquiryRequest();
    eventRequest.setAdults(1);
    eventRequest.setTime("18:80");
    eventRequest.setDate("2023-08-20");
    eventRequest.setFirstname("Xyz");
    eventRequest.setLastname("Zxy");
    eventRequest.setTelephoneNumber("12345a");
    eventRequest.setEmailAddress("abc@gmail.comzzz");
    assertThrows(IllegalArgumentException.class, () -> tableReservationPortImpl.events(eventRequest));
  }

  @Test
  void eventsWithInvalidEmailAddress() {
    EventOrEquiryRequest eventRequest = new EventOrEquiryRequest();
    eventRequest.setAdults(1);
    eventRequest.setTime("18:80");
    eventRequest.setDate("2023-08-20");
    eventRequest.setFirstname("Xyz");
    eventRequest.setLastname("Zxy");
    eventRequest.setTelephoneNumber("12345678901");
    eventRequest.setEmailAddress("abc@gmail.comzzz");
    assertThrows(IllegalArgumentException.class, () -> tableReservationPortImpl.events(eventRequest));
  }

  @Test
  void eventsWithInvalidAdults() {
    EventOrEquiryRequest eventRequest = new EventOrEquiryRequest();
    eventRequest.setTime("18:80");
    eventRequest.setDate("2023-08-20");
    eventRequest.setFirstname("Xyz");
    eventRequest.setLastname("Zxy");
    eventRequest.setTelephoneNumber("12345678901");
    eventRequest.setEmailAddress("abc@gmail.com");
    eventRequest.setAdults(-1);
    assertThrows(IllegalArgumentException.class, () -> tableReservationPortImpl.events(eventRequest));
  }

  @Test
  void eventsWithInvalidChildren() {
    EventOrEquiryRequest eventRequest = new EventOrEquiryRequest();
    eventRequest.setTime("18:80");
    eventRequest.setDate("2023-08-20");
    eventRequest.setFirstname("Xyz");
    eventRequest.setLastname("Zxy");
    eventRequest.setTelephoneNumber("12345678901");
    eventRequest.setEmailAddress("abc@gmail.com");
    eventRequest.setChildren(-1);
    assertThrows(IllegalArgumentException.class, () -> tableReservationPortImpl.events(eventRequest));
  }

  @Test
  void eventsById() {
    EventsResponse eventsResponse = mockEventsResponse();
    when(getResponseSpec.body(EventsResponse.class)).thenReturn(eventsResponse);
    EventsResponse response = tableReservationPortImpl.getEvent("12");
    assertNotNull(response);
  }

  @Test
  void enquiry() {
    EventOrEquiryRequest eventOrEquiryRequest = enquiryRequest();
    EnquiryResponse enquiryResponse = mockEnquiryResponse();
    when(postResponseSpec.body(EnquiryResponse.class)).thenReturn(enquiryResponse);
    EnquiryResponse response = tableReservationPortImpl.createEnquiry(eventOrEquiryRequest);
    assertNotNull(response);
  }

  @Test
  void enquiryWithInvalidFirstname() {
    EventOrEquiryRequest eventOrEquiryRequest = new EventOrEquiryRequest();
    eventOrEquiryRequest.setAdults(1);
    eventOrEquiryRequest.setTime("18:80");
    eventOrEquiryRequest.setDate("2023-08-20");
    eventOrEquiryRequest.setFirstname("X123");
    assertThrows(IllegalArgumentException.class,
        () -> tableReservationPortImpl.createEnquiry(eventOrEquiryRequest));
  }

  @Test
  void enquiryWithInvalidLastname() {
    EventOrEquiryRequest eventOrEquiryRequest = new EventOrEquiryRequest();
    eventOrEquiryRequest.setAdults(1);
    eventOrEquiryRequest.setTime("18:80");
    eventOrEquiryRequest.setDate("2023-08-20");
    eventOrEquiryRequest.setFirstname("Xyz");
    eventOrEquiryRequest.setLastname("Z");
    assertThrows(IllegalArgumentException.class,
        () -> tableReservationPortImpl.createEnquiry(eventOrEquiryRequest));
  }

  @Test
  void enquiryWithInvalidPhoneNumber() {
    EventOrEquiryRequest eventOrEquiryRequest = new EventOrEquiryRequest();
    eventOrEquiryRequest.setAdults(1);
    eventOrEquiryRequest.setTime("18:80");
    eventOrEquiryRequest.setDate("2023-08-20");
    eventOrEquiryRequest.setFirstname("Xyz");
    eventOrEquiryRequest.setLastname("Zxy");
    eventOrEquiryRequest.setTelephoneNumber("12345a");
    assertThrows(IllegalArgumentException.class,
        () -> tableReservationPortImpl.createEnquiry(eventOrEquiryRequest));
  }

  @Test
  void enquiryWithInvalidEmailAddress() {
    EventOrEquiryRequest eventOrEquiryRequest = new EventOrEquiryRequest();
    eventOrEquiryRequest.setAdults(1);
    eventOrEquiryRequest.setTime("18:80");
    eventOrEquiryRequest.setDate("2023-08-20");
    eventOrEquiryRequest.setFirstname("Xyz");
    eventOrEquiryRequest.setLastname("Zxy");
    eventOrEquiryRequest.setTelephoneNumber("12345678901");
    eventOrEquiryRequest.setEmailAddress("abc@gmail.comzzz");
    assertThrows(IllegalArgumentException.class,
        () -> tableReservationPortImpl.createEnquiry(eventOrEquiryRequest));
  }

  @Test
  void enquiryWithInvalidAdults() {
    EventOrEquiryRequest eventOrEquiryRequest = new EventOrEquiryRequest();
    eventOrEquiryRequest.setAdults(-1);
    eventOrEquiryRequest.setTime("18:80");
    eventOrEquiryRequest.setDate("2023-08-20");
    eventOrEquiryRequest.setFirstname("Xyz");
    eventOrEquiryRequest.setLastname("Zxy");
    eventOrEquiryRequest.setTelephoneNumber("12345678901");
    eventOrEquiryRequest.setEmailAddress("abc@gmail.com");
    assertThrows(IllegalArgumentException.class,
        () -> tableReservationPortImpl.createEnquiry(eventOrEquiryRequest));
  }

  @Test
  void enquiryWithInvalidChildren() {
    EventOrEquiryRequest eventOrEquiryRequest = new EventOrEquiryRequest();
    eventOrEquiryRequest.setChildren(-1);
    eventOrEquiryRequest.setTime("18:80");
    eventOrEquiryRequest.setDate("2023-08-20");
    eventOrEquiryRequest.setFirstname("Xyz");
    eventOrEquiryRequest.setLastname("Zxy");
    eventOrEquiryRequest.setTelephoneNumber("12345678901");
    eventOrEquiryRequest.setEmailAddress("abc@gmail.com");
    assertThrows(IllegalArgumentException.class,
        () -> tableReservationPortImpl.createEnquiry(eventOrEquiryRequest));
  }

  @Test
  void enquiryById() {
    EnquiryResponse enquiryResponse = mockEnquiryResponse();
    when(getResponseSpec.body(EnquiryResponse.class)).thenReturn(enquiryResponse);
    EnquiryResponse response = tableReservationPortImpl.getEnquiry("12");
    assertNotNull(response);
  }

  @Test
  void occasion_whenValidRequestGiven_returnWithSuccess() {
    OccasionsResponse occasionsResponse = mockOccasionsResponse();
    when(getResponseSpec.body(OccasionsResponse.class)).thenReturn(occasionsResponse);
    OccasionsResponse response = tableReservationPortImpl.occasions("2023-08-19", "2023-08-20", "123");
    assertNotNull(response);
    assertEquals(true, response.getOccasions().get(0).getAvailable());
  }

  @Test
  void occasion_whenValidRequestOnlySiteIdAndRestNullGiven_returnWithSuccess() {
    OccasionsResponse occasionsResponse = mockOccasionsResponse();
    when(getResponseSpec.body(OccasionsResponse.class)).thenReturn(occasionsResponse);
    OccasionsResponse response = tableReservationPortImpl.occasions(null, null, "123");
    assertNotNull(response);
    assertEquals(true, response.getOccasions().get(0).getAvailable());
  }

  @Test
  void occasion_whenValidRequestOnlySiteIdAndRestEmptyGiven_returnWithSuccess() {
    OccasionsResponse occasionsResponse = mockOccasionsResponse();
    when(getResponseSpec.body(OccasionsResponse.class)).thenReturn(occasionsResponse);
    OccasionsResponse response = tableReservationPortImpl.occasions("", "", "123");
    assertNotNull(response);
    assertEquals(true, response.getOccasions().get(0).getAvailable());
  }

  @Test
  void occasion_whenValidRequestGiven_IsNull() {
    OccasionsResponse occasionsResponse = mockOccasionsResponse();
    when(getResponseSpec.body(OccasionsResponse.class)).thenReturn(occasionsResponse);
    OccasionsResponse response = tableReservationPortImpl.occasions(null, null, null);
    assertNotNull(response);
  }

  @Test
  void menu_WhenMenuAvailabilityIsTrue() {
    MenuResponse menuResponse = mockMenuResponse();
    when(getResponseSpec.body(MenuResponse.class)).thenReturn(menuResponse);
    MenuResponse response = tableReservationPortImpl.getMenu("abc", "2023-09-21", "2023-09-21", "07:04", "oc1");
    assertNotNull(response);
  }

  @Test
  void menu_WhenMenuAvailabilityIsFalse() {
    MenuResponse menuResponse = mockMenuResponse1();
    when(getResponseSpec.body(MenuResponse.class)).thenReturn(menuResponse);
    MenuResponse response = tableReservationPortImpl.getMenu("abc", "2023-09-21", "2023-09-21", "07:04", "oc1");
    assertNotNull(response);
  }

  @Test
  void menu_WhenValidResponseIsGiven_IsNull() {
    MenuResponse menuResponse = mockMenuResponse();
    when(getResponseSpec.body(MenuResponse.class)).thenReturn(menuResponse);
    MenuResponse response = tableReservationPortImpl.getMenu(null, null, null, null, null);
    assertNotNull(response);
  }

  // ---- helpers ----

  private MenuResponse mockMenuResponse1() {
    MenuResponse menuResponse = new MenuResponse();
    MenuResp menuResp = new MenuResp();
    menuResp.setAvailable(false);
    menuResp.setId("23");
    menuResp.setName("abc");
    menuResponse.setMenus(List.of(menuResp));
    return menuResponse;
  }

  private MenuResponse mockMenuResponse() {
    MenuResponse menuResponse = new MenuResponse();
    MenuResp menuResp = new MenuResp();
    menuResp.setAvailable(true);
    menuResp.setId("23");
    menuResp.setName("abc");
    menuResponse.setMenus(List.of(menuResp));
    return menuResponse;
  }

  private OutletResponse mockOutletForConflictResponse() {
    OutletResponse resp = new OutletResponse();
    List<Company> companies = new ArrayList<>();

    Company company = new Company();
    company.setId("1");
    company.setName("NAME1");
    company.setTermsAndConditions("termsAndConditions");
    Consent consent = new Consent();
    ConsentStatement cs = new ConsentStatement();
    cs.setText("TEXT");
    cs.setUrl("https://www.barandblock.co.uk/privacy-cookies");
    cs.setEnabled(true);
    consent.setPrivacyStatement(cs);

    List<Site> sites1 = new ArrayList<>();
    Site site1 = new Site();
    site1.setId("1");
    site1.setName("barandblock Bar + Block");
    site1.setAztecSiteReference("id001");
    sites1.add(site1);
    company.setSites(sites1);
    company.setConsent(consent);

    Company company1 = new Company();
    company1.setId("2");
    company1.setName("NAME2");
    company1.setTermsAndConditions("termsAndConditions");
    Consent consent1 = new Consent();
    ConsentStatement cs1 = new ConsentStatement();
    cs1.setText("TEXT");
    cs1.setUrl("https://www.barandblock.co.uk/privacy-cookies");
    cs1.setEnabled(true);
    consent1.setPrivacyStatement(cs1);
    List<Site> sites2 = new ArrayList<>();
    Site site2 = new Site();
    site2.setId("1");
    site2.setName("ABC");
    site2.setAztecSiteReference("id001");
    sites2.add(site2);
    company1.setSites(sites2);
    company1.setConsent(consent1);

    companies.add(company);
    companies.add(company1);
    resp.setCompanies(companies);
    return resp;
  }

  private EventsResponse mockEventsResponse() {
    EventsResponse eventsResponse = new EventsResponse();
    eventsResponse.setAdults(1);
    eventsResponse.setId("1");
    uk.co.whitbread.reservation.domain.model.out.events.Consent consent =
        new uk.co.whitbread.reservation.domain.model.out.events.Consent();
    consent.setConsentStatement(true);
    consent.setPhone(true);
    eventsResponse.setConsent(consent);
    Menus menus = new Menus();
    menus.setId("1");
    OrderMenu orderMenu = new OrderMenu();
    orderMenu.setId(2);
    menus.setIOrderMenus(List.of(orderMenu));
    eventsResponse.setMenus(List.of(menus));
    return eventsResponse;
  }

  private EnquiryResponse mockEnquiryResponse() {
    EnquiryResponse enquiryResponse = new EnquiryResponse();
    enquiryResponse.setAdults(1);
    enquiryResponse.setId("1");
    uk.co.whitbread.reservation.domain.model.out.events.Consent consent =
        new uk.co.whitbread.reservation.domain.model.out.events.Consent();
    consent.setConsentStatement(true);
    consent.setPhone(true);
    enquiryResponse.setConsent(consent);
    return enquiryResponse;
  }

  private EventOrEquiryRequest requestBody() {
    EventOrEquiryRequest eventRequest = new EventOrEquiryRequest();
    eventRequest.setAdults(1);
    eventRequest.setTime("18:80");
    eventRequest.setDate("2023-08-20");
    eventRequest.setFirstname("Xyz");
    eventRequest.setLastname("Zxy");
    eventRequest.setEmailAddress("abc@gmail.com");
    eventRequest.setSiteId("ab");
    eventRequest.setOccasionId("OC12");
    eventRequest.setChildren(1);
    eventRequest.setConsent(new uk.co.whitbread.reservation.domain.model.out.events.Consent());
    eventRequest.setTelephoneNumber("+12345678901");
    return eventRequest;
  }

  private EventOrEquiryRequest enquiryRequest() {
    EventOrEquiryRequest request = new EventOrEquiryRequest();
    request.setAdults(1);
    request.setTime("18:80");
    request.setDate("2023-08-20");
    request.setFirstname("Xyz");
    request.setLastname("Zxy");
    request.setEmailAddress("abc@gmail.com");
    request.setSiteId("ab");
    request.setChildren(2);
    request.setOccasionId("OC12");
    request.setConsent(new uk.co.whitbread.reservation.domain.model.out.events.Consent());
    request.setTelephoneNumber("+12345678901");
    return request;
  }

  private OccasionsResponse mockOccasionsResponse() {
    OccasionsResponse response = new OccasionsResponse();
    Occasions occasions = new Occasions();
    occasions.setAvailable(true);
    occasions.setId("123");
    occasions.setName("Name");
    response.setOccasions(List.of(occasions));
    return response;
  }

  private OutletResponse mockOutletResponse() {
    OutletResponse outletResponse = new OutletResponse();
    List<Company> companies = new ArrayList<>();
    Company company = new Company();
    company.setId("1");
    company.setName("NAME1");
    company.setTermsAndConditions("termsAndConditions");
    Consent consent = new Consent();
    ConsentStatement consentStatement = new ConsentStatement();
    consentStatement.setText("TEXT");
    consentStatement.setUrl("https://www.beefeater.co.uk/privacy-cookies");
    consentStatement.setEnabled(true);
    consent.setPrivacyStatement(consentStatement);

    List<Site> sites = new ArrayList<>();
    Site site = new Site();
    site.setId("1");
    site.setName("Whitbread Demo");
    Site site1 = new Site();
    site1.setId("1");
    site1.setName("ABC");
    sites.add(site);
    sites.add(site1);
    company.setSites(sites);
    company.setConsent(consent);

    Company company1 = new Company();
    company1.setId("1");
    company1.setName("NAME2");
    company1.setTermsAndConditions("termsAndConditions");
    Consent consent1 = new Consent();
    ConsentStatement consentStatement1 = new ConsentStatement();
    consentStatement1.setText("TEXT");
    consentStatement1.setUrl("https://www.Whitbread Demo.co.uk/privacy-cookies");
    consentStatement1.setEnabled(true);
    consent1.setPrivacyStatement(consentStatement1);
    company1.setSites(sites);
    List<Site> sites1 = new ArrayList<>();
    Site site11 = new Site();
    site11.setId("1");
    site11.setName("beefeater");
    sites1.add(site11);
    company.setSites(sites1);
    company1.setConsent(consent1);

    Company company2 = new Company();
    company2.setId("1");
    company2.setName("NAME3");
    company2.setTermsAndConditions("termsAndConditions");
    Consent consent2 = new Consent();
    ConsentStatement consentStatement2 = new ConsentStatement();
    consentStatement2.setText("TEXT");
    consentStatement2.setUrl("http://www.brandABC.co");
    consentStatement2.setEnabled(true);
    consent2.setPrivacyStatement(consentStatement2);
    company2.setSites(sites);
    company2.setConsent(consent2);

    companies.add(company);
    companies.add(company1);
    companies.add(company2);
    outletResponse.setCompanies(companies);
    return outletResponse;
  }
}
