package uk.co.whitbread.basket.infrastructure.rest.controller.ccui.payment;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.basket.domain.ports.primary.CcuiPaymentInPort;
import uk.co.whitbread.basket.infrastructure.rest.controller.ccui.payment.mapper.CcuiPaymentMapper;
import uk.co.whitbread.basket.infrastructure.rest.controller.ccui.payment.mapper.CcuiUpdateDiscountMapper;
import uk.co.whitbread.basket.infrastructure.rest.controller.ccui.payment.model.in.CcuiPaymentRequestDto;
import uk.co.whitbread.basket.infrastructure.rest.controller.ccui.payment.model.in.UpdateDiscountRequestDto;
import uk.co.whitbread.basket.infrastructure.rest.controller.ccui.payment.model.out.PaymentCcuiResponseDto;

@RestController
@RequestMapping("/v1/baskets/ccui")
@RequiredArgsConstructor
public class CcuiPaymentController implements CcuiPaymentApiDocumentation {

  private final CcuiPaymentMapper ccuiPaymentMapper;
  private final CcuiPaymentInPort ccuiPaymentInPort;
  private final CcuiUpdateDiscountMapper ccuiUpdateDiscountMapper;

  @Override
  @PostMapping(value = "/{basket-reference}/pay", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("@permissionEvaluator.hasAccess('CC_Role12')")
  public ResponseEntity<PaymentCcuiResponseDto> initiatePaymentProcess(
      @PathVariable("basket-reference") @NotNull final String basketReference,
      @RequestBody final CcuiPaymentRequestDto ccuiPaymentRequestDto) {

    final var paymentRequest = ccuiPaymentMapper.toModel(ccuiPaymentRequestDto);
    final var paymentResponse = ccuiPaymentInPort.initiateCcuiPaymentProcess(basketReference,
        paymentRequest);
    return ResponseEntity.status(HttpStatus.OK).body(ccuiPaymentMapper.toDto(paymentResponse));
  }

  @Override
  @PutMapping(value = "/discount", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("@permissionEvaluator.hasAccess('CC_Role05')")
  public ResponseEntity<Void> updateDiscount(
      @RequestBody final UpdateDiscountRequestDto updateDiscountRequestDto) {
    final var discountRequest = ccuiUpdateDiscountMapper.toModel(updateDiscountRequestDto);

    ccuiPaymentInPort.updateDiscount(discountRequest);

    return ResponseEntity.status(HttpStatus.OK).build();
  }
}
