package uk.co.whitbread.hotel.account.service.cdh;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
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
import uk.co.whitbread.hotel.account.model.StaysFilterType;
import uk.co.whitbread.hotel.account.model.StaysResponse;
import uk.co.whitbread.hotel.account.model.feature.FeatureFlag;
import uk.co.whitbread.hotel.account.model.feature.UnleashWrapper;
import uk.co.whitbread.hotel.account.properties.BookingHistoryProperties;

@ExtendWith(MockitoExtension.class)
class CdhPiBookingsServiceTest {

  private static final String USER_ID = "userId";
  private static final String CUSTOMER_ACCOUNT_ID = "customerAccountId";
  private ObjectMapper objectMapper;

  @Mock
  private CdhService cdhService;

  @InjectMocks
  private CdhPiBookingsService sut;

  private final CdhBookingHistoryRequestMapper bookingHistoryRequestMapper = Mappers.getMapper(CdhBookingHistoryRequestMapper.class);
  private final CdhBookingHistoryResponseMapper bookingHistoryResponseMapper = Mappers.getMapper(CdhBookingHistoryResponseMapper.class);
  @Mock
  private UnleashWrapper<FeatureFlag> unleashWrapper;
  @Mock
  private BookingHistoryProperties bookingHistoryProperties;

  @BeforeEach
  void setUp() {

    objectMapper = new ObjectMapper();
    objectMapper.findAndRegisterModules();
    unleashWrapper = Mockito.mock(UnleashWrapper.class);

    sut = new CdhPiBookingsService(cdhService, bookingHistoryRequestMapper,
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

    StaysResponse staysResponse = sut.retrieveCdhPiBookingsV2(bbStaysRequest, CUSTOMER_ACCOUNT_ID,
        USER_ID, 1, 20);

    assertNotNull(staysResponse);
    assertEquals(10, staysResponse.getPageSize());
    assertEquals(8, staysResponse.getStays().size());
  }

  @Test
  void testRetrieveCdhPiBookingsV2_whenFilterTypeIsNotNull() {
    BBStaysRequest staysRequest = new BBStaysRequest();

    // Test with CONFIRM_NUMBER
    staysRequest.setFilterType(StaysFilterType.CONFIRM_NUMBER);
    staysRequest.setFilterValue("CONFIRM123");
    StaysResponse responseConfirmNumber = sut.retrieveCdhPiBookingsV2(staysRequest, CUSTOMER_ACCOUNT_ID, USER_ID, 1, 20);
    assertNotNull(responseConfirmNumber, "Response should not be null for filter type: CONFIRM_NUMBER");

    // Test with NAME
    staysRequest.setFilterType(StaysFilterType.NAME);
    staysRequest.setFilterValue("John Doe");
    StaysResponse responseName = sut.retrieveCdhPiBookingsV2(staysRequest, CUSTOMER_ACCOUNT_ID, USER_ID, 1, 20);
    assertNotNull(responseName, "Response should not be null for filter type: NAME");

    // Test with ARRIVAL_DATE
    staysRequest.setFilterType(StaysFilterType.ARRIVAL_DATE);
    staysRequest.setFilterValue("2023-10-01");
    StaysResponse responseArrivalDate = sut.retrieveCdhPiBookingsV2(staysRequest, CUSTOMER_ACCOUNT_ID, USER_ID, 1, 20);
    assertNotNull(responseArrivalDate, "Response should not be null for filter type: ARRIVAL_DATE");
  }

  private CdhReservationSearchDto getCdhBookingsV2() throws IOException {
    return objectMapper.readValue(
        new File("src/test/resources/mapping/cdh/CdhBookingsV2.json"),
        CdhReservationSearchDto.class);
  }
}
