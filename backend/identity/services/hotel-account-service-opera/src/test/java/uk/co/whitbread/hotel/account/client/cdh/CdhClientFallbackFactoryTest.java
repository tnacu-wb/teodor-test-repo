package uk.co.whitbread.hotel.account.client.cdh;

import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.co.whitbread.cdh.adapter.service.generated.models.reservationSearch.CdhReservationSearchCriteriaDto;

class CdhClientFallbackFactoryTest {

  @Mock
  private Throwable throwable;

  private CdhClientFallbackFactory fallbackFactory;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    fallbackFactory = new CdhClientFallbackFactory();
  }

  @Test
  void testCreate() {
    CdhClient fallbackClient = fallbackFactory.create(throwable);
    CdhReservationSearchCriteriaDto queryParams = new CdhReservationSearchCriteriaDto();
    queryParams.setCustomerAccountId("testAccountId");

    assertNull(fallbackClient.getAccountBookingsV2(queryParams));
  }
}
