package uk.co.whitbread.ohip.domain.logic;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.ohip.domain.model.amend.in.AmendSummaryRequest;
import uk.co.whitbread.ohip.domain.model.amend.out.AmendSummaryResponse;
import uk.co.whitbread.ohip.domain.ports.primary.AmendInPort;
import uk.co.whitbread.ohip.domain.ports.secondary.AmendOutPort;

@RequiredArgsConstructor
@Slf4j
public class AmendInPortImpl implements AmendInPort {
  private final AmendOutPort amendOutPort;

  @Override
  public AmendSummaryResponse getAmendSummary(AmendSummaryRequest amendSummaryRequest) {
    return amendOutPort.getRateInfoSummary(amendSummaryRequest);
  }
}
