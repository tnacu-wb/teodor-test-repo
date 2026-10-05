package uk.co.whitbread.basket.infrastructure.rest.controller.ccui.eckoh;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.basket.domain.ports.primary.CcuiEckohInPort;
import uk.co.whitbread.basket.infrastructure.rest.controller.ccui.eckoh.mapper.CcuiEckohMapper;
import uk.co.whitbread.basket.infrastructure.rest.controller.ccui.eckoh.model.in.EckohPaymentRequestDto;
import uk.co.whitbread.basket.infrastructure.rest.controller.ccui.eckoh.model.out.EckohPaymentResponseDto;
import uk.co.whitbread.basket.infrastructure.rest.controller.ccui.eckoh.model.out.PaymentStatusResponseDto;

@RestController
@RequestMapping("/v1/baskets/ccui")
@RequiredArgsConstructor
public class CcuiEckohController implements CcuiEckohApiDocumentation {

  private final CcuiEckohInPort ccuiEckohInPort;
  private final CcuiEckohMapper ccuiEckohMapper;

  @Override
  @PostMapping(value = "/{basket-reference}/eckoh", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("@permissionEvaluator.hasAccess('CC_Role12')")
  public ResponseEntity<EckohPaymentResponseDto> initiateEckohPayment(
      @PathVariable("basket-reference") @NotNull String reference,
      @RequestBody @Valid EckohPaymentRequestDto eckohPaymentRequestDto) {
    final var eckohPaymentRequest = ccuiEckohMapper.toEckohPaymentRequestDto(
        eckohPaymentRequestDto);
    final var paymentResponse = ccuiEckohInPort.getEckohPayment(reference, eckohPaymentRequest);

    return ResponseEntity.status(HttpStatus.OK)
        .body(ccuiEckohMapper.toEckohPaymentResponseDto(paymentResponse));
  }

  @Override
  @GetMapping(value = "/{basket-reference}/eckoh", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("@permissionEvaluator.hasAccess('CC_Role12')")
  public ResponseEntity<PaymentStatusResponseDto> getEckohRecordingStatus(
      @PathVariable("basket-reference") @NotNull String reference) {
    var paymentStatusResponse = ccuiEckohInPort.paymentStatus(reference);
    return ResponseEntity.status(HttpStatus.OK)
        .body(ccuiEckohMapper.toDto(paymentStatusResponse));
  }
}
