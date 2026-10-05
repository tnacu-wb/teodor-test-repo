package uk.co.whitbread.dashboard.domain.logic.chain;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import uk.co.whitbread.dashboard.domain.model.RetrieveDashboardChainRequest;
import uk.co.whitbread.dashboard.domain.model.out.DashboardElement;
import uk.co.whitbread.dashboard.domain.model.out.DashboardType;

@Component
@Order(4)
public class PastSearchesChain extends DashboardChain {

  @Override
  protected void get(RetrieveDashboardChainRequest request, DashboardElement response) {

    if (!request.isRecentSearches()) {
      response.setType(DashboardType.PAST_SEARCHES);
    }
  }
}
