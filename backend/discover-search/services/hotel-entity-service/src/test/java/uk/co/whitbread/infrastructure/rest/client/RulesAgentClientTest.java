package uk.co.whitbread.infrastructure.rest.client;

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
import java.util.function.Function;
import java.util.function.Predicate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.infrastructure.config.RulesAgentProperties;
import uk.co.whitbread.infrastructure.rest.client.rulesagent.exceptions.RulesAgentException;
import uk.co.whitbread.infrastructure.rest.client.rulesagent.model.in.RoomSubstitutionRuleRequestDto;
import uk.co.whitbread.infrastructure.rest.client.utils.CustomTestResponseSpec;
import uk.co.whitbread.rules.agent.generated.models.MaxNightsRuleResponseDto;
import uk.co.whitbread.rules.agent.generated.models.MaxRoomOccupancyResponseDto;
import uk.co.whitbread.rules.agent.generated.models.MaxRoomsRuleResponseDto;
import uk.co.whitbread.rules.agent.generated.models.MultiOccupancySupplementRequestDto;
import uk.co.whitbread.rules.agent.generated.models.MultiOccupancySupplementResponseDto;
import uk.co.whitbread.rules.agent.generated.models.OccupancySupplementRequestDto;
import uk.co.whitbread.rules.agent.generated.models.RateSuppressionRuleResponseDto;
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
  private WebClient.RequestBodySpec requestBodySpec;
  @Mock
  private WebClient.RequestBodyUriSpec requestBodyUriSpec;
  @Mock
  private CustomTestResponseSpec responseSpec;
  @Mock
  private RulesAgentProperties rulesAgentProperties;
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
  void getMaxNightsRule() {
    //Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(MaxNightsRuleResponseDto.class)).thenReturn(mockMaxNightsRuleResponseDto());

    //Act
    var maxNightsRuleResponse = this.rulesAgentClient.getMaxNightsRule("PI");

    //Assert
    assertThat(maxNightsRuleResponse, notNullValue());
    assertThat(maxNightsRuleResponse.getMaxNights(), is(9));
  }

  @Test
  void getMaxRoomsRule() {
    //Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(MaxRoomsRuleResponseDto.class)).thenReturn(mockMaxRoomsRuleResponseDto());

    //Act
    var maxRoomsRuleResponse = this.rulesAgentClient.getMaxRoomsRule("PI");

    //Assert
    assertThat(maxRoomsRuleResponse, notNullValue());
    assertThat(maxRoomsRuleResponse.getMaxRooms(), is(4));
  }

  @Test
  void getMaxRoomOccupanciesRule() {
    //Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(MaxRoomOccupancyResponseDto.class)).thenReturn(mockMaxRoomOccupancyResponseDto());

    //Act
    var maxRoomOccupancyResponse = this.rulesAgentClient.getMaxRoomOccupancyResponse("PI");

    //Assert
    assertThat(maxRoomOccupancyResponse, notNullValue());
    assertThat(maxRoomOccupancyResponse.getChannelId(), is("PI"));
  }

  @Test
  void getRateSuppressionRule() {
    //Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(RateSuppressionRuleResponseDto.class)).thenReturn(mockRateSuppressionResponseDto());

    //Act
    var rateSuppressionResponse = this.rulesAgentClient.getRateSuppressions();

    //Assert
    assertThat(rateSuppressionResponse, notNullValue());
    assertThat(rateSuppressionResponse.getRateSuppressionList(), containsInRelativeOrder("RATE1", "RATE2"));
  }

  @Test
  void getMaxRoomOccupanciesRule__ShouldThrowException() {

    //Arrange
    String error = "Error while trying to get max room occupancy rule response";

    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();
    when(responseSpec.bodyToMono(MaxRoomOccupancyResponseDto.class)).thenReturn(
            Mono.error(new RulesAgentException("message", error, new Exception(), 1)));

    //Act
    RulesAgentException exception = Assertions.assertThrows(RulesAgentException.class,
            () -> rulesAgentClient.getMaxRoomOccupancyResponse("PI"));

    // Assert
    Assertions.assertEquals(exception.getMessage(), error);
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
    Assertions.assertEquals(exception.getMessage(), error);
  }

  @Test
  void getMaxNightsRule__ShouldThrowException() {
    //Arrange
    String error = "Error while trying to get max nights rule response";

    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();
    when(responseSpec.bodyToMono(MaxNightsRuleResponseDto.class)).thenReturn(
            Mono.error(new RulesAgentException("message", error, new Exception(), 1)));

    //Act
    RulesAgentException exception = Assertions.assertThrows(RulesAgentException.class,
            () -> rulesAgentClient.getMaxNightsRule("PI"));

    // Assert
    Assertions.assertEquals(exception.getMessage(), error);
  }

  @Test
  void getMaxRoomsRule__ShouldThrowException() {
    //Arrange
    String error = "Error while trying to get max rooms rule response";

    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();
    when(responseSpec.bodyToMono(MaxRoomsRuleResponseDto.class)).thenReturn(
            Mono.error(new RulesAgentException("message", error, new Exception(), 1)));

    //Act
    RulesAgentException exception = Assertions.assertThrows(RulesAgentException.class,
            () -> rulesAgentClient.getMaxRoomsRule("PI"));

    // Assert
    Assertions.assertEquals(exception.getMessage(), error);
  }

  @Test
  void getRateSuppressionRule__ShouldThrowException() {

    //Arrange
    String error = "Error while trying to get rate suppression rule response";

    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();
    when(responseSpec.bodyToMono(RateSuppressionRuleResponseDto.class)).thenReturn(
        Mono.error(new RulesAgentException("message", error, new Exception(), 1)));

    //Act
    RulesAgentException exception = Assertions.assertThrows(RulesAgentException.class,
        () -> rulesAgentClient.getRateSuppressions());

    // Assert
    Assertions.assertEquals(exception.getMessage(), error);
  }

  @Test
  void getMultiOccupancySupplementPricing() {
    //Arrange
    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(rulesAgentProperties.getMultiOccupancySupplementEndpoint()))
        .thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(MultiOccupancySupplementResponseDto.class))
        .thenReturn(mockMultiOccupancySupplementPricingResponse());
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);

    var multiOccupancySupplementPricingRequest = mockMultiOccupancySupplementPricingRequestDto();

    //Act
    var multiOccupancySupplementPricingResponse = this.rulesAgentClient.getMultiOccupancySupplementPricing(
        multiOccupancySupplementPricingRequest);

    //Assert
    assertThat(multiOccupancySupplementPricingResponse, notNullValue());
  }

  @Test
  void getMultiOccupancySupplementPricing__ShouldThrowException() {

    //Arrange
    String error = "Some random error";

    when(webClient.post()).thenReturn(requestBodyUriSpec);
    when(requestBodyUriSpec.uri(rulesAgentProperties.getMultiOccupancySupplementEndpoint()))
        .thenReturn(requestBodySpec);
    when(requestBodySpec.body(any(), (Class<Object>) any())).thenReturn(requestHeadersSpec);
    when(requestBodySpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(requestBodySpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();
    when(responseSpec.bodyToMono(MultiOccupancySupplementResponseDto.class))
        .thenReturn(Mono.error(new RulesAgentException("message", error, new Exception(), 1)));

    var multiOccupancySupplementPricingRequest = mockMultiOccupancySupplementPricingRequestDto();

    //Act
    RulesAgentException exception = Assertions.assertThrows(RulesAgentException.class,
        () -> rulesAgentClient.getMultiOccupancySupplementPricing(multiOccupancySupplementPricingRequest));

    // Assert
    Assertions.assertEquals(exception.getMessage(), error);
  }

  private Mono<MaxRoomOccupancyResponseDto> mockMaxRoomOccupancyResponseDto() {
    MaxRoomOccupancyResponseDto maxRoomOccupancyResponseDto = new MaxRoomOccupancyResponseDto();
    maxRoomOccupancyResponseDto.setChannelId("PI");
    return Mono.just(maxRoomOccupancyResponseDto);
  }

  private Mono<MaxRoomsRuleResponseDto> mockMaxRoomsRuleResponseDto() {
    var maxRoomsRuleResponseDto = new MaxRoomsRuleResponseDto();
    maxRoomsRuleResponseDto.setMaxRooms(4);
    return Mono.just(maxRoomsRuleResponseDto);
  }

  private Mono<MaxNightsRuleResponseDto> mockMaxNightsRuleResponseDto() {
    var maxNightsRuleResponseDto = new MaxNightsRuleResponseDto();
    maxNightsRuleResponseDto.setMaxNights(9);
    return Mono.just(maxNightsRuleResponseDto);
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

  private Mono<RateSuppressionRuleResponseDto> mockRateSuppressionResponseDto() {
    var response = new RateSuppressionRuleResponseDto();
    response.setRateSuppressionList(List.of("RATE1", "RATE2"));

    return Mono.just(response);
  }

  private Mono<MultiOccupancySupplementResponseDto> mockMultiOccupancySupplementPricingResponse() {
    var response = new MultiOccupancySupplementResponseDto();
    response.setDictionary(Collections.singletonMap("HOTEL1", BigDecimal.ONE));
    return Mono.just(response);
  }

  private MultiOccupancySupplementRequestDto mockMultiOccupancySupplementPricingRequestDto() {

    var request = new MultiOccupancySupplementRequestDto();
    var occSupplementRequestList = new ArrayList<OccupancySupplementRequestDto>();

    var occSupplementRequest = new OccupancySupplementRequestDto();
    occSupplementRequest.setHotelId("HOTEL1");
    occSupplementRequestList.add(occSupplementRequest);

    request.setHotelIds(occSupplementRequestList);

    return request;
  }
}
