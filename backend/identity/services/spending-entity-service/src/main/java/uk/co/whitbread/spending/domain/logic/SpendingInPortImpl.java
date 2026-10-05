package uk.co.whitbread.spending.domain.logic;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.shared.auth.security.AuthenticatedUserService;
import uk.co.whitbread.spending.domain.exceptions.ErrorCode;
import uk.co.whitbread.spending.domain.exceptions.UnknownAccountException;
import uk.co.whitbread.spending.domain.exceptions.WrongTokenException;
import uk.co.whitbread.spending.domain.model.in.AccountSpendingRequest;
import uk.co.whitbread.spending.domain.model.in.CompanySpendingRequest;
import uk.co.whitbread.spending.domain.model.in.EmployeeSpendRequest;
import uk.co.whitbread.spending.domain.model.in.PaymentInfoModel;
import uk.co.whitbread.spending.domain.model.out.AccountSpendingResponse;
import uk.co.whitbread.spending.domain.model.out.CompanySpendingResponse;
import uk.co.whitbread.spending.domain.model.out.UpcomingSpendingResponse;
import uk.co.whitbread.spending.domain.model.out.cdh.EmployeeSpendReport;
import uk.co.whitbread.spending.domain.model.out.cdh.TetheredGuidResponse;
import uk.co.whitbread.spending.domain.model.out.worldline.AccountInfoResponse;
import uk.co.whitbread.spending.domain.model.out.worldline.AccountValue;
import uk.co.whitbread.spending.domain.model.out.worldline.PaymentInfoResponse;
import uk.co.whitbread.spending.domain.ports.primary.SpendingInPort;
import uk.co.whitbread.spending.domain.ports.secondary.CdhOutPort;
import uk.co.whitbread.spending.domain.ports.secondary.EmployeeSpendOutPort;
import uk.co.whitbread.spending.domain.ports.secondary.PibaAccountServiceOutPort;
import uk.co.whitbread.spending.domain.ports.secondary.WorldlineOutPort;

@Slf4j
@RequiredArgsConstructor
@Service
public class SpendingInPortImpl implements SpendingInPort {

  private static final String ACCESS_CONTEXT = "InnB";

  private final CdhOutPort cdhOutPort;
  private final EmployeeSpendOutPort employeeSpendOutPort;
  private final PibaAccountServiceOutPort pibaAccountServiceOutPort;
  private final AuthenticatedUserService authenticatedUserService;
  private final WorldlineOutPort worldlineOutPort;
  private final Clock clock;


  @Override
  public CompanySpendingResponse getCompanySpending(CompanySpendingRequest companySpendingRequest) {
    var authenticatedUser = authenticatedUserService.getAuthenticatedUser();
    companySpendingRequest.setCompanyAccountId(authenticatedUser.getAccount().getCompanyId());
    return cdhOutPort.getCompanySpending(companySpendingRequest,
        authenticatedUser.getAccount().getEmail());
  }

  @Override
  public AccountSpendingResponse getAccountSpending(AccountSpendingRequest accountSpendingRequest,
      String authorization) {
    var authenticatedUser = authenticatedUserService.getAuthenticatedUser();
    var jwtEmployeeId = authenticatedUser.getAccount().getBartEmployeeId();
    var jwtCompanyId = authenticatedUser.getAccount().getBartId();
    var jwtEmail = authenticatedUser.getAccount().getEmail();

    validateToken(jwtEmployeeId, jwtCompanyId, jwtEmail);

    // validate account belongs to user
    if (isTetheredUserPresent(accountSpendingRequest.getTetheredUserGuid())) {
      getMatchingTetherUserGuidFromCdh(jwtCompanyId, jwtEmployeeId,
          accountSpendingRequest.getTetheredUserGuid(), jwtEmail);
    } else {
      getTetheredGuidForAccountId(jwtCompanyId, jwtEmployeeId, authorization,
          accountSpendingRequest.getPibaAccountId());
    }

    return cdhOutPort.getAccountSpending(accountSpendingRequest,
        authenticatedUser.getAccount().getEmail());
  }

