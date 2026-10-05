package uk.co.whitbread.piba.account.service;

import static java.time.LocalDate.now;
import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;
import static uk.co.whitbread.piba.account.util.AppConstants.FILE_EXTENSION_XLS;
import static uk.co.whitbread.piba.account.util.AppConstants.INVOICE_PREFIX;
import static uk.co.whitbread.piba.account.util.AppConstants.TRANSACTIONS_PREFIX;
import static uk.co.whitbread.piba.account.util.AppConstants.UNDERSCORE;

import com.opencsv.CSVWriter;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.client.SoapFaultClientException;
import uk.co.whitbread.piba.account.converter.WorldlineTransformer;
import uk.co.whitbread.piba.account.exception.InValidTokenException;
import uk.co.whitbread.piba.account.exception.PibaAccountException;
import uk.co.whitbread.piba.account.exception.TransactionCSVListException;
import uk.co.whitbread.piba.account.exception.UnknownAccountException;
import uk.co.whitbread.piba.account.model.CreditProposeLimitRequest;
import uk.co.whitbread.piba.account.model.CreditProposeLimitResponse;
import uk.co.whitbread.piba.account.model.CustomerAccount;
import uk.co.whitbread.piba.account.model.CustomerAccountCardTransaction;
import uk.co.whitbread.piba.account.model.CustomerAccountCurrentBalances;
import uk.co.whitbread.piba.account.model.CustomerAccountCurrentBalancesRequest;
import uk.co.whitbread.piba.account.model.CustomerAccountCurrentBalancesResponse;
import uk.co.whitbread.piba.account.model.CustomerAccountInvoiceListDownloadResponse;
import uk.co.whitbread.piba.account.model.CustomerAccountInvoiceRequest;
import uk.co.whitbread.piba.account.model.CustomerAccountInvoiceResponse;
import uk.co.whitbread.piba.account.model.CustomerAccountTransactionsByDateCriteria;
import uk.co.whitbread.piba.account.model.CustomerAccountTransactionsByInvoiceNumberCriteria;
import uk.co.whitbread.piba.account.model.CustomerAccountTransactionsCriteria;
import uk.co.whitbread.piba.account.model.CustomerAccountTransactionsRequest;
import uk.co.whitbread.piba.account.model.CustomerAccountTransactionsResponse;
import uk.co.whitbread.piba.account.model.CustomerAccountsResponse;
import uk.co.whitbread.piba.account.model.PagingRequestWithoutSort;
import uk.co.whitbread.piba.account.model.PibaTetheredGuidResponse;
import uk.co.whitbread.piba.account.model.ResetMemorableWordResponse;
import uk.co.whitbread.piba.account.model.TetheredLoginResponse;
import uk.co.whitbread.piba.account.model.TetheredUserDetailsResponse;
import uk.co.whitbread.piba.account.model.TransactionsFileResponse;
import uk.co.whitbread.piba.account.model.UpdateMemorableWordRequest;
import uk.co.whitbread.piba.account.model.enums.RegistrationRoles;
import uk.co.whitbread.piba.account.model.enums.Scheme;
import uk.co.whitbread.piba.account.properties.CdhProperties;
import uk.co.whitbread.piba.account.util.SessionTokenUtil;
import uk.co.whitbread.piba.account.util.TransactionFileWriter;
import uk.co.whitbread.piba.account.util.WorldlineUtils;
import uk.co.whitbread.piba.account.validation.WorldLineAccountResponseValidator;
import uk.co.whitbread.piba.api.properties.WorldLineProperties;
import uk.co.whitbread.piba.api.security.WorldLineWebServiceMessageCallback;
import uk.co.whitbread.shared.auth.account.CdhEmployeeDetails;
import uk.co.whitbread.shared.auth.account.EmployeeDetails;
import uk.co.whitbread.shared.auth.service.TokenService;
import worldline.mst.bsm.api.b2b.pi.data.CreditProposeNewLimitResponse;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountInvoiceDownloadResponse;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountInvoiceListResponse;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountViewCurrentBalancesResponse;
import worldline.mst.bsm.api.b2b.pi.data.CustomerAccountViewTransactionsResponse;
import worldline.mst.bsm.api.b2b.pi.data.LoginTetheredUserResponse;
import worldline.mst.bsm.api.b2b.pi.data.UpdateMemorableWordResponse;

@Service
@Slf4j
@RequiredArgsConstructor
public class PibaAccountService {

  private static final int MAX_ROWS_FIRST_PAGE = 20;
  private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
  private static final int MAX_DISPLAY_ROWS = 30;
  private static final String INVOICE_DATE_FORMAT = "yyyyMMdd";

