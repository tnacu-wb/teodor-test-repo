package uk.co.whitbread.rules.agent.domain.logic;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.rules.agent.domain.exception.RuleEngineException;
import uk.co.whitbread.rules.agent.domain.model.in.MaxArrivalDateRuleRequest;
import uk.co.whitbread.rules.agent.domain.model.in.MaxNightsRuleRequest;
import uk.co.whitbread.rules.agent.domain.model.in.MaxRoomOccupancyRequest;
import uk.co.whitbread.rules.agent.domain.model.in.MaxRoomsRuleRequest;
import uk.co.whitbread.rules.agent.domain.model.out.MaxArrivalDateRule;
import uk.co.whitbread.rules.agent.domain.model.out.MaxNightsRule;
import uk.co.whitbread.rules.agent.domain.model.out.MaxRoomOccupancyRule;
import uk.co.whitbread.rules.agent.domain.model.out.MaxRoomsRule;
import uk.co.whitbread.rules.agent.domain.model.out.RuleStatus;
import uk.co.whitbread.rules.agent.domain.ports.secondary.MaxArrivalDateRuleRepositoryOutPort;
import uk.co.whitbread.rules.agent.domain.ports.secondary.MaxNightsRuleRepositoryOutPort;
import uk.co.whitbread.rules.agent.domain.ports.secondary.MaxRoomsRuleRepositoryOutPort;
import uk.co.whitbread.rules.agent.domain.ports.secondary.RoomOccRuleRepoOutPort;

@ExtendWith(MockitoExtension.class)
class MaxLimitationsRuleInPortImplTest {

  @Mock
  private MaxNightsRuleRepositoryOutPort maxNightsRepository;
  @Mock
  private MaxRoomsRuleRepositoryOutPort maxRoomsRepository;
  @Mock
  private RoomOccRuleRepoOutPort roomOccupancyRuleRepoOutPort;
  @Mock
  private MaxArrivalDateRuleRepositoryOutPort maxArrivalDateRuleRepositoryOutPort;
  @InjectMocks
  private MaxLimitationsRuleInPortImpl maxLimitationsRuleInPort;

  @Test
  void getMaxNightsRule__shouldThrowException() {
    //Arrange
    final String expectedMessage = "Error while trying to get max nights rule";
    when(maxNightsRepository.findRule("CCUI")).thenReturn(
        Optional.empty());
    var request = createMaxNightsRuleRequest();

    //Act
    Exception exception = assertThrows(RuleEngineException.class,
        () -> maxLimitationsRuleInPort.getMaxNightsRule(request));

    //Assert
    assertThat(exception, notNullValue());
    assertThat(exception.getMessage(), is(expectedMessage));
    verifyNoMoreInteractions(maxNightsRepository);
  }

  @Test
  void getMaxNightsRule__shouldReturnOk() {
    //Arrange
    var maxNightsRuleRequest = createMaxNightsRuleRequest();
    when(maxNightsRepository.findRule("CCUI")).thenReturn(createMaxNightsRule());

    //Act
    var response = maxLimitationsRuleInPort.getMaxNightsRule(maxNightsRuleRequest);

    //Assert
    assertThat(response, notNullValue());
    AssertionsForClassTypes.assertThat(response.getRequestDetails().getChannelId()).isEqualTo(
        maxNightsRuleRequest.getChannelId());
    AssertionsForClassTypes.assertThat(response.getMaxNights())
        .isEqualTo(9);
    verifyNoMoreInteractions(maxNightsRepository);
  }

  @Test
  void getMaxRoomsRule__shouldThrowException() {
    //Arrange
    final String expectedMessage = "Error while trying to get max rooms rule";
    when(maxRoomsRepository.findRule("CCUI")).thenReturn(
        Optional.empty());
    var request = createMaxRoomsRuleRequest();

    //Act
    Exception exception = assertThrows(RuleEngineException.class,
        () -> maxLimitationsRuleInPort.getMaxRoomsRule(request));

    //Assert
    assertThat(exception, notNullValue());
    assertThat(exception.getMessage(), is(expectedMessage));
    verifyNoMoreInteractions(maxRoomsRepository);
  }