  @Override
  public List<EmployeeSpendReport> getEmployeeSpend(EmployeeSpendRequest employeeSpendRequest) {
    var authenticatedUser = authenticatedUserService.getAuthenticatedUser();
    var companyAccountId = authenticatedUser.getAccount().getCompanyId();
    var employeeAccountId = authenticatedUser.getAccount().getEmployeeId();
    var accessedBy = authenticatedUser.getAccount().getEmail();

    validateToken(employeeAccountId, companyAccountId, accessedBy);

    employeeSpendRequest.setCompanyAccountId(companyAccountId);
    employeeSpendRequest.setEmployeeAccountId(employeeAccountId);
    employeeSpendRequest.setAccessContext(ACCESS_CONTEXT);
    employeeSpendRequest.setAccessedBy(accessedBy);

    return employeeSpendOutPort.getEmployeeSpend(employeeSpendRequest);
  }

  @Override
  public UpcomingSpendingResponse getUpcomingSpending(String authorization, String accountId,
      String clientIpAddress, String tetheredUserGuid) {

    var authenticatedUser = authenticatedUserService.getAuthenticatedUser();
    var jwtEmployeeId = authenticatedUser.getAccount().getBartEmployeeId();
    var jwtCompanyId = authenticatedUser.getAccount().getBartId();
    var jwtEmail = authenticatedUser.getAccount().getEmail();

    validateToken(jwtEmployeeId, jwtCompanyId, jwtEmail);
    TetheredGuidResponse matchingGuid;
    // validate account belongs to user
    if (isTetheredUserPresent(tetheredUserGuid)) {
      matchingGuid = getMatchingTetherUserGuidFromCdh(jwtCompanyId, jwtEmployeeId, tetheredUserGuid,
          jwtEmail);
    } else {
      matchingGuid = getTetheredGuidForAccountId(jwtCompanyId, jwtEmployeeId,
          authorization, accountId);
    }


    // check billing frequency in Worldline
    var worldlineAccount = worldlineOutPort.getAccountInfo(matchingGuid.getScheme(),
        clientIpAddress, matchingGuid.getTetheredGuid());
    log.info("Worldline account retrieved, status: {}, billingFrequency:{}.",
        worldlineAccount.getData().getStatus(), worldlineAccount.getData().getBillingFrequency());

    // populate upcoming spending values from CDH
    var response = UpcomingSpendingResponse.builder().build();
    response.setAccountStatus(worldlineAccount.getData().getStatus());
    response.setCurrency(getCurrencyFromWorldline(worldlineAccount));
    populateBillingDates(worldlineAccount.getData().getBillingFrequency(), response);
    populateAggregatedUpcomingSpendFromCdh(response, accountId, jwtEmail);

    return response;
  }

  @Override
  public PaymentInfoResponse getPaymentInfo(PaymentInfoModel paymentInfoModel) {

    var authenticatedUser = authenticatedUserService.getAuthenticatedUser();
    var jwtEmployeeId = authenticatedUser.getAccount().getBartEmployeeId();
    var jwtCompanyId = authenticatedUser.getAccount().getBartId();
    var jwtEmail = authenticatedUser.getAccount().getEmail();

    validateToken(jwtEmployeeId, jwtCompanyId, jwtEmail);

    TetheredGuidResponse matchingGuid;
    if (isTetheredUserPresent(paymentInfoModel.getTetheredUserGuid())) {
      matchingGuid = getMatchingTetherUserGuidFromCdh(jwtCompanyId, jwtEmployeeId,
          paymentInfoModel.getTetheredUserGuid(), jwtEmail);
    } else {
      matchingGuid = getTetheredGuidForAccountId(jwtCompanyId, jwtEmployeeId,
          paymentInfoModel.getAuthorization(), paymentInfoModel.getAccountId());
    }


    return worldlineOutPort.getPaymentInfo(matchingGuid.getScheme(),
        matchingGuid.getTetheredGuid(), paymentInfoModel);
  }

