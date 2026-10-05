package uk.co.whitbread.rules.agent.infrastructure.rest.controller;

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
import uk.co.whitbread.rules.agent.domain.model.out.RoomSubstitution;
import uk.co.whitbread.rules.agent.domain.model.out.RoomSubstitutionRequestDetails;
import uk.co.whitbread.rules.agent.domain.model.out.RoomSubstitutionRuleResponse;
import uk.co.whitbread.rules.agent.domain.ports.primary.RoomSubstitutionRuleInPort;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.mapper.RoomSubstitutionRuleDtoMapper;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.in.RoomSubstitutionRuleRequestDto;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.out.RoomSubstitutionDto;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.out.RoomSubstitutionRequestDetailsDto;
import uk.co.whitbread.rules.agent.infrastructure.rest.controller.model.out.RoomSubstitutionRuleResponseDto;

@ExtendWith(MockitoExtension.class)
class RoomSubstitutionRuleControllerIT {

  private static final String OP = "OP";
  private static final String CHANNEL = "PI";
  private static final String DOUBLE = "DOUBLE";
  private static final String DBLWIN = "DBLWIN";

  @InjectMocks
  private RoomSubstitutionRuleController roomSubstitutionRuleController;

  @Mock
  private RoomSubstitutionRuleInPort roomSubstitutionRuleInPort;

  @Mock
  private RoomSubstitutionRuleDtoMapper roomSubstitutionRuleDtoMapper;

  @Test
  void shouldRetrieveRoomSubstitutionRule() {
    //Arrange
    when(roomSubstitutionRuleInPort.getRoomSubstitutionRule(any())).thenReturn(mockRoomSubstitutionRuleResponse());
    when(roomSubstitutionRuleDtoMapper.toDto(any())).thenReturn(mockRoomSubstitutionRuleResponseDto());

    //Act
    var response =
        roomSubstitutionRuleController.getRoomSubstitutionRule(getRoomSubstitutionRuleRequestDto());

    //Assert
    Assertions.assertNotNull(response);
    Assertions.assertEquals("DBLWIN", response.getSubstitutionList().get(0).getType());
    Assertions.assertEquals(DOUBLE, response.getRequestDetails().getRoomType());
  }

  @Test
  void shouldHandleRoomSubstitutionRuleNotFound() {

    when(roomSubstitutionRuleDtoMapper.toModel(any())).thenReturn(null);
    when(roomSubstitutionRuleInPort.getRoomSubstitutionRule(any()))
        .thenThrow(new RuleEngineException(ErrorCode.DIGITAL_ROOM_SUBSTITUTION_RULE_EXCEPTION,
              "Room Substitution Rule not found."));

    //Act
    var thrownException = assertThrowsExactly(RuleEngineException.class,
        () -> roomSubstitutionRuleController.getRoomSubstitutionRule(getRoomSubstitutionRuleRequestDto()));

    //Assert
    String debugMessage = thrownException.getMessage();
    Assertions.assertEquals("Room Substitution Rule not found.", debugMessage);
  }

  private RoomSubstitutionRuleRequestDto getRoomSubstitutionRuleRequestDto() {
    return RoomSubstitutionRuleRequestDto.builder()
        .adults(2)
        .children(0)
        .pms(OP)
        .roomType(DOUBLE)
        .channel(CHANNEL)
        .build();
  }

  private RoomSubstitutionRuleResponse mockRoomSubstitutionRuleResponse() {
    return RoomSubstitutionRuleResponse.builder()
        .generatedAt(LocalDateTime.parse("2022-07-01T08:20:02.220734582"))
        .requestDetails(RoomSubstitutionRequestDetails.builder()
            .adults(2)
            .children(0)
            .pms(OP)
            .roomType(DOUBLE)
            .channel(CHANNEL)
            .build())
        .substitutionList(of(RoomSubstitution.builder()
            .silent(true)
            .type(DBLWIN)
            .build()))
        .build();
  }

  private RoomSubstitutionRuleResponseDto mockRoomSubstitutionRuleResponseDto() {
    return RoomSubstitutionRuleResponseDto.builder()
        .generatedAt(LocalDateTime.parse("2022-07-01T08:20:02.220734582"))
        .requestDetails(RoomSubstitutionRequestDetailsDto.builder()
            .adults(2)
            .children(0)
            .pms(OP)
            .roomType(DOUBLE)
            .channel(CHANNEL)
            .build())
        .substitutionList(of(RoomSubstitutionDto.builder()
            .silent(true)
            .type(DBLWIN)
            .build()))
        .build();
  }
}
