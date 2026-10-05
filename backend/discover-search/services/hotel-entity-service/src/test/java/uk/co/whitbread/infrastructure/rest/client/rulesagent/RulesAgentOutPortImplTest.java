package uk.co.whitbread.infrastructure.rest.client.rulesagent;


import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static wiremock.org.hamcrest.MatcherAssert.assertThat;
import static wiremock.org.hamcrest.Matchers.containsInRelativeOrder;
import static wiremock.org.hamcrest.Matchers.is;
import static wiremock.org.hamcrest.Matchers.notNullValue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.domain.model.rulesagent.in.RoomSubstitutionRuleRequest;
import uk.co.whitbread.domain.model.rulesagent.out.MaxNightsRuleResponse;
import uk.co.whitbread.domain.model.rulesagent.out.MaxRoomOccupancyResponse;
import uk.co.whitbread.domain.model.rulesagent.out.MaxRoomsRuleResponse;
import uk.co.whitbread.domain.model.rulesagent.out.RateSuppressionRuleResponse;
import uk.co.whitbread.domain.model.rulesagent.out.RoomSubstitution;
import uk.co.whitbread.domain.model.rulesagent.out.RoomSubstitutionRequestDetails;
import uk.co.whitbread.domain.model.rulesagent.out.RoomSubstitutionRuleResponse;
import uk.co.whitbread.infrastructure.rest.client.RulesAgentClient;
import uk.co.whitbread.infrastructure.rest.client.rulesagent.exceptions.RulesAgentException;
import uk.co.whitbread.infrastructure.rest.client.rulesagent.mapper.MaxNightsRuleResponseMapper;
import uk.co.whitbread.infrastructure.rest.client.rulesagent.mapper.MaxRoomOccupancyRuleResponseMapper;
import uk.co.whitbread.infrastructure.rest.client.rulesagent.mapper.MaxRoomsRuleResponseMapper;
import uk.co.whitbread.infrastructure.rest.client.rulesagent.mapper.RateSuppressionRuleResponseMapper;
import uk.co.whitbread.infrastructure.rest.client.rulesagent.mapper.RoomSubstitutionRuleRequestMapper;
import uk.co.whitbread.infrastructure.rest.client.rulesagent.mapper.RoomSubstitutionRuleResponseMapper;
import uk.co.whitbread.infrastructure.rest.client.rulesagent.model.in.RoomSubstitutionRuleRequestDto;
import uk.co.whitbread.rules.agent.generated.models.*;

@ExtendWith(MockitoExtension.class)
class RulesAgentOutPortImplTest {

  private static final String HOTEL_ID = "HOTEL1";
  private static final String RULES_AGENT_ERROR = "An error was returned by Rules Agent";
  @InjectMocks
  private RulesAgentOutPortImpl rulesAgentOutPort;
  @Mock
  private RulesAgentClient rulesAgentClient;
  @Mock
  private RoomSubstitutionRuleRequestMapper roomSubstitutionRuleRequestMapper;
  @Mock
  private RoomSubstitutionRuleResponseMapper roomSubstitutionRuleResponseMapper;
  @Mock
  private MaxNightsRuleResponseMapper maxNightsRuleResponseMapper;
  @Mock
  private MaxRoomsRuleResponseMapper maxRoomsRuleResponseMapper;
  @Mock
  private MaxRoomOccupancyRuleResponseMapper maxRoomOccupancyRuleResponseMapper;
  @Mock
  private RateSuppressionRuleResponseMapper rateSuppressionRuleResponseMapper;

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
  void getMaxNightsRule() {
    //Arrange
    when(rulesAgentClient.getMaxNightsRule(any(String.class)))
        .thenReturn(createMaxNightsRuleResponseDto());
    when(maxNightsRuleResponseMapper.toModel(any(MaxNightsRuleResponseDto.class)))
        .thenReturn(createMaxNightsRuleResponse());

    //Act
    var maxNightsRuleResponse = rulesAgentOutPort.getMaxNightsRule("PI");

    //Assert
    assertThat(maxNightsRuleResponse, notNullValue());
    assertThat(maxNightsRuleResponse.getMaxNights(), is(9));
  }

