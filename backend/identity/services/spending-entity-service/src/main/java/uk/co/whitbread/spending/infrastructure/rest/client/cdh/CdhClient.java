package uk.co.whitbread.spending.infrastructure.rest.client.cdh;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.co.whitbread.shared.cdh.RegistrationDataService;
import uk.co.whitbread.shared.cdh.ReportDataService;
import uk.co.whitbread.shared.cdh.model.AccountSpendingResponse;
import uk.co.whitbread.shared.cdh.model.CompanySpendingResponse;
import uk.co.whitbread.shared.cdh.model.GetAccountLevelDetailsQueryParams;
import uk.co.whitbread.shared.cdh.model.GetCompanyLevelDetailsQueryParams;
import uk.co.whitbread.shared.cdh.model.GetDashboardDetailsQueryParams;
import uk.co.whitbread.shared.cdh.model.PibaTetheredGuidResponse;
import uk.co.whitbread.shared.cdh.model.spending.transaction.TransactionDetailsRequest;
import uk.co.whitbread.shared.cdh.model.spending.transaction.TransactionDetailsResponse;
import uk.co.whitbread.spending.domain.model.in.AccountSpendingRequest;
import uk.co.whitbread.spending.domain.model.in.CompanySpendingRequest;

@Component
@Slf4j
@RequiredArgsConstructor
public class CdhClient {

  private static final String ACCESS_CONTEXT = "InnBusiness";

  private final ReportDataService reportDataService;
  private final RegistrationDataService registrationDataService;

  public List<CompanySpendingResponse> getCompanySpending(
      CompanySpendingRequest companySpendingRequest, String email) {

    return reportDataService.getCompanyLevelDetails(
        companySpendingRequest.getCompanyAccountId(),
        GetCompanyLevelDetailsQueryParams.builder()
            .fromMonthYear(companySpendingRequest.getFromMonthYear())
            .toMonthYear(companySpendingRequest.getToMonthYear())
            .build(),
        email,
        ACCESS_CONTEXT);
  }

  public List<AccountSpendingResponse> getAccountSpending(
      AccountSpendingRequest accountSpendingRequest, String email) {

    return reportDataService.getAccountLevelDetails(
        accountSpendingRequest.getPibaAccountId(),
        GetAccountLevelDetailsQueryParams.builder()
            .fromMonthYear(accountSpendingRequest.getFromMonthYear())
            .toMonthYear(accountSpendingRequest.getToMonthYear())
            .build(),
        email,
        ACCESS_CONTEXT
    );
  }

  public List<PibaTetheredGuidResponse> getTetheredGuids(String companyId, String employeeId, String email) {
    log.info("Retrieve tethered guids from CDH request for company id {}, employee id {}", companyId, employeeId);

    return registrationDataService.getDashboardDetails(
          GetDashboardDetailsQueryParams.builder()
                .companyId(companyId)
                .employeeId(employeeId)
                .build(),
          email,
          ACCESS_CONTEXT);
  }

  public TransactionDetailsResponse getTransactions(String accountId, String email,
        LocalDate fromDate, LocalDate toDate, int pageNumber, int pageSize) {
    String sanitizedAccountId = accountId
          .replace("\n", "")
          .replace("\r", "");
    log.info("Retrieve transactions from CDH for accountId: {}, fromDate:{}, toDate: {}, "
          + "pageNumber:{}, pageSize:{}.", sanitizedAccountId, fromDate, toDate, pageNumber,
          pageSize);

    return reportDataService.getTransactionDetails(
          TransactionDetailsRequest
                .builder()
                .pibaAccountNo(sanitizedAccountId)
                .fromDate(fromDate)
                .toDate(toDate)
                .pageNumber(pageNumber)
                .pageSize(pageSize)
                .build(),
          email,
          ACCESS_CONTEXT);
  }
}
