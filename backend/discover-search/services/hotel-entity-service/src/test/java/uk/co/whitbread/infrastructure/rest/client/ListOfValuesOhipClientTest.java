package uk.co.whitbread.infrastructure.rest.client;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CancellationReasonDto;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CancellationReasonsResponseDto;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
class ListOfValuesOhipClientTest {

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
  @InjectMocks
  private OhipClient ohipClient;

  @Test
  void getListOfCancellationReasons_ShouldReturnOk() {
    //Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(CancellationReasonsResponseDto.class)).thenReturn(mockCancellationReasonsResponseOhipDto());

    //Act
    var cancellationReasons = this.ohipClient.getListOfCancellationReasons("MANOLD");

    //Assert
    assertThat(cancellationReasons, notNullValue());
    assertThat(cancellationReasons.getCancellationReasons(), notNullValue());
    assertThat(cancellationReasons.getCancellationReasons(), hasSize(1));
  }

  private Mono<CancellationReasonsResponseDto> mockCancellationReasonsResponseOhipDto() {

    var cancellationReasonsResponseDto = new CancellationReasonsResponseDto();

    var cancellationReasonDto = new CancellationReasonDto();
    cancellationReasonDto.setCode("ILL");
    cancellationReasonDto.setName("Illness");
    cancellationReasonDto.setDescription("Illness");
    cancellationReasonDto.setActive(Boolean.TRUE);

    cancellationReasonsResponseDto.setCancellationReasons(List.of(cancellationReasonDto));

    return Mono.just(cancellationReasonsResponseDto);
  }

}
