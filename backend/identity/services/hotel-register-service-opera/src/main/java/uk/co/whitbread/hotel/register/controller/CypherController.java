package uk.co.whitbread.hotel.register.controller;

import static uk.co.whitbread.hotel.register.utils.register.Utils.sanitizeInputString;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.co.whitbread.common.exceptions.entity.ErrorResponse;
import uk.co.whitbread.hotel.captcha.exception.CypherVerificationException;
import uk.co.whitbread.hotel.register.model.EncryptRequest;
import uk.co.whitbread.hotel.register.model.EncryptResponse;
import uk.co.whitbread.hotel.register.service.EncryptionService;

@Slf4j
@RequiredArgsConstructor
@RestController
@Tag(name = "Endpoint created to cypher emails. Random endpoint name to mask its purpose")
public class CypherController {

    public static final String ERROR_WHILE_TRYING_TO_ENCRYPT_TEXT = "Error while trying to encrypt text.";
    @Qualifier("emailEncryptionService")
    private final EncryptionService encryptionService;

    @Operation(method = "cypher plain text", description = "Cypher a plain text",
            summary = "This endpoint provides the functionality to create a new customer resource")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "All Good!",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = EncryptResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Error Occurred: You did something wrong check your input",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "500", description = "Server Error: Our bad something went wrong on our side",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))})})
    @RequestMapping(value = "/va3PkOkJNTUf76oe3UDoIqrA", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<EncryptResponse> cypherBase64(@Parameter(required = true, name = "base64", description = "Text in base64 to be cyphered")
                                                        @RequestBody EncryptRequest b64Text) {

        log.debug("Called /va3PkOkJNTUf76oe3UDoIqrA - cypher (POST -  cypherBase64) with {}",
            sanitizeInputString(b64Text.getEmailEncodedInBase64()));

        if (StringUtils.isBlank(b64Text.getEmailEncodedInBase64())) {
            throw new CypherVerificationException("Request should not be empty");
        }

        try {
            final String encrypted = encryptionService.encrypt(b64Text.getEmailEncodedInBase64());
            final EncryptResponse encryptResponse = new EncryptResponse();
            encryptResponse.setCyphered(encrypted);
            return new ResponseEntity<>(encryptResponse, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error(ERROR_WHILE_TRYING_TO_ENCRYPT_TEXT + " {}",
                sanitizeInputString(b64Text.getEmailEncodedInBase64()), e);
            throw new CypherVerificationException(ERROR_WHILE_TRYING_TO_ENCRYPT_TEXT);
        }
    }

    @Operation(method = "cypher plain text", description = "Cypher a plain text",
            summary = "This endpoint provides the functionality to create a new customer resource")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "All Good!",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = EncryptResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Error Occurred: You did something wrong check your input",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "500", description = "Server Error: Our bad something went wrong on our side",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))})})
    @RequestMapping(value = "/cypher", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Deprecated
    public ResponseEntity<EncryptResponse> cypherPlainText(@Parameter(required = true, name = "plainText", description = "The plainText to be cyphered")
                                                           @RequestBody EncryptRequest plainText) {

        log.debug("Called /cypher (POST -  cypherPlainText) with {}",
            sanitizeInputString(plainText.getPlainText()));

        if (StringUtils.isBlank(plainText.getPlainText())) {
            throw new CypherVerificationException("Request should not be empty");
        }

        try {
            final String encrypt = encryptionService.encrypt(plainText.getPlainText());
            final EncryptResponse encryptResponse = new EncryptResponse();
            encryptResponse.setEncrypted(encrypt);
            return new ResponseEntity<>(encryptResponse, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error(ERROR_WHILE_TRYING_TO_ENCRYPT_TEXT + " {}",
                sanitizeInputString(plainText.getPlainText()), e);
            throw new CypherVerificationException(ERROR_WHILE_TRYING_TO_ENCRYPT_TEXT);
        }
    }

}
