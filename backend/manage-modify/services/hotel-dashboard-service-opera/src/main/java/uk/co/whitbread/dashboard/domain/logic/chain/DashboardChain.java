package uk.co.whitbread.dashboard.domain.logic.chain;

import java.util.Objects;
import uk.co.whitbread.dashboard.domain.model.RetrieveDashboardChainRequest;
import uk.co.whitbread.dashboard.domain.model.out.DashboardElement;

public abstract class DashboardChain {

  protected abstract void get(RetrieveDashboardChainRequest request, DashboardElement response);

  public void handle(final RetrieveDashboardChainRequest request, final DashboardElement response) {
    if (Objects.nonNull(response.getType())) {
      return;
    }
    get(request, response);
  }
}
