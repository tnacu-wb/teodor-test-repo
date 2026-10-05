package uk.co.whitbread.availabilitycacheservice.infrastructure.adapters;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.co.whitbread.availabilitycacheservice.domain.model.pricefinder.HotelRoomRateInfo;
import uk.co.whitbread.availabilitycacheservice.domain.ports.secondary.PriceFinderAvailabilitiesOutPort;
import uk.co.whitbread.availabilitycacheservice.infrastructure.model.hotelavailability.HotelAvailabilitiesResultSet;
import uk.co.whitbread.availabilitycacheservice.infrastructure.repository.read.GqtHotelAvailabilitiesJpaRepository;

@Component
@Slf4j
@RequiredArgsConstructor
public class PriceFinderAvailabilitiesOutPortImpl implements PriceFinderAvailabilitiesOutPort {

  private static final String EMPLOYEE_RATECODE = "EMPLOYEE";
  private final GqtHotelAvailabilitiesJpaRepository hotelAvailabilitiesJpaRepository;

  public List<HotelRoomRateInfo> getMinRateHotelAvailabilities(final List<String> operaHotelCodes,
      final LocalDate arrivalDate,
      final LocalDate departureDate) {

    log.info("Process getHotelAvailabilitiesFromRepository - Invoking JPA repository with params "
        + "- operaHotelCodes - {}, arrivalDate - {}, departureDate - {}", operaHotelCodes, arrivalDate, departureDate);

    List<HotelAvailabilitiesResultSet> hotelAvailabilitiesResultSet =
        hotelAvailabilitiesJpaRepository.findAvailabilitiesForGqtOpera(operaHotelCodes, arrivalDate, departureDate);
    log.debug("hotelAvailabilitiesResultSet from JPA repository - {}", hotelAvailabilitiesResultSet);

    Map<String, List<HotelAvailabilitiesResultSet>> availabilitiesPerHotel = hotelAvailabilitiesResultSet.stream()
        .collect(Collectors.groupingBy(HotelAvailabilitiesResultSet::getHotelCode));

    return availabilitiesPerHotel.values().stream()
        .map(availabilities -> availabilities.stream()
            .filter(av -> av.getAvailableDate().isEqual(arrivalDate) && av.getQuantity() > 0)
            .filter(rate -> !rate.getRateCode().equals(EMPLOYEE_RATECODE))
            .min(Comparator.comparing(HotelAvailabilitiesResultSet::getAmount))
            .orElse(null))
        .filter(Objects::nonNull)
        .map(perHotelAv -> HotelRoomRateInfo.builder()
            .hotelCode(perHotelAv.getHotelCode())
            .classification(perHotelAv.getRateClassification())
            .rateCode(perHotelAv.getRateCode())
            .roomType(perHotelAv.getRoomType())
            .amount(perHotelAv.getAmount())
            .currency(perHotelAv.getCurrency())
            .build())
        .toList();
  }
}
