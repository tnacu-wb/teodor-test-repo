package uk.co.whitbread.piba.account.controller;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
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
import uk.co.whitbread.piba.account.model.CustomerAccountCurrentBalances;
import uk.co.whitbread.piba.account.model.CustomerAccountInvoiceListDownloadResponse;
import uk.co.whitbread.piba.account.model.CustomerAccountInvoiceRequest;
import uk.co.whitbread.piba.account.model.CustomerAccountInvoiceResponse;
import uk.co.whitbread.piba.account.model.TransactionsFileResponse;
import uk.co.whitbread.piba.account.model.enums.Scheme;
import uk.co.whitbread.piba.account.service.PibaAccountService;
import uk.co.whitbread.piba.account.util.LogUtils;
import uk.co.whitbread.piba.account.util.WorldlineUtils;

@Slf4j
@RequestMapping("/v2/piba/account")
@RestController
@AllArgsConstructor
@Tag(name = "Piba Account operations v2")
public class PibaAccountControllerV2 {

  private final PibaAccountService pibaAccountService;
  private final WorldlineUtils worldlineUtils;

  private static final String CONTENT_DISPOSITION = "attachment; filename=";
  private static final String TRANSACTIONS_XLS_TYPE = "application/vnd.ms-excel";

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
  public CustomerAccountInvoiceResponse viewCustomerInvoicesV2(
      @RequestHeader(name = "Authorization") String authorization,
      @Parameter(name = "payload", description = "Account invoice request") @Valid @RequestBody CustomerAccountInvoiceRequest customerAccountInvoiceRequest) {
    return pibaAccountService.viewInvoicesV2(authorization, customerAccountInvoiceRequest);
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
  @GetMapping(value = "/invoices/download/{schemeCustomerId}/{tetheredUserGuid}/{fileId}", produces = {
      MediaType.APPLICATION_PDF_VALUE})
  public ResponseEntity<byte[]> downloadCustomerInvoicesV2(
      @RequestHeader(name = "Authorization") String authorization,
      @Parameter(required = true) @PathVariable("schemeCustomerId") int schemeCustomerId,
      @Parameter(required = true) @PathVariable("tetheredUserGuid") String tetheredUserGuid,
      @Parameter(required = true) @PathVariable("fileId") int fileId,
      @Parameter(schema = @Schema(defaultValue = "GB", allowableValues = "DE, GB"))
      @RequestParam(name = "scheme", required = false) Scheme scheme) {

    log.info(
        "Called GET /v2/piba/account/invoices/download/{schemeCustomerId}/{tetheredUserGuid}/{fileId} with  SchemeCustomerId={} and scheme={} "
            +
            "TetheredUserGuid={} and FileId={}", schemeCustomerId,
        LogUtils.sanitisedStringWithMaxLengthLimit(tetheredUserGuid, 50), fileId, scheme);

    CustomerAccountInvoiceListDownloadResponse downloadInvoices = pibaAccountService.downloadInvoicesV2(
        authorization, schemeCustomerId, tetheredUserGuid, fileId, scheme);
    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.CONTENT_DISPOSITION,
        CONTENT_DISPOSITION + downloadInvoices.getFileName());

    return ResponseEntity.ok().headers(headers).body(downloadInvoices.getBinaryData());
  }

  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "OK",
          content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = CustomerAccountCurrentBalances.class))}),
      @ApiResponse(responseCode = "400", description = "Error Occurred: Please check your input",
          content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = ErrorResponse.class))}),
      @ApiResponse(responseCode = "500", description = "Server Error: Our bad something went wrong on our side",
          content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = ErrorResponse.class))})})
  @GetMapping(value = "/balance/summary/{tetheredUserGuid}/{scheme}", produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public CustomerAccountCurrentBalances getAccountBalanceSummary(
      @RequestHeader(name = "Authorization") String authorization,
      @Parameter(required = true) @PathVariable("scheme") Scheme scheme,
      @Parameter(required = true) @PathVariable("tetheredUserGuid") String tetheredUserGuid) {

    log.info("Called GET /v2/piba/account/balance/summary with TetheredUserGuid:{} for Scheme {}",
        LogUtils.sanitisedStringWithMaxLengthLimit(tetheredUserGuid, 50), scheme);

    return pibaAccountService.getCustomerAccountCurrentBalance(authorization, tetheredUserGuid,
        scheme);
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
  @GetMapping(value = "/transactions/download/{schemeCustomerId}/{tetheredUserGuid}/{invoiceNo}",
      produces = {TRANSACTIONS_XLS_TYPE})
  public ResponseEntity<byte[]> downloadAccountTransactionsV2(
      @RequestHeader(name = "Authorization") String authorization,
      @Parameter(required = true) @PathVariable("schemeCustomerId") int schemeCustomerId,
      @Parameter(required = true) @PathVariable("tetheredUserGuid") String tetheredUserGuid,
      @Parameter(required = true) @PathVariable("invoiceNo") int invoiceNumber,
      @Parameter(schema = @Schema(defaultValue = "GB", allowableValues = "DE, GB"))
      @RequestParam(name = "scheme", required = false) Scheme scheme,
      HttpServletRequest httpServletRequest) {

    log.info("Called Get /v2/piba/account/transactions/download/{schemeCustomerId}/{tetheredUserGuid}/{invoiceNo} " +
            "with schemeCustomerId={} tetheredUserGuid={} and invoiceNo={}",
        schemeCustomerId, LogUtils.sanitisedStringWithMaxLengthLimit(tetheredUserGuid,50), invoiceNumber);

    if (scheme == null) {
      scheme = Scheme.GB;
    }

    TransactionsFileResponse transactionsFileResponse =
        pibaAccountService.downloadTransactionsForInvoice(schemeCustomerId, tetheredUserGuid, invoiceNumber,
            authorization, worldlineUtils.getClientIp(httpServletRequest), scheme);

    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.CONTENT_TYPE, TRANSACTIONS_XLS_TYPE);
    headers.add(HttpHeaders.CONTENT_DISPOSITION, CONTENT_DISPOSITION + transactionsFileResponse.fileName());

    return ResponseEntity.ok().headers(headers).body(transactionsFileResponse.filedata().toByteArray());
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
  @GetMapping(value = "/transactions/download/{schemeCustomerId}/{tetheredUserGuid}",
      produces = {TRANSACTIONS_XLS_TYPE})
  public ResponseEntity<byte[]> downloadTransactionsForAccountV2(
      @RequestHeader(name = "Authorization") String authorization,
      @Parameter(required = true) @PathVariable("schemeCustomerId") int schemeCustomerId,
      @Parameter(required = true) @PathVariable("tetheredUserGuid") String tetheredUserGuid,
      @Parameter(schema = @Schema(defaultValue = "GB", allowableValues = "DE, GB"))
      @RequestParam(name = "scheme", required = false) Scheme scheme) {


    TransactionsFileResponse transactionsFileResponse =
        pibaAccountService.downloadTransactionsForAccountV2(schemeCustomerId, tetheredUserGuid, scheme, authorization);

    HttpHeaders headers = new HttpHeaders();
    headers.add(HttpHeaders.CONTENT_TYPE, TRANSACTIONS_XLS_TYPE);
    headers.add(HttpHeaders.CONTENT_DISPOSITION, CONTENT_DISPOSITION + transactionsFileResponse.fileName());

    return ResponseEntity.ok().headers(headers).body(transactionsFileResponse.filedata().toByteArray());
  }

}
