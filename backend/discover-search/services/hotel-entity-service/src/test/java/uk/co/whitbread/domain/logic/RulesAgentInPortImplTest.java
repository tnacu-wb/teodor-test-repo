package uk.co.whitbread.domain.logic;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static wiremock.org.hamcrest.MatcherAssert.assertThat;
import static wiremock.org.hamcrest.Matchers.containsInRelativeOrder;
import static wiremock.org.hamcrest.Matchers.is;
import static wiremock.org.hamcrest.Matchers.notNullValue;

import java.util.List;
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
import uk.co.whitbread.domain.ports.secondary.RulesAgentOutPort;

@ExtendWith(MockitoExtension.class)
class RulesAgentInPortImplTest {

  @Mock
  private RulesAgentOutPort rulesAgentOutPort;
  @InjectMocks
  private RulesAgentInPortImpl rulesAgentInPortImpl;

  @Test
  void getRoomSubstitutionRule__ShouldReturnOk() {
    var request = buildRoomSubstitutionRuleRequest();
    var response = buildRoomSubstitutionRuleResponse();

    when(this.rulesAgentOutPort.getRoomSubstitutionRule(any(RoomSubstitutionRuleRequest.class)))
        .thenReturn(response);

    var roomSubstitutionRuleResponse = rulesAgentInPortImpl.getRoomSubstitutionRule(request);

    assertThat(roomSubstitutionRuleResponse, notNullValue());
    assertThat(roomSubstitutionRuleResponse.getRequestDetails().getAdults(),
        is(request.getAdults()));
  }

  @Test
  void getMaxNightsRule__ShouldReturnOk() {
    var response = mockMaxNightsRule();

    when(this.rulesAgentOutPort.getMaxNightsRule(any(String.class)))
        .thenReturn(response);

    var maxNightsRuleResponse = rulesAgentInPortImpl.getMaxNightsRule("PI");

    assertThat(maxNightsRuleResponse, notNullValue());
    assertThat(maxNightsRuleResponse.getMaxNights(),
        is(9));
  }

  @Test
  void getMaxRoomsRule__ShouldReturnOk() {
    var response = mockMaxRoomsRule();

    when(this.rulesAgentOutPort.getMaxRoomsRule(any(String.class)))
        .thenReturn(response);

    var maxRoomsRuleResponse = rulesAgentInPortImpl.getMaxRoomsRule("PI");

    assertThat(maxRoomsRuleResponse, notNullValue());
    assertThat(maxRoomsRuleResponse.getMaxRooms(),
        is(4));
  }

  @Test
  void getMaxRoomOccupancyRule__ShouldReturnOk() {
    var response = mockMaxRoomOccupancyRule();

    when(this.rulesAgentOutPort.getMaxRoomOccupancyRule(any(String.class)))
        .thenReturn(response);

    var maxRoomOccupancyResponse = rulesAgentInPortImpl.getMaxRoomOccupancyRule("PI");

    assertThat(maxRoomOccupancyResponse, notNullValue());
    assertThat(maxRoomOccupancyResponse.getChannelId(),
        is("PI"));
  }

  @Test
  void getRateSuppressionRule__ShouldReturnOk() {
    var response = mockRateSuppressionRule();

    when(this.rulesAgentOutPort.getRateSuppressionRule())
        .thenReturn(response);

    var rateSuppressionRuleResponse = rulesAgentInPortImpl.getRateSuppressionRule();

    assertThat(rateSuppressionRuleResponse, notNullValue());
    assertThat(rateSuppressionRuleResponse.getRateSuppressionList(),
        containsInRelativeOrder("RATE1", "RATE2"));
  }

  private RateSuppressionRuleResponse mockRateSuppressionRule() {
    return RateSuppressionRuleResponse.builder()
        .rateSuppressionList(List.of("RATE1", "RATE2"))
        .build();
  }

  private MaxRoomOccupancyResponse mockMaxRoomOccupancyRule() {
    return MaxRoomOccupancyResponse.builder()
        .channelId("PI")
        .build();
  }

  private MaxRoomsRuleResponse mockMaxRoomsRule() {
    return MaxRoomsRuleResponse.builder()
        .maxRooms(4)
        .build();
  }

  private MaxNightsRuleResponse mockMaxNightsRule() {
    return MaxNightsRuleResponse.builder()
        .maxNights(9)
        .build();
  }

  private RoomSubstitutionRuleResponse buildRoomSubstitutionRuleResponse() {
    List<RoomSubstitution> roomSubstitutionList = List.of(
        RoomSubstitution.builder().type("DBLWIN").silent(false).build());
    return RoomSubstitutionRuleResponse.builder()
        .requestDetails(RoomSubstitutionRequestDetails.builder()
            .adults(1)
            .children(0)
            .pms("OP")
            .roomType("DB")
            .build())
        .substitutionList(roomSubstitutionList)
        .build();
  }

  private RoomSubstitutionRuleRequest buildRoomSubstitutionRuleRequest() {
    return RoomSubstitutionRuleRequest.builder()
        .adults(1)
        .children(0)
        .pms("OP")
        .roomType("DB")
        .channel("PI")
        .build();
  }
}
