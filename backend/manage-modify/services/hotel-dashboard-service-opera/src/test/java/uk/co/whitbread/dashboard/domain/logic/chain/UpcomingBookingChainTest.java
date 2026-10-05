package uk.co.whitbread.dashboard.domain.logic.chain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import uk.co.whitbread.cdh.adapter.generated.cdhadapter.model.CdhReservationSearchDto;
import uk.co.whitbread.cdh.adapter.generated.cdhadapter.model.PriceDto;
import uk.co.whitbread.cdh.adapter.generated.cdhadapter.model.ResultsDto;
import uk.co.whitbread.cdh.adapter.generated.cdhadapter.model.RoomsDto;
import uk.co.whitbread.dashboard.domain.external.hotelinfo.model.in.HotelInfo;
import uk.co.whitbread.dashboard.domain.external.hotelinfo.model.in.InfoAddress;
import uk.co.whitbread.dashboard.domain.external.hotelinfo.model.in.InfoImage;
import uk.co.whitbread.dashboard.domain.external.hotelinfo.model.in.InfoMap;
import uk.co.whitbread.dashboard.domain.logic.mapper.AddressMapper;
import uk.co.whitbread.dashboard.domain.logic.mapper.MapMapper;
import uk.co.whitbread.dashboard.domain.model.RetrieveDashboardChainRequest;
import uk.co.whitbread.dashboard.domain.model.in.FindBookingResponse;
import uk.co.whitbread.dashboard.domain.model.in.ManageBookingResponse;
import uk.co.whitbread.dashboard.domain.model.in.RetrieveDashboardRequest;
import uk.co.whitbread.dashboard.domain.model.in.RoomType;
import uk.co.whitbread.dashboard.domain.model.in.RoomTypeInformation;
import uk.co.whitbread.dashboard.domain.model.out.Action;
import uk.co.whitbread.dashboard.domain.model.out.ActionType;
import uk.co.whitbread.dashboard.domain.model.out.Address;
import uk.co.whitbread.dashboard.domain.model.out.Content;
import uk.co.whitbread.dashboard.domain.model.out.DashboardElement;
import uk.co.whitbread.dashboard.domain.model.out.DashboardType;
import uk.co.whitbread.dashboard.domain.model.out.Map;
import uk.co.whitbread.dashboard.domain.model.out.Room;
import uk.co.whitbread.dashboard.domain.ports.secondary.CdhReservationsOutPort;
import uk.co.whitbread.dashboard.domain.ports.secondary.ContentOutPort;
import uk.co.whitbread.dashboard.domain.ports.secondary.HotelInfoOutPort;
import uk.co.whitbread.dashboard.domain.ports.secondary.HotelReservationsOutPort;
import uk.co.whitbread.dashboard.domain.properties.DashboardProperties;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UpcomingBookingChainTest {

  public static final String LANGUAGE = "en";
  public static final String C_ID = "c_id";
  @Mock
  private CdhReservationsOutPort cdhReservationsOutPort;
  @Mock
  private HotelInfoOutPort hotelInfoOutPort;
  @Mock
  private DashboardProperties properties;
  @Mock
  private MapMapper mockMapMapper;
  @Mock
  private AddressMapper mockAddressMapper;
  @Mock
  private HotelReservationsOutPort hotelReservationOutPort;
  @Mock
  private ContentOutPort contentOutPort;

  @InjectMocks
  private UpcomingBookingChain target;

  private static final String CONFIRMATION_NUMBER = "AQOR885046";
  private static final String SURNAME = "Jones";
  private static final String HOTEL_CODE = "KINPTI";
  private static final String HOTEL_NAME = "London Kings Cross";
  private static final String VALID_TOKEN = "validToken123";
  static final String ORIGIN = "origin.header.for.test";

  @BeforeEach
  void setUp() {
    when(properties.getEnvironment()).thenReturn("https://www.beta.premierinn.digital");
    when(properties.getActionMapping()).thenReturn(getActionMapping("en"));
    when(hotelInfoOutPort.getHotelInfo(HOTEL_CODE)).thenReturn(getHotelInfo());
    when(properties.getMaxDays()).thenReturn(14);
    var map = Map.builder().latitude(51.532001).longitude(-0.122086).build();
    when(mockMapMapper.infoMapToMap(any())).thenReturn(map);
    when(mockAddressMapper.infoAddressToAddress(any())).thenReturn(getHotelAddress());
    when(hotelReservationOutPort.findBooking(any(), any(), any(), any())).thenReturn(null);
    RoomType roomType = RoomType.builder().roomTypes(
        List.of(RoomTypeInformation.builder().roomTypeCode(List.of("DOUBLE"))
            .build())).build();
    when(contentOutPort.getRoomTypes(any(),any(),any())).thenReturn(roomType);
  }

  @Test
  void retrieveDashboardLeisure() {
    when(cdhReservationsOutPort.retrieveBooking(CONFIRMATION_NUMBER)).thenReturn(getBookingDetails());

    var request = RetrieveDashboardRequest.builder()
        .arrivalDate(getNextWeek())
        .confirmationNumber(CONFIRMATION_NUMBER)
        .surname(SURNAME)
        .language(LANGUAGE)
        .companyId(C_ID)
        .business(false)
        .build();

    var requestChain = new RetrieveDashboardChainRequest(request, VALID_TOKEN,
        null, false, ORIGIN, "customerId");
    DashboardElement expectedResponse = getExpectedResponse();
    DashboardElement actualResponse = new DashboardElement();
    target.handle(requestChain, actualResponse);
    assertEquals(expectedResponse.getType(), actualResponse.getType());
  }

  @Test
  void retrieveDashboardBusiness() {

    CdhReservationSearchDto bookingDetails = getBookingDetails();
    bookingDetails.getResults().get(0).setArrivalDate("2024-16-11");
    bookingDetails.getResults().get(0).setDepartureDate("2024-17-11");
    when(cdhReservationsOutPort.retrieveBooking(CONFIRMATION_NUMBER)).thenReturn(bookingDetails);

    var request = RetrieveDashboardRequest.builder()
        .arrivalDate(getNextWeek())
        .confirmationNumber(CONFIRMATION_NUMBER)
        .surname(SURNAME)
        .business(true)
        .build();

    var requestChain = new RetrieveDashboardChainRequest(request, VALID_TOKEN,
        null, false, ORIGIN, "customerId");

    DashboardElement expectedResponse = getExpectedResponseBusiness();

    DashboardElement actualResponse = new DashboardElement();

    target.handle(requestChain, actualResponse);

    assertEquals(expectedResponse.getType(), actualResponse.getType());
    assertNull(actualResponse.getContent().getArrivalDate());
    assertNull(actualResponse.getContent().getDepartureDate());
  }

  @Test
  void shouldReturnUpcomingBookingsV1() {
    LocalDate plusWeeks = LocalDate.now().plusWeeks(1);
    RetrieveDashboardRequest retrieve = RetrieveDashboardRequest.builder()
        .arrivalDate(plusWeeks)
        .confirmationNumber("AQOR885046")
        .surname("CERVELIN")
        .business(false)
        .build();
    when(hotelReservationOutPort.findBooking(any(), any(), any(), any())).thenReturn(new FindBookingResponse());
    when(hotelReservationOutPort.getManageBookingInfo(any(), any(), any(), any(), any()))
        .thenReturn(getManageBookingResponse());

    var requestChain = new RetrieveDashboardChainRequest(retrieve, VALID_TOKEN, null, false,
        ORIGIN, "customerId");

    var actualResponse = new DashboardElement();

    CdhReservationSearchDto bookingDetails = getBookingDetails();
    bookingDetails.getResults().get(0).setArrivalDate(null);
    bookingDetails.getResults().get(0).setDepartureDate(null);
    when(cdhReservationsOutPort.retrieveBooking("AQOR885046")).thenReturn(bookingDetails);

    target.handle(requestChain, actualResponse);

    Mockito.verify(cdhReservationsOutPort).retrieveBooking(anyString());
    Assertions.assertThat(actualResponse.getType()).isEqualTo(DashboardType.UPCOMING_BOOKING);
    assertEquals(getExpectedResponse().getType(), actualResponse.getType());
    assertNull(actualResponse.getContent().getArrivalDate());
    assertNull(actualResponse.getContent().getDepartureDate());
  }

  @Test
  void shouldReturnErrorWhenInvalidDetailsForUpcomingBookingsV1() {
    LocalDate plusWeeks = LocalDate.now().plusWeeks(1);
    RetrieveDashboardRequest retrieve = RetrieveDashboardRequest.builder()
        .arrivalDate(plusWeeks)
        .confirmationNumber("AQOR885046")
        .surname("CERVELIN")
        .business(false)
        .build();

    var requestChain = new RetrieveDashboardChainRequest(retrieve, VALID_TOKEN, null, false,
        ORIGIN, "customerId");

    var actualResponse = new DashboardElement();

    when(cdhReservationsOutPort.retrieveBooking("AQOR885046"))
        .thenThrow(new RuntimeException("Invalid reservation details"));

    assertThrows(RuntimeException.class,
        () -> target.handle(requestChain, actualResponse),
        "Invalid reservation details");
  }

  @Test
  void shouldSkipUpcomingBookingsIfTypeIsNotNull() {
    LocalDate plusWeeks = LocalDate.now().plusWeeks(1);
    RetrieveDashboardRequest retrieve = RetrieveDashboardRequest.builder()
        .arrivalDate(plusWeeks)
        .confirmationNumber("AQOR885046")
        .surname("CERVELIN")
        .business(false)
        .build();

    var requestChain = new RetrieveDashboardChainRequest(retrieve, VALID_TOKEN, null, false,
        ORIGIN, "customerId");

    var actualResponse = new DashboardElement(DashboardType.PAST_SEARCHES, null);

    target.handle(requestChain, actualResponse);

    Mockito.verify(cdhReservationsOutPort, Mockito.never()).retrieveBooking(anyString());
    Mockito.verify(hotelInfoOutPort, Mockito.never()).getHotelInfo(anyString());

    Assertions.assertThat(actualResponse.getType()).isNotEqualTo(DashboardType.UPCOMING_BOOKING);
  }

  private LocalDate getNextWeek() {
    return LocalDate.now().plusWeeks(1);
  }

  private HashMap<String, HashMap<String, String>> getActionMapping(String countryCode) {
    HashMap<String, HashMap<String, String>> actionMap = new HashMap<>();
    HashMap<String, String> map = new HashMap<>();
    map.put("CIOL", "Check in");
    map.put("UPSELLS", "Add meals or extras");
    map.put("DIRECTIONS", "Show hotel directions");
    map.put("BOOKING_DETAILS", "View booking details");
    actionMap.put(countryCode, map);
    return actionMap;
  }

  private InfoAddress getHotelInfoAddress() {
    return InfoAddress.builder().addressline1("120 Holborn").country("United Kingdom")
        .postcode("EC1N 2TD").build();
  }

  private Address getHotelAddress() {
    return Address.builder().addressLine1("120 Holborn").country("United Kingdom")
        .postCode("EC1N 2TD").build();
  }

  private HotelInfo getHotelInfo() {
    var infoMap = InfoMap.builder().latitude(51.532001).longitude(-0.122086).build();
    List<InfoImage> images = new ArrayList<>();
    var image = InfoImage.builder()
        .fileReference("/content/dam/pi/websites/hotelimages/gb/en/K/KINPTI/44521675ac.jpg")
        .build();
    images.add(image);
    return HotelInfo.builder().map(infoMap).images(images).address(getHotelInfoAddress())
        .name(HOTEL_NAME).brand("PI").build();
  }

  private CdhReservationSearchDto getBookingDetails() {
    var cdhReservationSearchDto = new CdhReservationSearchDto();
    var reservationDetails = new ResultsDto();
    reservationDetails.setBookingReference(CONFIRMATION_NUMBER);
    reservationDetails.setHotelCode(HOTEL_CODE);
    reservationDetails.setArrivalDate(getNextWeek().toString());
    reservationDetails.setDepartureDate(getNextWeek().plusDays(2).toString());
    PriceDto price = new PriceDto();
    price.setAmount(BigDecimal.valueOf(0.00));
    price.setCurrency("GBP");
    List<RoomsDto> rooms = new ArrayList<>();
    RoomsDto reservationRoom = new RoomsDto();
    reservationRoom.roomType("DOUBLE");
    reservationRoom.roomId("DBS,1");
    reservationRoom.roomNumber("1");
    reservationRoom.status("UNARRIVED");
    reservationRoom.noOfAdults("1");
    reservationRoom.noOfChildren("0");
    reservationRoom.roomCost(price);
    rooms.add(reservationRoom);

    reservationDetails.setRooms(rooms);
    reservationDetails.setBookingType("LEISURE");
    cdhReservationSearchDto.setResults(Collections.singletonList(reservationDetails));

    return cdhReservationSearchDto;
  }

  private DashboardElement getExpectedResponse() {

    List<Room> rooms = new ArrayList<>();
    rooms.add(Room.builder().type("Double").build());

    List<Action> actions = new ArrayList<>();
    actions.add(Action.builder().type(ActionType.CIOL).title("Check in").build());
    actions.add(Action.builder().type(ActionType.UPSELLS).title("Add meals or extras").build());
    actions.add(Action.builder().type(ActionType.BOOKING_DETAILS).title("View booking details").build());

    var content = Content.builder()
        .hotelImage(
            "https://www.beta.premierinn.digital/content/dam/pi/websites/hotelimages/gb/en/K/KINPTI/44521675ac.jpg")
        .hotelName(HOTEL_NAME)
        .hotelCode(HOTEL_CODE)
        .checkedIn(false)
        .map(Map.builder().latitude(getHotelInfo().getMap().getLatitude())
            .longitude(getHotelInfo().getMap().getLongitude()).build())
        .address(getHotelAddress())
        .confirmationNumber(CONFIRMATION_NUMBER)
        .arrivalDate(getNextWeek())
        .departureDate(getNextWeek().plusDays(2))
        .rooms(rooms)
        .guests(1)
        .actions(actions)
        .build();

    return DashboardElement.builder()
        .type(DashboardType.UPCOMING_BOOKING)
        .content(content)
        .build();
  }

  private DashboardElement getExpectedResponseBusiness() {

    List<Room> rooms = new ArrayList<>();
    rooms.add(Room.builder().type("Double").build());

    List<Action> actions = new ArrayList<>();
    actions.add(Action.builder().type(ActionType.DIRECTIONS).title("Show hotel directions").build());
    actions.add(Action.builder().type(ActionType.BOOKING_DETAILS).title("View booking details").build());

    var content = Content.builder()
        .hotelImage(
            "https://www.beta.premierinn.digital/content/dam/pi/websites/hotelimages/gb/en/K/KINPTI/44521675ac.jpg")
        .hotelName(HOTEL_NAME)
        .hotelCode(HOTEL_CODE)
        .checkedIn(false)
        .map(Map.builder().latitude(getHotelInfo().getMap().getLatitude())
            .longitude(getHotelInfo().getMap().getLongitude()).build())
        .address(getHotelAddress())
        .confirmationNumber(CONFIRMATION_NUMBER)
        .arrivalDate(getNextWeek())
        .departureDate(getNextWeek().plusDays(2))
        .rooms(rooms)
        .guests(1)
        .actions(actions)
        .build();

    return DashboardElement.builder()
        .type(DashboardType.UPCOMING_BOOKING)
        .content(content)
        .build();
  }

  private static ManageBookingResponse getManageBookingResponse() {
    ManageBookingResponse manageBookingResponse = new ManageBookingResponse();
    manageBookingResponse.setIsAmendable(true);
    return manageBookingResponse;
  }

}