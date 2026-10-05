package uk.co.whitbread.piba.account.controller;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.io.ByteArrayOutputStream;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import uk.co.whitbread.common.exceptions.entity.ErrorResponse;
import uk.co.whitbread.piba.account.model.CreditProposeLimitRequest;
import uk.co.whitbread.piba.account.model.CreditProposeLimitResponse;
import uk.co.whitbread.piba.account.model.CustomerAccount;
import uk.co.whitbread.piba.account.model.CustomerAccountCurrentBalancesResponse;
import uk.co.whitbread.piba.account.model.CustomerAccountInvoiceListDownloadResponse;
import uk.co.whitbread.piba.account.model.CustomerAccountInvoiceRequest;
import uk.co.whitbread.piba.account.model.CustomerAccountInvoiceResponse;
import uk.co.whitbread.piba.account.model.CustomerAccountTransactionsRequest;
import uk.co.whitbread.piba.account.model.CustomerAccountTransactionsResponse;
import uk.co.whitbread.piba.account.model.CustomerAccountsResponse;
import uk.co.whitbread.piba.account.model.ResetMemorableWordRequest;
import uk.co.whitbread.piba.account.model.ResetMemorableWordResponse;
import uk.co.whitbread.piba.account.model.TetheredUserDetailsResponse;
import uk.co.whitbread.piba.account.model.TetheredUserRequest;
import uk.co.whitbread.piba.account.model.UpdateMemorableWordRequest;
import uk.co.whitbread.piba.account.model.enums.Scheme;
import uk.co.whitbread.piba.account.service.CdhRegistrationService;
import uk.co.whitbread.piba.account.service.PibaAccountService;
import uk.co.whitbread.piba.account.util.LogUtils;

