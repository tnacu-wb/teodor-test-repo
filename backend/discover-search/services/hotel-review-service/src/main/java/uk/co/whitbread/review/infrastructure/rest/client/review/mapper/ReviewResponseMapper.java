package uk.co.whitbread.review.infrastructure.rest.client.review.mapper;

import java.util.Collection;
import java.util.Optional;
import org.springframework.stereotype.Component;
import uk.co.whitbread.review.domain.model.out.Award;
import uk.co.whitbread.review.domain.model.out.Review;
import uk.co.whitbread.review.domain.model.out.ReviewResponse;
import uk.co.whitbread.review.domain.model.out.SubRating;
import uk.co.whitbread.review.domain.model.out.User;
import uk.co.whitbread.review.infrastructure.rest.client.review.model.TripAdvisorAddress;
import uk.co.whitbread.review.infrastructure.rest.client.review.model.TripAdvisorAward;
import uk.co.whitbread.review.infrastructure.rest.client.review.model.TripAdvisorAwardImages;
import uk.co.whitbread.review.infrastructure.rest.client.review.model.TripAdvisorData;
import uk.co.whitbread.review.infrastructure.rest.client.review.model.TripAdvisorReview;
import uk.co.whitbread.review.infrastructure.rest.client.review.model.TripAdvisorReviews;
import uk.co.whitbread.review.infrastructure.rest.client.review.model.TripAdvisorSubRating;
import uk.co.whitbread.review.infrastructure.rest.client.review.model.TripAdvisorUser;

/**
 * Maps from TripAdvisor domain to Hotel Review domain.
 */
@Component
public class ReviewResponseMapper {

  public ReviewResponse toReviewResponseDto(TripAdvisorData tripAdvisorData, TripAdvisorReviews tripAdvisorReviews,
                                            String hotelCode) {
    return constructReviewResponse(tripAdvisorData, tripAdvisorReviews, hotelCode);
  }

  public Award toDto(TripAdvisorAward tripAdvisorAward) {
    Award award = Award.builder()
            .awardType(tripAdvisorAward.getAwardType())
            .year(tripAdvisorAward.getYear())
            .image(Optional.ofNullable(tripAdvisorAward.getImages())
                    .map(TripAdvisorAwardImages::getSmall)
                    .orElse(null))
            .build();
    return award;
  }

  public Review toDto(TripAdvisorReview tripAdvisorReview) {
    Review review = Review.builder()
            .publishedDate(tripAdvisorReview.getPublishedDate())
            .rating(tripAdvisorReview.getRating())
            .text(tripAdvisorReview.getText())
            .title(tripAdvisorReview.getTitle())
            .tripType(tripAdvisorReview.getTripType())
            .user(toDto(tripAdvisorReview.getUser()))
            .build();
    return review;
  }

  public User toDto(TripAdvisorUser tripAdvisorUser) {
    User user = User.builder()
            .username(tripAdvisorUser.getUsername())
            .location(tripAdvisorUser.getUserLocation().getName())
            .build();
    return user;
  }

  public SubRating toDto(TripAdvisorSubRating tripAdvisorSubRating) {
    SubRating subRating = SubRating.builder()
            .localisedName(tripAdvisorSubRating.getLocalizedName())
            .ratingImageUrl(tripAdvisorSubRating.getRatingImageUrl())
            .value(tripAdvisorSubRating.getValue())
            .build();
    return subRating;
  }


  private ReviewResponse constructReviewResponse(TripAdvisorData tripAdvisorData, TripAdvisorReviews
          tripAdvisorReviews, String hotelCode) {
    ReviewResponse response =  ReviewResponse.builder().build();
    response.setName(tripAdvisorData.getName());
    response.setNumberOfReviews(tripAdvisorData.getNumReviews());
    response.setWebUrl(tripAdvisorData.getWebUrl());
    response.setWriteReview(tripAdvisorData.getWriteReview());
    response.setRating(tripAdvisorData.getRating());
    response.setLocationId(tripAdvisorData.getLocationId());
    response.setHotelCode(hotelCode);

    response.setAddress(Optional.ofNullable(tripAdvisorData.getAddressObj())
                    .map(TripAdvisorAddress::getAddressString)
                    .orElse(null));

    response.setAwards(Optional.ofNullable(tripAdvisorData.getAwards()).stream()
                    .flatMap(Collection::stream)
                    .map(this::toDto).toList());

    response.setSubRatings(Optional.ofNullable(tripAdvisorData.getSubratings()).stream()
                    .flatMap(Collection::stream)
                    .map(this::toDto).toList());

    response.setReviews(Optional.ofNullable(tripAdvisorReviews.getData()).stream()
                    .flatMap(Collection::stream)
                    .map(this::toDto)
                    .toList());

    return response;
  }


}