  @Qualifier("worldlineWebServiceTemplate")
  private final WebServiceTemplate worldlineWebServiceTemplate;
  private final WorldlineTransformer worldlineAccountTransformer;
  private final WorldLineProperties worldLineProperties;
  private final WorldLineAccountResponseValidator worldLineResponseValidator;
  private final WorldLineWebServiceMessageCallback worldLineWebServiceMessageCallback;
  private final TransactionFileWriter transactionFileWriter;
  private final PibaGuidServiceClient pibaGuidServiceClient;
  private final CdhRegistrationService cdhRegistrationService;
  private final TokenService authTokenService;
  private final CdhProperties cdhProperties;
  private final Executor worldLineExecutor;
  private final WorldLineService worldLineService;
  private final InvoicesService invoicesService;
  private final WorldlineUtils worldlineUtils;

  @Value("${worldline.tetheringPlus.enabled}")
  private boolean tetheringPlusEnabledFlag = true;

  public PibaTetheredGuidResponse getTetheredGuids(String companyId, String employeeId) {
    log.info("Called Piba guid service to retrieve tethered guids companyId={}, employeeId={} ",
        companyId, employeeId);
    return pibaGuidServiceClient.getGuids(companyId, employeeId);
  }

  private List<PibaTetheredGuidResponse> getMultiTetheredGuids(String companyId,
      String employeeId) {
    log.info(
        "Called Piba guid service to retrieve multiple tethered guids companyId={}, employeeId={} ",
        companyId, employeeId);
    return pibaGuidServiceClient.getMultiGuids(companyId, employeeId);
  }

  public ResetMemorableWordResponse resetMemorableWord(
      UpdateMemorableWordRequest updateMemorableWordRequest) {
    TetheredLoginResponse tetheredLoginResponse = worldLineLoginTetheredUser(
        updateMemorableWordRequest.getTetheredUserGuid(),
        updateMemorableWordRequest.getScheme());
    log.info("Called worldLineLoginTetheredUser with TetheredUserGuid:{}",
        tetheredLoginResponse.getSessionId());
    updateMemorableWordRequest.setSessionId(tetheredLoginResponse.getSessionId());
    updateMemorableWordRequest.setHash(tetheredLoginResponse.getHash());
    updateMemorableWordRequest.setNonce(tetheredLoginResponse.getNonce());
    updateMemorableWordRequest.setTimestamp(tetheredLoginResponse.getTimestamp());

    UpdateMemorableWordResponse updateMemorableWordResponse = ofNullable(updateMemorableWordRequest)
        .map(worldlineAccountTransformer::toUpdateMemorableWordRequest)
        .map(this::dispatchWorldLineRequest)
        .map(UpdateMemorableWordResponse.class::cast).orElse(null);
    worldLineResponseValidator.validate(updateMemorableWordResponse);
    return worldlineAccountTransformer.toResetMemorableWordResponse(updateMemorableWordResponse);
  }

  public CustomerAccountCurrentBalancesResponse viewCurrentBalance(String authorization,
      boolean viewAll) {
    log.info("Calling viewCurrentBalance with tetheringPlusEnabledFlag : {}",
        tetheringPlusEnabledFlag);
    CustomerAccountCurrentBalancesResponse customerAccountCurrentBalancesResponse;
    if (tetheringPlusEnabledFlag) {
      customerAccountCurrentBalancesResponse = viewCurrentBalanceMultipleScheme(authorization,
          viewAll);
    } else {
      customerAccountCurrentBalancesResponse = viewCurrentBalanceSingleScheme(authorization,
          viewAll);
    }
    return customerAccountCurrentBalancesResponse;
  }

  public CustomerAccountCurrentBalancesResponse viewCurrentBalanceSummary(String authorization,
      boolean viewAll) {
    List<PibaTetheredGuidResponse> customerTetheredGuidsResponses = getPibaTetheredGuidResponseList(
        authorization);
    log.info("viewCurrentBalanceSummary - pibaTetheredGuidsResponses :: {}",
        customerTetheredGuidsResponses);

    return getCustomerAccountCurrentBalancesResponse(viewAll, customerTetheredGuidsResponses);
  }

  private CustomerAccountCurrentBalancesResponse viewCurrentBalanceSingleScheme(
      String authorization, boolean viewAll) {
    EmployeeDetails employeeDetails = getEmployeeDetails(authorization);
    PibaTetheredGuidResponse customerTetheredGuidsResponse = getTetheredGuids(
        employeeDetails.getCompanyId(), employeeDetails.getEmployeeId());
    Scheme scheme = customerTetheredGuidsResponse.getScheme();
    List<String> customerTetheredGuids = customerTetheredGuidsResponse.getTetheredGuid();
    int totalRows = customerTetheredGuids.size();
    List<CustomerAccountCurrentBalances> currentBalances = new ArrayList<>();
    int maxRows = viewAll ? totalRows : Math.min(totalRows, MAX_ROWS_FIRST_PAGE);
    customerTetheredGuids.subList(0, maxRows)
        .forEach(req -> currentBalances.add(getCustomerAccountCurrentBalance(req, scheme)));
    return new CustomerAccountCurrentBalancesResponse(currentBalances, totalRows);
  }

