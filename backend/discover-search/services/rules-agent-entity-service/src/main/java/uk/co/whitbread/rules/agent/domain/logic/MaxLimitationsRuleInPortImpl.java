package uk.co.whitbread.rules.agent.domain.logic;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.rules.agent.domain.exception.ErrorCode;
import uk.co.whitbread.rules.agent.domain.exception.RuleEngineException;
import uk.co.whitbread.rules.agent.domain.model.in.MaxArrivalDateRuleRequest;
import uk.co.whitbread.rules.agent.domain.model.in.MaxNightsRuleRequest;
import uk.co.whitbread.rules.agent.domain.model.in.MaxRoomOccupancyRequest;
import uk.co.whitbread.rules.agent.domain.model.in.MaxRoomsRuleRequest;
import uk.co.whitbread.rules.agent.domain.model.out.MaxArrivalDateRequestDetails;
import uk.co.whitbread.rules.agent.domain.model.out.MaxArrivalDateRuleResponse;
import uk.co.whitbread.rules.agent.domain.model.out.MaxNightsRequestDetails;
import uk.co.whitbread.rules.agent.domain.model.out.MaxNightsRuleResponse;
import uk.co.whitbread.rules.agent.domain.model.out.MaxRoomOccuRuleResp;
import uk.co.whitbread.rules.agent.domain.model.out.MaxRoomOccupancyData;
import uk.co.whitbread.rules.agent.domain.model.out.MaxRoomOccupancyRule;
import uk.co.whitbread.rules.agent.domain.model.out.MaxRoomsRequestDetails;
import uk.co.whitbread.rules.agent.domain.model.out.MaxRoomsRuleResponse;
import uk.co.whitbread.rules.agent.domain.ports.primary.MaxLimitationsRuleInPort;
import uk.co.whitbread.rules.agent.domain.ports.secondary.MaxArrivalDateRuleRepositoryOutPort;
import uk.co.whitbread.rules.agent.domain.ports.secondary.MaxNightsRuleRepositoryOutPort;
import uk.co.whitbread.rules.agent.domain.ports.secondary.MaxRoomsRuleRepositoryOutPort;
import uk.co.whitbread.rules.agent.domain.ports.secondary.RoomOccRuleRepoOutPort;

@Slf4j
@RequiredArgsConstructor
public class MaxLimitationsRuleInPortImpl implements MaxLimitationsRuleInPort {

  private final MaxNightsRuleRepositoryOutPort maxNightsRuleRepositoryOutPort;
  private final MaxRoomsRuleRepositoryOutPort maxRoomsRuleRepositoryOutPort;
  private final RoomOccRuleRepoOutPort roomOccupancyRuleRepoOutPort;
  private final MaxArrivalDateRuleRepositoryOutPort maxArrivalDateRuleRepositoryOutPort;

  @Override
  public MaxNightsRuleResponse getMaxNightsRule(MaxNightsRuleRequest maxNightsRuleRequest) {
    return MaxNightsRuleResponse.builder()
        .generatedAt(LocalDateTime.now())
        .maxNights(maxNights(maxNightsRuleRequest))
        .requestDetails(MaxNightsRequestDetails.builder()
            .channelId(maxNightsRuleRequest.getChannelId())
            .build())
        .build();
  }

  @Override
  public MaxRoomsRuleResponse getMaxRoomsRule(MaxRoomsRuleRequest maxRoomsRuleRequest) {
    return MaxRoomsRuleResponse.builder()
        .generatedAt(LocalDateTime.now())
        .maxRooms(maxRooms(maxRoomsRuleRequest))
        .requestDetails(MaxRoomsRequestDetails.builder()
            .channelId(maxRoomsRuleRequest.getChannelId())
            .build())
        .build();
  }

  @Override
  public MaxRoomOccuRuleResp getMaxRoomOccupancyRule(
      MaxRoomOccupancyRequest maxRoomOccupancyRequest) {
    var roomOccupancyRules = getMaxRoomOccupancyRules(maxRoomOccupancyRequest);
    var maxOccupancyData = mapRulesToData(roomOccupancyRules);
    return MaxRoomOccuRuleResp.builder()
        .channelId(maxRoomOccupancyRequest.getChannelId())
        .brand(maxRoomOccupancyRequest.getBrand())
        .maxOccupancyData(maxOccupancyData)
        .generatedAt(LocalDateTime.now())
        .build();
  }