  private void validateToken(String jwtEmployeeId, String jwtCompanyId, String jwtEmail) {
    if (StringUtils.isBlank(jwtEmployeeId) || StringUtils.isBlank(jwtCompanyId)
        || StringUtils.isBlank(jwtEmail)) {
      var ex = new WrongTokenException(ErrorCode.TOKEN_CANNOT_BE_PARSED,
          "Cannot parse the jwt token.");
      ExceptionLogger.log(log, ex);
      throw ex;
    }
  }

  private TetheredGuidResponse getTetheredGuidForAccountId(String companyId, String employeeId,
      String authorization, String accountId) {
    var matchingGuid = findMatchingTetheredGuid(authorization, accountId);
    if (matchingGuid.isEmpty()) {
      var ex = new UnknownAccountException(ErrorCode.ACCOUNT_NOT_BELONGING_TO_USER,
          String.format("Invalid account id for current user, companyId: %s, employeeId: "
              + "%s, accountId: %s.", companyId, employeeId, accountId));
      ExceptionLogger.log(log, ex);
      throw ex;
    }

    return matchingGuid.get();
  }

  private Optional<TetheredGuidResponse> findMatchingTetheredGuid(String authorization,
      String accountId) {
    var customerAccountsResponse = pibaAccountServiceOutPort.getAccounts(authorization);

    if (customerAccountsResponse == null || customerAccountsResponse.getAccounts() == null) {
      log.error("No accounts found for the user.");
      return Optional.empty();
    }

    var matchingAccount = customerAccountsResponse.getAccounts().stream()
        .filter(acc -> acc.getAccountNumber() != null && acc.getAccountNumber().equals(accountId))
        .findFirst();

    TetheredGuidResponse tetheredGuidResponse = matchingAccount.map(acc -> {
      TetheredGuidResponse response = new TetheredGuidResponse();
      response.setTetheredGuid(acc.getTetheredGuid());
      response.setScheme(acc.getScheme().toString());
      return response;
    }).orElse(null);

    return Optional.ofNullable(tetheredGuidResponse);
  }