  private CustomerAccountCurrentBalancesResponse viewCurrentBalanceMultipleScheme(
      String authorization, boolean viewAll) {
    EmployeeDetails employeeDetails = getEmployeeDetails(authorization);
    List<PibaTetheredGuidResponse> customerTetheredGuidsResponses = getMultiTetheredGuids(
        employeeDetails.getCompanyId(), employeeDetails.getEmployeeId());
    log.info("customerTetheredGuidsResponses :: {}", customerTetheredGuidsResponses);
    return getCustomerAccountCurrentBalancesResponse(viewAll, customerTetheredGuidsResponses);
  }

  private CustomerAccountCurrentBalancesResponse getCustomerAccountCurrentBalancesResponse(
      boolean viewAll, List<PibaTetheredGuidResponse> customerTetheredGuidsResponses) {

    List<CompletableFuture<CustomerAccountCurrentBalances>> currentBalances = new ArrayList<>();
    int finalRows = 0;

    for (PibaTetheredGuidResponse customerTetheredGuidsResponse : customerTetheredGuidsResponses) {
      int totalRows = 0;
      Scheme scheme = customerTetheredGuidsResponse.getScheme();
      List<String> customerTetheredGuids = customerTetheredGuidsResponse.getTetheredGuid();
      totalRows = totalRows + customerTetheredGuids.size();
      int maxRows = viewAll ? totalRows : Math.min(totalRows, MAX_ROWS_FIRST_PAGE);

      var futures = customerTetheredGuids.subList(0, maxRows)
          .stream()
          .map(tetheredGuid -> CompletableFuture.supplyAsync(
              () -> getCustomerAccountCurrentBalance(tetheredGuid, scheme), worldLineExecutor))
          .toList();
      currentBalances.addAll(futures);
      finalRows = finalRows + totalRows;
    }
    CompletableFuture.allOf(currentBalances.toArray(new CompletableFuture[0])).join();
    List<CustomerAccountCurrentBalances> customerAccountCurrentBalances = currentBalances.stream()
        .map(CompletableFuture::join)
        .toList();

    return new CustomerAccountCurrentBalancesResponse(customerAccountCurrentBalances, finalRows);
  }

  public CustomerAccountCurrentBalances viewCurrentBalanceBySchemeCustomerId(
      CustomerAccountCurrentBalancesRequest request) {
    try {
      CustomerAccountViewCurrentBalancesResponse response = ofNullable(request)
          .map(worldlineAccountTransformer::toCustomerAccountCurrentBalancesRequest)
          .map(this::dispatchWorldLineRequest)
          .map(CustomerAccountViewCurrentBalancesResponse.class::cast).orElse(null);
      worldLineResponseValidator.validate(response);
      return worldlineAccountTransformer.toCustomerAccountCurrentBalancesResponse(response);
    } catch (Exception e) {
      // request.getTetheredUserGuid() can't be null
      log.error("Error occurred while retrieving balance Worldline service for tethered guid {} ",
          request.getTetheredUserGuid(), e);
      return new CustomerAccountCurrentBalances("Error while retrieving balance");
    }
  }

  public CustomerAccountsResponse getAccounts(String authorization, boolean viewAll,
      boolean ignoreWorldlineDetails) {
    List<PibaTetheredGuidResponse> customerTetheredGuidsResponses = getPibaTetheredGuidResponseList(
        authorization);
    log.info("getAccountsMultipleScheme - customerTetheredGuidsResponses :: {}",
        customerTetheredGuidsResponses);
    AtomicInteger finalRows = new AtomicInteger();
    if (ignoreWorldlineDetails) {
      var customerAccountsWithoutWLDetails = getCustomerAccountsWithoutWLDetails(viewAll,
          customerTetheredGuidsResponses, finalRows);
      return new CustomerAccountsResponse(customerAccountsWithoutWLDetails, finalRows.get());
    } else {
      var customerAccounts = getCustomerAccounts(viewAll, customerTetheredGuidsResponses,
          finalRows);
      return new CustomerAccountsResponse(customerAccounts, finalRows.get());
    }
  }

  @NotNull
  private static List<CustomerAccount> getCustomerAccountsWithoutWLDetails(boolean viewAll,
      List<PibaTetheredGuidResponse> customerTetheredGuidsResponses, AtomicInteger finalRows) {
    List<CustomerAccount> customerAccounts = new ArrayList<>();
    customerTetheredGuidsResponses.forEach(customerTetheredGuidsResponse -> {
      var scheme = customerTetheredGuidsResponse.getScheme();
      var customerTetheredGuids = customerTetheredGuidsResponse.getTetheredGuid();
      var totalRows = customerTetheredGuids.size();
      var maxRows = viewAll ? totalRows : Math.min(totalRows, MAX_ROWS_FIRST_PAGE);
      customerAccounts.addAll(customerTetheredGuids.subList(0, maxRows).stream().map(
          tetheredGuid -> CustomerAccount.builder().tetheredGuid(tetheredGuid).scheme(scheme)
              .build()).toList());
      finalRows.addAndGet(maxRows);
    });
    return customerAccounts;
  }