  @Test
  void getMaxRoomsRule__shouldReturnOk() {
    //Arrange
    var maxRoomsRuleRequest = createMaxRoomsRuleRequest();
    when(maxRoomsRepository.findRule("CCUI")).thenReturn(createMaxRoomsRule());

    //Act
    var response = maxLimitationsRuleInPort.getMaxRoomsRule(maxRoomsRuleRequest);

    //Assert
    assertThat(response, notNullValue());
    AssertionsForClassTypes.assertThat(response.getRequestDetails().getChannelId()).isEqualTo(
        maxRoomsRuleRequest.getChannelId());
    AssertionsForClassTypes.assertThat(response.getMaxRooms())
        .isEqualTo(9);
    verifyNoMoreInteractions(maxRoomsRepository);
  }

  @Test
  void getMaxRoomOccupancyRule__shouldThrowExceptionWhenListIsNull() {
    //Arrange
    final String expectedMessage = "Error while getting room occupancy rule!";

    when(roomOccupancyRuleRepoOutPort.findRules("PI", "PI"))
        .thenReturn(null);
    var request = createMaxRoomOccupancyRequest();

    //Act
    Exception exception = assertThrows(RuleEngineException.class,
        () -> maxLimitationsRuleInPort.getMaxRoomOccupancyRule(request));

    //Assert
    assertThat(exception, notNullValue());
    assertThat(exception.getMessage(), is(expectedMessage));
  }

  @Test
  void getMaxRoomOccupancyRule__shouldThrowExceptionWhenListIsEmpty() {
    //Arrange
    final String expectedMessage = "Error while getting room occupancy rule!";

    when(roomOccupancyRuleRepoOutPort.findRules("PI", "PI"))
        .thenReturn(Collections.emptyList());
    var request = createMaxRoomOccupancyRequest();

    //Act
    Exception exception = assertThrows(RuleEngineException.class,
        () -> maxLimitationsRuleInPort.getMaxRoomOccupancyRule(request));

    //Assert
    assertThat(exception, notNullValue());
    assertThat(exception.getMessage(), is(expectedMessage));
  }

  @Test
  void getMaxRoomOccupancyRule__shouldReturnOk() {
    //Arrange
    var maxRoomOccupancyRequest = createMaxRoomOccupancyRequest();
    when(roomOccupancyRuleRepoOutPort.findRules("PI", "PI"))
        .thenReturn(getRoomOccupancyRules());

    //Act
    var response = maxLimitationsRuleInPort.getMaxRoomOccupancyRule(maxRoomOccupancyRequest);

    //Assert
    assertThat(response, notNullValue());
    AssertionsForClassTypes.assertThat(response.getChannelId()).isEqualTo(
        maxRoomOccupancyRequest.getChannelId());
    assertThat(response.getMaxOccupancyData(), hasSize(6));
    verifyNoMoreInteractions(roomOccupancyRuleRepoOutPort);
  }

  @Test
  void getMaxArrivalDateRule__shouldThrowException() {
    //Arrange
    final String expectedMessage = "Error while trying to get max arrival date rule!";
    when(maxArrivalDateRuleRepositoryOutPort.findRule("CCUI")).thenReturn(
        Optional.empty());
    var request = createMaxArrivalDateRuleRequest();

    //Act
    Exception exception = assertThrows(RuleEngineException.class,
        () -> maxLimitationsRuleInPort.getMaxArrivalDateRule(request));

    //Assert
    assertThat(exception, notNullValue());
    assertThat(exception.getMessage(), is(expectedMessage));
    verifyNoMoreInteractions(maxArrivalDateRuleRepositoryOutPort);
  }

  @Test
  void getMaxArrivalDateRule__shouldRetrunOk() {
    //Arrange
    var maxArrivalDateRuleRequest = createMaxArrivalDateRuleRequest();
    when(maxArrivalDateRuleRepositoryOutPort.findRule("CCUI"))
        .thenReturn(createMaxArrivalDateRule());

    //Act
    var response = maxLimitationsRuleInPort.getMaxArrivalDateRule(maxArrivalDateRuleRequest);

    //Assert
    assertThat(response, notNullValue());
    AssertionsForClassTypes.assertThat(response.getRequestDetails().getChannelId()).isEqualTo(
        maxArrivalDateRuleRequest.getChannelId());
    AssertionsForClassTypes.assertThat(response.getMaxArrivalDate())
        .isEqualTo(9);
    verifyNoMoreInteractions(maxArrivalDateRuleRepositoryOutPort);
  }

  private MaxNightsRuleRequest createMaxNightsRuleRequest() {
    return MaxNightsRuleRequest.builder()
        .channelId("CCUI")
        .build();
  }

