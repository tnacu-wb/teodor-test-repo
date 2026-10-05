package uk.co.whitbread.ohip.domain.ports.secondary;

import uk.co.whitbread.ohip.domain.model.amend.in.AmendSummaryRequest;
import uk.co.whitbread.ohip.domain.model.amend.out.AmendSummaryResponse;

public interface AmendOutPort {
  AmendSummaryResponse getRateInfoSummary(AmendSummaryRequest amendSummaryRequest);
}
