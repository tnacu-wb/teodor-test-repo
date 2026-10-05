package uk.co.whitbread.ohip.infrastructure.rest.client.amend;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RateInfo;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationRateSummaryType;
import uk.co.whitbread.ohip.domain.model.amend.in.AmendSummaryRequest;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.ohip.OhipReservationClient;

@ExtendWith(MockitoExtension.class)
class AmendOutPortImplTest {

  @InjectMocks
  private AmendOutPortImpl amendOutPort;

  @Mock
  private OhipReservationClient ohipReservationClient;

  @Test
  void getRateInfoSummary__ShouldReturnOK() {
    //Arrange
    var request = mockAmendSummaryRequest();
    when(ohipReservationClient.getRateInfo(anyString(), anyString(), anyString(),
        anyString())).thenReturn(mockRateInfo());

    //Act
    var amendSummaryResponse = amendOutPort.getRateInfoSummary(request);

    //Assert
    assertThat(amendSummaryResponse, notNullValue());
    assertThat(amendSummaryResponse.getTotalCostOfStay(), is(new BigDecimal("236")));
  }

  private AmendSummaryRequest mockAmendSummaryRequest(){
    var request = new AmendSummaryRequest();
    request.setHotelId("HOTEL_ID");
    request.setReservationIds(List.of("1234", "5678"));
    return request;
  }

  private RateInfo mockRateInfo(){
    var summary = new ReservationRateSummaryType();
    summary.setNet(new BigDecimal("118"));
    summary.deposit(new BigDecimal("118"));
    summary.setTotalCostOfStay(new BigDecimal("118"));
    summary.setOutStandingCostOfStay(new BigDecimal("0"));
    summary.setGuestPay(new BigDecimal("0"));
    var rateInfo = new RateInfo();
    rateInfo.setSummary(summary);
    return rateInfo;
  }
}
