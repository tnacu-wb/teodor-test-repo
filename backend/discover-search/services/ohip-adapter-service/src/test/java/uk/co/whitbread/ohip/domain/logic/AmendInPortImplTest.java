package uk.co.whitbread.ohip.domain.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static wiremock.org.hamcrest.MatcherAssert.assertThat;
import static wiremock.org.hamcrest.Matchers.notNullValue;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.ohip.domain.model.amend.in.AmendSummaryRequest;
import uk.co.whitbread.ohip.domain.model.amend.out.AmendSummaryResponse;
import uk.co.whitbread.ohip.domain.ports.secondary.AmendOutPort;

@ExtendWith(MockitoExtension.class)
class AmendInPortImplTest {

  @InjectMocks
  private AmendInPortImpl amendInPort;

  @Mock
  private AmendOutPort amendOutPort;

  @Test
  void getAmendSummary__ShouldReturnOK() {
    //Arrange
    var request = mockAmendSummaryRequest();
    when(amendOutPort.getRateInfoSummary(any())).thenReturn(createAmendSummaryResponse());

    //Act
    var response = amendInPort.getAmendSummary(request);

    //Assert
    assertThat(response, notNullValue());
    assertEquals(new BigDecimal("257"), response.getTotalCostOfStay());
    assertEquals(new BigDecimal("0"), response.getOutStandingCostOfStay());

    verifyNoMoreInteractions(amendOutPort);

  }

  private AmendSummaryRequest mockAmendSummaryRequest(){
    var request = new AmendSummaryRequest();
    request.setHotelId("HOTELID");
    request.setReservationIds(List.of("789"));
    return request;
  }

  private AmendSummaryResponse createAmendSummaryResponse() {
    return AmendSummaryResponse.builder().net(new BigDecimal("257")).deposit(Map.of("1", new BigDecimal("257")))
        .totalCostOfStay(new BigDecimal("257")).outStandingCostOfStay(new BigDecimal("0"))
        .guestPay(Map.of("1", new BigDecimal("257"))).build();
  }
}
