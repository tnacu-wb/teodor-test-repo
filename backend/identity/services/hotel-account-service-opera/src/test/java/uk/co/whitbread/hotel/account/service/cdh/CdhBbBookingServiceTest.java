package uk.co.whitbread.hotel.account.service.cdh;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.cdh.adapter.service.generated.models.reservationSearch.CdhReservationSearchCriteriaDto;
import uk.co.whitbread.cdh.adapter.service.generated.models.reservationSearch.CdhReservationSearchDto;
import uk.co.whitbread.hotel.account.mapper.CdhBookingHistoryRequestMapper;
import uk.co.whitbread.hotel.account.mapper.CdhBookingHistoryResponseMapper;
import uk.co.whitbread.hotel.account.model.BBStaysRequest;
import uk.co.whitbread.hotel.account.model.Stay;
import uk.co.whitbread.hotel.account.model.StaysFilterType;
import uk.co.whitbread.hotel.account.model.StaysResponse;
import uk.co.whitbread.hotel.account.model.feature.FeatureFlag;
import uk.co.whitbread.hotel.account.model.feature.UnleashWrapper;
import uk.co.whitbread.hotel.account.properties.BookingHistoryProperties;
import uk.co.whitbread.shared.auth.account.CdhEmployeeDetails;

@ExtendWith(MockitoExtension.class)
class CdhBbBookingServiceTest {


  private static final String EMAIL = "email@test.com";
  private static final String COMPANY_ID = "companyId";
  private static final String EMPLOYEE_ID = "employeeID";

  private ObjectMapper objectMapper;

  @Mock
  private CdhService cdhService;

  private final CdhBookingHistoryRequestMapper bookingHistoryRequestMapper = Mappers.getMapper(CdhBookingHistoryRequestMapper.class);
  private final CdhBookingHistoryResponseMapper bookingHistoryResponseMapper = Mappers.getMapper(CdhBookingHistoryResponseMapper.class);
  @Mock
  private UnleashWrapper<FeatureFlag> unleashWrapper;
  @Mock
  private BookingHistoryProperties bookingHistoryProperties;

  @InjectMocks
  private CdhBbBookingsService sut;

  private CdhEmployeeDetails cdhEmployeeDetails;
  
  @BeforeEach
  void setUp() {

    objectMapper = new ObjectMapper();
    objectMapper.findAndRegisterModules();
    unleashWrapper = Mockito.mock(UnleashWrapper.class);

    cdhEmployeeDetails = CdhEmployeeDetails.builder().employeeAccountId(EMPLOYEE_ID)
        .companyAccountId(COMPANY_ID).userEmail(EMAIL).build();

    sut = new CdhBbBookingsService(cdhService,bookingHistoryRequestMapper,
        bookingHistoryResponseMapper, unleashWrapper, bookingHistoryProperties);
  }

  @Test
  void retrieveCdhPiBookingsV2_shouldReturnAllBookings() throws IOException {
    BBStaysRequest bbStaysRequest = new BBStaysRequest();
    bbStaysRequest.setContinuationToken("123456");
    var mockedFeatureFlag = mock(FeatureFlag.class);
    
    when(unleashWrapper.featureFlag())
        .thenReturn(mockedFeatureFlag);
    when(unleashWrapper.isEnabled(mockedFeatureFlag.getBookingHistoryPageOrdering()))
        .thenReturn(true);
    when(cdhService.getBookingHistoryV2(
        any(CdhReservationSearchCriteriaDto.class))).thenReturn(getCdhBookingsV2());

    StaysResponse staysResponse = sut.retrieveCdhBbBookingsV2(bbStaysRequest, cdhEmployeeDetails, 1, 20);
    List<Stay> stayResponseList = staysResponse.getStays();

    assertNotNull(staysResponse);
    assertEquals(10, staysResponse.getPageSize());
    assertEquals(8, staysResponse.getStays().size());
    // CheckedIn sorted by arrivalDate ascending
    assertTrue(stayResponseList.get(0).getArrivalDate().isBefore(stayResponseList.get(1).getArrivalDate()));
    
    // Past sorted by departureDate descending
    assertTrue(stayResponseList.get(2).getDepartureDate().isAfter(stayResponseList.get(3).getDepartureDate()));
    
    // Cancelled sorted by arrivalDate descending
    assertTrue(stayResponseList.get(6).getArrivalDate().isAfter(stayResponseList.get(7).getArrivalDate()));
  }

  @Test
  void testRetrieveCdhPiBookingsV2_whenFilterTypeIsNotNull() {
    BBStaysRequest staysRequest = new BBStaysRequest();

    // Test with CONFIRM_NUMBER
    staysRequest.setFilterType(StaysFilterType.CONFIRM_NUMBER);
    staysRequest.setFilterValue("CONFIRM123");
    StaysResponse responseConfirmNumber = sut.retrieveCdhBbBookingsV2(staysRequest, cdhEmployeeDetails, 1, 20);
    assertNotNull(responseConfirmNumber, "Response should not be null for filter type: CONFIRM_NUMBER");

    // Test with NAME
    staysRequest.setFilterType(StaysFilterType.NAME);
    staysRequest.setFilterValue("John Doe");
    StaysResponse responseName = sut.retrieveCdhBbBookingsV2(staysRequest, cdhEmployeeDetails, 1, 20);
    assertNotNull(responseName, "Response should not be null for filter type: NAME");

    // Test with ARRIVAL_DATE
    staysRequest.setFilterType(StaysFilterType.ARRIVAL_DATE);
    staysRequest.setFilterValue("2023-10-01");
    StaysResponse responseArrivalDate = sut.retrieveCdhBbBookingsV2(staysRequest, cdhEmployeeDetails, 1, 20);
    assertNotNull(responseArrivalDate, "Response should not be null for filter type: ARRIVAL_DATE");
  }

  private CdhReservationSearchDto getCdhBookingsV2() throws IOException {
    return objectMapper.readValue(
        new File("src/test/resources/mapping/cdh/CdhBookingsV2.json"),
        CdhReservationSearchDto.class);
  }

}