  @NotNull
  private List<CustomerAccount> getCustomerAccounts(boolean viewAll,
      List<PibaTetheredGuidResponse> customerTetheredGuidsResponses, AtomicInteger finalRows) {

    List<CompletableFuture<CustomerAccount>> customerAccountList = new ArrayList<>();
    customerTetheredGuidsResponses.forEach(customerTetheredGuidsResponse -> {
      var scheme = customerTetheredGuidsResponse.getScheme();
      var customerTetheredGuids = customerTetheredGuidsResponse.getTetheredGuid();
      var totalRows = customerTetheredGuids.size();
      var maxRows = viewAll ? totalRows : Math.min(totalRows, MAX_ROWS_FIRST_PAGE);
      var futures = customerTetheredGuids.subList(0, maxRows).stream().map(
          tetheredGuid -> CompletableFuture.supplyAsync(
              () -> getCustomerAccount(tetheredGuid, scheme), worldLineExecutor)).toList();
      customerAccountList.addAll(futures);
      finalRows.addAndGet(maxRows);
    });
    CompletableFuture.allOf(customerAccountList.toArray(new CompletableFuture[0])).join();
    return customerAccountList.stream().map(CompletableFuture::join).toList();
  }

  private List<PibaTetheredGuidResponse> getPibaTetheredGuidResponseList(String authorization) {
    EmployeeDetails employeeDetails = getEmployeeDetails(authorization);
    CdhEmployeeDetails cdhEmployeeDetails = authTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(
        authorization);
    return cdhRegistrationService.getTetheredGuids(
        employeeDetails.getCompanyId(), employeeDetails.getEmployeeId(),
        cdhEmployeeDetails.getUserEmail());
  }

  private CustomerAccount getCustomerAccount(String tetheredGuid, Scheme scheme) {
    try {
      CustomerAccount account = new CustomerAccount();
      TetheredUserDetailsResponse userDetailsResponse = worldLineService.getUserDetails(tetheredGuid, scheme);
      log.info(
          "getCustomerAccount - userDetailsResponse.getTetheredUserOverview().getUserRole() :: {}",
          userDetailsResponse.getTetheredUserOverview().getUserRole());
      setUserDetails(userDetailsResponse, account);
      account.setTetheredGuid(tetheredGuid);
      account.setScheme(scheme);
      account.setApiUserGuid(userDetailsResponse.getTetheredUserOverview().getApiUserGuid());
      return account;
    } catch (Exception e) {
      log.error(
          "Error occurred while retrieving user details from Worldline service for tethered guid {} ",
          tetheredGuid, e);
      return new CustomerAccount("Error while retrieving tethered user details");
    }
  }

  public CustomerAccountTransactionsResponse viewTransactions(
      CustomerAccountTransactionsRequest customerAccountTransactionsRequest) {
    CustomerAccountViewTransactionsResponse response = ofNullable(
        customerAccountTransactionsRequest)
        .map(worldlineAccountTransformer::toCustomerAccountTransactionsRequest)
        .map(this::dispatchWorldLineRequest)
        .map(CustomerAccountViewTransactionsResponse.class::cast).orElse(null);

    worldLineResponseValidator.validate(response);
    return worldlineAccountTransformer.toCustomerAccountTransactionsResponse(response);
  }

  public ByteArrayOutputStream downloadTransactions(int schemeCustomerId, String tetheredUserGuid) {
    CustomerAccountTransactionsRequest customerAccountTransactionsRequest = new CustomerAccountTransactionsRequest();
    CustomerAccountTransactionsCriteria customerAccountTransactionsCriteria = new CustomerAccountTransactionsCriteria();
    CustomerAccountTransactionsByDateCriteria dateCriteria = new CustomerAccountTransactionsByDateCriteria(
        LocalDate.now().minusMonths(2), LocalDate.now(), "Uninvoiced", null);
    customerAccountTransactionsCriteria.setDateSearch(dateCriteria);
    customerAccountTransactionsRequest.setSearchCriteria(customerAccountTransactionsCriteria);
    customerAccountTransactionsRequest.setSchemeCustomerId(schemeCustomerId);
    customerAccountTransactionsRequest.setTetheredUserGuid(tetheredUserGuid);

    List<CustomerAccountCardTransaction> transactionList = retrieveTransactions(
        customerAccountTransactionsRequest);

    return writeToCSV(transactionList);
  }

