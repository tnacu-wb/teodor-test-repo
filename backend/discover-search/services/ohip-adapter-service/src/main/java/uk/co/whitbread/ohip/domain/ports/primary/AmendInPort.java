package uk.co.whitbread.ohip.domain.ports.primary;

import uk.co.whitbread.ohip.domain.model.amend.in.AmendSummaryRequest;
import uk.co.whitbread.ohip.domain.model.amend.out.AmendSummaryResponse;

public interface AmendInPort {
  AmendSummaryResponse getAmendSummary(AmendSummaryRequest amendSummaryRequest);

}
