package uk.co.whitbread.domain.ports.secondary;

import java.util.List;
import uk.co.whitbread.domain.model.availability.in.HotelAvailabilityByIdsRequest;
import uk.co.whitbread.domain.model.availability.in.HotelAvailabilityByIdsV2Request;
import uk.co.whitbread.domain.model.availability.out.HotelAvailabilityByIds;
import uk.co.whitbread.domain.model.availability.out.HotelAvailabilityByIdsV2;
import uk.co.whitbread.domain.model.rulesagent.out.RoomSubstitutionRuleResponse;
import uk.co.whitbread.domain.model.srp.in.HotelAvailabilitiesRequest;
import uk.co.whitbread.domain.model.srp.out.HotelAvailabilitiesResponse;

public interface AvailabilityCacheV1SearchOutPort {

  HotelAvailabilitiesResponse getAvailabilitiesFromAvCache(
      HotelAvailabilitiesRequest hotelAvailabilitiesRequest,
      List<String> listOfHotelCodesByLocation,
      List<List<String>> roomSubstitutionRuleResponseList, boolean flagMlos);

  HotelAvailabilityByIds getHotelAvailabilityByIdsFromAvCache(
      HotelAvailabilityByIdsRequest hotelAvailabilityByIdsRequest,
      List<RoomSubstitutionRuleResponse> roomSubstitutionRuleResponses
  );

  HotelAvailabilityByIdsV2 getHotelAvailabilityByIdsV2FromAvCache(
      HotelAvailabilityByIdsV2Request hotelAvailabilityByIdsV2Request,
      List<RoomSubstitutionRuleResponse> roomSubstitutionRuleResponses
  );

}
