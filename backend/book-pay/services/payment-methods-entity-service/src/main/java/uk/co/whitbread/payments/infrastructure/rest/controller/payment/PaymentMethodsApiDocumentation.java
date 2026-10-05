package uk.co.whitbread.payments.infrastructure.rest.controller.payment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.List;
import org.springdoc.core.annotations.ParameterObject;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMethod;
import uk.co.whitbread.payments.infrastructure.rest.controller.payment.model.in.BookingChannel;
import uk.co.whitbread.payments.infrastructure.rest.controller.payment.model.in.PaymentMethodsCriteriaDto;
import uk.co.whitbread.payments.infrastructure.rest.controller.payment.model.in.SelectedPaymentMethodsDto;
import uk.co.whitbread.payments.infrastructure.rest.controller.payment.model.out.PaymentActionResponseDto;
import uk.co.whitbread.payments.infrastructure.rest.controller.payment.model.out.PaymentMethodDto;

public interface PaymentMethodsApiDocumentation {

  String PAYMENT_METHODS_PATH = "/payment-methods";
  String PAYMENT_CCUI_METHODS_PATH = "/payment-methods/ccui";
  String PAYMENT_ACTIONS_PATH = "/payment-methods/payment-actions/{basketReference}";

  @RouterOperations({@RouterOperation(method = RequestMethod.GET,
      path = PAYMENT_METHODS_PATH,
      operation = @Operation(operationId = "getPaymentMethods",
          summary = "Retrieves available payment methods for a reservation basket",
          parameters = {
              @Parameter(in = ParameterIn.QUERY, name = "basketReference", required = true,
                  description = "Basket reference for which the payment methods needs to be retrieved."),
              @Parameter(in = ParameterIn.QUERY, name = "country", example = "gb", required = true,
                  description = "Country to retrieve hotel payment details"),
              @Parameter(in = ParameterIn.QUERY, name = "language", example = "en", required = true,
                  description = "Language to retrieve hotel payment details"),
              @Parameter(in = ParameterIn.QUERY, name = "userType",
                  description = "Customer account type - required only for logged-in users to load their saved cards"),
              @Parameter(in = ParameterIn.HEADER, name = "Authorization",
                  description = "Authorization token - required only for logged-in users"),
              @Parameter(in = ParameterIn.HEADER, name = "bookingChannel",
                  description = "Channel name (Web/Mobile)")
          },
          responses = {
              @ApiResponse(responseCode = "200", description = "Payment methods successfully retrieved",
                  content = @Content(array = @ArraySchema(schema = @Schema(implementation = PaymentMethodDto.class)))),
              @ApiResponse(responseCode = "400", description = "Missing or invalid request"),
              @ApiResponse(responseCode = "404", description = "No payments methods found"),
              @ApiResponse(responseCode = "500", description = "Internal server error")
          }))
  })
  ResponseEntity<List<PaymentMethodDto>> getPaymentMethods(
      @ParameterObject PaymentMethodsCriteriaDto criteriaDto,
      BookingChannel bookingChannel,
      String authorization);


  @RouterOperations({@RouterOperation(method = RequestMethod.POST,
      path = PAYMENT_METHODS_PATH,
      operation = @Operation(operationId = "validateSelectedPaymentMethod",
          summary =
              "Verifies if the selected payment option is allowed. This operation is necessary, "
                  + "before allowing the payment process to proceed, to avoid scenarios where malicious "
                  + "users bypass a mandatory prepayment)",
          responses = {
              @ApiResponse(responseCode = "200", description = "If the selected payment option/method is valid"),
              @ApiResponse(responseCode = "400",
                  description = "If the selected payment option is not allowed for the reservation"),
              @ApiResponse(responseCode = "404", description = "The basketReference was not found"),
              @ApiResponse(responseCode = "500", description = "Internal server error")
          }))
  })
  ResponseEntity<Void> validateSelectedPaymentMethod(
      @RequestBody(description = "Details of the Item to be created", required = true,
          content = @Content(schema = @Schema(implementation = SelectedPaymentMethodsDto.class)))
      SelectedPaymentMethodsDto method);

  @RouterOperations({@RouterOperation(method = RequestMethod.GET,
      path = PAYMENT_CCUI_METHODS_PATH,
      operation = @Operation(operationId = "getPaymentMethods",
          summary = "Retrieves available payment methods for a reservation basket for CCUI",
          parameters = {
              @Parameter(in = ParameterIn.QUERY, name = "basketReference", required = true,
                  description = "Basket reference for which the payment methods needs to be retrieved."),
              @Parameter(in = ParameterIn.QUERY, name = "country", example = "gb", required = true,
                  description = "Country to retrieve hotel payment details"),
              @Parameter(in = ParameterIn.QUERY, name = "language", example = "en", required = true,
                  description = "Language to retrieve hotel payment details"),
              @Parameter(in = ParameterIn.QUERY, name = "userType",
                  description = "Customer account type - required only for logged-in users to load their saved cards"),
              @Parameter(in = ParameterIn.HEADER, name = "Authorization",
                  description = "Authorization token - required only for logged-in users"),
              @Parameter(in = ParameterIn.HEADER, name = "bookingChannel",
                  description = "Channel name (Web/Mobile)")
          },
          responses = {
              @ApiResponse(responseCode = "200", description = "Payment methods successfully retrieved",
                  content = @Content(array = @ArraySchema(schema = @Schema(implementation = PaymentMethodDto.class)))),
              @ApiResponse(responseCode = "400", description = "Missing or invalid request"),
              @ApiResponse(responseCode = "404", description = "No payments methods found"),
              @ApiResponse(responseCode = "500", description = "Internal server error")
          }))
  })
  ResponseEntity<List<PaymentMethodDto>> getCcuiPaymentMethods(
      @ParameterObject PaymentMethodsCriteriaDto criteriaDto,
      BookingChannel bookingChannel,
      String authorization);

  @RouterOperations({@RouterOperation(method = RequestMethod.GET,
      path = PAYMENT_ACTIONS_PATH,
      operation = @Operation(operationId = "getPaymentActions",
          summary = "Determine payment actions for a booking",
          parameters = {
              @Parameter(in = ParameterIn.PATH, name = "basketReference", required = true,
                  description = "Used to identify the booking and extract all necessary info")
          },
          responses = {
              @ApiResponse(responseCode = "200",
                  description = "Payment actions determined successfully",
                  content = @Content(schema = @Schema(
                      implementation = PaymentActionResponseDto.class))),
              @ApiResponse(responseCode = "404", description = "Reservation not found"),
              @ApiResponse(responseCode = "500", description = "Internal server error")
          }))
  })
  ResponseEntity<PaymentActionResponseDto> getPaymentActions(
      String basketReference);
}