  private Optional<MaxNightsRule> createMaxNightsRule() {
    var time = LocalDateTime.now();
    return Optional.of(MaxNightsRule.builder()
        .ruleId(1)
        .status(RuleStatus.ACTIVE)
        .createdAt(time)
        .lastModifiedAt(time)
        .maxNights(9)
        .channelId("CCUI")
        .build());
  }

  private MaxRoomsRuleRequest createMaxRoomsRuleRequest() {
    return MaxRoomsRuleRequest.builder()
        .channelId("CCUI")
        .build();
  }

  private Optional<MaxRoomsRule> createMaxRoomsRule() {
    var time = LocalDateTime.now();
    return Optional.of(MaxRoomsRule.builder()
        .ruleId(1)
        .status(RuleStatus.ACTIVE)
        .createdAt(time)
        .lastModifiedAt(time)
        .maxRooms(9)
        .channelId("CCUI")
        .build());
  }

  private MaxArrivalDateRuleRequest createMaxArrivalDateRuleRequest() {
    return MaxArrivalDateRuleRequest.builder()
        .channelId("CCUI")
        .build();
  }

  private Optional<MaxArrivalDateRule> createMaxArrivalDateRule() {
    var time = LocalDateTime.now();
    return Optional.of(MaxArrivalDateRule.builder()
        .ruleId(1)
        .status(RuleStatus.ACTIVE)
        .createdAt(time)
        .lastModifiedAt(time)
        .maxArrivalDate(9)
        .channelId("CCUI")
        .build());
  }

  private MaxRoomOccupancyRequest createMaxRoomOccupancyRequest() {
    return MaxRoomOccupancyRequest.builder()
        .channelId("PI")
        .brand("PI")
        .build();
  }

  private List<MaxRoomOccupancyRule> getRoomOccupancyRules() {
    var time = LocalDateTime.now();
    return List.of(MaxRoomOccupancyRule.builder()
            .ruleId(1)
            .status(RuleStatus.ACTIVE)
            .createdAt(time)
            .lastModifiedAt(time)
            .channelId("PI")
            .adults(2)
            .children(0)
            .singleRoom(false)
            .accessibleRoom(true)
            .doubleRoom(true)
            .twinRoom(true)
            .familyRoom(false)
            .brand("PI")
            .build(),
        MaxRoomOccupancyRule.builder()
            .ruleId(2)
            .status(RuleStatus.ACTIVE)
            .createdAt(time)
            .lastModifiedAt(time)
            .channelId("PI")
            .adults(2)
            .children(2)
            .singleRoom(false)
            .accessibleRoom(false)
            .doubleRoom(false)
            .twinRoom(false)
            .familyRoom(true)
            .brand("PI")
            .build(),
        MaxRoomOccupancyRule.builder()
            .ruleId(3)
            .status(RuleStatus.ACTIVE)
            .createdAt(time)
            .lastModifiedAt(time)
            .channelId("PI")
            .adults(2)
            .children(1)
            .singleRoom(false)
            .accessibleRoom(false)
            .doubleRoom(false)
            .twinRoom(false)
            .familyRoom(true)
            .brand("PI")
            .build(),
        MaxRoomOccupancyRule.builder()
            .ruleId(4)
            .status(RuleStatus.ACTIVE)
            .createdAt(time)
            .lastModifiedAt(time)
            .channelId("PI")
            .adults(1)
            .children(2)
            .singleRoom(false)
            .accessibleRoom(false)
            .doubleRoom(false)
            .twinRoom(false)
            .familyRoom(true)
            .brand("PI")
            .build(),
        MaxRoomOccupancyRule.builder()
            .ruleId(4)
            .status(RuleStatus.ACTIVE)
            .createdAt(time)
            .lastModifiedAt(time)
            .channelId("PI")
            .adults(1)
            .children(1)
            .singleRoom(false)
            .accessibleRoom(false)
            .doubleRoom(false)
            .twinRoom(false)
            .familyRoom(true)
            .brand("PI")
            .build(),
        MaxRoomOccupancyRule.builder()
            .ruleId(6)
            .status(RuleStatus.ACTIVE)
            .createdAt(time)
            .lastModifiedAt(time)
            .channelId("PI")
            .adults(1)
            .children(0)
            .singleRoom(true)
            .accessibleRoom(true)
            .doubleRoom(true)
            .twinRoom(false)
            .familyRoom(false)
            .brand("PI")
            .build());
  }
}