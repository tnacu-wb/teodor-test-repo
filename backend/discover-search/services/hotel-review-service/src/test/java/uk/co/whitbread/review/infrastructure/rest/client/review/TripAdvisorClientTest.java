package uk.co.whitbread.review.infrastructure.rest.client.review;

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
import uk.co.whitbread.review.infrastructure.rest.client.review.exceptions.ErrorCode;
import uk.co.whitbread.review.infrastructure.rest.client.review.exceptions.HotelReviewServiceException;
import uk.co.whitbread.review.infrastructure.rest.client.review.model.TripAdvisorData;
import uk.co.whitbread.review.infrastructure.rest.client.review.model.TripAdvisorReview;
import uk.co.whitbread.review.infrastructure.rest.client.review.model.TripAdvisorReviews;
import uk.co.whitbread.review.infrastructure.rest.client.review.properties.TripAdvisorProperties;
import uk.co.whitbread.review.infrastructure.rest.utils.CustomTestResponseSpec;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
public class TripAdvisorClientTest {

  @InjectMocks
  private TripAdvisorClient tripAdvisorClient;
  @Mock
  TripAdvisorProperties tripAdvisorProperties;
  @Mock
  private WebClient tripAdvisorWebClient;
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


  @Test
  void testGetTripAdvisorDataSuccess() {
    //mock
    String hotelCode = "KINPTI";
    String lang = "en_US";
    Integer limit = 5;
    TripAdvisorData mockTripAdvisorData = new TripAdvisorData();
    mockTripAdvisorData.setName("premium");
    mockTripAdvisorData.setRating(4.0);

    // Arrange
    when(tripAdvisorWebClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(TripAdvisorData.class)).thenReturn(Mono.just(mockTripAdvisorData));

    // Act
    var response = tripAdvisorClient.getTripAdvisorData(hotelCode, lang, limit);

    // Assert
    assertEquals("premium", response.getName());
  }


  @Test
  void testGetTripAdvisorDataStatusThrowsHotelReviewServiceException() {
    //Arrange
    String hotelCode = "KINPTI";
    String lang = "en_US";
    Integer limit = 5;

    when(tripAdvisorWebClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(customResponseSpec.onStatus(any(Predicate.class),
        any(Function.class))).thenCallRealMethod();

    // Act & Assert
    var exception = assertThrows(HotelReviewServiceException.class,
        () -> tripAdvisorClient.getTripAdvisorData(hotelCode, lang, limit));
    assertNotNull(exception);
    assertEquals(ErrorCode.TRIPADVISOR_DATA_EXCEPTION.getCode(), exception.getErrorCode());
    assertEquals("Error while trying to get trip data. hotelCode=KINPTI not found in TripAdvisor API",
        exception.getMessage());
  }


  @Test
  void testGetTripAdvisoryReviewSuccess() {
    //mock
    String hotelCode = "KINPTI";
    String lang = "en_US";
    Integer limit = 5;
    TripAdvisorReview mockTripAdvisorReview = new TripAdvisorReview();
    mockTripAdvisorReview.setText("mocking trip advisor review");
    mockTripAdvisorReview.setTitle("title");

    TripAdvisorReviews tripAdvisorReviews = new TripAdvisorReviews();
    tripAdvisorReviews.setData(List.of(mockTripAdvisorReview));


    // Arrange
    when(tripAdvisorWebClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(TripAdvisorReviews.class)).thenReturn(
        Mono.just(tripAdvisorReviews));

    // Act
    var response = tripAdvisorClient.getTripAdvisorReviews(hotelCode, lang, limit);

    // Assert
    assertEquals(mockTripAdvisorReview, response.getData().get(0));
  }

  @Test
  void testGetTripAdvisorReviewsStatusThrowsHotelReviewServiceException() {
    //Arrange
    String hotelCode = "KINPTI";
    String lang = "en_US";
    Integer limit = 5;

    when(tripAdvisorWebClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(customResponseSpec);
    when(customResponseSpec.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
    when(customResponseSpec.onStatus(any(Predicate.class),
        any(Function.class))).thenCallRealMethod();

    // Act & Assert
    var exception = assertThrows(HotelReviewServiceException.class,
        () -> tripAdvisorClient.getTripAdvisorReviews(hotelCode, lang, limit));

    //Assert
    assertNotNull(exception);
    assertEquals(ErrorCode.TRIPADVISOR_REVIEWS_EXCEPTION.getCode(), exception.getErrorCode());
    assertEquals("Error while trying to get reviews. hotelCode=KINPTI not found in TripAdvisor API",
        exception.getMessage());
  }
}
