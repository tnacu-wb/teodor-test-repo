package uk.co.whitbread.rules.agent.infrastructure.rest.controller;

import static java.util.Arrays.asList;
import static java.util.List.of;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
import uk.co.whitbread.rules.agent.domain.model.out.MaxRoomsRequestDetails;
import uk.co.whitbread.rules.agent.domain.model.out.MaxRoomsRuleResponse;
import uk.co.whitbread.rules.agent.domain.ports.primary.MaxLimitationsRuleInPort;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.mapper.MaxArrivalDateRuleDtoMapper;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.mapper.MaxNightsRuleDtoMapper;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.mapper.MaxRoomOccupancyDtoMapper;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.mapper.MaxRoomsRuleDtoMapper;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.in.MaxArrivalDateRuleRequestDto;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.in.MaxNightsRuleRequestDto;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.in.MaxRoomOccupancyRequestDto;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.in.MaxRoomsRuleRequestDto;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.out.MaxArrivalDateRequestDetailsDto;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.out.MaxArrivalDateRuleResponseDto;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.out.MaxNightsRequestDetailsDto;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.out.MaxNightsRuleResponseDto;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.out.MaxRoomOccupancyDataDto;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.out.MaxRoomOccupancyResponseDto;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.out.MaxRoomsRequestDetailsDto;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.out.MaxRoomsRuleResponseDto;

@ExtendWith(MockitoExtension.class)
class MaxLimitationsRulesControllerIT {

  @InjectMocks
  private MaxLimitationsRulesController maxLimitationsRulesController;

  @Mock
  private MaxNightsRuleDtoMapper maxNightsRuleDtoMapper;

  @Mock
  private MaxRoomsRuleDtoMapper maxRoomsRuleDtoMapper;

  @Mock
  private MaxLimitationsRuleInPort maxLimitationsRuleInPort;

  @Mock
  private MaxRoomOccupancyDtoMapper maxRoomOccupancyDtoMapper;

  @Mock
  private MaxArrivalDateRuleDtoMapper maxArrivalDateRuleDtoMapper;

  @Test
  void shouldRetrieveMaxNightsRule() {
    //Arrange
    when(maxNightsRuleDtoMapper.toModel(any())).thenReturn(getMaxNightsRuleRequest());
    when(maxLimitationsRuleInPort.getMaxNightsRule(any())).thenReturn(mockMaxNightsRuleResponse());
    when(maxNightsRuleDtoMapper.toDto(any())).thenReturn(mockMaxNightsRuleResponseDto());

    //Act
    var response = maxLimitationsRulesController.getMaxNightsRule(getMaxNightsRuleRequestDto());

    //Assert
    Assertions.assertNotNull(response);
    Assertions.assertEquals(364, response.getMaxNights());
    Assertions.assertEquals("CCUI", response.getRequestDetails().getChannelId());

  }

  @Test
  void shouldHandleMaxNightsRuleNotFound() {
    //Arrange
    when(maxNightsRuleDtoMapper.toModel(any())).thenReturn(null);
    when(maxLimitationsRuleInPort.getMaxNightsRule(any())).thenThrow(
        new RuleEngineException(ErrorCode.DIGITAL_MAX_NIGHT_RULE_EXCEPTION,
              "MaxNightsRule not found"));

    //Act
    var thrownException = assertThrowsExactly(RuleEngineException.class,
        () -> maxLimitationsRulesController.getMaxNightsRule(getMaxNightsRuleRequestDto()));

    //Assert
    String debugMessage = thrownException.getMessage();
    Assertions.assertEquals("MaxNightsRule not found", debugMessage);
  }

