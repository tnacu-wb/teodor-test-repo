package uk.co.whitbread.domain.logic;

import static uk.co.whitbread.domain.logic.OccupancySupplementDistributionUtils.getNumberOfNights;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import uk.co.whitbread.domain.model.availability.in.MultiHotelRestrictionsByDateRangeRequest;
import uk.co.whitbread.domain.model.availability.in.RestrictionsByDateRangeRequest;
import uk.co.whitbread.domain.model.availability.out.RestrictionSets;
import uk.co.whitbread.domain.model.availability.out.RestrictionsByDateRangeResult;
import uk.co.whitbread.domain.model.availability.out.RoomRate;
import uk.co.whitbread.domain.model.feature.FeatureFlag;
import uk.co.whitbread.domain.model.feature.UnleashWrapper;
import uk.co.whitbread.domain.ports.secondary.HotelAvailabilityOutPort;

/**
 * Encapsulates common logic dealing with "MinimumLengthOfStay" hotel restriction type.
 */
@Component
@AllArgsConstructor
public class MlosCommonLogic {

  private static final String CCUI_CHANNEL = "CCUI";
  private static final String RESTRICTION_CODE_MLOS = "MinimumLengthOfStay";

  private final UnleashWrapper<FeatureFlag> unleashWrapper;
  private final HotelAvailabilityOutPort availabilityOhipPort;

  public boolean isMlosEnabled(String channel) {
    return channel.equalsIgnoreCase(CCUI_CHANNEL)
          && unleashWrapper.isEnabled(unleashWrapper.featureFlag().getShowMlosCcui());
  }

  public boolean hasMlosRestriction(String hotelId, String startDate, String endDate,
        boolean isHotelAvailable, List<RoomRate> hotelRoomRates) {
    // the "available" flag is set based on hotel inventory which doesn't take into account MLOS
    if (!isHotelAvailable) {
      return false;
    }

    // if there are available rates, the hotel is available and no MLOS is displayed
    if (!hotelRoomRates.isEmpty()) {
      return false;
    }

    var hotelRestrictions = getRestrictionsForHotel(hotelId, startDate, endDate);
    return isMlosRestrictionApplied(hotelRestrictions, startDate, endDate);
  }

  public Map<String, RestrictionsByDateRangeResult> getRestrictionsMapForHotels(List<String> hotelIds,
                                                                                String startDate, String endDate) {
    if (hotelIds.isEmpty()) {
      return new HashMap<>();
    }
    var hotelRestrictions = availabilityOhipPort.getMultiHotelRestrictionsByDateRange(
        MultiHotelRestrictionsByDateRangeRequest.builder()
            .startDate(startDate)
            .endDate(endDate)
            .hotelIds(hotelIds)
            .build());
    return hotelRestrictions.stream().filter(restriction -> restriction.getHotelId() != null)
        .collect(Collectors.toMap(RestrictionsByDateRangeResult::getHotelId, result -> result));
  }

  public boolean isMlosRestrictionApplied(RestrictionsByDateRangeResult hotelRestrictions, String startDate,
                                          String endDate) {
    if (hotelRestrictions == null || hotelRestrictions.getRestrictionSets() == null
        || hotelRestrictions.getRestrictionSets().isEmpty()) {
      return false;
    }

    Optional<RestrictionSets> anyBrokenInterval = hotelRestrictions.getRestrictionSets().stream()
        .filter(p -> p.getRestrictionStatus().getCode().equals(RESTRICTION_CODE_MLOS))
        .filter(p -> p.getRestrictionStatus().getUnit() > getNumberOfNights(startDate, endDate))
        .findAny();

    return anyBrokenInterval.isPresent();
  }

  private RestrictionsByDateRangeResult getRestrictionsForHotel(String hotelId, String startDate, String endDate) {
    return availabilityOhipPort.getRestrictionsByDateRange(
        RestrictionsByDateRangeRequest.builder()
            .startDate(startDate)
            .endDate(endDate)
            .hotelId(hotelId)
            .build());
  }

}

