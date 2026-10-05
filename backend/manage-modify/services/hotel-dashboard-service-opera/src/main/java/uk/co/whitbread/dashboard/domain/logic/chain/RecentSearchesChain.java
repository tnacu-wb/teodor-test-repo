package uk.co.whitbread.dashboard.domain.logic.chain;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import uk.co.whitbread.dashboard.domain.model.RetrieveDashboardChainRequest;
import uk.co.whitbread.dashboard.domain.model.out.DashboardElement;
import uk.co.whitbread.dashboard.domain.model.out.DashboardType;

@Component
@Order(2)
public class RecentSearchesChain extends DashboardChain {

  @Override
  protected void get(RetrieveDashboardChainRequest request, DashboardElement response) {

    if (request.isRecentSearches()) {
      response.setType(DashboardType.RECENT_SEARCHES);
    }
  }
}