  @Test
  void shouldRetrieveMaxRoomsRule() {

    when(maxRoomsRuleDtoMapper.toModel(any())).thenReturn(getMaxRoomsRuleRequest());
    when(maxLimitationsRuleInPort.getMaxRoomsRule(any())).thenReturn(mockMaxRoomsRuleResponse());
    when(maxRoomsRuleDtoMapper.toDto(any())).thenReturn(mockMaxRoomsRuleResponseDto());

    //Act
    var response = maxLimitationsRulesController.getMaxRoomsRule(getMaxRoomsRuleRequestDto());

    //Assert
    Assertions.assertNotNull(response);
    Assertions.assertEquals(4, response.getMaxRooms());
    Assertions.assertEquals("PI", response.getRequestDetails().getChannelId());
  }

  @Test
  void shouldHandleMaxRoomsRuleNotFound() {
    //Arrange
    when(maxRoomsRuleDtoMapper.toModel(any())).thenReturn(null);
    when(maxLimitationsRuleInPort.getMaxRoomsRule(any())).thenThrow(
        new RuleEngineException(ErrorCode.DIGITAL_MAX_ROOM_RULE_EXCEPTION,
              "MaxRoomsRule not found"));

    //Act
    var thrownException = assertThrowsExactly(RuleEngineException.class,
        () -> maxLimitationsRulesController.getMaxRoomsRule(getMaxRoomsRuleRequestDto()));

    //Assert
    String debugMessage = thrownException.getMessage();
    Assertions.assertEquals("MaxRoomsRule not found", debugMessage);
  }

  @Test
  void shouldRetrieveMaxRoomOccupancyRule() {
    //Arrange
    when(maxRoomOccupancyDtoMapper.toModel(any())).thenReturn(getMaxRoomOccupancyRequest());
    when(maxLimitationsRuleInPort.getMaxRoomOccupancyRule(any())).thenReturn(mockMaxRoomOccuRuleResp());
    when(maxRoomOccupancyDtoMapper.toDto(any())).thenReturn(mockMaxRoomOccupancyResponseDto());

    //Act
    var response =
        maxLimitationsRulesController.getMaxRoomOccupancyRule(getMaxRoomOccupancyRequestDto());

    //Assert
    Assertions.assertNotNull(response);
    Assertions.assertEquals(6, response.getRoomOccupancies().size());
    Assertions.assertEquals(3, response.getRoomOccupancies().get(5).getAcceptedRoomTypes().size());
    Assertions.assertEquals("FAM", response.getRoomOccupancies().get(2).getAcceptedRoomTypes().get(0));
    Assertions.assertEquals(2, response.getRoomOccupancies().get(1).getAdultsNumber());
    Assertions.assertEquals(1, response.getRoomOccupancies().get(2).getChildrenNumber());
  }

  @Test
  void shouldHandleMaxRoomOccupancyRuleNotFound() {
    //Arrange
    when(maxRoomOccupancyDtoMapper.toModel(any())).thenReturn(null);
    when(maxLimitationsRuleInPort.getMaxRoomOccupancyRule(any())).thenThrow(
        new RuleEngineException(ErrorCode.DIGITAL_MAX_ROOM_OCCUPANCY_RULE_EXCEPTION,
              "MaxRoomOccupancyRules not found"));

    //Act
    var thrownException = assertThrowsExactly(RuleEngineException.class,
        () -> maxLimitationsRulesController.getMaxRoomOccupancyRule(getMaxRoomOccupancyRequestDto()));

    //Assert
    String debugMessage = thrownException.getMessage();
    Assertions.assertEquals("MaxRoomOccupancyRules not found", debugMessage);
  }

  @Test
  void shouldRetrieveMaxArrivalDateRule() {
    //Arrange
    var request = MaxArrivalDateRuleRequest.builder()
        .channelId("CCUI")
        .build();

    when(maxArrivalDateRuleDtoMapper.toModel(any())).thenReturn(request);
    when(maxLimitationsRuleInPort.getMaxArrivalDateRule(any())).thenReturn(mockMaxArrivalDateRuleResponse());
    when(maxArrivalDateRuleDtoMapper.toDto(any())).thenReturn(mockMaxArrivalDateRuleResponseDto());

    //Act
    var response =
        maxLimitationsRulesController.getMaxArrivalDateRule(getMaxArrivalDateRuleRequestDto());

    //Assert
    Assertions.assertNotNull(response);
    Assertions.assertEquals(364, response.getMaxArrivalDate());
  }

