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
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.common.exceptions.entity.ErrorResponse;
import uk.co.whitbread.marketing.model.MarketingSubscriptionInfoResponse;
import uk.co.whitbread.marketing.model.MarketingSubscriptionRequest;
import uk.co.whitbread.marketing.model.MarketingSubscriptionResponse;
import uk.co.whitbread.marketing.service.MarketingNewsletterService;
import uk.co.whitbread.marketing.utils.RequestUtils;


@Slf4j
@RequestMapping("/marketing/hotels")
@RestController
@RequiredArgsConstructor
@Tag(name = "Marketing API Controller")
@Deprecated
public class MarketingNewsletterController {

    private final MarketingNewsletterService marketingNewsletterService;
    private final RequestUtils requestUtils;

    @Operation(summary = "subscribe", description = "sign up to marketing newsletters")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = @Content(schema = @Schema(implementation = MarketingSubscriptionResponse.class))),
            @ApiResponse(responseCode = "400", description = "Error Occurred ", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
    @PostMapping(value = "/newsletter", produces = MediaType.APPLICATION_JSON_VALUE)
    public MarketingSubscriptionResponse subscribe(@Valid @RequestBody MarketingSubscriptionRequest request) {
        log.info("Called /marketing/hotels/newsletter");
        requestUtils.validateRequest(request);
        String customerId = requestUtils.getCustomerId(request.getEmailAddress(), request.getCustomerId());
        request.setEmailAddress(customerId);

        MarketingSubscriptionResponse marketingSubscriptionResponse = marketingNewsletterService.subscribeToNewsletters(request);
        marketingSubscriptionResponse.setEmailAddress(customerId);
        marketingSubscriptionResponse.setCustomerId(customerId);
        return marketingSubscriptionResponse;
    }

    @Operation(summary = "Newsletter Subscription Info Service", description = "Newsletter Subscription - user info method")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All good",content = @Content(schema = @Schema(implementation = MarketingSubscriptionInfoResponse.class))),
            @ApiResponse(responseCode = "400", description = "Error Occurred: You did something wrong check your input", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Server Error: Our bad something went wrong on our side", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
    @GetMapping(value = "/newsletter/{customerId:.+}", produces = MediaType.APPLICATION_JSON_VALUE)
    public MarketingSubscriptionInfoResponse subscriptionInfo(@PathVariable("customerId") String customerId) {
        log.info("Called /marketing/hotels/newsletter with {}", customerId);
        return marketingNewsletterService.getSubscriptionInfo(customerId);
    }

    @Operation(summary = "Newsletter Status Service", description = "Newsletter Subscription - subscriptionInfo method", tags = {})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All good", content = @Content(schema = @Schema(implementation = MarketingSubscriptionInfoResponse.class))),
            @ApiResponse(responseCode = "400", description = "Error Occurred: You did something wrong check your input", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Server Error: Our bad something went wrong on our side", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
    @GetMapping(value = "/newsletter/{customerId:.+}/status", produces = MediaType.APPLICATION_JSON_VALUE)
    public MarketingSubscriptionInfoResponse status(@PathVariable("customerId") String emailAddress) {
        log.info("Called /marketing/hotels/newsletter/****/status");
        return marketingNewsletterService.getSubscriptionStatus(emailAddress);
    }
}
