package uk.co.whitbread.ohip.infrastructure.rest.client.rules;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
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
import uk.co.whitbread.hotel.rules.agent.generated.models.VatRuleResponseDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.rules.exceptions.BookingChannelException;
import uk.co.whitbread.ohip.infrastructure.rest.client.rules.model.out.BaseRateRuleResponseDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.rules.model.out.ChannelRuleRequestDetailsDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.rules.model.out.ChannelRuleResponseDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.rules.model.out.RoomSubstitutionDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.rules.model.out.RoomSubstitutionRequestDetailsDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.rules.model.out.RoomSubstitutionRuleResponseDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.rules.properties.RulesAgentProperties;
import uk.co.whitbread.ohip.infrastructure.rest.client.utils.CustomTestResponseSpec;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
class RulesAgentClientTest {

  private static String EXCEPTION_MESSAGE = "Error while trying to get channel source info for sourceId=44";
  @Mock
  RulesAgentProperties rulesAgentProperties;
  @InjectMocks
  private RulesAgentClient rulesAgentClient;
  @Mock
  private WebClient webClient;
  @Mock
  private WebClient.ResponseSpec responseSpec;
  @Mock
  private CustomTestResponseSpec responseSpecMock;
  @SuppressWarnings("rawtypes")
  @Mock
  private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
  @SuppressWarnings("rawtypes")
  @Mock
  private WebClient.RequestHeadersSpec requestHeadersSpec;

  @Test
  void getRoomSubstitutions__ShouldReturnOK() {
    // Arrange
    String roomType = "Double";
    Integer adultsNumber = 1;
    Integer childrenNumber = 0;
    String channel = "PI";

    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(RoomSubstitutionRuleResponseDto.class)).thenReturn(
        mockRoomSubstitutionRuleResponse());

    //Act
    RoomSubstitutionRuleResponseDto roomSubstitutionRuleResponse = rulesAgentClient.getRoomSubstitution(
        roomType, adultsNumber, childrenNumber, channel);

    //Assert
    assertThat(roomSubstitutionRuleResponse, notNullValue());
    assertEquals("DOUBLE", roomSubstitutionRuleResponse.getSubstitutionList().get(0).getType());
  }

  @Test
  void getVatCodes__ShouldReturnOK() {
    // Arrange
    String region = "UK";
    List<String> packCodes = List.of("__ACCMOD__","MD2DIN");

    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(VatRuleResponseDto.class)).thenReturn(
        mockVatResponse());

    //Act
    VatRuleResponseDto vatCodes = rulesAgentClient.getVatCodes(region, packCodes);

    //Assert
    assertThat(vatCodes, notNullValue());

  }

  @Test
  void getChannelInfo__ShouldReturnOK() {
    // Arrange
    String sourceId = "44";

    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(ChannelRuleResponseDto.class)).thenReturn(
        mockChannelRuleResponseDto());

    //Act
    var channelSourceInfo = rulesAgentClient.getChannelSourceInfo(sourceId);

    //Assert
    assertThat(channelSourceInfo, notNullValue());
  }

  @Test
  void getChannelInfo__ShouldThrowException() {
    // Arrange
    BookingChannelException ex = mock(BookingChannelException.class);
    when(ex.getMessage()).thenReturn(
        "Error while trying to get channel source info for sourceId=44");
    Mono<ChannelRuleResponseDto> rsp = Mono.error(ex);
    when(responseSpecMock.bodyToMono(ChannelRuleResponseDto.class)).thenReturn(rsp);
    String sourceId = "44";
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();

    Exception exception = assertThrows(BookingChannelException.class,
        () -> rulesAgentClient.getChannelSourceInfo(sourceId));
    //Assert
    assertThat(exception, notNullValue());
    assertThat(exception.getMessage(), is(EXCEPTION_MESSAGE));

  }
  
  @Test
  void getBaseRate__shouldReturnOk() {
    // Arrange
    String ratePlanCode = "BUSIFLEX";
    
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(BaseRateRuleResponseDto.class)).thenReturn(mockBaseRateRuleResponseDto());
    
    //Act
    var rulesResponse = rulesAgentClient.getBaseRate(ratePlanCode);
    
    //Assert
    assertThat(rulesResponse, notNullValue());
    assertEquals("FLEXRATE", rulesResponse.getBaseRate());
  }

  private Mono<RoomSubstitutionRuleResponseDto> mockRoomSubstitutionRuleResponse() {
    return Mono.just(RoomSubstitutionRuleResponseDto.builder()
        .generatedAt(new Date())
        .requestDetails(createRequestDetails())
        .substitutionList(creteSubstitutionList())
        .build());
  }

  private Mono<VatRuleResponseDto> mockVatResponse() {
    return Mono.just(new VatRuleResponseDto());
  }

  private RoomSubstitutionRequestDetailsDto createRequestDetails() {
    return RoomSubstitutionRequestDetailsDto.builder()
        .adults(1)
        .children(0)
        .roomType("Double")
        .pms("OP")
        .build();
  }

  private List<RoomSubstitutionDto> creteSubstitutionList() {
    List<RoomSubstitutionDto> roomSubstitutionList = new LinkedList<>();
    RoomSubstitutionDto roomSubstitution = RoomSubstitutionDto.builder()
        .type("DOUBLE")
        .silent(true)
        .build();
    roomSubstitutionList.add(roomSubstitution);
    return roomSubstitutionList;
  }

  private Mono<ChannelRuleResponseDto> mockChannelRuleResponseDto(){
    return Mono.just(ChannelRuleResponseDto.builder().sourceId("44").requestDetails(
        ChannelRuleRequestDetailsDto.builder().channel("PI").build()).build());
  }
  
  private Mono<BaseRateRuleResponseDto> mockBaseRateRuleResponseDto() {
    return Mono.just(BaseRateRuleResponseDto.builder()
            .baseRate("FLEXRATE")
            .ratePlanCode("BUSIFLEX")
            .promoCode("BUSIFLEX")
        .build());
  }
}
