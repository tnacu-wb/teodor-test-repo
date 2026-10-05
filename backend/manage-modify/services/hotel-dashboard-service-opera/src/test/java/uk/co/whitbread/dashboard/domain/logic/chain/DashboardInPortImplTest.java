package uk.co.whitbread.dashboard.domain.logic.chain;

import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.dashboard.domain.logic.DashboardInPortImpl;
import uk.co.whitbread.dashboard.domain.model.RetrieveDashboardChainRequest;
import uk.co.whitbread.dashboard.domain.model.in.RetrieveDashboardRequest;
import uk.co.whitbread.dashboard.domain.model.out.DashboardElement;

@ExtendWith(MockitoExtension.class)
class DashboardInPortImplTest {

  @Mock
  private UpcomingBookingChain upcomingBookingChain;

  @Mock
  private FrequentBookingChain frequentBookingChain;

  @Mock
  private RecentSearchesChain recentSearchesChain;

  @Mock
  private PastSearchesChain pastSearchesChain;

  private DashboardInPortImpl target;

  static final String ORIGIN = "origin.header.for.test";

  @BeforeEach
  void setUp() {
    target = new DashboardInPortImpl(
        List.of(upcomingBookingChain, recentSearchesChain, frequentBookingChain,
            pastSearchesChain));
  }

  @Test
  void shouldCheckIfAllServicesAreCalled() {
    final LocalDate todayPlus10 = LocalDate.now().plusMonths(10);
    final RetrieveDashboardRequest retrieve = RetrieveDashboardRequest.builder()
        .arrivalDate(todayPlus10)
        .confirmationNumber("CON123")
        .surname("CERVELIN")
        .business(false)
        .build();
    final var requestChain = new RetrieveDashboardChainRequest(retrieve, "TOKEN", null, false,
        ORIGIN, "test@best.com");

    final var response = new DashboardElement();

    final List<DashboardElement> dashboardElements = target.retrieveDashboard(retrieve, "TOKEN",
        null, false, ORIGIN, "test@best.com");

    verify(upcomingBookingChain).handle(requestChain, response);
    verify(recentSearchesChain).handle(requestChain, response);
    verify(frequentBookingChain).handle(requestChain, response);
    verify(pastSearchesChain).handle(requestChain, response);

    Assertions.assertThat(dashboardElements).hasSize(1);
    Assertions.assertThat(response).isEqualTo(dashboardElements.get(0));
  }

}
