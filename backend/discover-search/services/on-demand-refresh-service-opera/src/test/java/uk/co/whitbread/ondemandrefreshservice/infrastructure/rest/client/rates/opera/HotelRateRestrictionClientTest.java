package uk.co.whitbread.ondemandrefreshservice.infrastructure.rest.client.rates.opera;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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
import uk.co.whitbread.ondemandrefreshservice.infrastructure.model.rates.DailyRates;
import uk.co.whitbread.ondemandrefreshservice.infrastructure.rest.client.rates.exceptions.DailyRatesException;
import uk.co.whitbread.ondemandrefreshservice.utils.CustomTestResponseSpec;

@ExtendWith(MockitoExtension.class)
class HotelRateRestrictionClientTest {

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
  private CustomTestResponseSpec customResponseSpec;

  @InjectMocks
  private HotelDailyRatesClient hotelDailyRatesClient;

  @Test
  void getDailyRatesSucceeds() {

    DailyRates expectedResponse = new DailyRates();
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(DailyRates.class)).thenReturn(Mono.just(expectedResponse));

    DailyRates actualResponse =
        hotelDailyRatesClient.getDailyRates("HOTEL-ONE", "RPLANC", 1L, "2024-10-20", "2024-10-20");

    assertEquals(expectedResponse, actualResponse);
  }

  @Test
  void getDailyRates_on4xxErrorCode() {
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.BAD_REQUEST);
    when(customResponseSpec.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();

    assertThrows(DailyRatesException.class,
        () -> hotelDailyRatesClient.getDailyRates("HOTEL-ONE", "RPLANC", 1L, "2024-10-20", "2024-10-20"));
  }
}