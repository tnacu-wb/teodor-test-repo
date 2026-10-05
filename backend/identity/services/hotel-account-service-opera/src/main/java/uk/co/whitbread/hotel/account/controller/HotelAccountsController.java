package uk.co.whitbread.hotel.account.controller;

import static uk.co.whitbread.hotel.account.utils.Utils.sanitizeInputString;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.common.exceptions.entity.ErrorResponse;
import uk.co.whitbread.common.validators.password.PasswordByConfigValidator;
import uk.co.whitbread.hotel.account.client.HotelAccountCacheProvider;
import uk.co.whitbread.hotel.account.exceptions.InvalidTokenException;
import uk.co.whitbread.hotel.account.exceptions.PasswordPolicyException;
import uk.co.whitbread.hotel.account.model.BBStaysRequest;
import uk.co.whitbread.hotel.account.model.BBStaysRequestV2;
import uk.co.whitbread.hotel.account.model.BookingChannelCode;
import uk.co.whitbread.hotel.account.model.Customer;
import uk.co.whitbread.hotel.account.model.CustomerRequest;
import uk.co.whitbread.hotel.account.model.CustomerResponse;
import uk.co.whitbread.hotel.account.model.HotelBrandCode;
import uk.co.whitbread.hotel.account.model.HotelCustomerRequest;
import uk.co.whitbread.hotel.account.model.SearchCustomerRequest;
import uk.co.whitbread.hotel.account.model.StaysResponse;
import uk.co.whitbread.hotel.account.model.SuccessfulDeleteResponse;
import uk.co.whitbread.hotel.account.service.AccountStaysService;
import uk.co.whitbread.hotel.account.service.HotelAccountsService;
import uk.co.whitbread.shared.auth.account.CCUIDetails;
import uk.co.whitbread.shared.auth.account.CdhEmployeeDetails;
import uk.co.whitbread.shared.auth.service.TokenService;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/customers/hotels")
@Tag(name = "Hotel Customer operations")
@Validated
public class HotelAccountsController {

    protected static final String PW_POLICY_CONFIG_KEY = "account";
    private static final String DEFAULT_PI_BOOKING_CHANNEL = "WEB";
    private static final String DEFAULT_BRAND = "PI";
    private static final String CCUI_BOOKING_FLOW = "CCUI";
    private static final String INVALID_TOKEN_ERROR = "Provided token is invalid or expired";
    private final HotelAccountsService hotelAccountsService;
    private final TokenService authTokenService;
    private final PasswordByConfigValidator passwordValidator;
    private final HotelAccountCacheProvider hotelAccountCacheProvider;
    private final AccountStaysService accountStaysService;

