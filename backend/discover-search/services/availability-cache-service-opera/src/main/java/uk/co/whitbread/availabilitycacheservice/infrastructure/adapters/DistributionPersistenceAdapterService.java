package uk.co.whitbread.availabilitycacheservice.infrastructure.adapters;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.co.whitbread.availabilitycacheservice.domain.model.distribution.DistributionHotel;
import uk.co.whitbread.availabilitycacheservice.domain.ports.secondary.DistributionHotelsPostProcessorPort;
import uk.co.whitbread.availabilitycacheservice.domain.ports.secondary.DistributionPersistenceAdapter;
import uk.co.whitbread.availabilitycacheservice.infrastructure.model.hotelavailability.DistributionHotelAvailResultWithRestrictionSet;
import uk.co.whitbread.availabilitycacheservice.infrastructure.repository.read.HotelAvailabilitiesJpaRepository;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.request.distribution.DistributionPayload;

@Slf4j
@Service
@RequiredArgsConstructor
public class DistributionPersistenceAdapterService implements DistributionPersistenceAdapter {

  private final HotelAvailabilitiesJpaRepository hotelAvailabilitiesJpaRepository;
  private final DistributionHotelsPostProcessorPort distributionHotelsPostProcessorPort;

  @Override
  public List<DistributionHotel> getHotelAvailabilitiesForDistribution(
      final DistributionPayload distributionPayload) {

    log.debug("Processing started for the getHotelAvailabilitiesForDistribution with distributionPayload - {}",
        distributionPayload);
    final LocalDate arrivalDate = LocalDate.parse(distributionPayload.getArrival());
    final LocalDate departureDate = LocalDate.parse(distributionPayload.getDeparture());
    final List<String> hotelCodes = distributionPayload.getHotelCodes();
    final Set<String> roomTypesFromCriteria =
        Arrays.stream(distributionPayload.getRoomTypes())
            .flatMap(Arrays::stream)
            .collect(Collectors.toSet());

    if (hotelCodes.isEmpty() || roomTypesFromCriteria.isEmpty()) {
      log.info("empty hotel codes or roomTypes passed in request, so returning emptyList - {}", distributionPayload);
      return Collections.emptyList();
    }
    final List<DistributionHotelAvailResultWithRestrictionSet> distributionHotelAvailResultSets =
        hotelAvailabilitiesJpaRepository.findAvailabilitiesForDistributionWithRestriction(
            hotelCodes, arrivalDate, departureDate, roomTypesFromCriteria, distributionPayload.getRateCodes());

    if (distributionHotelAvailResultSets.isEmpty()) {
      log.info("No results returned from DB, so returning empty list");
      return Collections.emptyList();
    }
    log.debug("distributionHotelAvailResultSets - {}", distributionHotelAvailResultSets);
    return distributionHotelsPostProcessorPort.performDistributionHotelsPostProcess(
        distributionPayload, distributionHotelAvailResultSets);
  }
}
