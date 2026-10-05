package uk.co.whitbread.infrastructure.rest.client.rulesagent;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import uk.co.whitbread.domain.model.rulesagent.in.RoomSubstitutionRuleRequest;
import uk.co.whitbread.domain.model.rulesagent.out.MaxNightsRuleResponse;
import uk.co.whitbread.domain.model.rulesagent.out.MaxRoomOccupancyResponse;
import uk.co.whitbread.domain.model.rulesagent.out.MaxRoomsRuleResponse;
import uk.co.whitbread.domain.model.rulesagent.out.RateSuppressionRuleResponse;
import uk.co.whitbread.domain.model.rulesagent.out.RoomSubstitutionRuleResponse;
import uk.co.whitbread.domain.ports.secondary.RulesAgentOutPort;
import uk.co.whitbread.infrastructure.rest.client.RulesAgentClient;
import uk.co.whitbread.infrastructure.rest.client.rulesagent.mapper.MaxNightsRuleResponseMapper;
import uk.co.whitbread.infrastructure.rest.client.rulesagent.mapper.MaxRoomOccupancyRuleResponseMapper;
import uk.co.whitbread.infrastructure.rest.client.rulesagent.mapper.MaxRoomsRuleResponseMapper;
import uk.co.whitbread.infrastructure.rest.client.rulesagent.mapper.RateSuppressionRuleResponseMapper;
import uk.co.whitbread.infrastructure.rest.client.rulesagent.mapper.RoomSubstitutionRuleRequestMapper;
import uk.co.whitbread.infrastructure.rest.client.rulesagent.mapper.RoomSubstitutionRuleResponseMapper;
import uk.co.whitbread.rules.agent.generated.models.MultiOccupancySupplementRequestDto;
import uk.co.whitbread.rules.agent.generated.models.OccupancySupplementRequestDto;
import uk.co.whitbread.rules.agent.generated.models.RoomSubstitutionDto;
import uk.co.whitbread.rules.agent.generated.models.RoomSubstitutionRequestDetailsDto;
import uk.co.whitbread.rules.agent.generated.models.RoomSubstitutionRuleResponseDto;

@Slf4j
@RequiredArgsConstructor
@Component
public class RulesAgentOutPortImpl implements RulesAgentOutPort {

  private final RulesAgentClient rulesAgentClient;
  private final RoomSubstitutionRuleRequestMapper roomSubstitutionRuleRequestMapper;
  private final RoomSubstitutionRuleResponseMapper roomSubstitutionRuleResponseMapper;
  private final MaxNightsRuleResponseMapper maxNightsRuleResponseMapper;
  private final MaxRoomsRuleResponseMapper maxRoomsRuleResponseMapper;
  private final MaxRoomOccupancyRuleResponseMapper maxRoomOccupancyRuleResponseMapper;
  private final RateSuppressionRuleResponseMapper rateSuppressionRuleResponseMapper;

  private static final String RULES_AGENT_ERROR = "An error was returned by Rules Agent";
  public static final String SING_SPECIAL_REQUEST = "SING";

  @Override
  public MaxNightsRuleResponse getMaxNightsRule(String channelId) {
    log.debug(
        "Entered getMaxNightsRule with channelId={}",
        channelId);
    var maxNightsRuleResponseDto = rulesAgentClient.getMaxNightsRule(
        channelId);
    return maxNightsRuleResponseMapper.toModel(maxNightsRuleResponseDto);
  }

  @Override
  public MaxRoomsRuleResponse getMaxRoomsRule(String channelId) {
    log.debug(
        "Entered getMaxRoomsRule with channelId={}",
        channelId);
    var maxRoomsRuleResponseDto = rulesAgentClient.getMaxRoomsRule(
        channelId);
    return maxRoomsRuleResponseMapper.toModel(maxRoomsRuleResponseDto);
  }

  @Override
  public MaxRoomOccupancyResponse getMaxRoomOccupancyRule(
      String channelId) {
    log.debug(
        "Entered getMaxRoomOccupancyRule with channelId={}",
        channelId);
    var maxRoomOccupancyRuleResponseDto = rulesAgentClient.getMaxRoomOccupancyResponse(
        channelId);
    return maxRoomOccupancyRuleResponseMapper.toModel(maxRoomOccupancyRuleResponseDto);
  }

  @Override
  public RoomSubstitutionRuleResponse getRoomSubstitutionRule(
      RoomSubstitutionRuleRequest roomSubstitutionRuleRequest) {
    log.debug(
        "Entered getRoomSubstitutionRule with adults={}, children={}, roomType={}, pms={}, channel={}",
        roomSubstitutionRuleRequest.getAdults(), roomSubstitutionRuleRequest.getChildren(),
        roomSubstitutionRuleRequest.getRoomType(), roomSubstitutionRuleRequest.getPms(),
        roomSubstitutionRuleRequest.getChannel());
    var roomSubstitutionRuleRequestDto = roomSubstitutionRuleRequestMapper.toDto(
        roomSubstitutionRuleRequest);
    var roomSubstitutionRuleResponseDto = rulesAgentClient.getSubstitutionRoomRules(
        roomSubstitutionRuleRequestDto);
    return roomSubstitutionRuleResponseMapper.toModel(roomSubstitutionRuleResponseDto);
  }

  @Override
  public RateSuppressionRuleResponse getRateSuppressionRule() {
    log.debug("Entered getRateSuppressionRule");
    var rateSuppressionRuleResponseDto = rulesAgentClient.getRateSuppressions();
    return rateSuppressionRuleResponseMapper.toModel(rateSuppressionRuleResponseDto);

  }

  @Override
  public Map<String, BigDecimal> getMultiOccupancySupplementPricing(List<String> hotelIds) {
    log.debug("Entered getMultiOccupancySupplementPricing with hotelIds={}}", hotelIds);
    var request = new MultiOccupancySupplementRequestDto();
    var occSupplementRequestList = new ArrayList<OccupancySupplementRequestDto>();
    hotelIds.forEach(hotelId -> {
      var occSupplementRequest = new OccupancySupplementRequestDto();
      occSupplementRequest.setHotelId(hotelId);
      occSupplementRequestList.add(occSupplementRequest);
    });
    request.setHotelIds(occSupplementRequestList);
    return rulesAgentClient.getMultiOccupancySupplementPricing(request).getDictionary();
  }


  @Override
  public RoomSubstitutionRuleResponse createRoomSubstitutionRule(
      String roomType, Integer adults, Integer children, String pmsRoomType, String channelId) {
    RoomSubstitutionRequestDetailsDto requestDetails = new RoomSubstitutionRequestDetailsDto()
        .adults(adults)
        .children(children)
        .roomType(roomType)
        .pms(pmsRoomType)
        .channel(channelId);
    RoomSubstitutionDto roomSubstitution = new RoomSubstitutionDto()
        .type(pmsRoomType)
        .silent(Boolean.FALSE)
        .specialRequest(SING_SPECIAL_REQUEST);
    RoomSubstitutionRuleResponseDto roomSubstitutionRuleResponseDto = new RoomSubstitutionRuleResponseDto()
        .requestDetails(requestDetails)
        .generatedAt(new Date())
        .substitutionList(Arrays.asList(roomSubstitution));
    return roomSubstitutionRuleResponseMapper.toModel(roomSubstitutionRuleResponseDto);
  }
}
