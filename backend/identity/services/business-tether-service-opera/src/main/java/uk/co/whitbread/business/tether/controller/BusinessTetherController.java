package uk.co.whitbread.business.tether.controller;

import static uk.co.whitbread.business.tether.utils.SanitizingUtils.sanitize;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.co.whitbread.business.tether.model.LoginCriteria;
import uk.co.whitbread.business.tether.model.TetherLinkRequest;
import uk.co.whitbread.business.tether.model.TetherLinkResponse;
import uk.co.whitbread.business.tether.model.TetheredLoginResponse;
import uk.co.whitbread.business.tether.service.BusinessTetherLoginService;
import uk.co.whitbread.business.tether.service.BusinessTetherService;
import uk.co.whitbread.common.exceptions.entity.ErrorResponse;

@Slf4j
@RequestMapping("/business")
@RestController
@AllArgsConstructor
@Tag(name = "Business Tether operations")
public class BusinessTetherController {

    private final BusinessTetherService businessAccountService;
    private final BusinessTetherLoginService businessTetherLoginService;

    @Operation(method = "tetherLink", description = "tether by account number or card number")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TetheredLoginResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Error Occurred: Please check your input",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "500", description = "Server Error: Our bad something went wrong on our side",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))})})
    @RequestMapping(value = "/tether", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<TetherLinkResponse> tetherLink(
            @RequestHeader(name = "Authorization", required = true) String authorization,
            @Parameter(required = true, name = "payload", description = "The tetherLink JSON payload") @Valid @RequestBody TetherLinkRequest tetherLinkRequest) {
        log.info("Called POST /business/tether with linkCode={}, linkId={}",
                sanitize(tetherLinkRequest.getLinkCode()), sanitize(tetherLinkRequest.getLinkId()));

        String tetheredGuid = businessAccountService.tetherByAccountOrCardRequest(tetherLinkRequest, authorization);
        TetherLinkResponse tetheredLinkResponse = new TetherLinkResponse();
        tetheredLinkResponse.setGuid(tetheredGuid);
        return new ResponseEntity<>(tetheredLinkResponse, HttpStatus.CREATED);
    }


    @Operation(method = "tetheredlogin", description = "Worldline tethered login")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TetheredLoginResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Error Occurred: Please check your input",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "500", description = "Server Error: Our bad something went wrong on our side",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))})})
    @RequestMapping(value = "/tether/login", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<TetheredLoginResponse> tetheredlogin(
            @RequestHeader(name = "Authorization", required = true) String authorization,
            @Parameter(required = true, name = "payload", description = "The tetheredlogin JSON payload") @Valid @RequestBody LoginCriteria loginCriteria) {
        log.info("Called POST /login");

        TetheredLoginResponse tetheredLoginResponse = businessTetherLoginService.login(loginCriteria);

        return new ResponseEntity<>(tetheredLoginResponse, HttpStatus.CREATED);
    }
}
