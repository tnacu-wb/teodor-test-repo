package uk.co.whitbread.reservation.domain.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import uk.co.whitbread.reservation.domain.model.out.enquiry.EnquiryResponse;
import uk.co.whitbread.reservation.domain.model.out.enquiry.EventOrEquiryRequest;
import uk.co.whitbread.reservation.domain.model.out.events.EventsResponse;
import uk.co.whitbread.reservation.domain.model.out.menu.MenuResponse;
import uk.co.whitbread.reservation.domain.model.out.menu.RequestMenu;
import uk.co.whitbread.reservation.domain.model.out.occasion.Occasions;
import uk.co.whitbread.reservation.domain.model.out.occasion.OccasionsResponse;
import uk.co.whitbread.reservation.domain.model.out.outlets.Company;
import uk.co.whitbread.reservation.domain.model.out.outlets.Consent;
import uk.co.whitbread.reservation.domain.model.out.outlets.ConsentStatement;
import uk.co.whitbread.reservation.domain.model.out.outlets.OutletResponse;
import uk.co.whitbread.reservation.domain.model.out.slots.Dates;
import uk.co.whitbread.reservation.domain.model.out.slots.Session;
import uk.co.whitbread.reservation.domain.model.out.slots.SessionDates;
import uk.co.whitbread.reservation.domain.model.out.slots.SessionResponse;
import uk.co.whitbread.reservation.domain.model.out.slots.SlotsResponse;
import uk.co.whitbread.reservation.domain.model.out.slots.Times;
import uk.co.whitbread.reservation.infrastructure.rest.client.reservation.TableReservationOutPortImpl;

@ExtendWith(MockitoExtension.class)
class TableReservationInPortImplTest {


  @InjectMocks
  private TableReservationInPortImpl tableReservationPortImpl;

  @Mock
  private TableReservationOutPortImpl tableReservationOutPort;

  @BeforeEach
  public void setUp() {
  }

  @Test
  void testCheck() {
    String mockResponseBody = "You are authorised to use Events External API";
    ResponseEntity<String> mockResponse = ResponseEntity.ok(mockResponseBody);
    when(tableReservationOutPort.check()).thenReturn(mockResponse);

    // Call the method and assert the response
    ResponseEntity<String> response = tableReservationPortImpl.check();
    assertEquals(mockResponse, response);

    // Verify that tableReservationOutPortImpl.check() was called exactly once
    verify(tableReservationOutPort, times(1)).check();

  }

  @Test
  void slots() {
    String from = "2023-08-19";
    String until = "2023-08-20";
    String time = "10:00";
    String adult = "4";
    String children = "2";
    String siteId = "123";
    SessionResponse mockSessionResponse = mockSessionResponse();

    Mockito.doReturn(mockSessionResponse).when(tableReservationOutPort).slots(
        Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
        Mockito.anyString(), Mockito.anyString()
    );
    SessionResponse response = tableReservationPortImpl.slots(from, until, time, adult, children,
        siteId);
    assertTrue(response.getDates().get(0).isDinnerAvailable());

  }

  @Test
  void slotsWhenTimeNull() throws JsonProcessingException {
    String from = "2023-08-19";
    String until = "2023-08-20";
    String adult = "4";
    String children = "2";
    String siteId = "123";
    SessionResponse mockSessionResponse = mockSessionResponse();

    Mockito.doReturn(mockSessionResponse).when(tableReservationOutPort).slots(
        Mockito.anyString(), Mockito.anyString(), Mockito.isNull(), Mockito.anyString(),
        Mockito.anyString(), Mockito.anyString()
    );
    SessionResponse response = tableReservationPortImpl.slots(from, until, null, adult, children,
        siteId);
    assertNotNull(response);
    assertTrue(response.getDates().get(0).isDinnerAvailable());
  }

  @Test
  void slotsWhenException_throwException() throws JsonProcessingException {
    when(tableReservationOutPort.slots(
        anyString(), anyString(), anyString(), anyString(), anyString(), anyString()
    )).thenThrow(new RuntimeException("Test exception"));

    // Call the method and assert that it throws an exception
    assertThrows(RuntimeException.class, () -> {
      tableReservationPortImpl.slots(
          "2023-08-19", "2023-08-20", "10:00", "4", "2", "123"
      );
    });
  }