  @Test
  void shouldHandleMaxArrivalDateRuleNotFound() {

    when(maxLimitationsRuleInPort.getMaxArrivalDateRule(any())).thenThrow(
        new RuleEngineException(ErrorCode.DIGITAL_MAX_ARRIVAL_DATE_RULE_EXCEPTION,
              "MaxArrivalDateRules not found"));

    //Act
    var thrownException = assertThrowsExactly(RuleEngineException.class,
        () -> maxLimitationsRulesController.getMaxArrivalDateRule(getMaxArrivalDateRuleRequestDto()));

    //Assert
    String debugMessage = thrownException.getMessage();
    Assertions.assertEquals("MaxArrivalDateRules not found", debugMessage);
  }

  private MaxNightsRuleRequestDto getMaxNightsRuleRequestDto(){
    return MaxNightsRuleRequestDto.builder()
        .channelId("CCUI")
        .build();
  }
  private MaxNightsRuleRequest getMaxNightsRuleRequest(){
    return MaxNightsRuleRequest.builder()
        .channelId("CCUI")
        .build();
  }

  private MaxNightsRuleResponseDto mockMaxNightsRuleResponseDto(){
    return MaxNightsRuleResponseDto.builder()
        .maxNights(364)
        .requestDetails(MaxNightsRequestDetailsDto.builder()
            .channelId("CCUI")
            .build())
        .generatedAt(LocalDateTime.parse("2022-07-01T08:20:02.220734582"))
        .build();
  }
  private MaxNightsRuleResponse mockMaxNightsRuleResponse(){
    return MaxNightsRuleResponse.builder()
        .maxNights(364)
        .requestDetails(MaxNightsRequestDetails.builder()
            .channelId("CCUI")
            .build())
        .generatedAt(LocalDateTime.parse("2022-07-01T08:20:02.220734582"))
        .build();
  }

  private MaxRoomsRuleRequest getMaxRoomsRuleRequest() {
    return MaxRoomsRuleRequest.builder()
        .channelId("PI")
        .build();
  }
  private MaxRoomsRuleRequestDto getMaxRoomsRuleRequestDto() {
    return MaxRoomsRuleRequestDto.builder()
        .channelId("PI")
        .build();
  }
  private MaxRoomsRuleResponse mockMaxRoomsRuleResponse() {
    return MaxRoomsRuleResponse.builder()
        .maxRooms(4)
        .requestDetails(MaxRoomsRequestDetails.builder()
            .channelId("PI")
            .build())
        .generatedAt(LocalDateTime.parse("2022-07-01T08:20:02.220734582"))
        .build();
  }
  private MaxRoomsRuleResponseDto mockMaxRoomsRuleResponseDto() {
    return MaxRoomsRuleResponseDto.builder()
        .maxRooms(4)
        .requestDetails(MaxRoomsRequestDetailsDto.builder()
            .channelId("PI")
            .build())
        .generatedAt(LocalDateTime.parse("2022-07-01T08:20:02.220734582"))
        .build();
  }
  private MaxRoomOccupancyRequest getMaxRoomOccupancyRequest() {
    return MaxRoomOccupancyRequest.builder()
        .channelId("PI")
        .brand("PI")
        .build();
  }
  private MaxRoomOccupancyRequestDto getMaxRoomOccupancyRequestDto() {
    return MaxRoomOccupancyRequestDto.builder()
        .channelId("PI")
        .brand("PI")
        .build();
  }