  @Test
  void getMaxRoomsRule() {
    //Arrange
    when(rulesAgentClient.getMaxRoomsRule(any(String.class)))
        .thenReturn(createMaxRoomsRuleResponseDto());
    when(maxRoomsRuleResponseMapper.toModel(any(MaxRoomsRuleResponseDto.class)))
        .thenReturn(createMaxRoomsRuleResponse());

    //Act
    var maxRoomsRuleResponse = rulesAgentOutPort.getMaxRoomsRule("PI");

    //Assert
    assertThat(maxRoomsRuleResponse, notNullValue());
    assertThat(maxRoomsRuleResponse.getMaxRooms(), is(4));
  }

  @Test
  void getMaxRoomOccupancyRule() {
    //Arrange
    when(rulesAgentClient.getMaxRoomOccupancyResponse(any(String.class)))
        .thenReturn(createMaxRoomOccupancyRuleResponseDto());
    when(maxRoomOccupancyRuleResponseMapper.toModel(any(MaxRoomOccupancyResponseDto.class)))
        .thenReturn(createMaxRoomOccupancyResponse());

    //Act
    var maxRoomOccupancyResponse = rulesAgentOutPort.getMaxRoomOccupancyRule("PI");

    //Assert
    assertThat(maxRoomOccupancyResponse, notNullValue());
    assertThat(maxRoomOccupancyResponse.getChannelId(), is("PI"));
  }

  @Test
  void getRateSuppressionRule() {
    //Arrange
    when(rulesAgentClient.getRateSuppressions())
        .thenReturn(createRateSuppressionResponseDto());
    when(rateSuppressionRuleResponseMapper.toModel(any(RateSuppressionRuleResponseDto.class)))
        .thenReturn(createRateSuppressionResponse());

    //Act
    var rateSuppressionResponse = rulesAgentOutPort.getRateSuppressionRule();

    //Assert
    assertThat(rateSuppressionResponse, notNullValue());
    assertThat(rateSuppressionResponse.getRateSuppressionList(),
        containsInRelativeOrder("RATE1", "RATE2"));
  }

