package uk.co.whitbread.spending.domain.ports.secondary;

import java.time.LocalDate;
import java.util.List;
import uk.co.whitbread.spending.domain.model.in.AccountSpendingRequest;
import uk.co.whitbread.spending.domain.model.in.CompanySpendingRequest;
import uk.co.whitbread.spending.domain.model.out.AccountSpendingResponse;
import uk.co.whitbread.spending.domain.model.out.CompanySpendingResponse;
import uk.co.whitbread.spending.domain.model.out.cdh.TetheredGuidResponse;
import uk.co.whitbread.spending.domain.model.out.cdh.TransactionDetails;

public interface CdhOutPort {

  CompanySpendingResponse getCompanySpending(CompanySpendingRequest companySpendingRequest,
        String email);

  AccountSpendingResponse getAccountSpending(AccountSpendingRequest accountSpendingRequest,
        String email);

  List<TetheredGuidResponse> getTetheredGuids(String companyId, String employeeId, String email);

  TransactionDetails getTransactions(String accountId, String email, LocalDate fromDate,
        LocalDate toDate, int pageNumber, int pageSize);
}
