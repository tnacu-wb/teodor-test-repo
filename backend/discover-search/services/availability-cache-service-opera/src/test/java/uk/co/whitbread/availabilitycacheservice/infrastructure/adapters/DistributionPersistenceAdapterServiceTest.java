package uk.co.whitbread.availabilitycacheservice.infrastructure.adapters;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.availabilitycacheservice.domain.ports.secondary.DistributionHotelsPostProcessorPort;
import uk.co.whitbread.availabilitycacheservice.infrastructure.model.hotelavailability.DistributionHotelAvailResultWithRestrictionSet;
import uk.co.whitbread.availabilitycacheservice.infrastructure.repository.read.HotelAvailabilitiesJpaRepository;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.distribution.DistributionPayload;

@ExtendWith(MockitoExtension.class)
class DistributionPersistenceAdapterServiceTest {

  @Mock
  private HotelAvailabilitiesJpaRepository hotelAvailabilitiesJpaRepository;

  @Mock
  private DistributionHotelsPostProcessorPort distributionHotelsPostProcessorPort;

  @InjectMocks
  private DistributionPersistenceAdapterService distributionPersistenceAdapter;

  private DistributionPayload distributionPayload;

  @BeforeEach
  void setup() {
    distributionPayload = buildDistributionPayload();
  }

  @Test
  void testGetHotelAvailabilitiesForDistributionEmptyHotelCodes() {
    distributionPayload.setHotelCodes(Collections.emptyList());

    assertTrue(distributionPersistenceAdapter.getHotelAvailabilitiesForDistribution(distributionPayload).isEmpty());
    verify(hotelAvailabilitiesJpaRepository, never()).findAvailabilitiesForDistributionWithRestriction(
        anyList(), any(), any(), anySet(), anySet());
    verify(distributionHotelsPostProcessorPort, never()).performDistributionHotelsPostProcess(any(), anyList());
  }

  @Test
  void testGetHotelAvailabilitiesForDistributionEmptyRoomTypes() {
    distributionPayload.setRoomTypes(new String[][]{{}});

    assertTrue(distributionPersistenceAdapter.getHotelAvailabilitiesForDistribution(distributionPayload).isEmpty());
    verify(hotelAvailabilitiesJpaRepository, never()).findAvailabilitiesForDistributionWithRestriction(
        anyList(), any(), any(), anySet(), anySet());
    verify(distributionHotelsPostProcessorPort, never()).performDistributionHotelsPostProcess(any(), anyList());
  }

  @Test
  void testGetHotelAvailabilitiesForDistributionEmptyResultsFromRepository() {
    when(hotelAvailabilitiesJpaRepository.findAvailabilitiesForDistributionWithRestriction(
        anyList(), any(), any(), anySet(), anySet())).thenReturn(Collections.emptyList());

    assertTrue(distributionPersistenceAdapter.getHotelAvailabilitiesForDistribution(distributionPayload).isEmpty());
    verify(distributionHotelsPostProcessorPort, never()).performDistributionHotelsPostProcess(any(), anyList());
  }

  @Test
  void testGetHotelAvailabilitiesForDistributionSuccess() {
    List<DistributionHotelAvailResultWithRestrictionSet> resultSets = Collections.singletonList(
        DistributionHotelAvailResultWithRestrictionSet.builder()
            .hotelCode("LONSLA")
            .pmsSource("OPERA")
            .availableDate(LocalDate.now())
            .amount(BigDecimal.valueOf(100.00))
            .quantity(10)
            .rateClassification("A")
            .rateCode("Non-Flex")
            .availability(true)
            .build());

    when(hotelAvailabilitiesJpaRepository.findAvailabilitiesForDistributionWithRestriction(
        anyList(), any(), any(), anySet(), anySet())).thenReturn(resultSets);

    distributionPersistenceAdapter.getHotelAvailabilitiesForDistribution(distributionPayload);

    verify(distributionHotelsPostProcessorPort, times(1))
        .performDistributionHotelsPostProcess(distributionPayload, resultSets);
  }

  private DistributionPayload buildDistributionPayload() {
    return DistributionPayload.builder()
        .arrival("2024-01-01")
        .departure("2024-01-03")
        .roomTypes(new String[][]{{"SB", "EXTSB", "SBDB"}, {"DB", "EXTSB", "SB"}})
        .hotelCodes(List.of("LONSLA"))
        .rateCodes(Set.of("Non-Flex"))
        .build();
  }
}
