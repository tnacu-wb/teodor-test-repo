package uk.co.whitbread.reservation.infrastructure.rest.controller.booking;


import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import uk.co.whitbread.reservation.domain.model.out.enquiry.EventOrEquiryRequest;
import uk.co.whitbread.reservation.domain.model.out.enquiry.EnquiryResponse;
import uk.co.whitbread.reservation.domain.model.out.events.EventsResponse;
import uk.co.whitbread.reservation.domain.model.out.menu.MenuResp;
import uk.co.whitbread.reservation.domain.model.out.menu.MenuResponse;
import uk.co.whitbread.reservation.domain.model.out.occasion.Occasions;
import uk.co.whitbread.reservation.domain.model.out.occasion.OccasionsResponse;
import uk.co.whitbread.reservation.domain.model.out.outlets.Company;
import uk.co.whitbread.reservation.domain.model.out.outlets.Consent;
import uk.co.whitbread.reservation.domain.model.out.outlets.OutletResponse;
import uk.co.whitbread.reservation.domain.model.out.slots.Session;
import uk.co.whitbread.reservation.domain.model.out.slots.SessionDates;
import uk.co.whitbread.reservation.domain.model.out.slots.SessionResponse;
import uk.co.whitbread.reservation.domain.model.out.slots.Times;
import uk.co.whitbread.reservation.domain.ports.primary.TableReservationInPort;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.mapper.EnquiryResponseMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.mapper.EventResponseMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.mapper.MenuResponseMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.mapper.OccasionResponseMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.mapper.OutletResponseMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.mapper.SlotsResponseMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.ConsentDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.EnquiryResponseDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.EventsResponseDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.MenuRespDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.MenuResponseDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.OccasionsDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.OccasionsResponseDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.outlets.CompanyDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.outlets.OutletResponseDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.SessionDatesDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.SessionDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.SessionResponseDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.TimesDto;


@SpringBootTest(classes = TableReservationController.class)
@WebAppConfiguration
@EnableWebMvc
class TableReservationControllerTest {


  private final String SLOT_SERVICE = "/event/v1/slots";
  private final String OUTLET_SERVICE = "/event/v1/outlets";
  private final String EVENT_SERVICE = "/event/v1/events";

  @Autowired
  WebApplicationContext webApplicationContext;
  @Autowired
  private TableReservationController tableReservationController;
  @MockitoBean
  SlotsResponseMapper slotsResponseMapper;
  private MockMvc mockMvc;
  @MockitoBean
  private TableReservationInPort tableReservationInPort;
  @MockitoBean
  private OutletResponseMapper outletResponseMapper;
  @MockitoBean
  private EventResponseMapper eventResponseMapper;
  @MockitoBean
  private EnquiryResponseMapper enquiryResponseMapper;
  @MockitoBean
  private OccasionResponseMapper occasionResponseMapper;
  @MockitoBean
  MenuResponseMapper menuResponseMapper;

  @BeforeEach
  void beforeSetup() {
    mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
  }

