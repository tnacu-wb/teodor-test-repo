package uk.co.whitbread.ohip.infrastructure.rest.client.lov.ohip;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.function.Function;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.lov.ListOfValues;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
class OhipListOfValuesClientTest {

  @InjectMocks
  private OhipListOfValuesClient ohipListOfValuesClient;
  @Mock
  private WebClient webClient;
  @Mock
  private WebClient.ResponseSpec responseSpec;
  @SuppressWarnings("rawtypes")
  @Mock
  private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
  @SuppressWarnings("rawtypes")
  @Mock
  private WebClient.RequestHeadersSpec requestHeadersSpec;

  @Test
  void getListOfCancellationReasons__shouldReturnOk() {
    //Arrange
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(ListOfValues.class)).thenReturn(
        mockListOfValues());

    //Act
    ListOfValues response = ohipListOfValuesClient.getListOfCancellationReasons("HOTELID");

    //Assert
    assertThat(response, notNullValue());
    verifyNoMoreInteractions(webClient);
  }

  private Mono<ListOfValues> mockListOfValues() {
    return Mono.just(new ListOfValues());
  }

}
