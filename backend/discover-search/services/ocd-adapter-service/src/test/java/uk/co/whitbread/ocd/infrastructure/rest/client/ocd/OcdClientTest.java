package uk.co.whitbread.ocd.infrastructure.rest.client.ocd;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

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
import uk.co.whitbread.hotel.ocd.adapter.generated.models.Offer;
import uk.co.whitbread.hotel.ocd.adapter.generated.models.OfferDetailsResponse;
import uk.co.whitbread.ocd.domain.model.tax.in.TaxRequest;
import uk.co.whitbread.ocd.infrastructure.rest.client.ocd.exceptions.OcdOfferException;
import uk.co.whitbread.ocd.infrastructure.rest.client.utils.CustomTestResponseSpec;

@ExtendWith(MockitoExtension.class)
public class OcdClientTest {

  @InjectMocks
  private OcdClient ocdClient;
  @Mock
  private WebClient ocdWebClient;
  @Mock
  private WebClient.ResponseSpec responseSpec;
  @SuppressWarnings("rawtypes")
  @Mock
  private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
  @SuppressWarnings("rawtypes")
  @Mock
  private WebClient.RequestHeadersSpec requestHeadersSpec;
  @Mock
  private CustomTestResponseSpec responseSpecMock;

  @Test
  void getTaxDetails____ShouldReturnOK() {

    //Arrange
    when(ocdWebClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(OfferDetailsResponse.class)).thenReturn(
        mockOfferDetailsResponse());

    //Act
    OfferDetailsResponse response =
        ocdClient.getTaxDetails(mockTaxRequest());

    //Assert
    assertThat(response, notNullValue());
  }

  @Test
  void getProfileIdByReservation__ShouldThrowException() {
    // Arrage
    when(ocdWebClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpecMock);
    when(responseSpecMock.getStatus()).thenReturn(HttpStatus.BAD_REQUEST);
    when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class))).thenCallRealMethod();

    // Act
    var thrownException = assertThrowsExactly(OcdOfferException.class,
        () -> ocdClient.getTaxDetails(mockTaxRequest()));

    //Assert
    String debugMessage = thrownException.getMessage();
    Assertions.assertEquals("Error while trying to get the offers for hotelId=BRECIT", debugMessage);
    verifyNoMoreInteractions(ocdWebClient);
  }

  private TaxRequest mockTaxRequest() {
    return TaxRequest.builder()
        .hotelId("BRECIT")
        .adults(1)
        .roomType("FLEXRATE")
        .roomType("DOUBLE")
        .arrivalDate("2025-10-10")
        .departureDate("2025-10-11")
        .build();
  }

  private Mono<OfferDetailsResponse> mockOfferDetailsResponse() {
    Offer offer = new Offer();
    offer.setRatePlanCode("FLEXRATE");
    OfferDetailsResponse offerDetailsResponse = new OfferDetailsResponse();
    offerDetailsResponse.setOffer(offer);
    return Mono.just(offerDetailsResponse);
  }
}
