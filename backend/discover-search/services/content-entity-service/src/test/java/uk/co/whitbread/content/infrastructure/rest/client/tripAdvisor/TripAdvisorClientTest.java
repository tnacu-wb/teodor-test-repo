package uk.co.whitbread.content.infrastructure.rest.client.tripAdvisor;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.content.domain.model.ErrorCode.TRIPADVISOR_HOTEL_INFORMATION_EXCEPTION;

import java.math.BigDecimal;
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
import uk.co.whitbread.content.domain.model.hotel.out.Awards;
import uk.co.whitbread.content.domain.model.hotel.out.Reviews;
import uk.co.whitbread.content.domain.model.hotel.out.SubRatings;
import uk.co.whitbread.content.infrastructure.rest.client.aem.exceptions.AemResponseException;
import uk.co.whitbread.content.infrastructure.rest.client.content.aem.adapter.HotelReviewClient;
import uk.co.whitbread.content.infrastructure.rest.controller.hotel.model.out.TripAdvisorReviewsDto;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
class TripAdvisorClientTests {

  @InjectMocks
  private HotelReviewClient hotelReviewClient;
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

  @Test
  void getContent_TripAdvisor_ShouldReturnOK() {
    //Arrange

    when(responseSpec.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpec);
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class)))
        .thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    when(responseSpec.bodyToMono(TripAdvisorReviewsDto.class)).thenReturn(
        mockTripAdvisorReviewResponse());
    //Act
    final var contentTripAdvisorResponse =
        hotelReviewClient.getTripAdvisorReviews("KINPTI", "en");

    //Assert
    assertThat(contentTripAdvisorResponse, notNullValue());
    assertEquals("Premier Inn London King's Cross Hotel", contentTripAdvisorResponse.getName());
    assertEquals(6689, contentTripAdvisorResponse.getNumberOfReviews());
  }

  @Test
  void getContent_TripAdvisor_ShouldReturnException() {
    when(webClient.get()).thenReturn(requestHeadersUriSpec);
    when(requestHeadersUriSpec.uri(any(Function.class)))
        .thenReturn(requestHeadersSpec);
    when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    var exception = new AemResponseException(
        TRIPADVISOR_HOTEL_INFORMATION_EXCEPTION,
        "message",
        new Exception());
    when(responseSpec.onStatus(any(), any())).thenThrow(exception);

    //Act
    var actual =
        assertThrows(AemResponseException.class,
            () -> hotelReviewClient.getTripAdvisorReviews("KINPTI", "en"));

    //Assert
    assertThat(actual, notNullValue());
    assertThat(actual.getGlobalErrTextTemplate(),
        is(TRIPADVISOR_HOTEL_INFORMATION_EXCEPTION.getMessage()));
    assertThat(actual.getMessage(), is("message"));
    assertThat(actual.getErrorCode(), is(TRIPADVISOR_HOTEL_INFORMATION_EXCEPTION.getCode()));
  }

  private Mono<TripAdvisorReviewsDto> mockTripAdvisorReviewResponse() {
    Awards award = Awards.builder()
        .awardType("Travelers Choice")
        .image("https://static.tacdn.com/img2/travelers_choice/widgets/tchotel_2023_L.png")
        .year("2023")
        .build();

    SubRatings subRating = SubRatings.builder()
        .localisedName("Location")
        .ratingImageUrl("https://static.tacdn.com/img2/ratings/traveler/ss5.0.svg")
        .value(BigDecimal.valueOf(5.0))
        .build();

    Reviews review = Reviews.builder()
        .title("Very enjoyable stay.")
        .text("We stayed in The Premier Inn Kings Cross for 6 nights")
        .rating(BigDecimal.valueOf(4.0))
        .publishedDate("2024-02-12T07:09:56-0500")
        .tripType("Couples")
        .build();

    TripAdvisorReviewsDto reviewResponse1 = TripAdvisorReviewsDto.builder()
        .awards(List.of(award))
        .rating(BigDecimal.valueOf(4.0))
        .subRatings(List.of(subRating))
        .name("Premier Inn London King's Cross Hotel")
        .address("26-30 York Way Kings Cross, London N1 9AA England")
        .reviews(List.of(review))
        .numberOfReviews(6689)
        .writeReview(
            "https://www.tripadvisor.com/UserReview-g186338-d571109-Premier_Inn_London_King_s_Cross_Hotel-London_England.html?m=67640")
        .webUrl(
            "https://www.tripadvisor.com/Hotel_Review-g186338-d571109-Reviews-Premier_Inn_London_King_s_Cross_Hotel-London_England.html?m=67640")
        .locationId("571109")
        .hotelCode("KINPTI")
        .build();
    return Mono.just(reviewResponse1);
  }
}