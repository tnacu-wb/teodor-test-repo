package uk.co.whitbread.payments.infrastructure.rest.client.hotels;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.co.whitbread.payments.domain.exception.HotelInfoException;
import uk.co.whitbread.payments.infrastructure.rest.client.hotels.model.out.HotelInfoDto;
import uk.co.whitbread.payments.infrastructure.rest.client.hotels.service.HotelInfoClient;
import uk.co.whitbread.payments.infrastructure.rest.client.service.CustomTestResponseSpec;

import java.util.function.Function;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
class HotelInfoClientTest {

    @Mock
    private WebClient webClient;
    @SuppressWarnings("rawtypes")
    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;
    @SuppressWarnings("rawtypes")
    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
    @Mock
    private CustomTestResponseSpec responseSpecMock;
    @InjectMocks
    private HotelInfoClient hotelInfoClient;


    @Test
    void findHotelPaymentDetails_throwsException() {
      var hotelCode = "hotelCode";
      var country = "country";
      var language = "language";
      String errorMessage = String.format("Error while trying to get hotel payment details "
              + "from content service with hotelCode=%s, country=%s and language=%s",
          hotelCode, country, language);
      //Arrange
      when(webClient.get()).thenReturn(requestHeadersUriSpec);
      when(requestHeadersUriSpec.uri(any(Function.class)))
          .thenReturn(requestHeadersSpec);
      when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
      when(responseSpecMock.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
      when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();
      when(responseSpecMock.bodyToMono(HotelInfoDto.class))
            .thenReturn(Mono.error(new HotelInfoException("message",
                  errorMessage, new Exception(), 1)));

      //Act
      Exception exception = assertThrows(HotelInfoException.class,
          () -> hotelInfoClient.findHotelPaymentDetails(hotelCode, country, language));

      //Assert
      String actualMessage = exception.getMessage();
      assertTrue(actualMessage.contains(errorMessage));
    }
}