  public TransactionsFileResponse downloadTransactionsForInvoice(int schemeCustomerId, String tetheredUserGuid,
                                                                 int invoiceNumber, String authorization,
                                                                 String clientIp, Scheme scheme) {
    validateUserTetheredGuid(authorization, tetheredUserGuid);

    // Initial request to determine the total number of pages
    CustomerAccountTransactionsRequest initialRequest = buildCustomerAccountTransactionsRequestForInvoice(
        schemeCustomerId, tetheredUserGuid, invoiceNumber);
    initialRequest.setPagingRequest(new PagingRequestWithoutSort(1, MAX_DISPLAY_ROWS));
    CustomerAccountTransactionsResponse initialResponse = viewTransactions(initialRequest);

    List<CustomerAccountCardTransaction> allTransactions =
        retrieveTransactionsForInvoice(schemeCustomerId, tetheredUserGuid, invoiceNumber, initialResponse);

    var invoiceDateOptional = getInvoiceDate(initialResponse, invoiceNumber);
    Pair<LocalDate, LocalDate> dateInterval =
        invoicesService.getDateRangeForInvoice(invoiceDateOptional, clientIp, scheme, tetheredUserGuid);
    ByteArrayOutputStream fileData =
        writeToExcel(allTransactions, dateInterval.getLeft(), dateInterval.getRight());
    String fileName = INVOICE_PREFIX + UNDERSCORE + invoiceNumber + UNDERSCORE
        + formatDate(invoiceDateOptional) + FILE_EXTENSION_XLS;
    return new TransactionsFileResponse(fileName, fileData);
  }

  private Optional<LocalDate> getInvoiceDate(CustomerAccountTransactionsResponse initialResponse, int invoiceNumber) {
    if (initialResponse != null && initialResponse.getResponse() != null) {
      if (initialResponse.getResponse().getTransactions() == null ||
          initialResponse.getResponse().getTransactions().isEmpty()) {
        log.info("No transactions found for the given invoice number {}.", invoiceNumber);
        return Optional.empty();
      } else {
        Optional<LocalDate> invoiceDate = initialResponse.getResponse().getTransactions().stream()
            .filter(transaction -> transaction.getInvoiceDate() != null)
            .findFirst()
            .map(CustomerAccountCardTransaction::getInvoiceDate);
        if (invoiceDate.isEmpty()) {
          log.warn("No invoice date found for the transactions of invoice number {}.", invoiceNumber);
          return Optional.empty();
        }
        return invoiceDate;
      }
    }
    return Optional.empty();
  }

  private String formatDate(Optional<LocalDate> date) {
    return date.map(localDate -> localDate.format(DateTimeFormatter.ofPattern(INVOICE_DATE_FORMAT)))
        .orElse(Strings.EMPTY);
  }

  private ByteArrayOutputStream writeToExcel(List<CustomerAccountCardTransaction> allTransactions,
                                             LocalDate startDate, LocalDate endDate) {
    ByteArrayOutputStream transactionListOutputStream = new ByteArrayOutputStream();
    transactionFileWriter.populateExcel(transactionListOutputStream, allTransactions, startDate, endDate);
    return transactionListOutputStream;
  }

  private List<CustomerAccountCardTransaction> retrieveTransactionsForInvoice(int schemeCustomerId,
                                                                              String tetheredUserGuid,
                                                                              int invoiceNumber,
                                                                              CustomerAccountTransactionsResponse firstPageResponse) {
    if (firstPageResponse.getPagingResult() == null) {
      return Optional.ofNullable(firstPageResponse.getResponse().getTransactions()).orElse(List.of());
    }
    int totalPages = firstPageResponse.getPagingResult().getLastPage();
    if (totalPages <= 1) {
      return Optional.ofNullable(firstPageResponse.getResponse().getTransactions()).orElse(List.of());
    }

    // Trigger parallel calls for the rest of the pages
    List<CompletableFuture<CustomerAccountTransactionsResponse>> futures = IntStream.rangeClosed(2, totalPages)
        .mapToObj(page -> CompletableFuture.supplyAsync(() -> {
          CustomerAccountTransactionsRequest paginatedRequest = buildCustomerAccountTransactionsRequestForInvoice(
              schemeCustomerId, tetheredUserGuid, invoiceNumber);
          paginatedRequest.setPagingRequest(new PagingRequestWithoutSort(page, MAX_DISPLAY_ROWS));
          return viewTransactions(paginatedRequest);
        }, worldLineExecutor))
        .toList();

    List<CustomerAccountCardTransaction> transactions = new ArrayList<>(firstPageResponse.getResponse().getTransactions());
    futures.stream()
        .map(CompletableFuture::join)
        .filter(response -> response.getResponse().getTransactions() != null)
        .flatMap(response -> response.getResponse().getTransactions().stream())
        .forEach(transactions::add);
    return transactions;
  }

  private CustomerAccountTransactionsRequest buildCustomerAccountTransactionsRequestForInvoice(
      int schemeCustomerId, String tetheredUserGuid, int invoiceNumber) {
    CustomerAccountTransactionsRequest customerAccountTransactionsRequest = new CustomerAccountTransactionsRequest();
    CustomerAccountTransactionsCriteria customerAccountTransactionsCriteria = new CustomerAccountTransactionsCriteria();
    CustomerAccountTransactionsByInvoiceNumberCriteria invoiceNumberCriteria = new CustomerAccountTransactionsByInvoiceNumberCriteria();
    invoiceNumberCriteria.setInvoiceNumber(invoiceNumber);
    customerAccountTransactionsCriteria.setInvoiceNumberSearch(invoiceNumberCriteria);
    customerAccountTransactionsRequest.setSearchCriteria(customerAccountTransactionsCriteria);
    customerAccountTransactionsRequest.setSchemeCustomerId(schemeCustomerId);
    customerAccountTransactionsRequest.setTetheredUserGuid(tetheredUserGuid);
    return customerAccountTransactionsRequest;
  }

