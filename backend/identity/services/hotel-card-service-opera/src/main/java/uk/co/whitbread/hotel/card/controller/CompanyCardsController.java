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
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import uk.co.whitbread.common.exceptions.entity.ErrorResponse;
import uk.co.whitbread.hotel.card.model.PaymentCard;
import uk.co.whitbread.hotel.card.service.CdhCompanyCardsService;
import uk.co.whitbread.shared.auth.account.CdhEmployeeDetails;
import uk.co.whitbread.shared.auth.service.TokenService;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/companies")
@Tag(name = "Company Cards operations")
public class CompanyCardsController {

    private final CdhCompanyCardsService cdhCompanyCardsService;
    private final TokenService authTokenService;

    @Operation(method = "getCompanyPaymentCards", summary = "Get all Company Payment Cards",
        description = "This endpoint provides the functionality to retrieve all payment cards for an existing company")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Success"),
        @ApiResponse(responseCode = "400", description = "Bad Request: please check your input",
            content = {@Content(schema = @Schema(implementation = ErrorResponse.class))}),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Bad Request"),
        @ApiResponse(responseCode = "500", description = "Server Error: unrecoverable error server-side",
            content = {@Content(schema = @Schema(implementation = ErrorResponse.class))})})
    @RequestMapping(value = "/{companyId}/cards", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<PaymentCard>> getCompanyPaymentCards(
        @Parameter(required = true) @PathVariable("companyId") String companyId,
        @Parameter(required = true) @RequestHeader(value = "Authorization") String authorization) {

        log.info("Called /companies/{}/cards (GET)", sanitize(companyId));
        CdhEmployeeDetails cdhEmployeeDetails = authTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(
            authorization);
        if (!companyId.equals(cdhEmployeeDetails.getCompanyAccountId())) {
            String sanitizedCompanyId = companyId.replaceAll("[\\r\\n]", "");
            log.warn(
                "Unauthorized access attempt to company cards for companyId: {} by user from a different company: companyAccountId={}, employeeAccountId={}",
                sanitizedCompanyId, cdhEmployeeDetails.getCompanyAccountId(),
                cdhEmployeeDetails.getEmployeeAccountId());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        String userEmail = cdhEmployeeDetails.getUserEmail();
        List<PaymentCard> paymentCards = cdhCompanyCardsService.getPaymentCards(companyId,
            userEmail);
        return new ResponseEntity<>(paymentCards, HttpStatus.OK);
    }

    @Operation(method = "createCompanyPaymentCard", summary = "Create Company Card",
        description = "This endpoint creates a new company payment card")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Created",
            headers =
            @Header(name = "Location",
                            description = "Provides an identifier for the primary resource created")),
        @ApiResponse(responseCode = "400", description = "Bad Request: please check your input",
            content =
                {@Content(mediaType = "application/json", schema =
                @Schema(implementation = ErrorResponse.class))}),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Bad Request"),
        @ApiResponse(responseCode = "500", description = "Server Error: unrecoverable error server-side",
            content = {@Content(mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class))})})
    @RequestMapping(value = "/admin/{companyId}/cards", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Void> createCompanyPaymentCard(
        @Parameter(required = true) @PathVariable("companyId") String companyId,
        @Parameter(required = true) @RequestHeader(value = "Authorization") String authorization,
        @Parameter(required = true, name = "payload", description = "The PaymentCard JSON payload") @Valid @RequestBody PaymentCard paymentCard) {
        log.debug("Called /companies/{}/cards (POST)", sanitize(companyId));

        CdhEmployeeDetails cdhEmployeeDetails = authTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(
            authorization);
        String userEmail = cdhEmployeeDetails.getUserEmail();
        var response = cdhCompanyCardsService.addCdhPaymentCard(companyId,
            paymentCard, userEmail);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
            .buildAndExpand(response.getCardId()).toUri();
        return ResponseEntity.created(location).build();
    }

    @Operation(method = "updateCompanyPaymentCard", summary = "Update Company Card",
        description = "This endpoint updates a new company payment card")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Success. No content."),
        @ApiResponse(responseCode = "400", description = "Bad Request: please check your input",
            content =
                {@Content(mediaType = "application/json", schema =
                @Schema(implementation = ErrorResponse.class))}),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Bad Request"),
        @ApiResponse(responseCode = "404", description = "Not Found: card not found for the given company-id and card-id",
            content =
                {@Content(mediaType = "application/json", schema =
                @Schema(implementation = ErrorResponse.class))}),
        @ApiResponse(responseCode = "500", description = "Server Error: unrecoverable error server-side",
            content = {@Content(mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class))})})
    @RequestMapping(value = "/admin/{companyId}/cards/{card-id}", method = RequestMethod.PUT,
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> updateCompanyPaymentCard(
        @Parameter(required = true) @PathVariable("companyId") String companyId,
        @Parameter(required = true) @PathVariable("card-id") String cardId,
        @Parameter(required = true) @RequestHeader(value = "Authorization") String authorization,
        @Parameter(required = true, name = "payload", description = "The PaymentCard JSON payload") @Valid @RequestBody PaymentCard paymentCard) {
        log.debug("Called /companies/{}/cards/{} (PUT)", sanitize(companyId), sanitize(cardId));
        CdhEmployeeDetails cdhEmployeeDetails = authTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(
            authorization);
        String userEmail = cdhEmployeeDetails.getUserEmail();
        cdhCompanyCardsService.updatePaymentCard(companyId, cardId, paymentCard, userEmail);
        return ResponseEntity.noContent().build();
    }

    @Operation(method = "deleteCompanyPaymentCard", summary = "Delete Company Card",
        description = "This endpoint deletes a company payment card")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Success. No content."),
        @ApiResponse(responseCode = "400", description = "Bad Request: please check your input",
            content =
                {@Content(mediaType = "application/json", schema =
                @Schema(implementation = ErrorResponse.class))}),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Bad Request"),
        @ApiResponse(responseCode = "500", description = "Server Error: unrecoverable error server-side",
            content = {@Content(mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class))})})
    @RequestMapping(value = "/admin/{companyId}/cards/{card-id}", method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteCompanyPaymentCard(
        @Parameter(required = true) @PathVariable("companyId") String companyId,
        @Parameter(required = true) @PathVariable("card-id") String cardId,
        @Parameter(required = true) @RequestHeader(value = "Authorization") String authorization) {
        log.debug("Called /companies/{}/cards/{} (DELETE)", sanitize(companyId), sanitize(cardId));
        CdhEmployeeDetails cdhEmployeeDetails = authTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(
            authorization);
        String userEmail = cdhEmployeeDetails.getUserEmail();
        cdhCompanyCardsService.deletePaymentCard(companyId, cardId, userEmail);
        return ResponseEntity.noContent().build();
    }

    @Operation(method = "RemoveCompanyPaymentCard", summary = "Delete Company Card",
            description = "This endpoint deletes a company payment card using POST HTTP method as a workaround for Appsync integration")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Success. No content."),
            @ApiResponse(responseCode = "400", description = "Bad Request: please check your input",
                    content =
                            {@Content(mediaType = "application/json", schema =
                            @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Bad Request"),
            @ApiResponse(responseCode = "500", description = "Server Error: unrecoverable error server-side",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))})})
    @PostMapping(value = "/admin/{companyId}/cards/{card-id}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> removeCompanyPaymentCard(
            @Parameter(required = true) @PathVariable("companyId") String companyId,
            @Parameter(required = true) @PathVariable("card-id") String cardId,
            @Parameter(required = true) @RequestHeader(value = "Authorization") String authorization) {
        log.debug("Called /companies/{}/cards/{} (POST)", companyId, cardId);
        CdhEmployeeDetails cdhEmployeeDetails = authTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(
                authorization);
        cdhCompanyCardsService.deletePaymentCard(companyId, cardId, cdhEmployeeDetails.getUserEmail());
        return ResponseEntity.noContent().build();
    }
}