  @Test
  void getMultiOccupancySupplementPricing() {
    //Arrange
    var responseDto = new MultiOccupancySupplementResponseDto();
    responseDto.setDictionary(createMultiOccupancySupplementPricingDto());
    when(rulesAgentClient.getMultiOccupancySupplementPricing(any()))
        .thenReturn(responseDto);

    //Act
    var multiOccSupplPricingResponse = rulesAgentOutPort.getMultiOccupancySupplementPricing(new ArrayList<>());

    //Assert
    assertThat(multiOccSupplPricingResponse, notNullValue());
    assertEquals(BigDecimal.ONE, multiOccSupplPricingResponse.get(HOTEL_ID));
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

  @Test
  void getMaxNightsRule__ShouldThrowException() {
    //Arrange
    when(rulesAgentClient.getMaxNightsRule(any(String.class)))
        .thenThrow(new RulesAgentException("message", RULES_AGENT_ERROR, new Exception(), 1));

    //Act
    RulesAgentException exception = Assertions.assertThrows(RulesAgentException.class,
        () -> rulesAgentOutPort.getMaxNightsRule("PI"));

    // Assert
    Assertions.assertEquals(RULES_AGENT_ERROR, exception.getMessage());

  }

  @Test
  void getMaxRoomOccupancyRule__ShouldThrowException() {
    //Arrange
    when(rulesAgentClient.getMaxRoomOccupancyResponse(any(String.class)))
        .thenThrow(new RulesAgentException("message", RULES_AGENT_ERROR, new Exception(), 1));

    //Act
    RulesAgentException exception = Assertions.assertThrows(RulesAgentException.class,
        () -> rulesAgentOutPort.getMaxRoomOccupancyRule("PI"));

    // Assert
    Assertions.assertEquals(RULES_AGENT_ERROR, exception.getMessage());

  }

  @Test
  void getMaxRoomsRule__ShouldThrowException() {
    //Arrange
    when(rulesAgentClient.getMaxRoomsRule(any(String.class)))
        .thenThrow(new RulesAgentException("message", RULES_AGENT_ERROR, new Exception(), 1));

    //Act
    RulesAgentException exception = Assertions.assertThrows(RulesAgentException.class,
        () -> rulesAgentOutPort.getMaxRoomsRule("PI"));

    // Assert
    Assertions.assertEquals(RULES_AGENT_ERROR, exception.getMessage());

  }

  @Test
  void getRateSuppressionRule__ShouldThrowException() {
    String errorMessage = "Error while trying to get rate suppressions";
    //Arrange
    when(rulesAgentClient.getRateSuppressions())
        .thenThrow(new RulesAgentException("message", errorMessage, new Exception(), 1));

    //Act
    RulesAgentException exception = Assertions.assertThrows(RulesAgentException.class,
        () -> rulesAgentOutPort.getRateSuppressionRule());

    // Assert
    Assertions.assertEquals(errorMessage, exception.getDebugMessage());

  }

  @Test
  void getMultiOccupancySupplementPricing__ShouldThrowException() {
    //Arrange
    when(rulesAgentClient.getMultiOccupancySupplementPricing(any()))
        .thenThrow(new RulesAgentException("message", RULES_AGENT_ERROR, new Exception(), 1));
    var hotelIds = new ArrayList<String>();

    //Act
    RulesAgentException exception = Assertions.assertThrows(RulesAgentException.class,
        () -> rulesAgentOutPort.getMultiOccupancySupplementPricing(hotelIds));

    //Assert
    Assertions.assertEquals(RULES_AGENT_ERROR, exception.getMessage());
  }

  private MaxRoomOccupancyResponse createMaxRoomOccupancyResponse() {
    return MaxRoomOccupancyResponse.builder()
        .channelId("PI")
        .build();
  }

  private MaxRoomOccupancyResponseDto createMaxRoomOccupancyRuleResponseDto() {
    var maxRoomOccupancyResponseDto = new MaxRoomOccupancyResponseDto();
    maxRoomOccupancyResponseDto.setChannelId("PI");
    return maxRoomOccupancyResponseDto;
  }

  private MaxRoomsRuleResponse createMaxRoomsRuleResponse() {
    return MaxRoomsRuleResponse.builder()
        .maxRooms(4)
        .build();
  }

  private MaxRoomsRuleResponseDto createMaxRoomsRuleResponseDto() {
    var maxRoomsRuleResponseDto = new MaxRoomsRuleResponseDto();
    maxRoomsRuleResponseDto.setMaxRooms(4);
    return maxRoomsRuleResponseDto;
  }

  private MaxNightsRuleResponse createMaxNightsRuleResponse() {
    return MaxNightsRuleResponse.builder()
        .maxNights(9)
        .build();
  }

  private MaxNightsRuleResponseDto createMaxNightsRuleResponseDto() {
    var maxNightsRuleResponseDto = new MaxNightsRuleResponseDto();
    maxNightsRuleResponseDto.setMaxNights(9);
    return maxNightsRuleResponseDto;
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

  private RateSuppressionRuleResponseDto createRateSuppressionResponseDto() {
    var response = new RateSuppressionRuleResponseDto();
    response.setRateSuppressionList(List.of("RATE1", "RATE2"));

    return response;
  }

  private RateSuppressionRuleResponse createRateSuppressionResponse() {
    return RateSuppressionRuleResponse.builder()
        .rateSuppressionList(List.of("RATE1", "RATE2"))
        .build();
  }

  private Map<String, BigDecimal> createMultiOccupancySupplementPricingDto() {
    return Collections.singletonMap(HOTEL_ID, BigDecimal.ONE);
  }
}