  public List<CustomerAccountCardTransaction> retrieveTransactions(
      CustomerAccountTransactionsRequest customerAccountTransactionsRequest) {
    List<CustomerAccountCardTransaction> transactionList = new ArrayList<>();
    PagingRequestWithoutSort pagingRequest = new PagingRequestWithoutSort();
    pagingRequest.setMaximumDisplayRows(500);
    int lastPage = 1;
    int currentPage = 1;
    while (currentPage <= lastPage) {
      pagingRequest.setPage(currentPage);
      customerAccountTransactionsRequest.setPagingRequest(pagingRequest);
      CustomerAccountTransactionsResponse response = viewTransactions(
          customerAccountTransactionsRequest);
      if (response.getResponse().getTransactions() != null) {
        transactionList.addAll(response.getResponse().getTransactions());
      }
      lastPage = response.getPagingResult().getLastPage();
      currentPage++;
    }
    return transactionList;
  }

  public CreditProposeLimitResponse proposeNewCreditLimit(
      CreditProposeLimitRequest creditProposeLimitRequest) {
    CreditProposeNewLimitResponse response = ofNullable(creditProposeLimitRequest)
        .map(worldlineAccountTransformer::toCreditProposeNewLimitRequest)
        .map(this::dispatchWorldLineRequest)
        .map(CreditProposeNewLimitResponse.class::cast).orElse(null);

    worldLineResponseValidator.validate(response);
    return worldlineAccountTransformer.toCreditProposeNewLimitResponse(response);
  }

  private ByteArrayOutputStream writeToCSV(List<CustomerAccountCardTransaction> transactionList) {
    ByteArrayOutputStream transactionListOutputStream = new ByteArrayOutputStream();
    CSVWriter csvWriter = new CSVWriter(new BufferedWriter(
        new OutputStreamWriter(transactionListOutputStream)));
    try {
      transactionFileWriter.populateCsv(csvWriter, transactionList);
    } catch (IOException e) {
      throw new TransactionCSVListException("Failed to write transaction list to CSV file:" + e);
    }
    return transactionListOutputStream;
  }

  public CustomerAccountInvoiceResponse viewInvoices(
      CustomerAccountInvoiceRequest customerAccountInvoiceRequest) {
    CustomerAccountInvoiceListResponse response = ofNullable(customerAccountInvoiceRequest)
        .map(worldlineAccountTransformer::toCustomerAccountInvoiceRequest)
        .map(this::dispatchWorldLineRequest)
        .map(CustomerAccountInvoiceListResponse.class::cast).orElse(null);
    worldLineResponseValidator.validate(response);
    return worldlineAccountTransformer.toCustomerAccountInvoiceResponse(response);
  }

  public CustomerAccountInvoiceResponse viewInvoicesV2(String authorization,
      CustomerAccountInvoiceRequest customerAccountInvoiceRequest) {
    validateUserTetheredGuid(authorization,
        customerAccountInvoiceRequest.getTetheredUserGuid());

    return viewInvoices(customerAccountInvoiceRequest);
  }

  private void validateUserTetheredGuid(String authorization, String tetheredGuid) {
    var tetheredGuidResponseList = getPibaTetheredGuidResponseList(authorization);
    var foundTetheredGuid = tetheredGuidResponseList.stream()
        .flatMap(response -> response.getTetheredGuid().stream())
        .filter(guid -> guid.equals(tetheredGuid))
        .findFirst();

    if (foundTetheredGuid.isEmpty()) {
      throw new UnknownAccountException("Invalid tethered guid for current user");
    }
  }

  public CustomerAccountCurrentBalances getCustomerAccountCurrentBalance(String authorization,
      String tetheredGuid, Scheme scheme) {
    validateUserTetheredGuid(authorization, tetheredGuid);
    return getCustomerAccountCurrentBalance(tetheredGuid, scheme);
  }

  public CustomerAccountInvoiceListDownloadResponse downloadInvoicesV2(String authorization,
      int schemeCustomerId,
      String tetheredUserGuid, int fileId, Scheme scheme) {
    validateUserTetheredGuid(authorization, tetheredUserGuid);
    return downloadInvoices(schemeCustomerId, tetheredUserGuid, fileId, scheme);
  }

