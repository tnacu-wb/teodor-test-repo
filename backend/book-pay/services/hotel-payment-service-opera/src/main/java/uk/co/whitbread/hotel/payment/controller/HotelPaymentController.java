package uk.co.whitbread.hotel.payment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import uk.co.whitbread.common.exceptions.entity.ErrorResponse;
import uk.co.whitbread.hotel.payment.model.*;
import uk.co.whitbread.hotel.payment.service.PaymentService;

import java.util.Optional;

@RestController
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Hotel Payment")
@Validated
public class HotelPaymentController {
    private static final String PIBAEURO = "635629";
    private static final String CARD_TYPE_PIBAEURO = "BD";
    private static final String CARD_TYPE_PIBA = "AT";
    private static final String CARD_TYPE_VISA = "VI";
    private final PaymentService paymentService;

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = {
                    @Content(
                            schema = @Schema(implementation = ValidateBinResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = {
                    @Content(
                            schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
                    @Content(
                            schema = @Schema(implementation = ErrorResponse.class))})})
    @Operation(summary = "validateBin", description = "Validate six digit card pan")
    @RequestMapping(value = "/payment/validations/{bin}", method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ValidateBinResponse validateBin(@PathVariable("bin") @Size(min = 6, max = 6, message = "bin length must 6 characters") String bin) {
        if ("308950".equals(bin) || "982613".equals(bin)) {
            return ValidateBinResponse.builder()
                    .cardType(CARD_TYPE_PIBA)
                    .startDateRequired(false)
                    .build();
        } else if (PIBAEURO.equals(bin)) {
            return ValidateBinResponse.builder()
                .cardType(CARD_TYPE_PIBAEURO)
                .startDateRequired(false)
                .build();
        }

        // Return dummy type when BART is decommissioned as the type will be taken from 3cp during card tokenization
        return ValidateBinResponse.builder()
                .cardType(CARD_TYPE_VISA)
                .startDateRequired(false)
                .build();
    }

    @Operation(summary = "Calls the 3D Secure's Confirmation service")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = {
                    @Content(
                            schema = @Schema(implementation = String.class))}),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = {
                    @Content(
                            schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
                    @Content(
                            schema = @Schema(implementation = ErrorResponse.class))})})
    @Parameters({
            @Parameter(name = "PaRes", required = true, schema = @Schema(type = "string"), in = ParameterIn.QUERY),
            @Parameter(name = "env", schema = @Schema(type = "string"), in = ParameterIn.DEFAULT)
    })
    @RequestMapping(value = "/payment/hotels/{paymentId}/complete", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = "text/html;charset=UTF-8")
    public String threeDSecureConfirmation(@PathVariable("paymentId") String paymentId,
                                           String PaRes,
                                           @RequestParam(value = "env", required = false) String env) {
        log.debug("Called /payment/hotels/{}/complete", paymentId);

        return paymentService.threeDSecureConfirmation(paymentId, PaRes, env);
    }

    @Operation(summary = "Calls the 3D Secure's v2.1 Confirmation service",
            description = "Returns an HTML document which submits the JSON to the parent window.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = {
                    @Content(
                            schema = @Schema(implementation = ResponseEntity.class))}),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = {
                    @Content(
                            schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
                    @Content(
                            schema = @Schema(implementation = ErrorResponse.class))})})
    @Parameters({
            @Parameter(example = "12345", name = "sessionId", schema = @Schema(type = "string"), in = ParameterIn.PATH, required = true),
    })
    @RequestMapping(value = "/payment/authentication/{sessionId}/complete", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<?> threeDSecureV2Confirmation(@PathVariable("sessionId") String sessionId,
                                                        @RequestBody(required = false) MultiValueMap<String, String> formData) {
        log.info("Called /payment/authentication/{}/complete with formData {}.", sessionId, formData);
        var threeDSecureVersion2Response = paymentService.constructThreeDSecureVersion2Response(formData, sessionId);
        String bookingChannel = Optional.ofNullable(formData.getFirst("bookingChannel")).orElse("WEB");
        String htmlResponse = paymentService.threeDSecureV2Confirmation(threeDSecureVersion2Response, formData.getFirst("env"), bookingChannel);
        log.info("Returning 3DSv2 complete HTML response for sessionId {}.", sessionId);
        return new ResponseEntity<>(htmlResponse, HttpStatus.OK);
    }

}
