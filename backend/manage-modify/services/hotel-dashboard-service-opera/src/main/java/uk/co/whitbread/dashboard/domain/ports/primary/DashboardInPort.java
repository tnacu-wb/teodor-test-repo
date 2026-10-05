package uk.co.whitbread.dashboard.domain.ports.primary;

import java.util.List;
import uk.co.whitbread.dashboard.domain.model.in.RetrieveDashboardRequest;
import uk.co.whitbread.dashboard.domain.model.out.DashboardElement;

public interface DashboardInPort {

  List<DashboardElement> retrieveDashboard(RetrieveDashboardRequest request, String token,
      String sessionId, boolean hasRecentSearches, String origin, String customerId);

}
