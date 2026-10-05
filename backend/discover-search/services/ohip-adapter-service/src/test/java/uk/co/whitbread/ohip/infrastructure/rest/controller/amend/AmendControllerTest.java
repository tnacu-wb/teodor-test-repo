package uk.co.whitbread.ohip.infrastructure.rest.controller.amend;

import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertEquals;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import uk.co.whitbread.ohip.domain.model.amend.in.AmendSummaryRequest;
import uk.co.whitbread.ohip.domain.model.amend.out.AmendSummaryResponse;
import uk.co.whitbread.ohip.domain.ports.primary.AmendInPort;
import uk.co.whitbread.ohip.infrastructure.rest.controller.amend.mapper.AmendSummaryRequestMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.amend.mapper.AmendSummaryResponseMapper;
import uk.co.whitbread.ohip.infrastructure.rest.controller.amend.model.in.AmendSummaryRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.controller.amend.model.out.AmendSummaryResponseDto;

@ExtendWith(MockitoExtension.class)
class AmendControllerTest {

  @InjectMocks
  AmendController amendController;

  @Mock
  private AmendInPort amendInPort;

  @Mock
  private AmendSummaryRequestMapper amendSummaryRequestMapper;

  @Mock
  private AmendSummaryResponseMapper amendSummaryResponseMapper;

  @Test
  void getReservationDetailsForAmend__ShouldReturnOk() {
    //Arrange
    AmendSummaryRequestDto amendSummaryRequestDto = createAmendSummaryRequestDto();

    when(amendSummaryRequestMapper.toModel(amendSummaryRequestDto)).thenReturn(
        createAmendSummaryRequest());
    when(amendInPort.getAmendSummary(createAmendSummaryRequest())).thenReturn(
        createAmendSummaryResponse());
    when(amendSummaryResponseMapper.toDto(createAmendSummaryResponse())).thenReturn(
        createAmendSummaryResponseDto());

    //act
    AmendSummaryRequest request = amendSummaryRequestMapper.toModel(amendSummaryRequestDto);
    AmendSummaryResponse amendSummaryResponse = amendInPort.getAmendSummary(request);
    AmendSummaryResponseDto amendSummaryResponseDto =
        amendSummaryResponseMapper.toDto(amendSummaryResponse);
    ResponseEntity<AmendSummaryResponseDto> response =
        amendController.getReservationDetailsForAmend(amendSummaryRequestDto);

    //Assert
    Assertions.assertNotNull(response);
    assertEquals(response.toString(), 200, response.getStatusCode().value());
    assertEquals(response.toString(), amendSummaryResponseDto.getNet(),
        Objects.requireNonNull(response.getBody()).getNet());
  }

  private AmendSummaryRequestDto createAmendSummaryRequestDto(){
    var request = new AmendSummaryRequestDto();
    request.setHotelId("HOTEL_ID");
    request.setReservationIds(List.of("1234"));
    return request;
  }

  private AmendSummaryRequest createAmendSummaryRequest(){
    var request = new AmendSummaryRequest();
    request.setHotelId("HOTEL_ID");
    request.setReservationIds(List.of("1234"));
    return request;
  }

  private AmendSummaryResponse createAmendSummaryResponse() {
    return AmendSummaryResponse.builder().net(new BigDecimal("118")).deposit(Map.of("1", new BigDecimal("118")))
        .totalCostOfStay(new BigDecimal("118")).outStandingCostOfStay(new BigDecimal("0"))
        .guestPay(Map.of("1", new BigDecimal("118"))).build();
  }
  private AmendSummaryResponseDto createAmendSummaryResponseDto() {
    return AmendSummaryResponseDto.builder().net(new BigDecimal("118")).deposit(Map.of("1", new BigDecimal("118")))
        .totalCostOfStay(new BigDecimal("118")).outStandingCostOfStay(new BigDecimal("0"))
        .guestPay(Map.of("1", new BigDecimal("118"))).build();
  }
}
