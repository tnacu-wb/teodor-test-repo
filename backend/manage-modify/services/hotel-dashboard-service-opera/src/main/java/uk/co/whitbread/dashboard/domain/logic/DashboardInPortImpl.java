package uk.co.whitbread.dashboard.domain.logic;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.co.whitbread.dashboard.domain.logic.chain.DashboardChain;
import uk.co.whitbread.dashboard.domain.logic.chain.FrequentBookingChain;
import uk.co.whitbread.dashboard.domain.logic.chain.PastSearchesChain;
import uk.co.whitbread.dashboard.domain.logic.chain.RecentSearchesChain;
import uk.co.whitbread.dashboard.domain.logic.chain.UpcomingBookingChain;
import uk.co.whitbread.dashboard.domain.model.RetrieveDashboardChainRequest;
import uk.co.whitbread.dashboard.domain.model.in.RetrieveDashboardRequest;
import uk.co.whitbread.dashboard.domain.model.out.DashboardElement;
import uk.co.whitbread.dashboard.domain.model.out.DashboardType;
import uk.co.whitbread.dashboard.domain.ports.primary.DashboardInPort;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardInPortImpl implements DashboardInPort {

  private final List<DashboardChain> dashboardChain;

  /**
   * There are 4 possible outcomes for this method. 1)
   * arrivalDate is within 2 weeks Service will retrieve reservation from upcomingBookingService
   * {@link UpcomingBookingChain}
   *
   * <p>2) arrivalDate is not within 2 weeks and hasRecentSearches=true Service will return flag RECENT_SEARCHES
   * {@link RecentSearchesChain}
   *
   * <p>3) arrivalDate is not within 2 weeks, hasRecentSearches=false, Authorization is not empty and customer has
   * frequent
   * bookings Service will return frequent bookings
   * {@link FrequentBookingChain}
   *
   * <p>4) arrivalDate is not within 2 weeks, hasRecentSearches=false, Authorization is not empty and customer has no
   * frequent bookings Service will return flag PAST_SEARCHES
   * {@link PastSearchesChain}
   *
   * @param request           is the upcoming details
   * @param hasRecentSearches is to check if we should send the flag RECENT_SEARCHES or PAST_SEARCHES
   * @param token             Auth0 JWT token the user gets when is logged in
   * @param sessionId         sessionId of the user
   * @param origin            origin of the request
   * @param customerId        customerId of the user
   * @return List of DashboardElement with one of the types {@link DashboardType}
   */
  @Override
  public List<DashboardElement> retrieveDashboard(final RetrieveDashboardRequest request, final String token,
      final String sessionId, final boolean hasRecentSearches, final String origin, final String customerId) {

    final RetrieveDashboardChainRequest chainRequest = new RetrieveDashboardChainRequest(request, token, sessionId,
        hasRecentSearches, origin, customerId);
    final DashboardElement response = new DashboardElement();

    dashboardChain.forEach(chain -> chain.handle(chainRequest, response));

    return List.of(response);
  }
}
