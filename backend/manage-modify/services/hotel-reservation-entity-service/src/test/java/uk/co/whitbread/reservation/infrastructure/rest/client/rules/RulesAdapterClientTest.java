package uk.co.whitbread.reservation.infrastructure.rest.client.rules;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.function.Function;
import java.util.function.Predicate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.reservation.infrastructure.rest.client.rules.exceptions.RulesException;
import uk.co.whitbread.reservation.infrastructure.rest.client.rules.service.RulesAdapterClient;
import uk.co.whitbread.reservation.infrastructure.rest.utils.CustomTestResponseSpec;
import uk.co.whitbread.rules.entity.service.generated.models.agent.AmendmentRuleResponseDto;
import uk.co.whitbread.rules.entity.service.generated.models.agent.ChannelRuleResponseDto;
import uk.co.whitbread.rules.entity.service.generated.models.agent.MaxNightsRuleResponseDto;
import uk.co.whitbread.rules.entity.service.generated.models.agent.MaxRoomOccupancyResponseDto;
import uk.co.whitbread.rules.entity.service.generated.models.agent.MaxRoomsRuleResponseDto;
import uk.co.whitbread.rules.entity.service.generated.models.agent.SingleOccupancySupplementResponseDto;
import uk.co.whitbread.rules.entity.service.generated.models.agent.VatRuleResponseDto;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
class RulesAdapterClientTest {

  @InjectMocks
  private RulesAdapterClient rulesAdapterClient;
  @Mock
  private WebClient webClient;
  @SuppressWarnings("rawtypes")
  @Mock
  private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
  @SuppressWarnings("rawtypes")
  @Mock
  private WebClient.RequestHeadersSpec requestHeadersSpec;
  @Mock
  private WebClient.ResponseSpec responseSpec;
  @Mock
  private CustomTestResponseSpec responseSpecMock;

