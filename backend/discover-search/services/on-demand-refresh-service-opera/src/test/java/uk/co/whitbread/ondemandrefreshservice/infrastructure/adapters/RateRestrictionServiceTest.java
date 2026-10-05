package uk.co.whitbread.ondemandrefreshservice.infrastructure.adapters;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static uk.co.whitbread.ondemandrefreshservice.utils.MockDataReader.generateMockRsp;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.model.raterestriction.RateRestrictionInput;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.model.raterestriction.RateRestrictionResponse;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.model.raterestriction.RestrictionSets;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.model.raterestriction.RestrictionsByDateRange;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.model.raterestriction.RestrictionsByDateRangeParent;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.rest.client.raterestriction.opera.HotelRateRestrictionClient;

@ExtendWith(MockitoExtension.class)
class RateRestrictionServiceTest {

  @Mock
  private HotelRateRestrictionClient hotelRateRestrictionClient;

  @InjectMocks
  private RateRestrictionService rateRestrictionOutPort;

  private final String hotelId = "TKINPT";
  private static final LocalDate startDate = LocalDate.parse("2022-11-01");
  private static final LocalDate endDate = LocalDate.parse("2022-11-30");

  @Test
  void searchRateRestrictionCriteriaTest() {
    final RateRestrictionResponse rateRestrictionResponse = buildRateRestrictionResponse();

    Mockito.doReturn(rateRestrictionResponse).when(hotelRateRestrictionClient)
        .searchRateRestrictionCriteria(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
    final RateRestrictionInput rateRestrictionInput1 = buildRateRestrictionInput();
    RateRestrictionResponse restrictionResponse =
        rateRestrictionOutPort.searchRateRestrictionCriteria(rateRestrictionInput1);
    assertEquals(restrictionResponse.getRestrictionsByDateRange().getRestrictionsByDateRange().getHotelId(), hotelId);

  }

  private RateRestrictionResponse buildRateRestrictionResponse() {

    RestrictionsByDateRangeParent restrictionsByDateRangeParent = new RestrictionsByDateRangeParent();
    RestrictionsByDateRange restrictionsByDateRange = new RestrictionsByDateRange();
    restrictionsByDateRange.setHotelId(hotelId);
    restrictionsByDateRangeParent.setRestrictionsByDateRange(restrictionsByDateRange);

    return RateRestrictionResponse.builder()
        .restrictionsByDateRange(restrictionsByDateRangeParent)
        .links(Collections.emptyList())
        .build();

  }

  private RateRestrictionInput buildRateRestrictionInput() {
    return RateRestrictionInput.builder()
        .startDate("2023-10-01")
        .endDate("2023-10-25")
        .hotelId("TKINPT")
        .build();
  }

  @Test
  void getRateRestrictions_Success() throws IOException {
    final RateRestrictionResponse expectedRateRestrictions =
        generateMockRsp("/mock_data/los_restriction_rsp.json", RateRestrictionResponse.class);

    Mockito.doReturn(expectedRateRestrictions).when(hotelRateRestrictionClient)
        .searchRateRestrictionCriteria(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());

    final RateRestrictionResponse actualRateRestrictions =
        rateRestrictionOutPort.getRateRestrictions(hotelId, startDate, endDate);
    assertNotNull(actualRateRestrictions);

    String expectedLosType = null;
    int expectedLosValue = 0;
    final Optional<RestrictionSets> expectedLos =
        expectedRateRestrictions.getRestrictionsByDateRange().getRestrictionsByDateRange().getRestrictionSets().stream()
            .findFirst();
    if (expectedLos.isPresent()) {
      expectedLosType = expectedLos.get().getRestrictionStatus().getCode();
      expectedLosValue = expectedLos.get().getRestrictionStatus().getUnit();
    }

    String actualLosType = null;
    int actualLosValue = 0;
    final Optional<RestrictionSets> actualLos =
        actualRateRestrictions.getRestrictionsByDateRange().getRestrictionsByDateRange().getRestrictionSets().stream()
            .findFirst();
    if (expectedLos.isPresent()) {
      actualLosType = actualLos.get().getRestrictionStatus().getCode();
      actualLosValue = actualLos.get().getRestrictionStatus().getUnit();
    }

    assertNotNull(actualLosType);
    assertNotEquals(0, actualLosValue);
    assertEquals(expectedLosType, actualLosType);
    assertEquals(expectedLosValue, actualLosValue);
  }

}