  @Test
  void outlets() {

    String location = "exampleLocation";
    String id = "40015420";

    OutletResponse mockResponse = getOutletResponse(); // Create a mock OutletResponse object
    when(tableReservationOutPort.outlets(location, id)).thenReturn(
        mockResponse); // Mock the behavior

    OutletResponse response = tableReservationPortImpl.outlets(location, id); // Call the method

    assertNotNull(response);
    assertEquals(mockResponse.getCompanies().get(0).getName(),
        response.getCompanies().get(0).getName());
    assertEquals(3, mockResponse.getCompanies().size());
  }

  @Test
  void events() {
    EventOrEquiryRequest eventRequest = requestBody();
    EventsResponse eventsResponse = mockEventsResponse();
    when(tableReservationOutPort.events(eventRequest)).thenReturn(
        eventsResponse); // Mock the behavior
    EventsResponse response = tableReservationPortImpl.events(eventRequest);
    assertEquals(response.getAdults(), eventsResponse.getAdults());
    assertNotNull(response);
  }

  @Test
  void eventsById() {
    EventsResponse eventsResponse = mockEventsResponse();
    when(tableReservationOutPort.getEvent(Mockito.anyString())).thenReturn(eventsResponse);
    EventsResponse response = tableReservationPortImpl.getEvent("12");
    assertNotNull(response);
    assertEquals(response.getAdults(), eventsResponse.getAdults());
    assertNotNull(response);
  }

  @Test
  void enquiry() {
    EventOrEquiryRequest eventOrEquiryRequest = enquiryRequest();
    EnquiryResponse enquiryResponse = mockEnquiryResponse();
    when(tableReservationOutPort.createEnquiry(eventOrEquiryRequest)).thenReturn(
        enquiryResponse); // Mock the behavior
    EnquiryResponse response = tableReservationPortImpl.createEnquiry(eventOrEquiryRequest);
    assertNotNull(response);
  }

  @Test
  void enquiryById() {
    EnquiryResponse enquiryResponse = mockEnquiryResponse();
    when(tableReservationOutPort.getEnquiry(Mockito.anyString())).thenReturn(enquiryResponse);
    EnquiryResponse response = tableReservationPortImpl.getEnquiry("12");
    assertNotNull(response);
  }

  @Test
  void occasions() {
    String from = "2023-08-19";
    String until = "2023-08-20";
    String siteId = "123";
    OccasionsResponse occasionsResponse = mockOccasionResponse();
    when(tableReservationOutPort.occasions(Mockito.any(), Mockito.any(),
        Mockito.anyString())).thenReturn(occasionsResponse);
    OccasionsResponse response = tableReservationPortImpl.occasions(from,
        until, siteId);
    assertNotNull(response);

  }

  @Test
  void menu(){
    //Act
    MenuResponse menuResponse = Mockito.mock(MenuResponse.class);
    when(tableReservationOutPort.getMenu(Mockito.any(), Mockito.any(), Mockito.anyString(), Mockito.anyString(),Mockito.anyString())).thenReturn(menuResponse);
    MenuResponse response = tableReservationPortImpl.getMenu("abc", "2023-09-21","2023-09-21", "7:12","occassionId");

    //Assert
    assertNotNull(response);
  }
  private EnquiryResponse mockEnquiryResponse() {
    EnquiryResponse enquiryResponse = new EnquiryResponse();
    enquiryResponse.setAdults(1);
    enquiryResponse.setId("1");
    uk.co.whitbread.reservation.domain.model.out.events.Consent consent = new uk.co.whitbread.reservation.domain.model.out.events.Consent();
    consent.setConsentStatement(true);
    consent.setPhone(true);
    enquiryResponse.setConsent(consent);
    return enquiryResponse;
  }