  @Test
  void isBookingAmendable_success() {
    // Arrange
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class)))
        .thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(AmendmentRuleResponseDto.class)).thenReturn(
        mockAmendmentResponse());

    // Act
    var response = rulesAdapterClient.isBookingAmendable("Flex",
        "20221128", "20221128T130001", "GB");

    // Assert
    assertNotNull(response);
    assertThat(response.getIsAmendable(), is(true));
  }

  @Test
  void getMaxRoomsRule_success() {
    // Arrange
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class)))
        .thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(MaxRoomsRuleResponseDto.class)).thenReturn(
        Mono.just(new MaxRoomsRuleResponseDto()));

    // Act
    var response = rulesAdapterClient.getMaxRoomsRule("PI");

    // Assert
    assertNotNull(response);
  }

  @Test
  void getMaxRoomsRule_RulesException() {
    // Arrange
    RulesException ex = mock(RulesException.class);
    Mono<MaxRoomsRuleResponseDto> rsp = Mono.error(ex);
    when(responseSpecMock.bodyToMono(MaxRoomsRuleResponseDto.class)).thenReturn(rsp);
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class)))
        .thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.getStatus()).thenReturn(HttpStatus.BAD_REQUEST);
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();

    // Act & Assert
    assertThrows(RulesException.class, () -> rulesAdapterClient.getMaxRoomsRule("PI"));
  }

  @Test
  void getMaxNightsRule_success() {
    // Arrange
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class)))
        .thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(MaxNightsRuleResponseDto.class)).thenReturn(
        Mono.just(new MaxNightsRuleResponseDto()));

    // Act
    var response = rulesAdapterClient.getMaxNightsRule("PI");

    // Assert
    assertNotNull(response);
  }

  @Test
  void getMaxNightsRule_RulesException() {
    // Arrange
    RulesException ex = mock(RulesException.class);
    Mono<MaxNightsRuleResponseDto> rsp = Mono.error(ex);
    when(responseSpecMock.bodyToMono(MaxNightsRuleResponseDto.class)).thenReturn(rsp);
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class)))
        .thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.getStatus()).thenReturn(HttpStatus.BAD_REQUEST);
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();

    // Act & Assert
    assertThrows(RulesException.class, () -> rulesAdapterClient.getMaxNightsRule("PI"));
  }

  @Test
  void getMaxRoomOccupancyResponse_success() {
    // Arrange
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class)))
        .thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(MaxRoomOccupancyResponseDto.class)).thenReturn(
        Mono.just(new MaxRoomOccupancyResponseDto()));

    // Act
    var response = rulesAdapterClient.getMaxRoomOccupancyResponse("PI", "PI");

    // Assert
    assertNotNull(response);
  }

  @Test
  void getSingleOccupancySupplement_success() {
    // Arrange
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class)))
            .thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(SingleOccupancySupplementResponseDto.class)).thenReturn(
            Mono.just(new SingleOccupancySupplementResponseDto()));

    // Act
    var response = rulesAdapterClient.getSingleOccupancySupplement("hotelId");

    // Assert
    assertNotNull(response);
  }

  @Test
  void getSingleOccupancySupplement_NotFound_RulesException() {
    // Arrange
    RulesException ex = mock(RulesException.class);
    Mono<SingleOccupancySupplementResponseDto> rsp = Mono.error(ex);
    when(responseSpecMock.bodyToMono(SingleOccupancySupplementResponseDto.class)).thenReturn(rsp);
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class)))
            .thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.getStatus()).thenReturn(HttpStatus.NOT_FOUND);
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();
    // Act & Assert
    assertThrows(RulesException.class,
            () -> rulesAdapterClient.getSingleOccupancySupplement("hotelId"));
  }

  @Test
  void getMaxRoomOccupancyResponse_NotFound_RulesException() {
    // Arrange
    RulesException ex = mock(RulesException.class);
    Mono<MaxRoomOccupancyResponseDto> rsp = Mono.error(ex);
    when(responseSpecMock.bodyToMono(MaxRoomOccupancyResponseDto.class)).thenReturn(rsp);
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class)))
        .thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.getStatus()).thenReturn(HttpStatus.NOT_FOUND);
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();
    // Act & Assert
    assertThrows(RulesException.class,
        () -> rulesAdapterClient.getMaxRoomOccupancyResponse("PI", "PI"));
  }

  @Test
  void getMaxRoomOccupancyResponse_InternalServer_RulesException() {
    // Arrange
    RulesException ex = mock(RulesException.class);
    Mono<MaxRoomOccupancyResponseDto> rsp = Mono.error(ex);
    when(responseSpecMock.bodyToMono(MaxRoomOccupancyResponseDto.class)).thenReturn(rsp);
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class)))
        .thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();

    // Act & Assert
    assertThrows(RulesException.class,
        () -> rulesAdapterClient.getMaxRoomOccupancyResponse("PI", "PI"));
  }

  @Test
  void getChannelBasedOnSourceId_success() {
    // Arrange
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class)))
        .thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(ChannelRuleResponseDto.class)).thenReturn(
        Mono.just(new ChannelRuleResponseDto()));

    // Act
    var response = rulesAdapterClient.getChannelBasedOnSourceId("PI");

    // Assert
    assertNotNull(response);
  }

  @Test
  void getChannelBasedOnSourceId_NotFound_RulesException() {
    // Arrange
    RulesException ex = mock(RulesException.class);
    Mono<ChannelRuleResponseDto> rsp = Mono.error(ex);
    when(responseSpecMock.bodyToMono(ChannelRuleResponseDto.class)).thenReturn(rsp);
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class)))
        .thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.getStatus()).thenReturn(HttpStatus.NOT_FOUND);
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();

    // Act & Assert
    assertThrows(RulesException.class,
        () -> rulesAdapterClient.getChannelBasedOnSourceId("PI"));
  }

  @Test
  void getChannelBasedOnSourceId_InternalServer_RulesException() {
    // Arrange
    RulesException ex = mock(RulesException.class);
    Mono<ChannelRuleResponseDto> rsp = Mono.error(ex);
    when(responseSpecMock.bodyToMono(ChannelRuleResponseDto.class)).thenReturn(rsp);
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class)))
        .thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();

    // Act & Assert
    assertThrows(RulesException.class,
        () -> rulesAdapterClient.getChannelBasedOnSourceId("PI"));
  }

  @Test
  void getGetVatCodesForPackage_success() {
    // Arrange
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class)))
        .thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(VatRuleResponseDto.class)).thenReturn(
        Mono.just(new VatRuleResponseDto()));

    // Act
    var response = rulesAdapterClient.getVatCodesForPackage("UK", "11111");

    // Assert
    assertNotNull(response);
  }

  @Test
  void getGetVatCodesForPackage_InternalServer_RulesException() {
    // Arrange
    RulesException ex = mock(RulesException.class);
    Mono<VatRuleResponseDto> rsp = Mono.error(ex);
    when(responseSpecMock.bodyToMono(VatRuleResponseDto.class)).thenReturn(rsp);
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class)))
        .thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();

    // Act & Assert
    assertThrows(RulesException.class,
        () -> rulesAdapterClient.getVatCodesForPackage("UK", "11111"));
  }

  private Mono<AmendmentRuleResponseDto> mockAmendmentResponse() {
    var response = new AmendmentRuleResponseDto();
    response.setIsAmendable(true);
    return Mono.just(response);
  }
}