  private MaxRoomOccuRuleResp mockMaxRoomOccuRuleResp() {
    return MaxRoomOccuRuleResp.builder()
        .maxOccupancyData(asList(MaxRoomOccupancyData.builder()
                .adultsNumber(2)
                .childrenNumber(0)
                .acceptedRoomTypes(of("DB", "TWIN", "DIS"))
                .build(),
            MaxRoomOccupancyData.builder()
                .adultsNumber(2)
                .childrenNumber(2)
                .acceptedRoomTypes(of("FAM"))
                .build(),
            MaxRoomOccupancyData.builder()
                .adultsNumber(2)
                .childrenNumber(1)
                .acceptedRoomTypes(of("FAM"))
                .build(),
            MaxRoomOccupancyData.builder()
                .adultsNumber(1)
                .childrenNumber(2)
                .acceptedRoomTypes(of("FAM"))
                .build(),
            MaxRoomOccupancyData.builder()
                .adultsNumber(1)
                .childrenNumber(1)
                .acceptedRoomTypes(of("FAM"))
                .build(),
            MaxRoomOccupancyData.builder()
                .adultsNumber(1)
                .childrenNumber(0)
                .acceptedRoomTypes(of("SB", "DB", "DIS"))
                .build()))
        .channelId("PI")
        .brand("PI")
        .generatedAt(LocalDateTime.parse("2022-07-01T08:20:02.220734582"))
        .build();
  }

  private MaxRoomOccupancyResponseDto mockMaxRoomOccupancyResponseDto() {
    return MaxRoomOccupancyResponseDto.builder()
        .roomOccupancies(asList(MaxRoomOccupancyDataDto.builder()
                .adultsNumber(2)
                .childrenNumber(0)
                .acceptedRoomTypes(of("DB", "TWIN", "DIS"))
                .build(),
            MaxRoomOccupancyDataDto.builder()
                .adultsNumber(2)
                .childrenNumber(2)
                .acceptedRoomTypes(of("FAM"))
                .build(),
            MaxRoomOccupancyDataDto.builder()
                .adultsNumber(2)
                .childrenNumber(1)
                .acceptedRoomTypes(of("FAM"))
                .build(),
            MaxRoomOccupancyDataDto.builder()
                .adultsNumber(1)
                .childrenNumber(2)
                .acceptedRoomTypes(of("FAM"))
                .build(),
            MaxRoomOccupancyDataDto.builder()
                .adultsNumber(1)
                .childrenNumber(1)
                .acceptedRoomTypes(of("FAM"))
                .build(),
            MaxRoomOccupancyDataDto.builder()
                .adultsNumber(1)
                .childrenNumber(0)
                .acceptedRoomTypes(of("SB", "DB", "DIS"))
                .build()))
        .channelId("PI")
        .generatedAt(LocalDateTime.parse("2022-07-01T08:20:02.220734582"))
        .build();
  }

  private MaxArrivalDateRuleRequestDto getMaxArrivalDateRuleRequestDto() {
    return MaxArrivalDateRuleRequestDto.builder()
        .channelId("CCUI")
        .build();
  }

  private MaxArrivalDateRuleResponse mockMaxArrivalDateRuleResponse() {
    return MaxArrivalDateRuleResponse.builder()
        .maxArrivalDate(364)
        .requestDetails(MaxArrivalDateRequestDetails.builder()
            .channelId("CCUI")
            .build())
        .generatedAt(LocalDateTime.parse("2022-07-01T08:20:02.220734582"))
        .build();
  }
  private MaxArrivalDateRuleResponseDto mockMaxArrivalDateRuleResponseDto() {
    return MaxArrivalDateRuleResponseDto.builder()
        .maxArrivalDate(364)
        .requestDetails(MaxArrivalDateRequestDetailsDto.builder()
            .channelId("CCUI")
            .build())
        .generatedAt(LocalDateTime.parse("2022-07-01T08:20:02.220734582"))
        .build();
  }
}
