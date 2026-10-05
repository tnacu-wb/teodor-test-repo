package uk.co.whitbread.spending.domain.ports.primary;

import java.util.List;
import uk.co.whitbread.spending.domain.model.in.AccountSpendingRequest;
import uk.co.whitbread.spending.domain.model.in.CompanySpendingRequest;
import uk.co.whitbread.spending.domain.model.in.EmployeeSpendRequest;
import uk.co.whitbread.spending.domain.model.in.PaymentInfoModel;
import uk.co.whitbread.spending.domain.model.out.AccountSpendingResponse;
import uk.co.whitbread.spending.domain.model.out.CompanySpendingResponse;
import uk.co.whitbread.spending.domain.model.out.UpcomingSpendingResponse;
import uk.co.whitbread.spending.domain.model.out.cdh.EmployeeSpendReport;
import uk.co.whitbread.spending.domain.model.out.worldline.PaymentInfoResponse;

public interface SpendingInPort {

  CompanySpendingResponse getCompanySpending(CompanySpendingRequest companySpendingRequest);

  AccountSpendingResponse getAccountSpending(AccountSpendingRequest accountSpendingRequest, String authorization);

  List<EmployeeSpendReport> getEmployeeSpend(EmployeeSpendRequest employeeSpendRequest);

  UpcomingSpendingResponse getUpcomingSpending(String authorization, String accountId,
        String clientIpAddress, String tetheredUserGuid);

  PaymentInfoResponse getPaymentInfo(PaymentInfoModel paymentInfoModel);
}
