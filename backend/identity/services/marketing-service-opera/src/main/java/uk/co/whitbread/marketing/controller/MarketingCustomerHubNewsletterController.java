package uk.co.whitbread.marketing.controller;

import io.swagger.v3.oas.annotations.Operation;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.common.exceptions.entity.ErrorResponse;
import uk.co.whitbread.marketing.model.NewsletterPreferencesEditRequest;
import uk.co.whitbread.marketing.model.NewsletterPreferencesGetRequest;
import uk.co.whitbread.marketing.model.NewsletterPreferencesGetResponse;
import uk.co.whitbread.marketing.model.NewsletterPreferencesUpdateRequest;
import uk.co.whitbread.marketing.service.CustomerHubService;
import uk.co.whitbread.marketing.utils.RequestUtils;

@Slf4j
@RequestMapping("/marketing/hotels/newsletter")
@RestController
@RequiredArgsConstructor
@Tag(name = "Marketing API Controller")
@Deprecated
public class MarketingCustomerHubNewsletterController {

    private final CustomerHubService customerHubService;
    private final RequestUtils requestUtils;

    /**
     * @deprecated The preferred way of update and unsubscribe is /marketing/newsletter/{contactType}/{contactValue}
     */
    @Deprecated
    @Operation(summary = "Newsletter Preferences Edit Service", description = "Newsletter Subscription - editNewsletterPreferences method")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Success. No content"),
            @ApiResponse(responseCode = "400", description = "Error Occurred: You did something wrong check your input", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Server Error: Our bad something went wrong on our side", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
    @PutMapping(value = "/edit", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> editNewsletterPreferences(@RequestBody @Valid NewsletterPreferencesEditRequest request) {

        log.info("Called /marketing/hotels/newsletter");
        requestUtils.validateEditRequest(request);
        customerHubService.editNewsletterPreferences(request);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * @deprecated The preferred way of update and unsubscribe is /marketing/hotels/newsletter/edit
     */
    @Operation(summary = "Newsletter Preferences Update Service", description = "Newsletter Subscription - updateNewsletterPreferences method")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Success. No content"),
            @ApiResponse(responseCode = "400", description = "Error Occurred: You did something wrong check your input", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Server Error: Our bad something went wrong on our side", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
    @PutMapping(value = "/{customerId:^(?!edit).+}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Deprecated
    public ResponseEntity<Void> updateNewsletterPreferences(@PathVariable("customerId") String customerId,
                                                            @RequestBody @Valid NewsletterPreferencesUpdateRequest request) {

        log.info("Called /marketing/hotels/newsletter/{}  with: {}", customerId, customerId);

        customerHubService.updateNewsletterPreferences(customerId, request);


        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Prefer /marketing/newsletter{contentType}/{contentValue}
     */
    @Deprecated
    //Using POST instead of GET because we need a payload in the request.
    @Operation(summary = "Get Newsletter Preferences", description = "Newsletter Subscription - getNewsletterPreferences method")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = @Content(schema = @Schema(implementation = NewsletterPreferencesGetResponse.class))),
            @ApiResponse(responseCode = "400", description = "Error Occurred: You did something wrong check your input", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Server Error: Our bad something went wrong on our side", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
    @PostMapping(value = "/get", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<NewsletterPreferencesGetResponse> getNewsletterPreferences(@RequestBody @Valid NewsletterPreferencesGetRequest request) {

        requestUtils.validateGetPreferencesRequest(request);
        log.info("Called /marketing/hotels/newsletter/get with requestId: {}", request.getRequestId());
        NewsletterPreferencesGetResponse response = customerHubService.getNewsletterPreferences(request);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
