package uk.co.whitbread.review.domain.model.out;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {

  private List<Award> awards;
  private Double rating;
  private List<SubRating> subRatings;
  private String name;
  private String address;
  private List<Review> reviews;
  private int numberOfReviews;
  private String writeReview;
  private String webUrl;
  private String locationId;
  private String hotelCode;

}
