package uk.co.whitbread.reservation.domain.ports.secondary;

import uk.co.whitbread.reservation.domain.model.out.BusinessAllowanceRuleResponse;
import uk.co.whitbread.reservation.domain.model.out.ChannelRuleResponse;
import uk.co.whitbread.reservation.domain.model.out.MaxNightsRuleResponse;
import uk.co.whitbread.reservation.domain.model.out.MaxRoomOccupancyResponse;
import uk.co.whitbread.reservation.domain.model.out.MaxRoomsRuleResponse;
import uk.co.whitbread.reservation.domain.model.out.RulesAmendmentResponse;
import uk.co.whitbread.reservation.domain.model.out.SingleOccupancySupplementResponse;
import uk.co.whitbread.reservation.domain.model.out.VatRuleResponse;

public interface RulesOutPort {

  RulesAmendmentResponse isBookingAmendable(String rateType, String arrivalDate,
                                            String hotelLocalDateTime, String hotelCountryCode);

  MaxRoomsRuleResponse getMaxRoomsRule(String channelId);

  MaxNightsRuleResponse getMaxNightsRule(String channelId);

  MaxRoomOccupancyResponse getMaxRoomOccupancyRule(String channelId, String brand);

  ChannelRuleResponse getChannelBasedOnSourceId(String sourceId);

  VatRuleResponse getVatCodesForPackage(String vatRegion, String packageCode);

  BusinessAllowanceRuleResponse getBusinessAllowanceRules();

  SingleOccupancySupplementResponse getSingleOccupancySupplementResponse(String hotelId);
}
