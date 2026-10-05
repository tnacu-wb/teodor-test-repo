package uk.co.whitbread.hotel.card.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.co.whitbread.common.exceptions.entity.ErrorResponse;
import uk.co.whitbread.hotel.card.model.*;
import uk.co.whitbread.hotel.card.service.CustomerCardsService;
import uk.co.whitbread.hotel.card.service.cards.PaymentCardContext;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v2/customers/cards")
@Tag(name = "Customer Cards operations")
public class CustomerCardsController {

  private final CustomerCardsService customerCardsService;
  private final PaymentCardContext paymentCardContext;

  @Operation(summary = "Initiate card storage session",
      description = "This endpoint starts a new card storage session for the customer.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Initiated"),
      @ApiResponse(responseCode = "400", description = "Bad Request: please check your input",
          content =
              {@Content(mediaType = "application/json", schema =
              @Schema(implementation = ErrorResponse.class))}),
      @ApiResponse(responseCode = "401", description = "Unauthorized",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class))}),
      @ApiResponse(responseCode = "403", description = "Bad Request",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class))}),
      @ApiResponse(responseCode = "500", description = "Server Error: unrecoverable error server-side",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class))})})
  @PostMapping(value = "/session",
      consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<PaymentRequiredDetails> initiateIframeSession(
      @Parameter(required = true) @RequestHeader(value = "Authorization") String authorization,
      @Parameter(required = true, name = "payload", description = "The Initiate PaymentCard JSON payload") @Valid @RequestBody SaveCardRequest saveCardRequest) {
    return ResponseEntity.ok().body(customerCardsService.initiateSave(saveCardRequest, authorization));
  }

  @Operation(method = "createOrUpdatePaymentCard", summary = "Create or Update Payment Card",
      description = "This endpoint creates a new payment card or updates an existing payment card")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Success. No content."),
      @ApiResponse(responseCode = "400", description = "Bad Request: please check your input",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class))}),
      @ApiResponse(responseCode = "404", description = "Not Found: card not found for given customer",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class))}),
      @ApiResponse(responseCode = "500", description = "Server Error: unrecoverable error server-side",
          content = {@Content(mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class))})})
  @PutMapping(value = "",
      consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public ResponseEntity<Void> createOrUpdatePaymentCard(
      @Parameter(required = true, name = "payload", description = "The PaymentCard JSON payload")
      @Valid @RequestBody PaymentCardDTO paymentCard) {
    log.debug("Called /v2/customers/cards (PUT)");

    SaveCardPurpose saveCardPurpose = paymentCardContext.getSaveCardPurpose(paymentCard);
    var finalCard = customerCardsService.mapPaymentCard(saveCardPurpose, paymentCard);
    customerCardsService.updatePaymentCard(saveCardPurpose, finalCard);

    return ResponseEntity.noContent().build();
  }

  @Operation(summary = "Initiate SCA session",
          description = "This endpoint starts a new session with Strong Customer Authentication (SCA) for card storage.")
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "SCA Session Initiated Successfully"),
          @ApiResponse(responseCode = "400", description = "Bad Request: Please check your input",
                  content = {@Content(mediaType = "application/json", schema =
                  @Schema(implementation = ErrorResponse.class))}),
          @ApiResponse(responseCode = "401", description = "Unauthorized",
                  content = {@Content(mediaType = "application/json",
                          schema = @Schema(implementation = ErrorResponse.class))}),
          @ApiResponse(responseCode = "403", description = "Forbidden: Access Denied",
                  content = {@Content(mediaType = "application/json",
                          schema = @Schema(implementation = ErrorResponse.class))}),
          @ApiResponse(responseCode = "500", description = "Server Error: Unrecoverable error on the server-side",
                  content = {@Content(mediaType = "application/json",
                          schema = @Schema(implementation = ErrorResponse.class))})
  })
  @PostMapping(value = "/sca",
          consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<PaymentRequiredDetails> initiateAuthorizeScaIframeSession(
          @Parameter(required = true, name = "payload", description = "The JSON payload to initiate SCA session")
          @Valid @RequestBody AuthorizeScaRequest authorizeScaRequest) {
    return ResponseEntity.ok().body(customerCardsService.initiateAuthorizeSca(authorizeScaRequest));
  }
}
