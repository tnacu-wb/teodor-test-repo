package uk.co.whitbread.availabilitycacheservice.infrastructure.rest.client.rulesagent;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static wiremock.org.hamcrest.MatcherAssert.assertThat;
import static wiremock.org.hamcrest.Matchers.notNullValue;

import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.availabilitycacheservice.domain.model.rulesagent.in.RoomSubstitutionRuleRequest;
import uk.co.whitbread.availabilitycacheservice.domain.model.rulesagent.out.RoomSubstitution;
import uk.co.whitbread.availabilitycacheservice.domain.model.rulesagent.out.RoomSubstitutionRequestDetails;
import uk.co.whitbread.availabilitycacheservice.domain.model.rulesagent.out.RoomSubstitutionRuleResponse;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.client.RulesAgentClient;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.client.rulesagent.exceptions.RulesAgentException;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.client.rulesagent.mapper.RoomSubstitutionRuleRequestMapper;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.client.rulesagent.mapper.RoomSubstitutionRuleResponseMapper;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.client.rulesagent.model.in.RoomSubstitutionRuleRequestDto;
import uk.co.whitbread.rules.agent.generated.models.RoomSubstitutionDto;
import uk.co.whitbread.rules.agent.generated.models.RoomSubstitutionRequestDetailsDto;
import uk.co.whitbread.rules.agent.generated.models.RoomSubstitutionRuleResponseDto;

@ExtendWith(MockitoExtension.class)
class RulesAgentOutPortImplTest {

  private static final String RULES_AGENT_ERROR = "An error was returned by Rules Agent";
  @InjectMocks
  private RulesAgentOutPortImpl rulesAgentOutPort;
  @Mock
  private RulesAgentClient rulesAgentClient;
  @Mock
  private RoomSubstitutionRuleRequestMapper roomSubstitutionRuleRequestMapper;
  @Mock
  private RoomSubstitutionRuleResponseMapper roomSubstitutionRuleResponseMapper;

  @Test
  void getRoomSubstitutionRule() {
    //Arrange
    when(roomSubstitutionRuleRequestMapper.toDto(any(RoomSubstitutionRuleRequest.class)))
        .thenReturn(createRoomSubstitutionRuleRequestDto());
    when(rulesAgentClient.getSubstitutionRoomRules(any(RoomSubstitutionRuleRequestDto.class)))
        .thenReturn(createRoomSubstitutionRuleResponseDto());
    when(roomSubstitutionRuleResponseMapper.toModel(any(RoomSubstitutionRuleResponseDto.class)))
        .thenReturn(createRoomSubstitutionRuleResponse());

    //Act
    var roomSubstitutionRuleResponse = rulesAgentOutPort.getRoomSubstitutionRule(
        createRoomSubstitutionRuleRequest());

    //Assert
    assertThat(roomSubstitutionRuleResponse, notNullValue());
  }

  @Test
  void getRoomSubstitutionRule__ShouldThrowException() {
    //Arrange
    when(roomSubstitutionRuleRequestMapper.toDto(any(RoomSubstitutionRuleRequest.class)))
        .thenReturn(createRoomSubstitutionRuleRequestDto());
    when(rulesAgentClient.getSubstitutionRoomRules(any(RoomSubstitutionRuleRequestDto.class)))
        .thenThrow(new RulesAgentException("message", RULES_AGENT_ERROR, new Exception(), 1));

    RoomSubstitutionRuleRequest roomSubstitutionRuleRequest = createRoomSubstitutionRuleRequest();

    //Act
    RulesAgentException exception = Assertions.assertThrows(RulesAgentException.class,
        () -> rulesAgentOutPort.getRoomSubstitutionRule(
            roomSubstitutionRuleRequest));

    // Assert
    Assertions.assertEquals(RULES_AGENT_ERROR, exception.getMessage());

  }

  private RoomSubstitutionRuleRequest createRoomSubstitutionRuleRequest() {
    return RoomSubstitutionRuleRequest
        .builder()
        .adults(1)
        .children(0)
        .roomType("DB")
        .pms("OP")
        .channel("PI")
        .build();
  }

  private RoomSubstitutionRuleRequestDto createRoomSubstitutionRuleRequestDto() {
    return RoomSubstitutionRuleRequestDto.builder()
        .adults(1)
        .children(0)
        .roomType("DB")
        .pms("OP")
        .channel("PI")
        .build();
  }

  private RoomSubstitutionRuleResponse createRoomSubstitutionRuleResponse() {
    return RoomSubstitutionRuleResponse.builder()
        .substitutionList(List.of(RoomSubstitution.builder().type("DBLWIN").build()))
        .requestDetails(RoomSubstitutionRequestDetails.builder()
            .adults(1)
            .children(0)
            .roomType("DB")
            .pms("OP")
            .channel("PI")
            .build())
        .build();
  }

  private RoomSubstitutionRuleResponseDto createRoomSubstitutionRuleResponseDto() {
    var requestDetails = new RoomSubstitutionRequestDetailsDto();
    requestDetails.setAdults(1);
    requestDetails.setChildren(0);
    requestDetails.setRoomType("DB");
    requestDetails.setPms("OP");
    requestDetails.setChannel("PI");

    var substitutionRoom = new RoomSubstitutionDto();
    substitutionRoom.setType("DBLWIN");

    var response = new RoomSubstitutionRuleResponseDto();
    response.setSubstitutionList(List.of(substitutionRoom));
    response.setRequestDetails(requestDetails);
    return response;
  }
}