  private void populateBillingDates(String accountType, UpcomingSpendingResponse result) {
    LocalDate today = LocalDate.now(clock);
    result.setExpectedSpendTodayDate(today);
    result.setExpectedNextBillingStartDate(today);
    switch (accountType) {
      case "Weekly":
        // Weekly, every Monday
        result.setExpectedNextBillingEndDate(today
            .with(TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY)));
        result.setExpectedNextPeriodStartDate(result
            .getExpectedNextBillingEndDate()
            .plusDays(1));
        result.setExpectedNextPeriodEndDate(result
            .getExpectedNextPeriodStartDate()
            .with(TemporalAdjusters.next(DayOfWeek.MONDAY)));
        break;
      case "Monthly":
        // Monthly, 2nd of each month
        if (today.getDayOfMonth() > 2) {
          result.setExpectedNextBillingEndDate(today.withDayOfMonth(2).plusMonths(1));
        } else {
          result.setExpectedNextBillingEndDate(today.withDayOfMonth(2));
        }
        result.setExpectedNextPeriodStartDate(result
            .getExpectedNextBillingEndDate()
            .plusDays(1));
        result.setExpectedNextPeriodEndDate(result
            .getExpectedNextPeriodStartDate()
            .plusMonths(1)
            .withDayOfMonth(2));
        break;
      default:
        // Fortnight, 2nd or 15th
        if (today.getDayOfMonth() <= 2) {
          result.setExpectedNextBillingEndDate(today.withDayOfMonth(2));
          result.setExpectedNextPeriodStartDate(result
              .getExpectedNextBillingEndDate()
              .plusDays(1));
          result.setExpectedNextPeriodEndDate(result
              .getExpectedNextPeriodStartDate()
              .withDayOfMonth(15));
        } else if (today.getDayOfMonth() <= 15) {
          result.setExpectedNextBillingEndDate(today.withDayOfMonth(15));
          result.setExpectedNextPeriodStartDate(result
              .getExpectedNextBillingEndDate()
              .plusDays(1));
          result.setExpectedNextPeriodEndDate(result
              .getExpectedNextPeriodStartDate()
              .plusMonths(1)
              .withDayOfMonth(2));
        } else {
          result.setExpectedNextBillingEndDate(today.withDayOfMonth(2).plusMonths(1));
          result.setExpectedNextPeriodStartDate(result
              .getExpectedNextBillingEndDate()
              .plusDays(1));
          result.setExpectedNextPeriodEndDate(result
              .getExpectedNextPeriodStartDate()
              .withDayOfMonth(15));
        }
    }
  }

  private void populateAggregatedUpcomingSpendFromCdh(UpcomingSpendingResponse response,
      String accountId, String email) {

    CompletableFuture<BigDecimal> spendTodayFuture = CompletableFuture.supplyAsync(() ->
        cdhOutPort.getTransactions(accountId, email,
                response.getExpectedSpendTodayDate(),
                response.getExpectedSpendTodayDate(), 1, 1)
            .getTotalBookingValue());

    final CompletableFuture<BigDecimal> nextPeriodFuture = CompletableFuture.supplyAsync(() ->
        cdhOutPort.getTransactions(accountId, email,
                response.getExpectedNextPeriodStartDate(),
                response.getExpectedNextPeriodEndDate(), 1, 1)
            .getTotalBookingValue());

    boolean needsSeparateNextBillingCall = !response.getExpectedNextBillingStartDate()
        .equals(response.getExpectedNextBillingEndDate())
        || !response.getExpectedNextBillingStartDate()
            .equals(response.getExpectedSpendTodayDate());

    CompletableFuture<BigDecimal> nextBillingFuture = null;
    if (needsSeparateNextBillingCall) {
      nextBillingFuture = CompletableFuture.supplyAsync(() ->
          cdhOutPort.getTransactions(accountId, email,
                  response.getExpectedNextBillingStartDate(),
                  response.getExpectedNextBillingEndDate(), 1, 1)
              .getTotalBookingValue());
    }

    BigDecimal spendTodayValue = spendTodayFuture.join();
    response.setExpectedSpendToday(spendTodayValue);

    if (needsSeparateNextBillingCall && nextBillingFuture != null) {
      response.setExpectedNextBilling(nextBillingFuture.join());
    } else {
      response.setExpectedNextBilling(spendTodayValue);
    }

    response.setExpectedNextPeriod(nextPeriodFuture.join());
  }

  private static String getCurrencyFromWorldline(AccountInfoResponse worldlineAccount) {
    return Optional
        .ofNullable(worldlineAccount.getData().getStatementValue())
        .map(AccountValue::getCurrencyCode)
        .orElse(Optional
            .ofNullable(worldlineAccount.getData().getOutStandingBalance())
            .map(AccountValue::getCurrencyCode)
            .orElse(null));
  }

  private TetheredGuidResponse getMatchingTetherUserGuidFromCdh(String companyId, String employeeId,
      String tetherUserGuid, String email) {

    var tetheredGuids = cdhOutPort.getTetheredGuids(companyId, employeeId, email);

    var matchingGuid = Optional
        .ofNullable(tetheredGuids)
        .flatMap(list -> findTetheredUserInCdhResponse(list, tetherUserGuid));

    if (matchingGuid.isEmpty()) {
      var ex = new UnknownAccountException(ErrorCode.ACCOUNT_NOT_BELONGING_TO_USER,
          String.format("Invalid tetherUserGuid for current user, companyId: %s, employeeId: "
              + "%s, tetherUserGuid: %s.", companyId, employeeId, tetherUserGuid));
      ExceptionLogger.log(log, ex);
      throw ex;
    }

    return matchingGuid.get();
  }

  private Optional<TetheredGuidResponse> findTetheredUserInCdhResponse(
      List<TetheredGuidResponse> cdhResponse,
      String tetheredUserGuid) {

    return cdhResponse.stream()
        .filter(Objects::nonNull)
        .filter(user -> Objects.nonNull(user.getTetheredGuid()))
        .filter(user -> Objects.nonNull(user.getScheme()))
        .filter(user -> user.getTetheredGuid().equals(tetheredUserGuid))
        .findFirst();
  }

  private boolean isTetheredUserPresent(String tetheredUser) {
    return StringUtils.isNotBlank(tetheredUser);
  }

}
