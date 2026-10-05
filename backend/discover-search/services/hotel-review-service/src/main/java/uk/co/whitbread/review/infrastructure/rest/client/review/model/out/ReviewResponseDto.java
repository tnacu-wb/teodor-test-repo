package uk.co.whitbread.review.infrastructure.rest.client.review.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponseDto {

  private List<AwardDto> awards;
  private Double rating;
  private List<SubRatingDto> subRatings;
  private String name;
  private String address;
  private List<ReviewDto> reviews;
  private int numberOfReviews;
  private String writeReview;
  private String webUrl;
  private String locationId;
  private String hotelCode;
}
