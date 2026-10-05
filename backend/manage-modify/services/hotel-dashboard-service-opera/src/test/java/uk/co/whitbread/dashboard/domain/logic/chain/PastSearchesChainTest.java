package uk.co.whitbread.dashboard.domain.logic.chain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.dashboard.domain.model.RetrieveDashboardChainRequest;
import uk.co.whitbread.dashboard.domain.model.in.RetrieveDashboardRequest;
import uk.co.whitbread.dashboard.domain.model.out.DashboardElement;
import uk.co.whitbread.dashboard.domain.model.out.DashboardType;

@ExtendWith(MockitoExtension.class)
class PastSearchesChainTest {

  @InjectMocks
  private PastSearchesChain target;

  static final String ORIGIN = "origin.header.for.test";

  @Test
  void shouldReturnPastSearches() {

    final var requestChain = new RetrieveDashboardChainRequest(new RetrieveDashboardRequest(), "", null,
        false, ORIGIN, "test@best.com");

    final var dashboardElement = new DashboardElement();

    target.handle(requestChain, dashboardElement);

    Assertions.assertThat(dashboardElement.getType()).isEqualTo(DashboardType.PAST_SEARCHES);
  }

  @Test
  void ShouldIgnoreDueToHasRecentSearchesFlagFalse() {
    final var requestChain = new RetrieveDashboardChainRequest(new RetrieveDashboardRequest(), "", null,
        true, ORIGIN, "test@best.com");

    final var dashboardElement = new DashboardElement();

    target.handle(requestChain, dashboardElement);

    Assertions.assertThat(dashboardElement.getType()).isNull();
  }

  @Test
  void shouldIgnoreDueToTypeIsNotNull() {
    final var requestChain = new RetrieveDashboardChainRequest(new RetrieveDashboardRequest(), "", null,
        false, ORIGIN, "test@best.com");

    final var dashboardElement = new DashboardElement();
    dashboardElement.setType(DashboardType.UPCOMING_BOOKING);

    target.handle(requestChain, dashboardElement);

    Assertions.assertThat(dashboardElement.getType()).isNotNull();
    Assertions.assertThat(dashboardElement.getType()).isNotEqualTo(DashboardType.PAST_SEARCHES);
  }

}