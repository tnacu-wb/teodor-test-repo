package uk.co.whitbread.reservation.infrastructure.rest.client.rules;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.reservation.domain.model.out.BusinessAllowanceRuleResponse;
import uk.co.whitbread.reservation.domain.model.out.ChannelRuleResponse;
import uk.co.whitbread.reservation.domain.model.out.MaxNightsRuleResponse;
import uk.co.whitbread.reservation.domain.model.out.MaxRoomOccupancyResponse;
import uk.co.whitbread.reservation.domain.model.out.MaxRoomsRuleResponse;
import uk.co.whitbread.reservation.domain.model.out.RulesAmendmentResponse;
import uk.co.whitbread.reservation.domain.model.out.SingleOccupancySupplementResponse;
import uk.co.whitbread.reservation.domain.model.out.VatRuleResponse;
import uk.co.whitbread.reservation.domain.ports.secondary.RulesOutPort;
import uk.co.whitbread.reservation.infrastructure.rest.client.rules.mapper.BusinessAllowanceRuleResponseMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.rules.mapper.ChannelRuleResponseMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.rules.mapper.MaxNightsRuleMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.rules.mapper.MaxRoomRuleMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.rules.mapper.RulesAmendmentMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.rules.mapper.SingleOccupancySupplementResponseMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.rules.mapper.VatRuleResponseMapper;
import uk.co.whitbread.reservation.infrastructure.rest.client.rules.service.RulesAdapterClient;
import uk.co.whitbread.rules.entity.service.generated.models.agent.AmendmentRuleResponseDto;

@Slf4j
@RequiredArgsConstructor
public class RulesOutPortImpl implements RulesOutPort {

  private final RulesAdapterClient rulesAdapterClient;
  private final RulesAmendmentMapper rulesAmendmentMapper;
  private final MaxRoomRuleMapper maxRoomRuleMapper;
  private final MaxNightsRuleMapper maxNightsRuleMapper;
  private final ChannelRuleResponseMapper channelRuleResponseMapper;
  private final VatRuleResponseMapper vatRuleResponseMapper;
  private final BusinessAllowanceRuleResponseMapper businessAllowanceRuleResponseMapper;

  private final SingleOccupancySupplementResponseMapper singleOccupancySupplementResponseMapper;

  @Override
  public RulesAmendmentResponse isBookingAmendable(String rateType, String arrivalDate, String hotelLocalDateTime,
                                                   String hotelCountryCode) {
    AmendmentRuleResponseDto rulesAmendmentResponseDto = rulesAdapterClient
            .isBookingAmendable(rateType, arrivalDate, hotelLocalDateTime, hotelCountryCode);
    return rulesAmendmentMapper.toModel(rulesAmendmentResponseDto);
  }

  @Override
  public MaxRoomsRuleResponse getMaxRoomsRule(String channelId) {
    var maxRoomRuleResponse = rulesAdapterClient.getMaxRoomsRule(channelId);
    return maxRoomRuleMapper.toModel(maxRoomRuleResponse);
  }

  @Override
  public MaxNightsRuleResponse getMaxNightsRule(String channelId) {
    var maxNightsRuleResponse = rulesAdapterClient.getMaxNightsRule(channelId);
    return maxNightsRuleMapper.toModel(maxNightsRuleResponse);
  }

  @Override
  public MaxRoomOccupancyResponse getMaxRoomOccupancyRule(
      String channelId, String brand) {
    log.debug(
        "Entered getMaxRoomOccupancyRule with channelId={} and brand={}",
        channelId, brand);
    var maxRoomOccupancyRuleResponseDto = rulesAdapterClient.getMaxRoomOccupancyResponse(
        channelId, brand);
    return maxNightsRuleMapper.toModel(maxRoomOccupancyRuleResponseDto);
  }

  @Override
  public ChannelRuleResponse getChannelBasedOnSourceId(String sourceId) {
    log.debug("Entered getChannelBasedOnSourceId with sourceId={}", sourceId);
    var channelRuleResponseDto = rulesAdapterClient.getChannelBasedOnSourceId(sourceId);
    return channelRuleResponseMapper.toModel(channelRuleResponseDto);
  }

  @Override
  public VatRuleResponse getVatCodesForPackage(String vatRegion, String packageCode) {
    return vatRuleResponseMapper.toModel(rulesAdapterClient.getVatCodesForPackage(vatRegion, packageCode));
  }

  @Override
  public BusinessAllowanceRuleResponse getBusinessAllowanceRules() {
    log.info("Entered getBusinessAllowanceRules");
    var businessAllowanceRulesResponse = rulesAdapterClient.getBusinessAllowances();
    return businessAllowanceRuleResponseMapper.toModel(businessAllowanceRulesResponse);
  }

  @Override
  public SingleOccupancySupplementResponse getSingleOccupancySupplementResponse(String hotelId) {
    var singleOccupancySupplementResponseDto = rulesAdapterClient.getSingleOccupancySupplement(hotelId);
    return singleOccupancySupplementResponseMapper.toModel(singleOccupancySupplementResponseDto);
  }
}
