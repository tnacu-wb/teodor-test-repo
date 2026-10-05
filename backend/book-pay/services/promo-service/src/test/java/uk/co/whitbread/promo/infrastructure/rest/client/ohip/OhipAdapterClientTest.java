package uk.co.whitbread.promo.infrastructure.rest.client.ohip;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import uk.co.whitbread.promo.infrastructure.exception.OhipException;
import uk.co.whitbread.promo.infrastructure.rest.client.ohip.model.out.PromotionResponseDto;

@ExtendWith(MockitoExtension.class)
class OhipAdapterClientTest {

  @Mock
  private WebClient webClient;
  @Mock
  private WebClient.ResponseSpec responseSpec;
  @Mock
  private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
  @Mock
  private WebClient.RequestHeadersSpec requestHeadersSpec;
  @InjectMocks
  private OhipAdapterClient ohipAdapterClient;


  @Test
  void getPromotions_success() {
    // Arrange

    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
    when(responseSpec.bodyToFlux(PromotionResponseDto.class))
        .thenReturn(Flux.fromIterable(mockPromotionResponse()));

    // Act
    var response = ohipAdapterClient.getPromotions(List.of("FX10R"), "HEAPTI");

    // Assert
    assertNotNull(response);
    assertThat(response.size(), is(1));
    assertThat(response.get(0).getPromotionCode(), is("FX10R"));
  }

  @Test
  void getPromotions_ThrowsException() {
    // Arrange

    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class)))
        .thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

    when(responseSpec.onStatus(any(), any()))
        .thenReturn(responseSpec);

    when(responseSpec.bodyToFlux(PromotionResponseDto.class))
        .thenReturn(reactor.core.publisher.Flux.error(
            new OhipException(
                "OHIP_ERROR",
                "OHIP downstream error",
                new Exception(),
                500
            )
        ));

    List<String> promoCodes = List.of("FX10R");

    assertThrows(OhipException.class,
        () -> ohipAdapterClient.getPromotions(promoCodes, "HEAPTI")
    );

  }


  private List<PromotionResponseDto> mockPromotionResponse() {
    return List.of(PromotionResponseDto.builder()
        .promotionCode("FX10R")
        .promotionName("Flex Rate 10% Discount Room Only")
        .bookingStartDate(LocalDate.parse("2025-08-19"))
        .bookingEndDate(LocalDate.parse("2025-09-30"))
        .stayStartDate(LocalDate.parse("2025-10-01"))
        .stayEndDate(LocalDate.parse("2025-11-30"))
        .build());
  }

}

