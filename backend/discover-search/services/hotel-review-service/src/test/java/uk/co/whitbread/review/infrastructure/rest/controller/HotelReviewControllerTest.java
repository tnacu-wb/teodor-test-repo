package uk.co.whitbread.review.infrastructure.rest.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.whitbread.review.domain.model.out.Award;
import uk.co.whitbread.review.domain.model.out.Review;
import uk.co.whitbread.review.domain.model.out.ReviewResponse;
import uk.co.whitbread.review.domain.model.out.SubRating;
import uk.co.whitbread.review.domain.ports.primary.ReviewInPort;
import uk.co.whitbread.review.infrastructure.rest.client.review.mapper.TripReviewMapper;
import uk.co.whitbread.review.infrastructure.rest.client.review.model.out.AwardDto;
import uk.co.whitbread.review.infrastructure.rest.client.review.model.out.AwardImagesDto;
import uk.co.whitbread.review.infrastructure.rest.client.review.model.out.ReviewDto;
import uk.co.whitbread.review.infrastructure.rest.client.review.model.out.ReviewResponseDto;
import uk.co.whitbread.review.infrastructure.rest.client.review.model.out.SubRatingDto;
import uk.co.whitbread.review.infrastructure.rest.controller.review.HotelReviewController;

@ExtendWith(MockitoExtension.class)
public class HotelReviewControllerTest {
  @InjectMocks
  private HotelReviewController hotelReviewController;
  @Mock
  private ReviewInPort reviewInPort;
  @Mock
  private TripReviewMapper tripReviewMapper;

  @Test
  void getSingleHotelShouldReturnOK() {

    String hotelCode = "KINPTI";
    String lang = "en_US";
    Integer limit = 5;

    ReviewResponse reviewResponse = mockReviewResponse();

    ReviewResponseDto reviewResponseDto = mockReviewResponseDTO(reviewResponse);

    //Arrange
    when(reviewInPort.getReviewResponse(anyString(), anyString(), any())).thenReturn(reviewResponse);
    when(tripReviewMapper.toDto(reviewResponse)).thenReturn(reviewResponseDto);
    //Act
    var response = hotelReviewController.getReviewsForSingleHotel(hotelCode, lang, limit);
    //Assert
    assertNotNull(response);
    assertEquals(response.toString(), 200, response.getStatusCode().value());

  }
  private ReviewResponseDto mockReviewResponseDTO(ReviewResponse reviewResponse) {
AwardImagesDto awardImagesDto = AwardImagesDto.builder()
        .large("https://static.tacdn.com/img2/travelers_choice/widgets/tchotel_2023_L.png")
        .small("https://static.tacdn.com/img2/travelers_choice/widgets/tchotel_2023_L.png")
        .build();

    AwardDto awardDto = AwardDto.builder()
            .awardType("Travelers Choice")
            .images(awardImagesDto)
            .year(2023)
            .build();

    SubRatingDto subRatingDto = SubRatingDto.builder()
            .localisedName("Location")
            .ratingImageUrl("https://static.tacdn.com/img2/ratings/traveler/ss5.0.svg")
            .value(5.0)
            .build();

    ReviewDto reviewDto = ReviewDto.builder()
            .title("Very enjoyable stay.")
            .text("We stayed in The Premier Inn Kings Cross for 6 nights")
            .rating(4.0)
            .publishedDate("2024-02-12T07:09:56-0500")
            .tripType("Couples")
            .build();

    return ReviewResponseDto
            .builder()
            .hotelCode(reviewResponse.getHotelCode())
            .awards(List.of(awardDto))
            .rating(4.0)
            .subRatings(List.of(subRatingDto))
            .name("Premier Inn London King's Cross Hotel")
            .address("26-30 York Way Kings Cross, London N1 9AA England")
            .reviews(List.of(reviewDto))
            .numberOfReviews(6689)
            .writeReview("https://www.tripadvisor.com/UserReview-g186338-d571109-Premier_Inn_London_King_s_Cross_Hotel-London_England.html?m=67640")
            .webUrl("https://www.tripadvisor.com/Hotel_Review-g186338-d571109-Reviews-Premier_Inn_London_King_s_Cross_Hotel-London_England.html?m=67640")
            .locationId("571109")
            .hotelCode("KINPTI")
            .build();
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