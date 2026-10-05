package uk.co.whitbread.availabilitycacheservice.infrastructure.rest.client;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static wiremock.org.hamcrest.MatcherAssert.assertThat;
import static wiremock.org.hamcrest.Matchers.notNullValue;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.availabilitycacheservice.infrastructure.client.utils.CustomTestResponseSpec;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.client.rulesagent.exceptions.RulesAgentException;
import uk.co.whitbread.availabilitycacheservice.infrastructure.rest.client.rulesagent.model.in.RoomSubstitutionRuleRequestDto;
import uk.co.whitbread.rules.agent.generated.models.RoomSubstitutionDto;
import uk.co.whitbread.rules.agent.generated.models.RoomSubstitutionRuleResponseDto;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
class RulesAgentClientTest {

  @Mock
  private WebClient webClient;
  @SuppressWarnings("rawtypes")
  @Mock
  private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
  @SuppressWarnings("rawtypes")
  @Mock
  private WebClient.RequestHeadersSpec requestHeadersSpec;
  @Mock
  private CustomTestResponseSpec responseSpec;
  @InjectMocks
  private RulesAgentClient rulesAgentClient;

  @Test
  void getSubstitutionRoomRules() {
    //Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(RoomSubstitutionRuleResponseDto.class)).thenReturn(
        mockRoomSubstitutionRuleResponseDto());

    var roomSubstitutionRuleRequest = mockRoomSubstitutionRuleRequestDto();

    //Act
    var roomSubstitutionRuleResponse = this.rulesAgentClient.getSubstitutionRoomRules(
        roomSubstitutionRuleRequest);

    //Assert
    assertThat(roomSubstitutionRuleResponse, notNullValue());
  }

  @Test
  void getSubstitutionRoomRules__ShouldThrowException() {
    //Arrange
    String error = "Error while trying to get room substitution rule response";

    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class)))
            .thenCallRealMethod();
    when(responseSpec.bodyToMono(RoomSubstitutionRuleResponseDto.class)).thenReturn(
            Mono.error(new RulesAgentException("message", error, new Exception(), 1)));

    var roomSubstitutionRuleRequest = mockRoomSubstitutionRuleRequestDto();

    //Act
    RulesAgentException exception = Assertions.assertThrows(RulesAgentException.class,
            () -> rulesAgentClient.getSubstitutionRoomRules(roomSubstitutionRuleRequest));

    // Assert
    Assertions.assertEquals(error, exception.getMessage());
  }

  private RoomSubstitutionRuleRequestDto mockRoomSubstitutionRuleRequestDto() {
    return RoomSubstitutionRuleRequestDto.builder()
        .adults(1)
        .children(0)
        .roomType("DB")
        .pms("OP")
        .build();
  }

  private Mono<RoomSubstitutionRuleResponseDto> mockRoomSubstitutionRuleResponseDto() {
    var roomSubstitution = new RoomSubstitutionDto();
    roomSubstitution.setType("DBLWIN");

    var roomSubstitutionRuleResponse = new RoomSubstitutionRuleResponseDto();
    roomSubstitutionRuleResponse.setSubstitutionList(List.of(roomSubstitution));

    return Mono.just(roomSubstitutionRuleResponse);
  }
}