    @Operation(method = "accountStays", description = "Customer Stay",
            summary = "This endpoint provides the functionality to retrieve all the future stays of an existing customer resource")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All Good!",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = StaysResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Error Occurred: You did something wrong check your input",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "500", description = "Server Error: Our bad something went wrong on our side",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))})})
    @PostMapping(value = "/{customer-id}/stays", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public StaysResponse retrieveAccountStays(
            @PathVariable("customer-id") String customerId,
            @RequestHeader(name = "Authorization") String authorization,
            @Valid @RequestBody BBStaysRequestV2 bbStaysRequest) {

        log.info("Called POST /customers/hotels/{}/stays", sanitizeInputString(customerId));

        return accountStaysService.getAccountStays(customerId, authorization, bbStaysRequest,
                bbStaysRequest.getPageIndex(), bbStaysRequest.getPageSize());
    }

    @Operation(method = "accountStays", description = "Customer Stay",
            summary = "This endpoint provides the functionality to retrieve all the future stays of an existing customer resource")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All Good!",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = StaysResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Error Occurred: You did something wrong check your input",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "500", description = "Server Error: Our bad something went wrong on our side",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))})})
    @Parameter(in = ParameterIn.QUERY, name = "business", description = "Business Booker or Guest",
        content = @Content(schema = @Schema(type = "boolean", defaultValue = "false")))
    @Parameter(in = ParameterIn.QUERY, name = "employeeId", description = "Employee id for business booker",
        content = @Content(schema = @Schema(type = "string")))
    @Parameter(in = ParameterIn.QUERY, name = "companyId", description = "Employee id for business booker",
        content = @Content(schema = @Schema(type = "string")))
    @Parameter(in = ParameterIn.QUERY, name = "typeOfBooking", description = "This is booking type for business booker. When no " +
        "booking type is specified, this endpoint retrieves all stays regardless of the type.",
        content = @Content(schema = @Schema(type = "string", allowableValues = "FUTURE, CANCELLED, PAST")))
    @GetMapping(value = "/{customer-id}/stays", produces = MediaType.APPLICATION_JSON_VALUE)
    public StaysResponse getAccountStays(
            @PathVariable("customer-id") String customerId,
            @RequestHeader(name = "Authorization") String authorization,
            @Validated BBStaysRequest bbStaysRequest,
            @RequestParam(value = "pageIndex", required = false, defaultValue = "1") @Min(1) Integer pageIndex,
            @RequestParam(value = "pageSize", required = false) @Min(1) Integer pageSize) {

        log.info("Called GET /customers/hotels/{}/stays", sanitizeInputString(customerId));

        return accountStaysService.getAccountStays(customerId, authorization, bbStaysRequest, pageIndex, pageSize);
    }

    @Operation(method = "updateCustomer", description = "Update Customer",
            summary = "This endpoint provides the functionality to update an existing customer resource")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All Good!",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CustomerResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Error Occurred: You did something wrong check your input",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "500", description = "Server Error: Our bad something went wrong on our side",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))})})
    @PutMapping(value = "/{customer-id}", consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CustomerResponse> updateCustomer(
            @PathVariable("customer-id") String customerId,
            @RequestHeader(name = "Authorization", required = false) String authorization,
            @RequestParam(required = false, defaultValue = "false") boolean business,
            @Valid @RequestBody CustomerRequest payload) {
        log.debug("Called /customers/hotels/{} (PUT - updateCustomer) with {}",
            sanitizeInputString(customerId),
            sanitizeInputString(payload.toString()));

        enforcePasswordPolicy(payload.getNewPassword());
        CustomerResponse customerResponse;
        try {
            if (Boolean.TRUE.equals(business)) {
                if (StringUtils.isBlank(authorization)) {
                    throw new InvalidTokenException(INVALID_TOKEN_ERROR);
                }
                var cdhEmployeeDetails = authTokenService
                    .retrieveCdhEmployeeDetailsAndVerifyToken(authorization);
                var triggerWLUpdate = shouldTriggerWLProfileUpdate(authorization,
                    payload, cdhEmployeeDetails);
                customerResponse = hotelAccountsService.updateBbCdhEmployee(cdhEmployeeDetails, payload);
                if (triggerWLUpdate) {
                    hotelAccountsService.updateWLContactDetails(authorization, payload, cdhEmployeeDetails);
                }
            } else {
                String customerAccountId = authTokenService.retrieveCustomerAccountIdAndVerifyToken(
                        authorization).orElseThrow(
                        () -> new InvalidTokenException(INVALID_TOKEN_ERROR));
                customerResponse = hotelAccountsService.updatePiCdhCustomer(customerAccountId, payload);
            }
        } catch (Exception e) {
            hotelAccountCacheProvider.deleteCacheCustomer(customerId);
            throw e;
        }

        // delete customerId from cache
        hotelAccountCacheProvider.deleteCacheCustomer(customerId);
        return new ResponseEntity<>(customerResponse, HttpStatus.OK);
    }

    @Operation(method = "getCustomer", description = "Get Customer",
            summary = "This endpoint retrieves the details of an existing customer resource")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Customer.class))}),
            @ApiResponse(responseCode = "400", description = "Bad Request: check you send customer-id",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized Error: Provided token is invalid or has expired",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "404", description = "Not Found: account not found for given customer-id",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "500", description = "Server Error: unrecoverable error server-side",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))})})
    @GetMapping(value = "/{customer-id}", consumes = MediaType.ALL_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public Customer getCustomer(
            @PathVariable("customer-id") String customerId,
            @RequestHeader(name = "hotel-brand", required = false, defaultValue = DEFAULT_BRAND) HotelBrandCode hotelBrand,
            @RequestHeader(name = "Authorization", required = false) String authorization,
            @RequestHeader(required = false, defaultValue = DEFAULT_PI_BOOKING_CHANNEL) BookingChannelCode bookingChannel,
            @RequestParam(required = false, defaultValue = "false") boolean business) {

        log.info(
            "Called /customers/hotels/{} (GET) with business: {} ; booking channel: {}",
            sanitizeInputString(customerId), business, bookingChannel);

        return hotelAccountsService.getCustomer(
            HotelCustomerRequest.builder()
                .authorization(authorization)
                .customerId(customerId).hotelBrand(hotelBrand)
                .business(business).bookingChannel(bookingChannel)
                .build());
    }

    @Operation(method = "searchCustomer", description = "search Customer",
            summary = "This endpoint searches for customer/s based on criteria")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Customer.class))}),
            @ApiResponse(responseCode = "400", description = "Bad Request: missing input data",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized Error: provided token is invalid or has expired",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "404", description = "Not Found: no Customer Account exists",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "413", description = "Too Many Records: refine your search criteria",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "500", description = "Server Error: unrecoverable error server-side",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))})})
    @PostMapping(value = "/search", consumes = MediaType.ALL_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Customer>> searchCustomer(
            @RequestHeader(name = "Authorization", required = false) String authorization,
            @RequestBody SearchCustomerRequest searchRequest) {

        log.info("Called /customers/hotels/search (POST)");

        CCUIDetails ccuiDetails = authTokenService.retrieveAndVerifyCCUIToken(authorization);
        if (!CCUI_BOOKING_FLOW.equals(ccuiDetails.getBookingFlow())) {
            log.error("Invalid bookingFlow: {}", ccuiDetails.getBookingFlow());
            throw new InvalidTokenException(INVALID_TOKEN_ERROR);
        }

        return ResponseEntity.ok(hotelAccountsService.getCustomer(searchRequest, ccuiDetails.getEmail()));
    }

    @Operation(method = "delete", description = "Delete Customer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = SuccessfulDeleteResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Bad request, check input",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "500", description = "Something went wrong on our side",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))})})
    @DeleteMapping(value = "/{customer-id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public SuccessfulDeleteResponse deleteCustomer(
        @PathVariable("customer-id") String customerId,
        @RequestHeader(name = "Authorization", required = false) String authorization,
        @RequestParam(required = false, defaultValue = "false") boolean business) {

        log.info("Delete customer /customers/hotels/{} with business = {}",
            sanitizeInputString(customerId), business);

        try {
            String customerAccountId = authTokenService.retrieveCustomerAccountIdAndVerifyToken(authorization)
                .orElseThrow(() -> new InvalidTokenException(INVALID_TOKEN_ERROR));
            String email = authTokenService.retrieveEmailAndVerifyToken(authorization)
                .orElseThrow(() -> new InvalidTokenException(INVALID_TOKEN_ERROR));
            hotelAccountsService.deleteCdhPICustomer(customerAccountId, email);
        } catch (Exception e) {
            hotelAccountCacheProvider.deleteCacheCustomer(customerId);
            throw e;
        }

        // delete customerId from cache
        hotelAccountCacheProvider.deleteCacheCustomer(customerId);
        return SuccessfulDeleteResponse.builder()
            .customerId(customerId)
            .success(true)
            .build();
    }

    protected void enforcePasswordPolicy(String password) {
        passwordValidator.isValid(password, PW_POLICY_CONFIG_KEY, this::triggerPasswordPolicyError);
    }

    protected void triggerPasswordPolicyError(String errorMessage) {
        throw new PasswordPolicyException(errorMessage);
    }

    private boolean shouldTriggerWLProfileUpdate(String authorization,
        CustomerRequest payload, CdhEmployeeDetails employeeDetails) {
        var oldProfile = hotelAccountsService.getCustomer(
            HotelCustomerRequest.builder().customerId(employeeDetails.getUserEmail())
                .authorization(authorization).business(true).build());
        var profileChanged = !Objects
            .equals(oldProfile.getContactDetail(), payload.getContactDetail());
        log.info("Should trigger Worldline update for employeeAccountId {} and companyAccountId {} : {}",
            employeeDetails.getEmployeeAccountId(), employeeDetails.getCompanyAccountId(), profileChanged);
        return profileChanged;
    }
}
