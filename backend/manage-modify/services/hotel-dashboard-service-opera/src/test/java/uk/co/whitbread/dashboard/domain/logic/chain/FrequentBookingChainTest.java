package uk.co.whitbread.dashboard.domain.logic.chain;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import tools.jackson.databind.json.JsonMapper;
import uk.co.whitbread.dashboard.domain.external.hotelinfo.model.in.HotelInfo;
import uk.co.whitbread.dashboard.domain.external.hotelinfo.model.in.InfoAddress;
import uk.co.whitbread.dashboard.domain.external.hotelinfo.model.in.InfoImage;
import uk.co.whitbread.dashboard.domain.external.hotelinfo.model.in.InfoMap;
import uk.co.whitbread.dashboard.domain.model.RetrieveDashboardChainRequest;
import uk.co.whitbread.dashboard.domain.model.in.RetrieveDashboardRequest;
import uk.co.whitbread.dashboard.domain.model.out.DashboardElement;
import uk.co.whitbread.dashboard.domain.ports.secondary.HotelAccountOutPort;
import uk.co.whitbread.dashboard.domain.ports.secondary.HotelInfoOutPort;
import uk.co.whitbread.dashboard.domain.properties.DashboardProperties;
import uk.co.whitbread.hotel.account.generated.hotelaccount.model.BBStaysRequestV2;
import uk.co.whitbread.hotel.account.generated.hotelaccount.model.BBStaysRequestV2.SortOrderEnum;
import uk.co.whitbread.hotel.account.generated.hotelaccount.model.BBStaysRequestV2.TypeOfBookingEnum;
import uk.co.whitbread.hotel.account.generated.hotelaccount.model.StaysResponse;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class FrequentBookingChainTest {

  private static final String BOOKING_CHANNEL = "MOBILE";
  private static final String CBT_BOOKING_CHANNEL = "CBT";
  private static final String TYPE_OF_BOOKING = "PAST";
  private static final String SORT_ORDER = "DEFAULT";
  private JsonMapper jsonMapper;

  private static final String HOTEL_CODE_KINPTI = "KINPTI";
  private static final String HOTEL_CODE_LONHOL = "LONHOL";
  private static final String HOTEL_CODE_LONBLA = "LONBLA";
  private static final String HOTEL_NAME_KINPTI = "London Kings Cross";
  private static final String DASHBOARD_USER = "dashboard-user";
  private static final String VALID_TOKEN = "VALIDTOKEN123";
  private static final String VALID_BB_TOKEN = "VALIDTOKEN123";
  private static final String BB_SESSION_ID = "BB_SOME_SESSION_ID";
  private static final String INVALID_TOKEN = "INVALIDTOKEN456";
  private static final boolean BUSINESS_FALSE = false;
  private static final boolean BUSINESS_TRUE = true;
  private static final String COMPANY_ID = "50";
  private static final String EMPLOYEE_ID = "3";
  static final String ORIGIN = "origin.header.for.test";

  @Mock
  private HotelInfoOutPort hotelInfoOutPort;

  @Mock
  private HotelAccountOutPort hotelAccountOutPort;

  @Mock
  private DashboardProperties properties;

  @InjectMocks
  private FrequentBookingChain target;

  @BeforeEach
  void setup() {
    jsonMapper = new JsonMapper();
    jsonMapper.rebuild()
            .enable(tools.jackson.databind.MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
            .enable(tools.jackson.databind.DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
            .enable(tools.jackson.databind.DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
            .disable(tools.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .disable(tools.jackson.databind.DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
      .build();

    when(properties.getMaxFrequent()).thenReturn(3);
    when(properties.getMinToBeFrequent()).thenReturn(2);
  }

  @Test
  void shouldReturnMaxOfThreeFrequentBookings() {
    StaysResponse accountStays = jsonMapper.readValue(
        new File("src/test/resources/__files/getPastStays11items_4frequent.json"),
        StaysResponse.class);

    BBStaysRequestV2 staysRequest = new BBStaysRequestV2().pageIndex(1)
        .pageSize(Integer.MAX_VALUE)
        .typeOfBooking(TypeOfBookingEnum.PAST)
        .sortOrder(SortOrderEnum.DEFAULT)
        .business(BUSINESS_FALSE);

    when(hotelAccountOutPort.getAccountStays(staysRequest, DASHBOARD_USER, VALID_TOKEN,
        BOOKING_CHANNEL, ORIGIN))
        .thenReturn(accountStays);

    when(hotelInfoOutPort.getHotelInfo(HOTEL_CODE_KINPTI))
        .thenReturn(getHotelInfoKINPTI());

    when(hotelInfoOutPort.getHotelInfo(HOTEL_CODE_LONBLA))
        .thenReturn(getHotelInfoLONBLA());

    when(hotelInfoOutPort.getHotelInfo(HOTEL_CODE_LONHOL))
        .thenReturn(getHotelInfoLONHOL());

    var requestChain = new RetrieveDashboardChainRequest(new RetrieveDashboardRequest(),
        VALID_TOKEN, null, false, ORIGIN, DASHBOARD_USER);

    var dashboardElement = new DashboardElement();

    target.handle(requestChain, dashboardElement);

    Mockito.verify(hotelInfoOutPort).getHotelInfo(HOTEL_CODE_KINPTI);
    Mockito.verify(hotelInfoOutPort).getHotelInfo(HOTEL_CODE_LONBLA);
    Mockito.verify(hotelInfoOutPort).getHotelInfo(HOTEL_CODE_LONHOL);
    Mockito.verify(hotelAccountOutPort).getAccountStays(staysRequest, DASHBOARD_USER, VALID_TOKEN,
        BOOKING_CHANNEL, ORIGIN);

    Assertions.assertThat(dashboardElement.getContent().getFrequentBookings()).hasSize(3);
    Assertions.assertThat(dashboardElement.getContent().getFrequentBookings().get(0).getHotelCode())
        .isEqualTo(HOTEL_CODE_LONHOL);
    Assertions.assertThat(dashboardElement.getContent().getFrequentBookings().get(1).getHotelCode())
        .isEqualTo(HOTEL_CODE_LONBLA);
    Assertions.assertThat(dashboardElement.getContent().getFrequentBookings().get(2).getHotelCode())
        .isEqualTo(HOTEL_CODE_KINPTI);
  }

  @Test
  void shouldReturnOneFrequentBookings() {

    StaysResponse accountStays = jsonMapper.readValue(
        new File("src/test/resources/__files/getPastStaysOneFrequentBooking.json"),
        StaysResponse.class);

    BBStaysRequestV2 staysRequest = new BBStaysRequestV2().pageIndex(1)
        .pageSize(Integer.MAX_VALUE)
        .typeOfBooking(TypeOfBookingEnum.PAST)
        .sortOrder(SortOrderEnum.DEFAULT)
        .business(BUSINESS_FALSE);

    when(hotelAccountOutPort.getAccountStays(staysRequest, DASHBOARD_USER, VALID_TOKEN,
        BOOKING_CHANNEL, ORIGIN))
        .thenReturn(accountStays);

    when(hotelInfoOutPort.getHotelInfo(HOTEL_CODE_LONBLA))
        .thenReturn(getHotelInfoLONBLA());

    var requestChain = new RetrieveDashboardChainRequest(new RetrieveDashboardRequest(),
        VALID_TOKEN, null, false, ORIGIN, DASHBOARD_USER);

    var dashboardElement = new DashboardElement();

    target.handle(requestChain, dashboardElement);

    Mockito.verify(hotelInfoOutPort).getHotelInfo(any());
    Mockito.verify(hotelAccountOutPort).getAccountStays(staysRequest, DASHBOARD_USER, VALID_TOKEN,
        BOOKING_CHANNEL, ORIGIN);

    Assertions.assertThat(dashboardElement).isNotNull();
    Assertions.assertThat(dashboardElement.getContent().getFrequentBookings().get(0).getHotelCode())
        .isEqualTo(HOTEL_CODE_LONBLA);
  }

  @Test
  void shouldReturnOneFrequentBookingsBB() {

    StaysResponse accountStays = jsonMapper.readValue(
        new File("src/test/resources/__files/getPastStaysOneFrequentBooking.json"),
        StaysResponse.class);
    BBStaysRequestV2 staysRequest = new BBStaysRequestV2().pageIndex(1)
        .pageSize(Integer.MAX_VALUE)
        .typeOfBooking(TypeOfBookingEnum.PAST)
        .sortOrder(SortOrderEnum.DEFAULT)
        .business(BUSINESS_TRUE)
        .companyId(COMPANY_ID)
        .employeeId(EMPLOYEE_ID);

    when(hotelAccountOutPort.getAccountStays(staysRequest, DASHBOARD_USER, VALID_BB_TOKEN,
        CBT_BOOKING_CHANNEL, ORIGIN))
        .thenReturn(accountStays);

    when(hotelInfoOutPort.getHotelInfo(HOTEL_CODE_LONBLA))
        .thenReturn(getHotelInfoLONBLA());

    RetrieveDashboardRequest retrieveDashboardRequest = new RetrieveDashboardRequest();
    retrieveDashboardRequest.setCompanyId(COMPANY_ID);
    retrieveDashboardRequest.setEmployeeId(EMPLOYEE_ID);
    retrieveDashboardRequest.setBusiness(true);

    var requestChain = new RetrieveDashboardChainRequest(retrieveDashboardRequest, VALID_BB_TOKEN,
        null, false, ORIGIN, DASHBOARD_USER);

    var dashboardElement = new DashboardElement();

    target.handle(requestChain, dashboardElement);

    Mockito.verify(hotelInfoOutPort).getHotelInfo(any());
    Mockito.verify(hotelAccountOutPort).getAccountStays(staysRequest, DASHBOARD_USER, VALID_BB_TOKEN,
        CBT_BOOKING_CHANNEL, ORIGIN);

    Assertions.assertThat(dashboardElement).isNotNull();
    Assertions.assertThat(dashboardElement.getContent().getFrequentBookings().get(0).getHotelCode())
        .isEqualTo(HOTEL_CODE_LONBLA);
  }

  @Disabled
  @Test
  void shouldReturnOneFrequentBookingsBBWithSessionId() {

    StaysResponse accountStays = jsonMapper.readValue(
        new File("src/test/resources/__files/getPastStaysOneFrequentBooking.json"),
        StaysResponse.class);
    BBStaysRequestV2 staysRequest = new BBStaysRequestV2().pageIndex(1)
        .pageSize(Integer.MAX_VALUE)
        .typeOfBooking(TypeOfBookingEnum.PAST)
        .sortOrder(SortOrderEnum.DEFAULT)
        .business(BUSINESS_TRUE)
        .companyId(COMPANY_ID)
        .employeeId(EMPLOYEE_ID);

    when(hotelAccountOutPort.getAccountStays(staysRequest, DASHBOARD_USER, null,
        CBT_BOOKING_CHANNEL, ORIGIN))
        .thenReturn(accountStays);

    when(hotelInfoOutPort.getHotelInfo(HOTEL_CODE_LONBLA))
        .thenReturn(getHotelInfoLONBLA());

    RetrieveDashboardRequest retrieveDashboardRequest = new RetrieveDashboardRequest();
    retrieveDashboardRequest.setCompanyId(COMPANY_ID);
    retrieveDashboardRequest.setEmployeeId(EMPLOYEE_ID);
    retrieveDashboardRequest.setBusiness(true);

    var requestChain = new RetrieveDashboardChainRequest(retrieveDashboardRequest, null,
        BB_SESSION_ID, false, ORIGIN, DASHBOARD_USER);

    var dashboardElement = new DashboardElement();

    target.handle(requestChain, dashboardElement);

    Mockito.verify(hotelInfoOutPort).getHotelInfo(any());
    Mockito.verify(hotelAccountOutPort).getAccountStays(staysRequest, DASHBOARD_USER, null,
        CBT_BOOKING_CHANNEL, ORIGIN);

    Assertions.assertThat(dashboardElement).isNotNull();
    Assertions.assertThat(dashboardElement.getContent().getFrequentBookings().get(0).getHotelCode())
        .isEqualTo(HOTEL_CODE_LONBLA);
  }

  @Test
  void shouldNotReturnFrequentBookings() {
    var requestChain = new RetrieveDashboardChainRequest(new RetrieveDashboardRequest(),
        VALID_TOKEN, null, false, ORIGIN, DASHBOARD_USER);

    var dashboardElement = new DashboardElement();
    BBStaysRequestV2 staysRequest = new BBStaysRequestV2().pageIndex(1)
        .pageSize(Integer.MAX_VALUE)
        .typeOfBooking(TypeOfBookingEnum.PAST)
        .sortOrder(SortOrderEnum.DEFAULT)
        .business(BUSINESS_FALSE);

    target.handle(requestChain, dashboardElement);

    Mockito.verify(hotelInfoOutPort, never()).getHotelInfo(any());
    Mockito.verify(hotelAccountOutPort).getAccountStays(staysRequest, DASHBOARD_USER, VALID_TOKEN,
        BOOKING_CHANNEL, ORIGIN);

    Assertions.assertThat(dashboardElement.getType()).isNull();
  }

  @Test
  void shouldReturnEmptyListIfHotelAccountIsNotWorkingOrInvalidToken() {
    var requestChain = new RetrieveDashboardChainRequest(new RetrieveDashboardRequest(),
        VALID_TOKEN, null, false, ORIGIN, null);

    var dashboardElement = new DashboardElement();

    BBStaysRequestV2 staysRequest = new BBStaysRequestV2().pageIndex(1)
        .pageSize(Integer.MAX_VALUE)
        .typeOfBooking(TypeOfBookingEnum.PAST)
        .sortOrder(SortOrderEnum.DEFAULT)
        .business(BUSINESS_FALSE)
        .employeeId(null);

    when(hotelAccountOutPort.getAccountStays(staysRequest, null, VALID_TOKEN,
        BOOKING_CHANNEL, ORIGIN))
        .thenReturn(null);

    target.handle(requestChain, dashboardElement);

    Mockito.verify(hotelInfoOutPort, never()).getHotelInfo(any());
    Mockito.verify(hotelAccountOutPort).getAccountStays(staysRequest, null, VALID_TOKEN,
        BOOKING_CHANNEL, ORIGIN);

    Assertions.assertThat(dashboardElement.getType()).isNull();
  }

  @Test
  void shouldNotReturnErrorWhenTokenIsInvalid() {
    var requestChain = new RetrieveDashboardChainRequest(new RetrieveDashboardRequest(),
        INVALID_TOKEN, null, false, ORIGIN, DASHBOARD_USER);

    var dashboardElement = new DashboardElement();

    BBStaysRequestV2 staysRequest = new BBStaysRequestV2().pageIndex(1)
        .pageSize(Integer.MAX_VALUE)
        .typeOfBooking(TypeOfBookingEnum.PAST)
        .sortOrder(SortOrderEnum.DEFAULT)
        .business(BUSINESS_FALSE);

    target.handle(requestChain, dashboardElement);

    Mockito.verify(hotelInfoOutPort, never()).getHotelInfo(any());
    Mockito.verify(hotelAccountOutPort).getAccountStays(staysRequest, DASHBOARD_USER, INVALID_TOKEN,
        BOOKING_CHANNEL, ORIGIN);

    Assertions.assertThat(dashboardElement.getType()).isNull();
  }

  private HotelInfo getHotelInfoKINPTI() {
    var infoMap = InfoMap.builder().latitude(51.532001).longitude(-0.122086).build();
    List<InfoImage> images = new ArrayList<>();
    var image = InfoImage.builder()
        .fileReference("/content/dam/pi/websites/hotelimages/gb/en/K/KINPTI/44521675ac.jpg")
        .build();
    images.add(image);
    return HotelInfo.builder().map(infoMap).images(images).address(getHotelInfoAddress())
        .name(HOTEL_NAME_KINPTI).brand("PI").build();
  }

  private HotelInfo getHotelInfoLONHOL() {
    var infoMap = InfoMap.builder().latitude(52.532001).longitude(-0.222086).build();
    List<InfoImage> images = new ArrayList<>();
    var image = InfoImage.builder()
        .fileReference("/content/dam/pi/websites/hotelimages/gb/en/L/LONHOL/44521675ac.jpg")
        .build();
    images.add(image);
    return HotelInfo.builder().map(infoMap).images(images).address(getHotelInfoAddress())
        .name(HOTEL_NAME_KINPTI).brand("PI").build();
  }

  private HotelInfo getHotelInfoLONBLA() {
    var infoMap = InfoMap.builder().latitude(53.532001).longitude(-0.322086).build();
    List<InfoImage> images = new ArrayList<>();
    var image = InfoImage.builder()
        .fileReference("/content/dam/pi/websites/hotelimages/gb/en/L/LONBLA/44521675ac.jpg")
        .build();
    images.add(image);
    return HotelInfo.builder().map(infoMap).images(images).address(getHotelInfoAddress())
        .name(HOTEL_NAME_KINPTI).brand("PI").build();
  }

  private InfoAddress getHotelInfoAddress() {
    return InfoAddress.builder().addressline1("120 Holborn").country("United Kingdom")
        .postcode("EC1N 2TD").build();
  }

}