  @Override
  public MaxArrivalDateRuleResponse getMaxArrivalDateRule(
      MaxArrivalDateRuleRequest maxArrivalDateRuleRequest) {
    return MaxArrivalDateRuleResponse.builder()
        .generatedAt(LocalDateTime.now())
        .maxArrivalDate(maxArrivalDate(maxArrivalDateRuleRequest))
        .requestDetails(MaxArrivalDateRequestDetails.builder()
            .channelId(maxArrivalDateRuleRequest.getChannelId())
            .build())
        .build();
  }

  private List<MaxRoomOccupancyData> mapRulesToData(List<MaxRoomOccupancyRule> roomOccupancyRules) {
    return roomOccupancyRules.stream().map(rule -> {
      List<String> acceptedRooms = new ArrayList<>();
      if (rule.getSingleRoom().booleanValue()) {
        acceptedRooms.add("SB");
      }
      if (rule.getDoubleRoom().booleanValue()) {
        acceptedRooms.add("DB");
      }
      if (rule.getTwinRoom().booleanValue()) {
        acceptedRooms.add("TWIN");
      }
      if (rule.getAccessibleRoom().booleanValue()) {
        acceptedRooms.add("DIS");
      }
      if (rule.getFamilyRoom().booleanValue()) {
        acceptedRooms.add("FAM");
      }
      return MaxRoomOccupancyData.builder()
          .adultsNumber(rule.getAdults())
          .childrenNumber(rule.getChildren())
          .acceptedRoomTypes(acceptedRooms)
          .build();
    }).toList();
  }

  private List<MaxRoomOccupancyRule> getMaxRoomOccupancyRules(
      MaxRoomOccupancyRequest maxRoomOccupancyRequest) {
    var maxRoomOccupancyRules = roomOccupancyRuleRepoOutPort.findRules(
        maxRoomOccupancyRequest.getChannelId(), maxRoomOccupancyRequest.getBrand());
    if (maxRoomOccupancyRules == null || maxRoomOccupancyRules.isEmpty()) {
      var message = "Error while getting room occupancy rule!";
      var exception = new RuleEngineException(ErrorCode.DIGITAL_MAX_ROOM_OCCUPANCY_RULE_EXCEPTION,
            message);
      ExceptionLogger.log(log, exception);
      throw exception;
    }
    return maxRoomOccupancyRules;
  }

  private Integer maxNights(MaxNightsRuleRequest maxNightsRuleRequest) {
    var maxNightsRule = maxNightsRuleRepositoryOutPort.findRule(
        maxNightsRuleRequest.getChannelId());
    if (maxNightsRule.isEmpty()) {
      var message = "Error while trying to get max nights rule";
      var exception = new RuleEngineException(ErrorCode.DIGITAL_MAX_NIGHT_RULE_EXCEPTION, message);
      ExceptionLogger.log(log, exception);
      throw exception;
    }
    return maxNightsRule.get().getMaxNights();
  }

  private Integer maxRooms(MaxRoomsRuleRequest maxRoomsRuleRequest) {
    var maxRoomsRule = maxRoomsRuleRepositoryOutPort.findRule(
        maxRoomsRuleRequest.getChannelId());
    if (maxRoomsRule.isEmpty()) {
      var message = "Error while trying to get max rooms rule";
      var exception = new RuleEngineException(ErrorCode.DIGITAL_MAX_ROOM_RULE_EXCEPTION, message);
      ExceptionLogger.log(log, exception);
      throw exception;
    }
    return maxRoomsRule.get().getMaxRooms();
  }

  private Integer maxArrivalDate(MaxArrivalDateRuleRequest maxArrivalDateRuleRequest) {
    var maxArrivalDateRule = maxArrivalDateRuleRepositoryOutPort.findRule(
        maxArrivalDateRuleRequest.getChannelId());
    if (maxArrivalDateRule.isEmpty()) {
      var message = "Error while trying to get max arrival date rule!";
      var exception = new RuleEngineException(ErrorCode.DIGITAL_MAX_ARRIVAL_DATE_RULE_EXCEPTION,
            message);
      ExceptionLogger.log(log, exception);
      throw exception;
    }
    return maxArrivalDateRule.get().getMaxArrivalDate();
  }
}