  @Test
  void slots_responseAs200() throws Exception {
    SessionDates sessionDates = new SessionDates();
    Session session = new Session();
    Times times = new Times();
    times.setTime("19:15");
    times.setAvailable(true);
    times.setRemainingCapacity(12);
    session.setBreakFast(new ArrayList<>(List.of(times)));
    session.setDinner(new ArrayList<>(List.of(times)));
    sessionDates.setBreakFastAvailable(true);
    sessionDates.setSession(session);
    sessionDates.setDate("2023-08-20");
    List<SessionDates> dates = new ArrayList<>(List.of(sessionDates));
    SessionResponse sessionResponse = new SessionResponse(dates);

    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("from", "2023-08-20");
    params.add("until", "2023-08-20");
    params.add("adult", "1");
    params.add("children", "2");
    params.add("time", "19:15");
    params.add("siteId", "b62be154-a301-49bb-9f00-edcee8a6fe9f");

    when(
        tableReservationInPort.slots(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
            Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(sessionResponse);
    when(slotsResponseMapper.toDto(Mockito.any())).thenReturn(mockSessionResponseDto());

    MvcResult mvcResult = mockMvc.perform(
        MockMvcRequestBuilders.get(SLOT_SERVICE).accept(MediaType.APPLICATION_JSON_VALUE)
            .params(params)).andReturn();
    int status = mvcResult.getResponse().getStatus();
    Assertions.assertEquals(200, status);
    ObjectMapper mapper = new ObjectMapper();
    SessionResponseDto sessionResponseDto = mapper.readValue(
        mvcResult.getResponse().getContentAsString(), SessionResponseDto.class);
    Assertions.assertTrue(sessionResponseDto.getDates().get(0).isDinnerAvailable());

  }


  @Test
  void slots_whenInvalidFields_returnBadRequest() throws Exception {
    SessionResponse sessionResponse = new SessionResponse(new ArrayList<>());
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    when(
        tableReservationInPort.slots(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
            Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(sessionResponse);
    MvcResult mvcResult = mockMvc.perform(
        MockMvcRequestBuilders.get(SLOT_SERVICE).accept(MediaType.APPLICATION_JSON_VALUE)
            .params(params)).andReturn();
    int status = mvcResult.getResponse().getStatus();
    Assertions.assertEquals(400, status);
  }

  @Test
  void outlet_whenValidGiven_returnResponseSuccess() throws Exception {
    OutletResponse outletResponse = new OutletResponse();
    List<Company> companies = new ArrayList<>();
    Company company = new Company();
    company.setId("1");
    company.setName("NAME");
    company.setTermsAndConditions("termsAndConditions");
    Consent consent = new Consent();
    company.setConsent(consent);
    companies.add(company);
    outletResponse.setCompanies(companies);
    when(tableReservationInPort.outlets(Mockito.anyString(),Mockito.anyString())).thenReturn(outletResponse);
    when(outletResponseMapper.toDto(Mockito.any())).thenReturn(mockOutletResponseDto());
    MvcResult mvcResult = mockMvc.perform(
        MockMvcRequestBuilders.get(OUTLET_SERVICE).accept(MediaType.APPLICATION_JSON_VALUE)
            .param("location", "Bengaluru")).andReturn();
    int status = mvcResult.getResponse().getStatus();
    ObjectMapper mapper = new ObjectMapper();
    OutletResponseDto outletResponseDto = mapper.readValue(
        mvcResult.getResponse().getContentAsString(), OutletResponseDto.class);
    Assertions.assertEquals(200, status);
    Assertions.assertEquals("termsAndConditions",
        outletResponseDto.getCompanies().get(0).getTermsAndConditions());
    Assertions.assertEquals("1", outletResponseDto.getCompanies().get(0).getId());
    Assertions.assertEquals("NAME", outletResponseDto.getCompanies().get(0).getName());
  }

  @Test
  void outlet_whenMethodIsInvalid_returnMethodNotFound() throws Exception {
    MvcResult mvcResult = mockMvc.perform(
            MockMvcRequestBuilders.post(OUTLET_SERVICE).accept(MediaType.APPLICATION_JSON_VALUE))
        .andReturn();
    int status = mvcResult.getResponse().getStatus();
    Assertions.assertEquals(405, status);
  }

  @Test
  void event_whenValidGiven_returnWithSuccess() {
    EventOrEquiryRequest eventRequest = new EventOrEquiryRequest();
    eventRequest.setAdults(1);
    eventRequest.setChildren(0);
    eventRequest.setTime("18:80");
    eventRequest.setDate("2023-08-20");
    eventRequest.setFirstname("X");
    eventRequest.setLastname("Z");
    eventRequest.setEmailAddress("abc@gmail.com");
    eventRequest.setSiteId("ab");
    eventRequest.setOccasionId("OC12");
    eventRequest.setTelephoneNumber("+12345678901");
    eventRequest.setConsent(new uk.co.whitbread.reservation.domain.model.out.events.Consent());

    EventsResponse eventsResponse = new EventsResponse();
    eventsResponse.setAdults(1);
    eventsResponse.setId("1");
    uk.co.whitbread.reservation.domain.model.out.events.Consent consent = new uk.co.whitbread.reservation.domain.model.out.events.Consent();
    consent.setConsentStatement(true);
    consent.setPhone(true);
    eventsResponse.setConsent(consent);
    when(tableReservationInPort.events(Mockito.any())).thenReturn(eventsResponse);
    when(eventResponseMapper.toDto(Mockito.any())).thenReturn(mockEventsResponseDto());
    EventsResponseDto response = tableReservationController.events(eventRequest);
    Assertions.assertNotNull(response);
    Assertions.assertEquals(1, response.getAdults());
    Assertions.assertEquals("1", response.getId());
  }

  @Test
  void eventById_whenValidGiven_returnWithSuccess() throws Exception {
    EventsResponse eventsResponse = new EventsResponse();
    eventsResponse.setAdults(1);
    eventsResponse.setId("1");
    uk.co.whitbread.reservation.domain.model.out.events.Consent consent = new uk.co.whitbread.reservation.domain.model.out.events.Consent();
    consent.setConsentStatement(true);
    consent.setPhone(true);
    eventsResponse.setConsent(consent);
    when(eventResponseMapper.toDto(Mockito.any())).thenReturn(mockEventsResponseDto());
    when(tableReservationInPort.getEvent(Mockito.anyString())).thenReturn(eventsResponse);
    MvcResult mvcResult = mockMvc.perform(
            MockMvcRequestBuilders.get(EVENT_SERVICE + "/12").accept(MediaType.APPLICATION_JSON_VALUE))
        .andReturn();
    int status = mvcResult.getResponse().getStatus();
    ObjectMapper mapper = new ObjectMapper();
    EventsResponseDto eventsResponseDto = mapper.readValue(
        mvcResult.getResponse().getContentAsString(), EventsResponseDto.class);
    Assertions.assertEquals(200, status);
    Assertions.assertEquals(1, eventsResponseDto.getAdults());
    Assertions.assertEquals("1", eventsResponseDto.getId());
    Assertions.assertTrue(eventsResponseDto.getConsent().isConsentStatement());
  }

  @Test
  void eventByID_whenInValidGiven_returnBadRequest() throws Exception {
    MvcResult mvcResult = mockMvc.perform(
            MockMvcRequestBuilders.get(EVENT_SERVICE + "/").accept(MediaType.APPLICATION_JSON_VALUE))
        .andReturn();
    int status = mvcResult.getResponse().getStatus();
    Assertions.assertEquals(404, status);
  }

  @Test
  void enquiry_whenValidRequestGiven_returnWithSuccess() {
    EventOrEquiryRequest eventOrEquiryRequest = new EventOrEquiryRequest();
    eventOrEquiryRequest.setAdults(1);
    eventOrEquiryRequest.setChildren(0);
    eventOrEquiryRequest.setTime("18:80");
    eventOrEquiryRequest.setDate("2023-08-20");
    eventOrEquiryRequest.setFirstname("X");
    eventOrEquiryRequest.setLastname("Z");
    eventOrEquiryRequest.setEmailAddress("abc@gmail.com");
    eventOrEquiryRequest.setSiteId("ab");
    eventOrEquiryRequest.setOccasionId("OC12");
    eventOrEquiryRequest.setTelephoneNumber("+12345678901");
    eventOrEquiryRequest.setConsent(new uk.co.whitbread.reservation.domain.model.out.events.Consent());

    EnquiryResponse enquiryResponse = new EnquiryResponse();
    enquiryResponse.setAdults(1);
    enquiryResponse.setId("1");
    uk.co.whitbread.reservation.domain.model.out.events.Consent consent = new uk.co.whitbread.reservation.domain.model.out.events.Consent();
    consent.setConsentStatement(true);
    consent.setPhone(true);
    enquiryResponse.setConsent(consent);
    when(enquiryResponseMapper.toDto(Mockito.any())).thenReturn(mockEnquiryResponseDto());
    when(tableReservationInPort.createEnquiry(Mockito.any())).thenReturn(enquiryResponse);
    EnquiryResponseDto enquiryResponseDto = tableReservationController.enquiry(eventOrEquiryRequest);
    Assertions.assertNotNull(enquiryResponseDto);
    Assertions.assertEquals(1, enquiryResponseDto.getAdults());
    Assertions.assertEquals("1", enquiryResponseDto.getId());
    Assertions.assertTrue(enquiryResponseDto.getConsent().isConsentStatement());
  }

  @Test
  void enquiryByID_whenValidRequestGiven_returnWithSuccess() throws Exception {
    String enquiryService = "/event/v1/enquiry";
    EnquiryResponse enquiryResponse = new EnquiryResponse();
    enquiryResponse.setAdults(1);
    enquiryResponse.setId("1");
    uk.co.whitbread.reservation.domain.model.out.events.Consent consent = new uk.co.whitbread.reservation.domain.model.out.events.Consent();
    consent.setConsentStatement(true);
    consent.setPhone(true);
    enquiryResponse.setConsent(consent);
    when(enquiryResponseMapper.toDto(Mockito.any())).thenReturn(mockEnquiryResponseDto());
    when(tableReservationInPort.getEnquiry(Mockito.anyString()))
        .thenReturn(enquiryResponse);
    MvcResult mvcResult = mockMvc.perform(
        MockMvcRequestBuilders.get(enquiryService + "/12")
            .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();
    int status = mvcResult.getResponse().getStatus();
    ObjectMapper mapper = new ObjectMapper();
    EnquiryResponseDto enquiryResponseDto = mapper.readValue(
        mvcResult.getResponse().getContentAsString(), EnquiryResponseDto.class);
    Assertions.assertEquals(200, status);
    Assertions.assertEquals(1, enquiryResponseDto.getAdults());
    Assertions.assertEquals("1", enquiryResponseDto.getId());
    Assertions.assertTrue(enquiryResponseDto.getConsent().isConsentStatement());
  }

  @Test
  void occasion_whenValidRequestGiven_returnWithSuccess() throws Exception {
    String occasionService = "/event/v1/occasions";
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("from", "2023-08-20");
    params.add("until", "2023-08-20");
    params.add("siteId", "b62be154-a301-49bb-9f00-edcee8a6fe9f");
    when(
        tableReservationInPort.occasions(Mockito.anyString(),
            Mockito.anyString(), Mockito.anyString())).thenReturn(
        mockOccasionsResponse());
    when(occasionResponseMapper.toDto(Mockito.any()))
        .thenReturn(mockOccasionsResponseDto());
    MvcResult mvcResult = mockMvc.perform(
        MockMvcRequestBuilders.get(occasionService).accept(MediaType.APPLICATION_JSON_VALUE)
            .params(params)).andReturn();
    int status = mvcResult.getResponse().getStatus();
    Assertions.assertEquals(200, status);
  }
  @Test
  void testGetMenu() throws Exception {

    String menuService = "/event/v1/menus";
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("siteId", "b62be154-a301-49bb-9f00-edcee8a6fe9f");
    params.add("from", "2023-08-20");
    params.add("until", "2023-08-20");
    params.add("time", "7:12");
    params.add("ocassionId", "30b545b7-bd1b-43ae-bc96-4520b04be8e6");
    //Assign
    MenuResponse menuResponse = getMenuResponse();
    //Act

    when(tableReservationInPort.getMenu(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),Mockito.anyString(), Mockito.anyString())).thenReturn(menuResponse);

    when(menuResponseMapper.toDto(Mockito.any())).thenReturn(getMenuResponseDto());
    MvcResult mvcResult = mockMvc.perform(
        MockMvcRequestBuilders.get(menuService).accept(MediaType.APPLICATION_JSON_VALUE)
            .params(params)).andReturn();

    int status = mvcResult.getResponse().getStatus();

    //Assert
    Assertions.assertEquals(200, status);
  }
  private SessionResponseDto mockSessionResponseDto() {

    SessionResponseDto sessionResponseDto = new SessionResponseDto();
    SessionDatesDto sessionDates = new SessionDatesDto();

    SessionDto session = new SessionDto();
    TimesDto times = new TimesDto();
    times.setTime("19:15");
    times.setAvailable(true);
    times.setRemainingCapacity(12);
    session.setBreakFast(new ArrayList<>(List.of(times)));
    session.setDinner(new ArrayList<>(List.of(times)));
    sessionDates.setBreakFastAvailable(false);
    sessionDates.setDinnerAvailable(true);
    sessionDates.setSessionDto(session);
    sessionDates.setDate("2023-08-20");
    List<SessionDatesDto> dates = new ArrayList<>(List.of(sessionDates));
    sessionResponseDto.setDates(dates);
    return sessionResponseDto;

  }

  private OutletResponseDto mockOutletResponseDto() {
    OutletResponseDto outletResponseDto = new OutletResponseDto();
    List<uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.outlets.CompanyDto> companies = new ArrayList<>();
    uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.outlets.CompanyDto company = new CompanyDto();
    company.setId("1");
    company.setName("NAME");
    company.setTermsAndConditions("termsAndConditions");
    uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.outlets.ConsentDto consent = new uk.co.whitbread.reservation.infrastructure.rest.controller.booking.model.out.outlets.ConsentDto();
    company.setConsent(consent);
    companies.add(company);
    outletResponseDto.setCompanies(companies);
    return outletResponseDto;

  }

  private EventsResponseDto mockEventsResponseDto() {
    EventsResponseDto eventsResponseDto = new EventsResponseDto();
    eventsResponseDto.setAdults(1);
    eventsResponseDto.setId("1");
    ConsentDto consent = new ConsentDto();
    consent.setConsentStatement(true);
    consent.setPhone(true);
    eventsResponseDto.setConsent(consent);
    return eventsResponseDto;

  }

  private EnquiryResponseDto mockEnquiryResponseDto() {
    EnquiryResponseDto enquiryResponseDto = new EnquiryResponseDto();
    enquiryResponseDto.setAdults(1);
    enquiryResponseDto.setId("1");
    ConsentDto ConsentDto = new ConsentDto();
    ConsentDto.setConsentStatement(true);
    ConsentDto.setPhone(true);
    enquiryResponseDto.setConsent(ConsentDto);
    return enquiryResponseDto;
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

  private OccasionsResponseDto mockOccasionsResponseDto() {
    OccasionsResponseDto response = new OccasionsResponseDto();
    OccasionsDto occasions = new OccasionsDto();
    occasions.setAvailable(true);
    occasions.setId("123");
    occasions.setName("Name");
    response.setOccasions(List.of(occasions));
    return response;
  }

  private MenuResponse getMenuResponse(){
    MenuResponse menuResponse = new MenuResponse();
    List<MenuResp> menuRespList = new ArrayList<>();
    MenuResp menuResp = new MenuResp();

    menuResp.setId("1");
    menuResp.setName("test");

    menuRespList.add(menuResp);
    menuResponse.setMenus(menuRespList);
    return menuResponse;
  }
  private MenuResponseDto getMenuResponseDto(){
    MenuResponseDto menuResponseDto = new MenuResponseDto();
    List<MenuRespDto> menuResponseDtoList = new ArrayList<>();
    MenuRespDto menuRespDto = new MenuRespDto();

    menuRespDto.setId("1");
    menuRespDto.setName("test_name");

    menuResponseDtoList.add(menuRespDto);
    menuResponseDto.setMenus(menuResponseDtoList);
    return menuResponseDto;
  }
}