  public CustomerAccountInvoiceListDownloadResponse downloadInvoices(int schemeCustomerId,
      String tetheredUserGuid, int fileId, Scheme scheme) {
    CustomerAccountInvoiceDownloadResponse response = (CustomerAccountInvoiceDownloadResponse) dispatchWorldLineRequest(
        worldlineAccountTransformer.toCustomerAccountInvoiceDownloadRequest(schemeCustomerId,
            tetheredUserGuid, fileId, scheme));
    worldLineResponseValidator.validate(response);
    CustomerAccountInvoiceListDownloadResponse downloadResponse = worldlineAccountTransformer.toCustomerAccountInvoiceDownloadResponse(
        response);
    downloadResponse.setBinaryData(Base64.getDecoder().decode(downloadResponse.getBinaryData()));
    return downloadResponse;
  }

  public TetheredLoginResponse worldLineLoginTetheredUser(String guid, Scheme scheme) {
    String timestamp = new SimpleDateFormat(DATE_FORMAT).format(new Date());
    try {
      LoginTetheredUserResponse response = (LoginTetheredUserResponse) dispatchWorldLineRequest(
              worldlineAccountTransformer.toLoginTetheredUserRequest(guid, scheme));

      worldLineResponseValidator.validate(response);
      String worldLineSessionId = response.getResponse().getNewSession().getSessionId();
      String worldLineSharedSecret = response.getResponse().getNewSession().getSharedSecret();

      return createTetheredLoginResponse(timestamp, worldLineSessionId, worldLineSharedSecret);
    } catch (SoapFaultClientException | NoSuchAlgorithmException | UnsupportedEncodingException e) {
      log.error("Worldline tether login as failed with the following error: ", e);
      throw new PibaAccountException(e.getMessage());
    }
  }

  private <T> Object dispatchWorldLineRequest(T requestObject) {
    log.info("Sending WorldlineRequest for {} = {} #####",
        requestObject.getClass().getSimpleName(), worldlineUtils.serializeObject(requestObject));
    var response = worldlineWebServiceTemplate.marshalSendAndReceive(
        worldLineProperties.getPiba().getService().getUrl(),
        requestObject,
        worldLineWebServiceMessageCallback
    );
    log.info("Received WorldlineResponse for {} = {} #####",
        response.getClass().getSimpleName(), worldlineUtils.serializeObject(response));
    return response;
  }

  private CustomerAccountCurrentBalances getCustomerAccountCurrentBalance(String tetheredGuid,
      Scheme scheme) {
    try {
      CustomerAccountCurrentBalances balances = new CustomerAccountCurrentBalances();
      TetheredUserDetailsResponse userDetailsResponse = worldLineService.getUserDetails(tetheredGuid, scheme);
      log.info("userDetailsResponse.getTetheredUserOverview().getUserRole() :: {}",
          userDetailsResponse.getTetheredUserOverview().getUserRole());
      if (!(RegistrationRoles.ACCOUNT_CARD_HOLDER.getRegistrationRoles()
          .equals(userDetailsResponse.getTetheredUserOverview().getUserRole())
          || RegistrationRoles.COST_CENTRE_USER.getRegistrationRoles()
          .equals(userDetailsResponse.getTetheredUserOverview().getUserRole()))) {
        balances = viewCurrentBalanceBySchemeCustomerId(
            new CustomerAccountCurrentBalancesRequest(tetheredGuid,
                userDetailsResponse.getCustomerAccountOverview().getSchemeCustomerId(), scheme));
      }
      setUserDetails(userDetailsResponse, balances);
      balances.setTetheredGuid(tetheredGuid);
      balances.setScheme(scheme);
      return balances;
    } catch (Exception e) {
      log.error(
          "Error occurred while retrieving user details from Worldline service for tethered guid {} ",
          tetheredGuid, e);
      return new CustomerAccountCurrentBalances("Error while retrieving tethered user details");
    }
  }

  private <T extends CustomerAccount> void setUserDetails(
      TetheredUserDetailsResponse userDetailsResponse, T resp) {
    resp.setAccountName(userDetailsResponse.getCustomerAccountOverview().getAccountName());
    resp.setAccountNumber(userDetailsResponse.getCustomerAccountOverview().getAccountNumber());
    resp.setSchemeCustomerId(
        userDetailsResponse.getCustomerAccountOverview().getSchemeCustomerId());
    resp.setRegistrationRoles(
        populateRegistrationRoles(userDetailsResponse.getTetheredUserOverview().getUserRole(),
            userDetailsResponse.getTetheredUserOverview().getMyCards()));
  }

  private List<RegistrationRoles> populateRegistrationRoles(String mainRole, int myCard) {
    List<RegistrationRoles> registrationRoles = new ArrayList<>();
    if (RegistrationRoles.FINANCE_USER.getRegistrationRoles().equals(mainRole) ||
        RegistrationRoles.FINANCE_USER_CARD_HOLDER.getRegistrationRoles().equals(mainRole)) {
      registrationRoles.add(RegistrationRoles.FINANCE_USER);
    } else if (RegistrationRoles.ACCOUNT_HOLDER.getRegistrationRoles().equals(mainRole)) {
      registrationRoles.add(RegistrationRoles.ACCOUNT_HOLDER);
    } else if (RegistrationRoles.COST_CENTRE_USER.getRegistrationRoles().equals(mainRole)) {
      registrationRoles.add(RegistrationRoles.COST_CENTRE_USER);
    }

    if (isCardHolder(mainRole, myCard)) {
      registrationRoles.add(RegistrationRoles.CARD_HOLDER);
    }
    return registrationRoles;
  }

