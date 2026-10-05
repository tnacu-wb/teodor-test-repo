package uk.co.whitbread.hotel.card.controller;

import static uk.co.whitbread.hotel.card.utils.SanitizingUtils.sanitize;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.common.exceptions.entity.ErrorResponse;
import uk.co.whitbread.hotel.card.model.PaymentCard;
import uk.co.whitbread.hotel.card.service.CdhHotelCardsService;
import uk.co.whitbread.shared.auth.account.CdhEmployeeDetails;
import uk.co.whitbread.shared.auth.service.TokenService;


@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/customers/hotels")
@Tag(name = "Hotel Cards operations")
public class HotelCardsController {

    private final CdhHotelCardsService cdhHotelCardsService;
    private final TokenService tokenService;

    @Operation(method = "getCard", summary = "Get Card",
        description = "This endpoint retrieves an existing card. ")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Success"),
        @ApiResponse(responseCode = "400", description = "Bad Request: please check your input",
            content = {@Content(mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class))}),
        @ApiResponse(responseCode = "404", description = "Not Found: card not found for given customer-id, card-id",
            content = {@Content(mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class))}),
        @ApiResponse(responseCode = "500", description = "Server Error: unrecoverable error server-side",
            content = {@Content(mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class))})})
    @RequestMapping(value = "/{customer-id}/cards/{card-id}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PaymentCard> getPaymentCard(
        @Parameter(required = true) @PathVariable("customer-id") String customerId,
        @Parameter(required = true) @PathVariable("card-id") String cardId,
        @Parameter(required = true) @RequestHeader(value = "Authorization") String authorization,
        @Parameter @RequestParam(name = "unmaskCardNumber", required = false) Boolean unmaskCardNumber,
        @Parameter @RequestParam(required = false) boolean business) {
        log.info(
            "Called /customers/hotels/{}/cards/{} (GET); business: {}",
            sanitize(customerId), sanitize(cardId), business);
        CdhEmployeeDetails cdhEmployeeDetails = tokenService
            .retrieveCdhEmployeeDetailsAndVerifyToken(authorization);
        PaymentCard card = cdhHotelCardsService.getPaymentCard(cdhEmployeeDetails, business);
        return new ResponseEntity<>(card, HttpStatus.OK);

    }

    @Operation(method = "createCard", summary = "Create Card",
        description = "This endpoint creates a new payment card")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Created",
            headers =
            @Header(name = "Location",
                            description = "Provides an identifier for the primary resource created")),
        @ApiResponse(responseCode = "400", description = "Bad Request: please check your input",
            content = {@Content(mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class))}),
        @ApiResponse(responseCode = "500", description = "Server Error: unrecoverable error server-side",
            content = {@Content(mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class))})})
    @RequestMapping(value = "/{customer-id}/cards", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Void> createPaymentCard(
        @Parameter(required = true) @PathVariable(value = "customer-id") String customerId,
        @Parameter(required = true) @RequestHeader(value = "Authorization") String authorization,
        @Parameter @RequestParam(required = false) Boolean business,
        @Parameter(required = true, name = "payload", description = "The PaymentCard JSON payload") @Valid @RequestBody PaymentCard paymentCard) {
        log.debug("Called /customers/hotels/{}/cards (POST) (b:{})", sanitize(customerId), business);
        CdhEmployeeDetails cdhEmployeeDetails = tokenService.retrieveCdhEmployeeDetailsAndVerifyToken(authorization);
        cdhHotelCardsService.addOrUpdatePaymentCard(paymentCard, cdhEmployeeDetails, business);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Operation(method = "updateCard", summary = "Update Card",
        description = "This endpoint updates an existing payment card")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Success. No content."),
        @ApiResponse(responseCode = "400", description = "Bad Request: please check your input",
            content = {@Content(mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class))}),
        @ApiResponse(responseCode = "404", description = "Not Found: card not found for given customer-id, card-id",
            content = {@Content(mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class))}),
        @ApiResponse(responseCode = "500", description = "Server Error: unrecoverable error server-side",
            content = {@Content(mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class))})})
    @RequestMapping(value = "/{customer-id}/cards/{card-id}", method = RequestMethod.PUT,
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> updatePaymentCard(
        @Parameter(required = true) @RequestHeader(value = "Authorization") String authorization,
        @Parameter @RequestParam(required = false) Boolean business,
        @Parameter(required = true) @PathVariable(value = "customer-id") String customerId,
        @Parameter(required = true) @PathVariable(value = "card-id") String cardId,
        @Parameter(required = true, name = "payload", description = "The PaymentCard JSON payload") @Valid @RequestBody PaymentCard paymentCard) {
        log.debug("Called /customers/hotels/{}/cards/{} (PUT) (b:{})",
            sanitize(customerId), sanitize(cardId), business);
        CdhEmployeeDetails cdhEmployeeDetails = tokenService.retrieveCdhEmployeeDetailsAndVerifyToken(authorization);
        cdhHotelCardsService.addOrUpdatePaymentCard(paymentCard, cdhEmployeeDetails, business);

        return ResponseEntity.noContent().build();
    }

    @Operation(method = "deleteCard", summary = "Delete Card",
        description = "This endpoint deletes an existing payment card")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Success. No content."),
        @ApiResponse(responseCode = "400", description = "Bad Request: please check your input",
            content = {@Content(mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class))}),
        @ApiResponse(responseCode = "500", description = "Server Error: unrecoverable error server-side",
            content = {@Content(mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class))})})
    @RequestMapping(value = "/{customer-id}/cards/{card-id}", method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deletePaymentCard(
        @Parameter(required = true) @PathVariable("customer-id") String customerId,
        @Parameter(required = true) @PathVariable("card-id") String cardId,
        @Parameter(required = true) @RequestHeader(value = "Authorization") String authorization,
        @Parameter @RequestParam(required = false) Boolean business) {
        log.debug("Called /customers/hotels/{}/cards/{} (DELETE) (b:{},companyCard:{})",
            sanitize(customerId), sanitize(cardId), business, sanitize(cardId));

        CdhEmployeeDetails cdhEmployeeDetails = tokenService.retrieveCdhEmployeeDetailsAndVerifyToken(authorization);
        cdhHotelCardsService.deletePaymentCard(cdhEmployeeDetails);

        return ResponseEntity.noContent().build();
    }
}