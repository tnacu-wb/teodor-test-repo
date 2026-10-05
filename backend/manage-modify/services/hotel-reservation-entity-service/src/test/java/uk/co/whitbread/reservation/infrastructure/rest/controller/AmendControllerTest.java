package uk.co.whitbread.reservation.infrastructure.rest.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.AssertionErrors;
import uk.co.whitbread.reservation.domain.model.amend.in.AmendConfirmationPricesRequest;
import uk.co.whitbread.reservation.domain.model.amend.in.AmendPaymentPageRequest;
import uk.co.whitbread.reservation.domain.model.amend.in.ConfirmAmendLogicRequest;
import uk.co.whitbread.reservation.domain.model.amend.out.AmendConfirmationPricesResponse;
import uk.co.whitbread.reservation.domain.model.amend.out.AmendPaymentPageResponse;
import uk.co.whitbread.reservation.domain.model.amend.out.ConfirmAmendLogicResponse;
import uk.co.whitbread.reservation.domain.ports.primary.AmendLogicInPort;
import uk.co.whitbread.reservation.infrastructure.rest.controller.amend.AmendController;
import uk.co.whitbread.reservation.infrastructure.rest.controller.amend.mapper.AmendConfirmationPricesRequestMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.amend.mapper.AmendConfirmationPricesResponseMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.amend.mapper.AmendPaymentPageRequestMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.amend.mapper.AmendPaymentPageResponseMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.amend.mapper.ConfirmAmendLogicRequestMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.amend.mapper.ConfirmAmendLogicResponseMapper;
import uk.co.whitbread.reservation.infrastructure.rest.controller.amend.model.in.AmendConfirmationPricesRequestDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.amend.model.in.AmendPaymentPageRequestDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.amend.model.in.ConfirmAmendLogicRequestDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.amend.model.out.AmendConfirmationPricesResponseDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.amend.model.out.AmendPaymentPageResponseDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.amend.model.out.ConfirmAmendLogicResponseDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.amend.model.out.InitiatePaymentResponseDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.amend.model.out.PaymentRequiredDetailsDto;
import uk.co.whitbread.reservation.infrastructure.rest.controller.amend.model.out.PaymentStatusDto;

@ExtendWith(MockitoExtension.class)
class AmendControllerTest {

  @InjectMocks
  AmendController amendControllerUnderTest;
  @Mock
  AmendConfirmationPricesRequestMapper amendConfirmationPricesRequestMapper;
  @Mock
  AmendLogicInPort amendLogicInPort;
  @Mock
  AmendConfirmationPricesResponseMapper amendConfirmationPricesResponseMapper;
  @Mock
  ConfirmAmendLogicRequestMapper confirmAmendLogicRequestMapper;
  @Mock
  ConfirmAmendLogicResponseMapper confirmAmendLogicResponseMapper;
  @Mock
  AmendPaymentPageRequestMapper amendPaymentPageRequestMapper;
  @Mock
  AmendPaymentPageResponseMapper amendPaymentPageResponseMapper;


  @Test
  void getAmendConfirmationPrices_success() {

    //Arrange
    when(amendConfirmationPricesRequestMapper.toModel(any())).thenReturn(
        new AmendConfirmationPricesRequest());
    when(amendLogicInPort.getAmendConfirmationPrices(any())).thenReturn(
        AmendConfirmationPricesResponse.builder().build());
    when(amendConfirmationPricesResponseMapper.toDto(
        any())).thenReturn(new AmendConfirmationPricesResponseDto());

    //Act

    final ResponseEntity<AmendConfirmationPricesResponseDto> response =
        amendControllerUnderTest.amendConfirmationPrices(new AmendConfirmationPricesRequestDto());

    //Assert
    assertNotNull(response);
    AssertionErrors.assertEquals(response.toString(), 200, response.getStatusCode().value());
  }

  @Test
  void getAmendPaymentPage_success() {

    //Arrange
    when(amendPaymentPageRequestMapper.toModel(any())).thenReturn(
        new AmendPaymentPageRequest());
    when(amendLogicInPort.amendPaymentPage(any())).thenReturn(
        AmendPaymentPageResponse.builder().build());
    when(amendPaymentPageResponseMapper.toDto(any())).thenReturn(
        new AmendPaymentPageResponseDto());

    //Act
    final ResponseEntity<AmendPaymentPageResponseDto> response =
        amendControllerUnderTest.getAmendPaymentPage(new AmendPaymentPageRequestDto());

    //Assert
    assertNotNull(response);
    AssertionErrors.assertEquals(response.toString(), 200, response.getStatusCode().value());
  }

  @Test
  void confirmAmendLogic_success() {

    //Arrange
    when(confirmAmendLogicRequestMapper.toModel(any())).thenReturn(
        new ConfirmAmendLogicRequest());
    when(amendLogicInPort.confirmAmendLogic(any())).thenReturn(
        ConfirmAmendLogicResponse.builder().build());
    when(confirmAmendLogicResponseMapper.toDto(
        any())).thenReturn(mockConfirmAmendLogicResponseDto());

    //Act

    final ResponseEntity<ConfirmAmendLogicResponseDto> response =
        amendControllerUnderTest.confirmAmendLogic(new ConfirmAmendLogicRequestDto());

    //Assert
    assertNotNull(response);
    AssertionErrors.assertEquals(response.toString(), 200, response.getStatusCode().value());
  }

  private ConfirmAmendLogicResponseDto mockConfirmAmendLogicResponseDto() {
    PaymentRequiredDetailsDto paymentRequiredDetailsDto =
        new PaymentRequiredDetailsDto("test", "testTemplate", "sessionId");
    InitiatePaymentResponseDto initiatePaymentResponseDto =
        new InitiatePaymentResponseDto(PaymentStatusDto.PAYMENT_REQUIRED, paymentRequiredDetailsDto);
    return new ConfirmAmendLogicResponseDto(initiatePaymentResponseDto);
  }
}
