package uk.co.whitbread.cdh.infrastructure.rest.client.reservation.search;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.cdh.domain.model.booking.in.ReservationSearchCriteria;
import uk.co.whitbread.cdh.domain.model.booking.out.ReservationSearch;
import uk.co.whitbread.cdh.domain.model.feature.FeatureFlag;
import uk.co.whitbread.cdh.domain.model.feature.UnleashWrapper;

@ExtendWith(MockitoExtension.class)
class CdhReservationSearchOutPortImplTest {

  @InjectMocks
  CdhReservationSearchOutPortImpl cdhReservationSearchOutPort;

  @Mock
  CdhReservationSearchClientV2 cdhReservationSearchClientV2;

  @Mock
  CdhReservationSearchClientV1 cdhReservationSearchClientV1;

  @Mock
  CdhReservationSearchClientV3 cdhReservationSearchClientV3;

  @Mock
  UnleashWrapper<FeatureFlag> unleashWrapper;

  @Mock
  FeatureFlag featureFlag;

  @Test
  void test_getReservationSearch_featureFlagDisabled_callsV2() {
    var criteria = ReservationSearchCriteria.builder().reservationId(1234).build();

    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(featureFlag.getCdhApiDeprecation())).thenReturn(false);
    when(cdhReservationSearchClientV2.getReservationSearch(criteria))
        .thenReturn(ReservationSearch.builder().totalResults(1).build());

    ReservationSearch result = cdhReservationSearchOutPort.getReservationSearch(criteria);

    assertEquals(1, result.getTotalResults());
    verify(cdhReservationSearchClientV2).getReservationSearch(criteria);
  }

  @Test
  void test_getReservationSearch_featureFlagEnabled_callsV3() {
    var criteria = ReservationSearchCriteria.builder().reservationId(5678).build();

    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(featureFlag.getCdhApiDeprecation())).thenReturn(true);
    when(cdhReservationSearchClientV3.getReservationSearch(criteria))
        .thenReturn(ReservationSearch.builder().totalResults(3).build());

    ReservationSearch result = cdhReservationSearchOutPort.getReservationSearch(criteria);

    assertEquals(3, result.getTotalResults());
    verify(cdhReservationSearchClientV3).getReservationSearch(criteria);
  }

  @Test
  void test_getReservationSearch_featureFlagEnabled_nullResults_setsDefaults() {
    var criteria = ReservationSearchCriteria.builder().reservationId(9999).build();

    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(featureFlag.getCdhApiDeprecation())).thenReturn(true);
    when(cdhReservationSearchClientV3.getReservationSearch(criteria))
        .thenReturn(ReservationSearch.builder().results(null).totalResults(null).build());

    ReservationSearch result = cdhReservationSearchOutPort.getReservationSearch(criteria);

    assertEquals(0, result.getTotalResults());
    assertEquals(0, result.getSearchResults());
    assertEquals(0, result.getTotalSize());
  }

  @Test
  void test_getReservationSearch_featureFlagDisabled_nullResults_setsDefaults() {
    var criteria = ReservationSearchCriteria.builder().reservationId(1111).build();

    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(featureFlag.getCdhApiDeprecation())).thenReturn(false);
    when(cdhReservationSearchClientV2.getReservationSearch(criteria))
        .thenReturn(ReservationSearch.builder().results(null).totalResults(null).build());

    ReservationSearch result = cdhReservationSearchOutPort.getReservationSearch(criteria);

    assertEquals(0, result.getTotalResults());
    assertEquals(0, result.getSearchResults());
    assertEquals(0, result.getTotalSize());
  }

  @Test
  void test_getReservationById_v1() {
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(featureFlag.getCdhApiDeprecation())).thenReturn(false);
    when(cdhReservationSearchClientV1.getReservationById("BAV0407934")).thenReturn(ReservationSearch.builder()
        .totalResults(1).build());

    ReservationSearch reservationById = cdhReservationSearchOutPort.getReservationById("BAV0407934");

    assertEquals(1, reservationById.getTotalResults());
  }

  @Test
  void test_getReservationById_v3() {

    var criteria = ReservationSearchCriteria.builder().bookingReference("BAV0407934").bookingsDatabaseSearch(false).build();

    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(featureFlag.getCdhApiDeprecation())).thenReturn(true);
    when(cdhReservationSearchClientV3.getReservationSearch(criteria))
        .thenReturn(ReservationSearch.builder().results(null).totalResults(1).build());

    ReservationSearch reservationById = cdhReservationSearchOutPort.getReservationById("BAV0407934");

    assertEquals(1, reservationById.getTotalResults());
  }

  @Test
  void test_getReservationById_v1_nullResultsAndTotalResults_setsDefaults() {
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(featureFlag.getCdhApiDeprecation())).thenReturn(false);
    when(cdhReservationSearchClientV1.getReservationById("BAV0000001"))
        .thenReturn(ReservationSearch.builder().results(null).totalResults(null).build());

    ReservationSearch result = cdhReservationSearchOutPort.getReservationById("BAV0000001");

    assertEquals(0, result.getTotalResults());
    assertEquals(0, result.getSearchResults());
    assertEquals(0, result.getTotalSize());
  }

  @Test
  void test_getReservationById_v3_nullResultsAndTotalResults_setsDefaults() {
    var criteria = ReservationSearchCriteria.builder().bookingReference("BAV0000002").bookingsDatabaseSearch(false).build();

    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(featureFlag.getCdhApiDeprecation())).thenReturn(true);
    when(cdhReservationSearchClientV3.getReservationSearch(criteria))
        .thenReturn(ReservationSearch.builder().results(null).totalResults(null).build());

    ReservationSearch result = cdhReservationSearchOutPort.getReservationById("BAV0000002");

    assertEquals(0, result.getTotalResults());
    assertEquals(0, result.getSearchResults());
    assertEquals(0, result.getTotalSize());
  }

}
