package uk.co.whitbread.review.infrastructure.rest.client.review;

import io.micrometer.tracing.Tracer;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.review.domain.model.out.Award;
import uk.co.whitbread.review.domain.model.out.Review;
import uk.co.whitbread.review.domain.model.out.ReviewResponse;
import uk.co.whitbread.review.domain.model.out.SubRating;
import uk.co.whitbread.review.infrastructure.rest.client.review.mapper.ReviewResponseMapper;
import uk.co.whitbread.review.infrastructure.rest.client.review.model.TripAdvisorData;
import uk.co.whitbread.review.infrastructure.rest.client.review.model.TripAdvisorReview;
import uk.co.whitbread.review.infrastructure.rest.client.review.model.TripAdvisorReviews;
import uk.co.whitbread.shared.commons.logging.trace.ConcurrentTracer;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
public class ReviewOutPortImplTest {
  @InjectMocks
  private ReviewOutPortImpl reviewOutPort;
  @Mock
  private TripAdvisorClient tripAdvisorClient;
  @Spy
  private ConcurrentTracer concurrentTracer = new ConcurrentTracer(Tracer.NOOP);
  @Mock
  private ReviewResponseMapper reviewResponseMapper;

  @Test
  void testTripAdvisorResponse(){
    //mock
    String hotelCode = "KINPTI";
    String lang = "en_US";
    Integer limit = 5;

    TripAdvisorData tripAdvisorData = mock(TripAdvisorData.class);
    TripAdvisorReviews tripAdvisorReviews = new TripAdvisorReviews();

    ReviewResponse reviewResponse = mockReviewResponse();

    when(tripAdvisorClient.getTripAdvisorData(any(), any(), any())).thenReturn(tripAdvisorData);
    when(tripAdvisorClient.getTripAdvisorReviews(any(), any(), any())).thenReturn(tripAdvisorReviews);
    when(reviewResponseMapper.toReviewResponseDto(any(), any(), any())).thenReturn(reviewResponse);
    var result = reviewOutPort.getTripAdvisorResponse(hotelCode, lang, limit);
    // Assert
    assertThat(result.getName(), is("Premier Inn London King's Cross Hotel"));

  }
  private static ReviewResponse mockReviewResponse() {
    Award award = Award.builder()
            .awardType("Travelers Choice")
            .image("https://static.tacdn.com/img2/travelers_choice/widgets/tchotel_2023_L.png")
            .year(2023)
            .build();

    SubRating subRating = SubRating.builder()
            .localisedName("Location")
            .ratingImageUrl("https://static.tacdn.com/img2/ratings/traveler/ss5.0.svg")
            .value(5.0)
            .build();

    Review review = Review.builder()
            .title("Very enjoyable stay.")
            .text("We stayed in The Premier Inn Kings Cross for 6 nights")
            .rating(4.0)
            .publishedDate("2024-02-12T07:09:56-0500")
            .tripType("Couples")
            .build();

    ReviewResponse reviewResponse = ReviewResponse.builder()
            .awards(List.of(award))
            .rating(4.0)
            .subRatings(List.of(subRating))
            .name("Premier Inn London King's Cross Hotel")
            .address("26-30 York Way Kings Cross, London N1 9AA England")
            .reviews(List.of(review))
            .numberOfReviews(6689)
            .writeReview("https://www.tripadvisor.com/UserReview-g186338-d571109-Premier_Inn_London_King_s_Cross_Hotel-London_England.html?m=67640")
            .webUrl("https://www.tripadvisor.com/Hotel_Review-g186338-d571109-Reviews-Premier_Inn_London_King_s_Cross_Hotel-London_England.html?m=67640")
            .locationId("571109")
            .hotelCode("KINPTI")
            .build();
    return reviewResponse;
  }
}