@Slf4j
@RequestMapping("/piba/account")
@RestController
@AllArgsConstructor
@Tag(name = "Piba Account operations")
public class PibaAccountController {
    private final PibaAccountService pibaAccountService;
    private final CdhRegistrationService cdhRegistrationService;
    private static final String TRANSACTIONS_CSV_CONTENT_DISPOSITION = "attachment; filename=Transactions.csv";
    private static final String INVOICES_PDF_CONTENT_DISPOSITION = "attachment; filename=";

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CustomerAccountCurrentBalancesResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Error Occurred: Please check your input",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "500", description = "Server Error: Our bad something went wrong on our side",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))})})
    @GetMapping(value = "/balance", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public CustomerAccountCurrentBalancesResponse viewAccountBalance(
            @RequestHeader(name = "Authorization", required = false) String authorization,
            @Parameter @RequestHeader(required = false, name = "viewAll", defaultValue = "true") boolean viewAll) {

        log.info("Called GET /piba/account/balance");

        return pibaAccountService.viewCurrentBalance(authorization, viewAll);
    }

    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK",
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = CustomerAccountCurrentBalancesResponse.class))}),
        @ApiResponse(responseCode = "400", description = "Error Occurred: Please check your input",
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class))}),
        @ApiResponse(responseCode = "500", description = "Server Error: Our bad something went wrong on our side",
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class))})})
    @GetMapping(value = "/balance/summary", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public CustomerAccountCurrentBalancesResponse viewAccountBalanceSummary(
        @RequestHeader(name = "Authorization", required = false) String authorization,
        @Parameter @RequestHeader(required = false, name = "viewAll", defaultValue = "true") boolean viewAll) {

        log.info("Called GET /piba/account/balance/summary");

        return pibaAccountService.viewCurrentBalanceSummary(authorization, viewAll);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CustomerAccountTransactionsResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Error Occurred: Please check your input",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "500", description = "Server Error: Our bad something went wrong on our side",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))})})
    @PostMapping(value = "/transactions", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public CustomerAccountTransactionsResponse viewAccountTransactions(
            @RequestHeader(name = "Authorization", required = true) String authorization,
            @Parameter(name = "payload", description = "Account current balance request") @Valid @RequestBody CustomerAccountTransactionsRequest customerAccountTransactionsRequest) {

        log.info("Called POST /piba/account/transactions with  SchemeCustomerId={} TetheredUserGuid={}",
                customerAccountTransactionsRequest.getSchemeCustomerId(),
                LogUtils.sanitisedStringWithMaxLengthLimit(customerAccountTransactionsRequest.getTetheredUserGuid(),50));

        return pibaAccountService.viewTransactions(customerAccountTransactionsRequest);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CustomerAccountInvoiceResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Error Occurred: Please check your input",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "500", description = "Server Error: Our bad something went wrong on our side",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))})})
    @PostMapping(value = "/invoices", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public CustomerAccountInvoiceResponse viewCustomerInvoices(
            @RequestHeader(name = "Authorization", required = true) String authorization,
            @Parameter(name = "payload", description = "Account invoice request") @Valid @RequestBody CustomerAccountInvoiceRequest customerAccountInvoiceRequest) {

        return pibaAccountService.viewInvoices(customerAccountInvoiceRequest);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = byte[].class))}),
            @ApiResponse(responseCode = "400", description = "Error Occurred: Please check your input",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "500", description = "Server Error: Our bad something went wrong on our side",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))})})
    @GetMapping(value = "/transactions/download/{schemeCustomerId}/{tetheredUserGuid}", produces = {MediaType.APPLICATION_JSON_VALUE, "text/csv"})
    public ResponseEntity<byte[]> downloadAccountTransactions(
            @RequestHeader(name = "Authorization", required = true) String authorization,
            @Parameter(required = true) @PathVariable("schemeCustomerId") int schemeCustomerId,
            @Parameter(required = true) @PathVariable("tetheredUserGuid") String tetheredUserGuid) {

        log.info("Called Get /piba/account/transactions/download/{schemeCustomerId}/{tetheredUserGuid} with SchemeCustomerId={} TetheredUserGuid={}",
                schemeCustomerId, LogUtils.sanitisedStringWithMaxLengthLimit(tetheredUserGuid,50));
        ByteArrayOutputStream transactionListOutputStream = pibaAccountService.downloadTransactions(schemeCustomerId, tetheredUserGuid);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, TRANSACTIONS_CSV_CONTENT_DISPOSITION);

        return ResponseEntity.ok().headers(headers).body(transactionListOutputStream.toByteArray());
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = byte[].class))}),
            @ApiResponse(responseCode = "400", description = "Error Occurred: Please check your input",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "500", description = "Server Error: Our bad something went wrong on our side",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))})})
    @GetMapping(value = "/invoices/download/{schemeCustomerId}/{tetheredUserGuid}/{fileId}", produces = {MediaType.APPLICATION_PDF_VALUE})
    public ResponseEntity<byte[]> downloadCustomerInvoices(
            @RequestHeader(name = "Authorization", required = true) String authorization,
            @Parameter(required = true) @PathVariable("schemeCustomerId") int schemeCustomerId,
            @Parameter(required = true) @PathVariable("tetheredUserGuid") String tetheredUserGuid,
            @Parameter(required = true) @PathVariable("fileId") int fileId,
        @Parameter(schema = @Schema(defaultValue = "GB", allowableValues = "DE, GB"))
        @RequestParam(name = "scheme", required = false) Scheme scheme) {

        log.info("Called POST /invoices/download/{schemeCustomerId}/{tetheredUserGuid}/{fileId} with  SchemeCustomerId={} and scheme={}" +
                "TetheredUserGuid={} and FileId={}", schemeCustomerId, LogUtils.sanitisedStringWithMaxLengthLimit(tetheredUserGuid, 50), fileId, scheme);

        CustomerAccountInvoiceListDownloadResponse downloadInvoices = pibaAccountService.downloadInvoices(schemeCustomerId, tetheredUserGuid, fileId, scheme);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, INVOICES_PDF_CONTENT_DISPOSITION + downloadInvoices.getFileName());

        return ResponseEntity.ok().headers(headers).body(downloadInvoices.getBinaryData());

    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CreditProposeLimitResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Error Occurred: Please check your input",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "500", description = "Server Error: Our bad something went wrong on our side",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))})})
    @PostMapping(value = "/proposecreditlimit", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public CreditProposeLimitResponse proposeNewCreditLimit(
            @RequestHeader(name = "Authorization", required = true) String authorization,
            @Parameter(name = "payload", description = "New Credit Limit Request") @Valid @RequestBody CreditProposeLimitRequest creditProposeLimitRequest) {

        log.info("Called POST /proposecreditlimit with  SchemeCustomerId:{} TetheredUserGuid:{}",
                creditProposeLimitRequest.getSchemeCustomerId(),
                LogUtils.sanitisedStringWithMaxLengthLimit(creditProposeLimitRequest.getTetheredUserGuid(), 50));
        return pibaAccountService.proposeNewCreditLimit(creditProposeLimitRequest);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TetheredUserDetailsResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Error Occurred: Please check your input",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "500", description = "Server Error: Our bad something went wrong on our side",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))})})
    @GetMapping(value = "/tethereduserdetail/{tetheredUserGuid}/{scheme}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public TetheredUserDetailsResponse getTetheredUserDetails(
            @RequestHeader(name = "Authorization", required = true) String authorization,
            @Parameter(required = true) @PathVariable("scheme") Scheme scheme,
            @Parameter(required = true) @PathVariable("tetheredUserGuid") String tetheredUserGuid) {

        log.info("Called GET /tethereduserdetail/{tetheredUserGuid} with TetheredUserGuid:{} for {}",
                LogUtils.sanitisedStringWithMaxLengthLimit(tetheredUserGuid,50), scheme);
        return pibaAccountService.getUserDetails(tetheredUserGuid, scheme);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = TetheredUserDetailsResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Error Occurred: Please check your input",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "500", description = "Server Error: Our bad something went wrong on our side",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))})})
    @GetMapping(value = "/tethereduserdetail/{tetheredUserGuid}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public TetheredUserDetailsResponse getTetheredUserDetailsLegacy(
            @RequestHeader(name = "Authorization", required = true) String authorization,
            @Parameter(required = true) @PathVariable("tetheredUserGuid") String tetheredUserGuid) {

        log.info("Called GET /tethereduserdetail/{tetheredUserGuid} with TetheredUserGuid:{}",
                LogUtils.sanitisedStringWithMaxLengthLimit(tetheredUserGuid,50));
        return pibaAccountService.getUserDetails(tetheredUserGuid, null);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ResetMemorableWordResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Error Occurred: Please check your input",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "500", description = "Server Error: Our bad something went wrong on our side",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))})})
    @PostMapping(value = "/memorableword", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResetMemorableWordResponse resetMemorableWord(
            @RequestHeader(name = "Authorization", required = true) String authorization,
            @Parameter(schema = @Schema(defaultValue = "GB", allowableValues = "DE, GB")) @RequestParam(name = "scheme", required = false) Scheme scheme,
            @Parameter(name = "payload", description = "Reset memorable word request")
            @Valid @RequestBody ResetMemorableWordRequest request) {

        //scheme request param will not be removed now for backwards compatability reasons,
        //for now IB will use the scheme from request body
        var wlScheme = Objects.nonNull(scheme) ? scheme : request.getScheme();
        var tetheredUserGuid = LogUtils.sanitisedStringWithMaxLengthLimit(
            request.getTetheredUserGuid(), 50);

        log.info("Called POST /reset/memorableword with TetheredUserGuid:{} and scheme: {}",
            tetheredUserGuid, wlScheme);
        UpdateMemorableWordRequest updateMemorableWordRequest = new UpdateMemorableWordRequest();
        updateMemorableWordRequest.setNewMemorableWord(request.getMemorableWord());
        updateMemorableWordRequest.setTetheredUserGuid(request.getTetheredUserGuid());
        updateMemorableWordRequest.setScheme(wlScheme);
        return pibaAccountService.resetMemorableWord(updateMemorableWordRequest);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CustomerAccount.class))}),
            @ApiResponse(responseCode = "400", description = "Error Occurred: Please check your input",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))}),
            @ApiResponse(responseCode = "500", description = "Server Error: Our bad something went wrong on our side",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ErrorResponse.class))})})
    @GetMapping(value = "/customers", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public CustomerAccountsResponse getAccounts(
            @RequestHeader(name = "Authorization", required = false) String authorization,
            @Parameter @RequestHeader(required = false, name = "viewAll", defaultValue = "true") boolean viewAll,
            @Parameter @RequestHeader(required = false, name = "ignoreWorldlineDetails", defaultValue = "false") boolean ignoreWorldlineDetails) {

        log.info("Called GET /piba/account/customers");

        return pibaAccountService.getAccounts(authorization, viewAll, ignoreWorldlineDetails);
    }

    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Success. No content."),
        @ApiResponse(responseCode = "400", description = "Error Occurred: Please check your input",
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class))}),
        @ApiResponse(responseCode = "500", description = "Server Error: Our bad something went wrong on our side",
            content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class))})})
    @PostMapping(value = "/register/tetheredUser", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> registerTetheredUser(
        @RequestHeader(name = "Authorization") String authorization,
        @Valid @RequestBody TetheredUserRequest tetheredUserRequest) {
        log.info("Called POST /piba/account/register/tetheredUser with company id {} and scheme {}",
            LogUtils.sanitisedStringWithMaxLengthLimit(tetheredUserRequest.getCompanyId(), 50),
            LogUtils.sanitisedStringWithMaxLengthLimit(tetheredUserRequest.getScheme().toString(), 50));
        cdhRegistrationService.registerTetheredUser(authorization, tetheredUserRequest);
        return ResponseEntity.noContent().build();
    }

}