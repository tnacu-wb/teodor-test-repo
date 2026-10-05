package uk.co.whitbread.payments.infrastructure.rest.controller.payment;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.payments.domain.ports.primary.PaymentActionsPort;
import uk.co.whitbread.payments.domain.ports.primary.PaymentMethodsPort;
import uk.co.whitbread.payments.domain.ports.primary.PaymentsCcuiMethodsPort;
import uk.co.whitbread.payments.infrastructure.rest.controller.payment.mapper.PaymentActionsMapper;
import uk.co.whitbread.payments.infrastructure.rest.controller.payment.mapper.PaymentMethodsMapper;
import uk.co.whitbread.payments.infrastructure.rest.controller.payment.model.in.BookingChannel;
import uk.co.whitbread.payments.infrastructure.rest.controller.payment.model.in.PaymentMethodsCriteriaDto;
import uk.co.whitbread.payments.infrastructure.rest.controller.payment.model.in.SelectedPaymentMethodsDto;
import uk.co.whitbread.payments.infrastructure.rest.controller.payment.model.out.PaymentActionResponseDto;
import uk.co.whitbread.payments.infrastructure.rest.controller.payment.model.out.PaymentMethodDto;
import uk.co.whitbread.shared.auth.security.AuthenticatedUserService;

@Slf4j
@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class PaymentMethodsController implements PaymentMethodsApiDocumentation {

  private final PaymentMethodsPort paymentMethodsPort;
  private final PaymentsCcuiMethodsPort paymentsCcuiMethodsPort;
  private final PaymentActionsPort paymentActionsPort;
  private final PaymentMethodsMapper paymentMethodsMapper;
  private final PaymentActionsMapper paymentActionsMapper;
  private final AuthenticatedUserService authenticatedUserService;
  private static final String WB_AUTHORIZATION = "WB-Authorization";

  @Override
  @GetMapping(value = PAYMENT_METHODS_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<PaymentMethodDto>> getPaymentMethods(
      @Valid PaymentMethodsCriteriaDto criteria,
      @RequestHeader(value = "bookingChannel", required = false) BookingChannel bookingChannel,
      @RequestHeader(value = WB_AUTHORIZATION, required = false) String authorization) {

    if (!isRequestValid(authorization)) {
      return ResponseEntity.status(UNAUTHORIZED).build();
    }

    var request = paymentMethodsMapper.toModel(criteria, authorization, bookingChannel);
    var paymentMethods = paymentMethodsPort.getPaymentMethods(request);
    var response = paymentMethodsMapper.toDto(paymentMethods.getPaymentMethods());
    return ResponseEntity.ok(response);
  }

  @Override
  @PostMapping(value = PAYMENT_METHODS_PATH, produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> validateSelectedPaymentMethod(
      @Valid @RequestBody SelectedPaymentMethodsDto request) {
    var method = paymentMethodsMapper.toModel(request);
    paymentMethodsPort.validatePaymentMethods(method);
    return ResponseEntity.ok().build();
  }

  @Override
  @GetMapping(value = PAYMENT_CCUI_METHODS_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<PaymentMethodDto>> getCcuiPaymentMethods(
      @Valid PaymentMethodsCriteriaDto criteria,
      @RequestHeader(value = "bookingChannel", required = false) BookingChannel bookingChannel,
      @RequestHeader(value = AUTHORIZATION, required = false) String authorization) {

    if (!isRequestValid(authorization)) {
      return ResponseEntity.status(UNAUTHORIZED).build();
    }

    var request = paymentMethodsMapper.toModel(criteria, authorization, bookingChannel);
    var paymentMethods = paymentsCcuiMethodsPort.getPaymentMethods(request);
    var response = paymentMethodsMapper.toDto(paymentMethods.getPaymentMethods());
    return ResponseEntity.ok(response);
  }

  @Override
  @GetMapping(value = PAYMENT_ACTIONS_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<PaymentActionResponseDto> getPaymentActions(
      @PathVariable String basketReference) {

    var paymentActions = paymentActionsPort.determinePaymentActions(basketReference);
    var response = paymentActionsMapper.toDto(paymentActions);

    return ResponseEntity.ok(response);
  }

  private boolean isRequestValid(String authorization) {

    return (authenticatedUserService.isUserAuthenticated()
        || Objects.isNull(authorization));
  }
}
