package uk.co.whitbread.spending.infrastructure.rest.client.cdh;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.spending.domain.model.in.AccountSpendingRequest;
import uk.co.whitbread.spending.domain.model.in.CompanySpendingRequest;
import uk.co.whitbread.spending.domain.model.out.AccountSpendingResponse;
import uk.co.whitbread.spending.domain.model.out.CompanySpendingResponse;
import uk.co.whitbread.spending.domain.model.out.cdh.AccountSpending;
import uk.co.whitbread.spending.domain.model.out.cdh.CompanySpending;
import uk.co.whitbread.spending.domain.model.out.cdh.TetheredGuidResponse;
import uk.co.whitbread.spending.domain.model.out.cdh.TransactionDetails;
import uk.co.whitbread.spending.domain.ports.secondary.CdhOutPort;
import uk.co.whitbread.spending.infrastructure.rest.client.cdh.mapper.AccountSpendingResponseMapper;
import uk.co.whitbread.spending.infrastructure.rest.client.cdh.mapper.CompanySpendingResponseMapper;
import uk.co.whitbread.spending.infrastructure.rest.client.cdh.mapper.PibaTetheredGuidResponseMapper;
import uk.co.whitbread.spending.infrastructure.rest.client.cdh.mapper.TransactionDetailsResponseMapper;

@Slf4j
@RequiredArgsConstructor
public class CdhOutPortImpl implements CdhOutPort {

  private final CdhClient cdhClient;
  private final CompanySpendingResponseMapper companySpendingResponseMapper;
  private final AccountSpendingResponseMapper accountSpendingResponseMapper;
  private final PibaTetheredGuidResponseMapper pibaTetheredGuidResponseMapper;
  private final TransactionDetailsResponseMapper transactionDetailsResponseMapper;

  @Override
  public CompanySpendingResponse getCompanySpending(CompanySpendingRequest companySpendingRequest,
        String email) {

    var companySpendingResponseCDH = cdhClient.getCompanySpending(companySpendingRequest, email);

    List<CompanySpending> companySpendingList = companySpendingResponseMapper.toDto(
        companySpendingResponseCDH);

    return CompanySpendingResponse.builder().companySpendingList(companySpendingList).build();
  }

  @Override
  public AccountSpendingResponse getAccountSpending(AccountSpendingRequest accountSpendingRequest,
        String email) {

    var accountSpendingResponseCDH = cdhClient.getAccountSpending(accountSpendingRequest, email);

    List<AccountSpending> accountSpendingList = accountSpendingResponseMapper.toDto(
        accountSpendingResponseCDH);

    return AccountSpendingResponse.builder().accountSpendingList(accountSpendingList).build();
  }

  @Override
  public List<TetheredGuidResponse> getTetheredGuids(String companyId, String employeeId, String email) {
    var cdhResponse = cdhClient.getTetheredGuids(companyId, employeeId, email);
    return pibaTetheredGuidResponseMapper.toDto(cdhResponse);
  }

  @Override
  public TransactionDetails getTransactions(String accountId, String email,
        LocalDate fromDate, LocalDate toDate, int pageNumber, int pageSize) {
    var cdhResponse = cdhClient.getTransactions(accountId, email, fromDate, toDate, pageNumber,
          pageSize);

    return transactionDetailsResponseMapper.toDto(cdhResponse);
  }
}
