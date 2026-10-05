package uk.co.whitbread.spending.domain.ports.primary;

import uk.co.whitbread.spending.domain.model.in.Scheme;
import uk.co.whitbread.spending.domain.model.out.AccountSpendingResponse;
import uk.co.whitbread.spending.domain.model.out.SpendingReportFile;

public interface ReportingInPort {
  SpendingReportFile generateAccountSpendingCsv(
      AccountSpendingResponse accountSpending, String language, String pibaAccountId, Scheme scheme);
}
