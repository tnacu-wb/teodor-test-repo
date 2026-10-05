package uk.co.whitbread.rules.agent.domain.logic;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.rules.agent.domain.model.out.RuleStatus.ACTIVE;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.rules.agent.domain.exception.ErrorCode;
import uk.co.whitbread.rules.agent.domain.exception.RuleEngineException;
import uk.co.whitbread.rules.agent.domain.model.in.RoomSubstitutionRuleRequest;
import uk.co.whitbread.rules.agent.domain.model.out.RoomSubstitutionRule;
import uk.co.whitbread.rules.agent.domain.ports.secondary.RoomSubstitutionRuleRepositoryOutPort;

@ExtendWith(MockitoExtension.class)
class RoomSubstitutionRuleInPortImplTest {

  private static final String OP = "OP";
  private static final String DOUBLE = "DOUBLE";

  @Mock
  private RoomSubstitutionRuleRepositoryOutPort roomSubstitutionRuleRepositoryOutPort;

  @InjectMocks
  private RoomSubstitutionRuleInPortImpl roomSubstitutionRuleInPort;

  @Test
  void getRoomSubstitutionRule__shouldThrowException() {
    //Arrange
    final String expectedMessage = "Room Substitution Rule not found.";
    when(roomSubstitutionRuleRepositoryOutPort.findRoomSubstitutionRules(
        createRoomSubstitutionRuleRequest())).thenThrow(
        new RuleEngineException(ErrorCode.DIGITAL_ROOM_SUBSTITUTION_RULE_EXCEPTION,
              "Room Substitution Rule not found."));
    var request = createRoomSubstitutionRuleRequest();

    //Act
    Exception exception = assertThrows(RuleEngineException.class,
        () -> roomSubstitutionRuleInPort.getRoomSubstitutionRule(request));

    //Assert
    assertThat(exception, notNullValue());
    assertThat(exception.getMessage(), is(expectedMessage));
    verifyNoMoreInteractions(roomSubstitutionRuleRepositoryOutPort);
  }

  @Test
  void getRoomSubstitutionRule__shouldReturnOk() {
    //Arrange
    var roomSubstitutionRuleRequest = createRoomSubstitutionRuleRequest();
    when(roomSubstitutionRuleRepositoryOutPort.findRoomSubstitutionRules(
        roomSubstitutionRuleRequest)).thenReturn(createRoomSubstitutionRule());

    //Act
    var response = roomSubstitutionRuleInPort.getRoomSubstitutionRule(roomSubstitutionRuleRequest);

    //Assert
    assertThat(response, notNullValue());
    assertThat(response.getSubstitutionList().get(0).getType(), is("DBLWIN"));
    assertThat(response.getSubstitutionList().get(0).getSilent(), is(true));
    verifyNoMoreInteractions(roomSubstitutionRuleRepositoryOutPort);
  }

  private List<RoomSubstitutionRule> createRoomSubstitutionRule() {
    var time = LocalDateTime.now();
    return List.of(
        RoomSubstitutionRule.builder().adults(2).children(0).createdAt(time).lastModifiedAt(time)
            .offerOrder(1).pms(OP).pmsRoomType("DBLWIN").roomType(DOUBLE).ruleId(1).status(ACTIVE)
            .build());
  }

  private RoomSubstitutionRuleRequest createRoomSubstitutionRuleRequest() {
    return RoomSubstitutionRuleRequest.builder().adults(2).children(1).pms(OP).roomType(DOUBLE)
        .channel("PI")
        .build();
  }
}