  private OutletResponse getOutletResponse() {
    OutletResponse outletResponse = new OutletResponse();
    List<Company> companies = new ArrayList<>();
    Company company = new Company();
    company.setId("1");
    company.setName("NAME");
    company.setTermsAndConditions("termsAndConditions");
    Consent consent = new Consent();
    ConsentStatement consentStatement = new ConsentStatement();
    consentStatement.setText("TEXT");
    consentStatement.setUrl("https://www.barandblock.co.uk/privacy-cookies");
    consentStatement.setEnabled(true);
    consent.setPrivacyStatement(consentStatement);
    company.setConsent(consent);

    Company company1 = new Company();
    company1.setId("11");
    company1.setName("NAME");
    company1.setTermsAndConditions("termsAndConditions");
    Consent consent1 = new Consent();
    ConsentStatement consentStatement1 = new ConsentStatement();
    consentStatement1.setText("TEXT");
    consentStatement1.setUrl("https://www.barandblock.co.uk/privacy-cookies");
    consentStatement1.setEnabled(true);
    consent1.setPrivacyStatement(consentStatement1);
    company1.setConsent(consent1);

    Company company2 = new Company();
    company2.setId("2");
    company2.setName("NAME");
    company2.setTermsAndConditions("termsAndConditions");
    Consent consent2 = new Consent();
    ConsentStatement consentStatement2 = new ConsentStatement();
    consentStatement2.setText("TEXT");
    consentStatement2.setUrl("");
    consentStatement2.setEnabled(true);
    consent2.setPrivacyStatement(consentStatement2);
    company2.setConsent(consent2);

    companies.add(company);
    companies.add(company1);
    companies.add(company2);
    outletResponse.setCompanies(companies);
    return outletResponse;
  }

  private EventOrEquiryRequest requestBody() {
    EventOrEquiryRequest eventRequest = new EventOrEquiryRequest();
    eventRequest.setAdults(1);
    eventRequest.setTime("18:80");
    eventRequest.setDate("2023-08-20");
    eventRequest.setFirstname("X");
    eventRequest.setLastname("Z");
    eventRequest.setEmailAddress("abc@gmail.com");
    eventRequest.setSiteId("ab");
    eventRequest.setOccasionId("OC12");
    eventRequest.setConsent(new uk.co.whitbread.reservation.domain.model.out.events.Consent());
    eventRequest.setTelephoneNumber("12345");
    return eventRequest;
  }

  private EventOrEquiryRequest enquiryRequest() {
    EventOrEquiryRequest request = new EventOrEquiryRequest();
    request.setAdults(1);
    request.setTime("18:80");
    request.setDate("2023-08-20");
    request.setFirstname("X");
    request.setLastname("Z");
    request.setEmailAddress("abc@gmail.com");
    request.setSiteId("ab");
    request.setOccasionId("OC12");
    request.setConsent(new uk.co.whitbread.reservation.domain.model.out.events.Consent());
    request.setTelephoneNumber("12345");
    return request;
  }

  private SlotsResponse mockSlotsResponse() {
    SlotsResponse slotsResponse = new SlotsResponse();
    Dates dates = new Dates();
    Times times = new Times();
    times.setAvailable(true);
    times.setTotalCapacity(20);
    times.setRemainingCapacity(12);
    times.setTime("19:15");
    dates.setDate("2023-08-20");
    dates.setTimes(new ArrayList<>(List.of(times)));
    slotsResponse.setDates(new ArrayList<>(List.of(dates)));
    return slotsResponse;
  }

  private SessionResponse mockSessionResponse() {
    List<SessionDates> sessionDatesList = new ArrayList<>();
    SessionDates sessionDates = new SessionDates();
    Session session = new Session();
    List<Times> timesList = new ArrayList<>();
    Times times = new Times();
    times.setAvailable(true);
    times.setTime("10:00");
    times.setTotalCapacity(30);
    timesList.add(times);
    session.setBreakFast(timesList);
    session.setLunch(timesList);
    session.setDinner(timesList);
    sessionDates.setSession(session);
    sessionDates.setDinnerAvailable(true);
    sessionDatesList.add(sessionDates);
    SessionResponse sessionResponse = new SessionResponse(sessionDatesList);
    return sessionResponse;
  }

  private EventsResponse mockEventsResponse() {
    EventsResponse eventsResponse = new EventsResponse();
    eventsResponse.setAdults(1);
    eventsResponse.setId("1");
    uk.co.whitbread.reservation.domain.model.out.events.Consent consent = new uk.co.whitbread.reservation.domain.model.out.events.Consent();
    consent.setConsentStatement(true);
    consent.setPhone(true);
    eventsResponse.setConsent(consent);
    return eventsResponse;
  }

  private OccasionsResponse mockOccasionResponse() {
    OccasionsResponse occasionsResponse = new OccasionsResponse();
    Occasions occasions = new Occasions();
    occasions.setName("abc");
    occasions.setId("1");
    occasions.setAvailable(true);
    occasionsResponse.setOccasions(new ArrayList<>(List.of(occasions)));
    return occasionsResponse;

  }


}