  private boolean isCardHolder(String mainRole, int myCard) {
    return (myCard != 0 || RegistrationRoles.ACCOUNT_CARD_HOLDER.getRegistrationRoles()
        .equals(mainRole)
        || RegistrationRoles.FINANCE_USER_CARD_HOLDER.getRegistrationRoles().equals(mainRole));
  }

  private TetheredLoginResponse createTetheredLoginResponse(String timestamp,
      String worldLineSessionId, String worldLineSharedSecret)
      throws UnsupportedEncodingException, NoSuchAlgorithmException {
    String nonce = SessionTokenUtil.generateNonceGuidBase64();
    TetheredLoginResponse tetheredLoginResponse = new TetheredLoginResponse();
    tetheredLoginResponse.setHash(
        SessionTokenUtil.generateHashedSessionToken(worldLineSharedSecret, timestamp, nonce));
    tetheredLoginResponse.setNonce(nonce);
    tetheredLoginResponse.setSessionId(worldLineSessionId);
    tetheredLoginResponse.setSharedSecret(worldLineSharedSecret);
    tetheredLoginResponse.setTimestamp(timestamp);
    return tetheredLoginResponse;
  }

  public EmployeeDetails getEmployeeDetails(String authorization) {
    EmployeeDetails employeeDetails;
    if (!authorization.isEmpty()) {
      if (cdhProperties.isEnableBbDataFetch()) {
        CdhEmployeeDetails cdhEmployeeDetails = authTokenService.retrieveCdhEmployeeDetailsAndVerifyToken(
            authorization);
        employeeDetails = new EmployeeDetails();
        employeeDetails.setCompanyId(cdhEmployeeDetails.getCompanyAccountId());
        employeeDetails.setEmployeeId(cdhEmployeeDetails.getEmployeeAccountId());
      } else {
        employeeDetails = authTokenService.retrieveEmployeeDetailsAndVerifyToken(
            authorization);
      }
      validateEmployeeDetails(employeeDetails);
    } else {
      throw new InValidTokenException("Error while trying to retrieve token from Auth0");
    }
    return employeeDetails;
  }

  private void validateEmployeeDetails(EmployeeDetails employeeDetails) {
    if (employeeDetails == null || isNull(employeeDetails.getEmployeeId()) || isNull(
        employeeDetails.getCompanyId())) {
      log.error("Missing companyId or employeeId in auth0 token");
      throw new InValidTokenException(
          "Error while trying to retrieve company/employee details from Auth0 token");
    } else {
      log.info("Called companyId = {} and employeeId = {}", employeeDetails.getCompanyId(),
          employeeDetails.getEmployeeId());
    }
  }

  public TetheredUserDetailsResponse getUserDetails(String tetherUserGuid, Scheme scheme) {
    return worldLineService.getUserDetails(tetherUserGuid,scheme);
  }

  public TransactionsFileResponse downloadTransactionsForAccountV2(int schemeCustomerId,
      String tetheredUserGuid, Scheme scheme, String authorization) {

    validateUserTetheredGuid(authorization, tetheredUserGuid);
    CustomerAccountTransactionsRequest customerAccountTransactionsRequest = new CustomerAccountTransactionsRequest();
    CustomerAccountTransactionsCriteria customerAccountTransactionsCriteria = new CustomerAccountTransactionsCriteria();
    LocalDate dateFrom = now().minusYears(3);
    LocalDate dateTo = now();
    CustomerAccountTransactionsByDateCriteria dateCriteria = new CustomerAccountTransactionsByDateCriteria(
        dateFrom, dateTo, "Both", null);
    customerAccountTransactionsCriteria.setDateSearch(dateCriteria);
    customerAccountTransactionsRequest.setSearchCriteria(customerAccountTransactionsCriteria);
    customerAccountTransactionsRequest.setSchemeCustomerId(schemeCustomerId);
    customerAccountTransactionsRequest.setTetheredUserGuid(tetheredUserGuid);
    customerAccountTransactionsRequest.setScheme(scheme);

    List<CustomerAccountCardTransaction> transactionList = retrieveTransactions(
        customerAccountTransactionsRequest);

    ByteArrayOutputStream fileData = writeToExcel(transactionList, dateFrom, dateTo);
    String fileName = TRANSACTIONS_PREFIX + UNDERSCORE
        + formatDate(Optional.of(dateTo)) + FILE_EXTENSION_XLS;
    return new TransactionsFileResponse(fileName, fileData);
  }
}
